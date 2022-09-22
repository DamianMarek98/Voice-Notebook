package pg.masters.backend.recognition.azure;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import com.microsoft.cognitiveservices.speech.audio.PushAudioInputStream;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pg.masters.backend.recognition.RecognitionEvent;
import pg.masters.backend.recognition.StreamRecognition;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;


@Log4j2
@Service
class AzureStreamRecognition extends StreamRecognition<SpeechConfig> {

    private AudioConfig audioInput;
    private PushAudioInputStream pushStream;
    private SpeechRecognizer recognizer;

    public AzureStreamRecognition(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public void start(SpeechConfig config) {
        this.started = true;
        this.pushStream = AudioInputStream.createPushStream();
        this.audioInput = AudioConfig.fromStreamInput(pushStream);
        this.recognizer = new SpeechRecognizer(config);
        this.recognizer.recognized.addEventListener((s, e) -> {
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                log.info(e.getResult().getText());
                emitRecognition(e.getResult().getText() + " ");
            }
        });
        try {
            this.recognizer.startContinuousRecognitionAsync().get();
        } catch (InterruptedException ie) {
            log.error("Error on Azure stream recognition start: ", ie);
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            log.error("Error on Azure stream recognition start: ", ee);
        }
    }

    @Override
    protected void emitRecognition(String text) {
        this.applicationEventPublisher.publishEvent(new RecognitionEvent(text));
    }

    @Override
    public void pushFile(String path) {
        try (var inputStream = new FileInputStream(path)) {
            // Arbitrary buffer size.
            var bytes = Files.readAllBytes(Path.of(path));
            pushStream.write(bytes);
        } catch (IOException e) {
            log.warn("Error while reading temp file in azure stream recognition: ", e.getCause());
        }
    }

    @Override
    public void stop() {
        this.started = false;
        var recognitionAsync = recognizer.stopContinuousRecognitionAsync();
        try {
            recognitionAsync.get();
            audioInput.close();
            recognizer.close();
        } catch (InterruptedException ie) {
            log.error("Error on Azure stream recognition close: ", ie);
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            log.error("Error on Azure stream recognition close: ", ee);
        }
    }
}
