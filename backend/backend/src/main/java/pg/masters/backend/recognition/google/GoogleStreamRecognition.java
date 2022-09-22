package pg.masters.backend.recognition.google;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pg.masters.backend.recognition.RecognitionEvent;
import pg.masters.backend.recognition.StreamRecognition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class GoogleStreamRecognition extends StreamRecognition<StreamingRecognitionConfig> {

    private final ResponseObserver<StreamingRecognizeResponse> responseObserver;
    private ClientStream<StreamingRecognizeRequest> clientStream;
    private StreamController referenceToStreamController;

    public GoogleStreamRecognition(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
        this.responseObserver = new ResponseObserver<>() {

            public void onStart(StreamController controller) {
                referenceToStreamController = controller;
            }

            public void onResponse(StreamingRecognizeResponse response) {
                var result = response.getResultsList().get(0);
                if (result.getAlternativesCount() > 0) {
                    var transcript = result.getAlternativesList().get(0).getTranscript();
                    emitRecognition(transcript);
                }
            }

            public void onComplete() {
                log.error("Google stream recognition completed!");
            }

            public void onError(Throwable t) {
                log.error(t.getMessage());
            }
        };
    }


    @Override
    public void start(StreamingRecognitionConfig config) {
        try (var speechClient = SpeechClient.create()) {
            clientStream = speechClient.streamingRecognizeCallable().splitCall(responseObserver);

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(config)
                            .build(); // The first request in a streaming call has to be a config

            clientStream.send(request);
            started = true;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void pushFile(String webmFilePath) {
        // Subsequent requests must **only** contain the audio data.
        try {
            var bytes = Files.readAllBytes(Path.of(webmFilePath));
            clientStream.send(
                    StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(ByteString.copyFrom(bytes))
                            .build());
        } catch (IOException e) {
            log.warn("Error while reading temp file in azure stream recognition: ", e.getCause());
        }
    }

    @Override
    public void stop() {
        clientStream.closeSend();
        referenceToStreamController.cancel();
        started = false;
    }

    @Override
    protected void emitRecognition(String text) {
        this.applicationEventPublisher.publishEvent(new RecognitionEvent(text));
    }
}
