package pg.masters.backend.recognition;

import org.springframework.context.ApplicationEvent;

public class RecognitionEvent extends ApplicationEvent {
    public RecognitionEvent(String recognizedText) {
        super(recognizedText);
    }

    public String getText() {
        return (String) super.getSource();
    }
}
