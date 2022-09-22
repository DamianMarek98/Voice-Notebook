package pg.masters.backend.recognition.azure;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pg.masters.backend.recognition.RecognitionService;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;
import pg.masters.backend.utils.ResourceUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Service
public class AzureService implements RecognitionService {
    public static final String LANGUAGE_CODE = "pl-PL";
    public static final String AZURE_ERROR = "Azure error: ";
    private static Semaphore stopTranslationWithFileSemaphore;
    private final SpeechConfig speechConfig;
    private final AzureStreamRecognition azureStreamRecognition;
    private final Deque<String> recognizedTexts = new ArrayDeque<>();

    public AzureService(AzureProperties azureProperties, AzureStreamRecognition azureStreamRecognition) {
        this.speechConfig = SpeechConfig.fromSubscription(azureProperties.getKey(), azureProperties.getRegion());
        this.speechConfig.setSpeechRecognitionLanguage(LANGUAGE_CODE);
        this.azureStreamRecognition = azureStreamRecognition;
    }

    @Override
    public void recognizeInStream(String filePath) {
        if (!this.azureStreamRecognition.started()) {
            this.azureStreamRecognition.start(this.speechConfig);
        }

        this.azureStreamRecognition.pushFile(filePath);
    }

    @Override
    public Optional<String> getReply() {
        var sb = new StringBuilder();
        while (!recognizedTexts.isEmpty()) {
            sb.append(recognizedTexts.pop());
        }

        return wrapReply(sb.toString());
    }

    @Override
    public void handleNewRecognition(String text) {
        recognizedTexts.add(text);
    }

    @Override
    public void stopStreamRecognition() {
        this.azureStreamRecognition.stop();
        recognizedTexts.clear();
    }

    @Override
    public RecognitionServiceProvider getRecognitionServiceProvider() {
        return RecognitionServiceProvider.AZURE;
    }

    public String transcriptionFromFile(String filePath) throws InterruptedException, ExecutionException {
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(filePath);
        SpeechRecognizer recognizer = new SpeechRecognizer(this.speechConfig, LANGUAGE_CODE, audioConfig);

        // First initialize the semaphore.
        stopTranslationWithFileSemaphore = new Semaphore(0);
        AtomicReference<String> result = new AtomicReference<>("");
        recognizer.recognized.addEventListener((s, e) -> {
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                var currentText = result.get();
                result.set(currentText + e.getResult().getText());
                log.info("RECOGNIZED: Text=" + e.getResult().getText());
            } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                log.info("NOMATCH: Speech could not be recognized.");
            }
        });

        // recognizer.recognizing.addEventListener((s, e) -> log.info("RECOGNIZING: Text=" + e.getResult().getText()));

        recognizer.canceled.addEventListener((s, e) -> {
            log.info("CANCELED: Reason=" + e.getReason());

            if (e.getReason() == CancellationReason.Error) {
                log.info("CANCELED: ErrorCode=" + e.getErrorCode());
                log.info("CANCELED: ErrorDetails=" + e.getErrorDetails());
                result.set(AZURE_ERROR + e.getErrorDetails());
            }

            stopTranslationWithFileSemaphore.release();
        });

        recognizer.sessionStopped.addEventListener((s, e) -> {
            log.info("\n    Session stopped event.");
            stopTranslationWithFileSemaphore.release();
        });

        // Starts continuous recognition. Uses StopContinuousRecognitionAsync() to stop recognition.
        recognizer.startContinuousRecognitionAsync().get();

        // Waits for completion.
        stopTranslationWithFileSemaphore.acquire();

        // Stops recognition.
        recognizer.stopContinuousRecognitionAsync().get();
        audioConfig.close();
        recognizer.close();
        return result.get();
    }
}
