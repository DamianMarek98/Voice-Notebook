package pg.masters.backend.transcript;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pg.masters.backend.recognition.azure.AzureService;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;
import pg.masters.backend.recognition.google.GoogleService;
import pg.masters.backend.recognition.revai.RevaiService;
import pg.masters.backend.recognition.revai.RevaiTranscriptionException;
import pg.masters.backend.transcript.errors.TranscribeFileException;
import pg.masters.backend.utils.ResourceUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TranscribeService {

    private final GoogleService googleService;
    private final AzureService azureService;
    private final RevaiService revaiService;
    private final AudioFormat audioFormat = new AudioFormat(16000f, 16, 1, true, false);

    /**
     * @param transcriptGroup - has to contain proper wav file, params in audio format.
     *                        best converter online-audio-converter.com
     * @return list of transcriptions
     * @throws TranscribeFileException
     */
    public List<TranscribeResult> transcribe(TranscriptGroup transcriptGroup) throws TranscribeFileException {
        var pathForAzure = ResourceUtils.audioTempFilePath() + transcriptGroup.getName() + "-azure.wav";
        var pathForGoogle = ResourceUtils.audioTempFilePath() + transcriptGroup.getName() + "-google.wav";
        var pathForRevAi = ResourceUtils.audioTempFilePath() + transcriptGroup.getName() + "-rev-ai.wav";
        try {
            var fileAzure = new File(pathForAzure);
            var fileGoogle = new File(pathForGoogle);
            var fileRevAi = new File(pathForRevAi);
            writeAudioToFile(transcriptGroup, fileAzure);
            writeAudioToFile(transcriptGroup, fileGoogle);
            writeAudioToFile(transcriptGroup, fileRevAi);

            var executor = Executors.newFixedThreadPool(3);
            var completionService = new ExecutorCompletionService<TranscribeResult>(executor);
            transcribeWithAzure(pathForAzure, completionService);
            transcribeWithGoogle(fileGoogle, completionService);
            transcribeWithRevAi(pathForRevAi, completionService);
            var results = new ArrayList<TranscribeResult>();
            waitForResults(completionService, results);
            Files.delete(Path.of(pathForAzure));
            Files.delete(Path.of(pathForGoogle));
            Files.delete(Path.of(pathForRevAi));
            return results;
        } catch (IOException e) {
            throw new TranscribeFileException(e.getMessage());
        }
    }

    private void writeAudioToFile(TranscriptGroup transcriptGroup, File file) throws IOException, TranscribeFileException {
        if (file.createNewFile()) {
            try (var ais = new AudioInputStream(new ByteArrayInputStream(transcriptGroup.getWaveFile()), audioFormat,
                    transcriptGroup.getWaveFile().length)) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
            }
        } else {
            throw new TranscribeFileException("Couldn't create file with name: " + transcriptGroup.getName());
        }
    }

    private void waitForResults(ExecutorCompletionService<TranscribeResult> completionService, ArrayList<TranscribeResult> results) {
        try {
            while (results.size() != 3) {
                results.add(completionService.take().get());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error(e.getMessage());
        }
    }

    private void transcribeWithAzure(String filePath, CompletionService<TranscribeResult> completionService) {
        completionService.submit(() -> {
            var result = "";
            try {
                result = azureService.transcriptionFromFile(filePath);
                if (result.startsWith(AzureService.AZURE_ERROR)) {
                    return new TranscribeResult(result, TranscribeStatus.FAILURE, RecognitionServiceProvider.AZURE);
                }
            } catch (InterruptedException ie) {
                log.error("Error on Azure file recognition: ", ie);
                Thread.currentThread().interrupt();
                return new TranscribeResult(ie.getMessage(), TranscribeStatus.FAILURE, RecognitionServiceProvider.AZURE);
            } catch (ExecutionException ee) {
                log.error("Error on Azure file recognition: ", ee);
                return new TranscribeResult(ee.getMessage(), TranscribeStatus.FAILURE, RecognitionServiceProvider.AZURE);
            }

            return new TranscribeResult(result, TranscribeStatus.SUCCESS, RecognitionServiceProvider.AZURE);
        });
    }

    private void transcribeWithGoogle(File file, CompletionService<TranscribeResult> completionService) {
        completionService.submit(() -> {
            try {
                return new TranscribeResult(googleService.transcriptionFromFile(Files.readAllBytes(file.toPath())),
                        TranscribeStatus.SUCCESS, RecognitionServiceProvider.GOOGLE);
            } catch (IOException | ExecutionException e) {
                log.error("Error while google transcribe: " + e.getMessage());
                return new TranscribeResult(e.getMessage(), TranscribeStatus.FAILURE, RecognitionServiceProvider.GOOGLE);
            } catch (InterruptedException e) {
                log.error("Error while google transcribe: " + e.getMessage());
                Thread.currentThread().interrupt();
                return new TranscribeResult(e.getMessage(), TranscribeStatus.FAILURE, RecognitionServiceProvider.GOOGLE);
            }
        });
    }

    private void transcribeWithRevAi(String filePath, CompletionService<TranscribeResult> completionService) {
        completionService.submit(() -> {
            try {
                return new TranscribeResult(this.revaiService.transcriptionFromFile(filePath), TranscribeStatus.SUCCESS,
                        RecognitionServiceProvider.REV_AI);
            } catch (IOException | RevaiTranscriptionException e) {
                log.error("Error while rev ai transcribe: " + e.getMessage());
                return new TranscribeResult(e.getMessage(), TranscribeStatus.FAILURE, RecognitionServiceProvider.REV_AI);
            } catch (InterruptedException e) {
                log.error("Error while rev ai transcribe: " + e.getMessage());
                Thread.currentThread().interrupt();
                return new TranscribeResult(e.getMessage(), TranscribeStatus.FAILURE, RecognitionServiceProvider.REV_AI);
            }
        });
    }

    protected record TranscribeResult(String result, TranscribeStatus transcribeStatus,
                                      RecognitionServiceProvider provider) {
    }

    protected enum TranscribeStatus {
        SUCCESS,
        FAILURE
    }
}
