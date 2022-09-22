package pg.masters.backend.recognition.google;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.TimedRetryAlgorithm;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.threeten.bp.Duration;
import pg.masters.backend.recognition.RecognitionService;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
public class GoogleService implements RecognitionService {

    private final StreamingRecognitionConfig config;
    private final GoogleStreamRecognition googleStreamRecognition;
    private String latestRecognition = "";

    public GoogleService(GoogleStreamRecognition googleStreamRecognition) {
        this.googleStreamRecognition = googleStreamRecognition;
        var recConfig = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)
                .setLanguageCode("pl-PL")
                .setSampleRateHertz(48000)
                .setAudioChannelCount(1)
                .setEnableAutomaticPunctuation(true)
                .build();

        this.config = StreamingRecognitionConfig.newBuilder()
                .setConfig(recConfig)
                .setInterimResults(true)
                .build();
    }


    @Override
    public void recognizeInStream(String wavFilePath) {
        if (!googleStreamRecognition.started()) {
            this.googleStreamRecognition.start(config);
        }

        this.googleStreamRecognition.pushFile(wavFilePath);
    }

    @Override
    public Optional<String> getReply() {
        return wrapReply(latestRecognition);
    }

    @Override
    public void handleNewRecognition(String text) {
        latestRecognition = text;
    }

    @Override
    public void stopStreamRecognition() {
        this.googleStreamRecognition.stop();
        latestRecognition = "";
    }

    @Override
    public RecognitionServiceProvider getRecognitionServiceProvider() {
        return RecognitionServiceProvider.GOOGLE;
    }

    public String transcriptionFromFile(byte[] waveFile) throws IOException, InterruptedException, ExecutionException {
        StringBuilder transcriptionResult = new StringBuilder();
        SpeechSettings.Builder speechSettings = SpeechSettings.newBuilder();
        TimedRetryAlgorithm timedRetryAlgorithm =
                OperationTimedPollAlgorithm.create(
                        RetrySettings.newBuilder()
                                .setInitialRetryDelay(Duration.ofMillis(500L))
                                .setRetryDelayMultiplier(1.5)
                                .setMaxRetryDelay(Duration.ofMillis(5000L))
                                .setInitialRpcTimeout(Duration.ZERO) // ignored
                                .setRpcTimeoutMultiplier(1.0) // ignored
                                .setMaxRpcTimeout(Duration.ZERO) // ignored
                                .setTotalTimeout(Duration.ofHours(24L)) // set polling timeout to 24 hours
                                .build());
        speechSettings.longRunningRecognizeOperationSettings().setPollingAlgorithm(timedRetryAlgorithm);

        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        try (SpeechClient speech = SpeechClient.create(speechSettings.build())) {

            // Configure remote file request for FLAC
            RecognitionConfig localConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode("pl-PL")
                            .setSampleRateHertz(16000)
                            .build();

            var audioBytes = ByteString.copyFrom(waveFile);
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Use non-blocking call for getting file transcription
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speech.longRunningRecognizeAsync(localConfig, audio);
            while (!response.isDone()) {
                Thread.sleep(5000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();


            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcriptionResult.append(alternative.getTranscript()).append(" ");
            }
        }

        return transcriptionResult.toString();
    }
}
