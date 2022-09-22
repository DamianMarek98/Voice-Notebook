package pg.masters.backend.recognition.browser;

import org.springframework.stereotype.Service;
import pg.masters.backend.recognition.RecognitionService;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;

import java.util.Optional;

@Service
public class BrowserService implements RecognitionService {
    @Override
    public void recognizeInStream(String filePath) {
        // empty because browser handles recognition
    }

    @Override
    public Optional<String> getReply() {
        return Optional.empty();
    }

    @Override
    public void handleNewRecognition(String text) {
        // empty because browser handles recognition
    }

    @Override
    public void stopStreamRecognition() {
        // empty because browser handles recognition
    }

    @Override
    public RecognitionServiceProvider getRecognitionServiceProvider() {
        return RecognitionServiceProvider.BROWSER;
    }
}
