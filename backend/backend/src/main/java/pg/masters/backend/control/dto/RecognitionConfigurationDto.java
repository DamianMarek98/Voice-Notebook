package pg.masters.backend.control.dto;

import pg.masters.backend.recognition.enums.RecognitionServiceProvider;
import pg.masters.backend.recognition.enums.ReplyType;

public record RecognitionConfigurationDto(String name, String mimeType, Integer timeSlice, ReplyType replyType) {
    public RecognitionConfigurationDto(RecognitionServiceProvider recognitionServiceProvider) {
        this(recognitionServiceProvider.getName(), recognitionServiceProvider.getAudioMimeType().getMimeTypeOption(),
                500, recognitionServiceProvider.getReplyType());
    }
}
