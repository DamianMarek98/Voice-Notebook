package pg.masters.backend.recognition.revai;


import ai.rev.speechtotext.RevAiWebSocketListener;
import ai.rev.speechtotext.StreamingClient;
import ai.rev.speechtotext.models.streaming.ConnectedMessage;
import ai.rev.speechtotext.models.streaming.Hypothesis;
import lombok.extern.log4j.Log4j2;

import okhttp3.Response;
import okio.ByteString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pg.masters.backend.recognition.RecognitionEvent;
import pg.masters.backend.recognition.StreamRecognition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@Service
public class RevaiStreamRecognition extends StreamRecognition<RevaiConfig> {

    private StreamingClient streamingClient;

    public RevaiStreamRecognition(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public void start(RevaiConfig config) {
        this.started = true;
        streamingClient = new StreamingClient(config.token());
        WebSocketListener webSocketListener = new WebSocketListener();
        streamingClient.connect(webSocketListener, config.streamContentType(), config.sessionConfig());
    }

    @Override
    public void pushFile(String filePath) {
        try {
            var bytes = Files.readAllBytes(Path.of(filePath));
            streamingClient.sendAudioData(ByteString.of(bytes));
        } catch (IOException e) {
            log.warn("Error while reading temp file in Rev Ai stream recognition: ", e.getCause());
        }
    }

    @Override
    public void stop() {
        streamingClient.close();
        this.started = false;
    }

    @Override
    protected void emitRecognition(String text) {
        this.applicationEventPublisher.publishEvent(new RecognitionEvent(text));
    }

    // Your WebSocket listener for all streaming responses
    private static class WebSocketListener implements RevAiWebSocketListener {

        @Override
        public void onConnected(ConnectedMessage message) {
            log.info(message);
        }

        @Override
        public void onHypothesis(Hypothesis hypothesis) {
            log.info(hypothesis.toString());
        }

        @Override
        public void onError(Throwable t, Response response) {
            log.info(response);
        }

        @Override
        public void onClose(int code, String reason) {
            log.info(reason);
        }

        @Override
        public void onOpen(Response response) {
            log.info(response.toString());
        }
    }
}
