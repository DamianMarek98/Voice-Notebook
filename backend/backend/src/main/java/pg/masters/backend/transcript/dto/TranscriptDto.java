package pg.masters.backend.transcript.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;
import pg.masters.backend.transcript.serializers.RecognitionServiceProviderSerializer;

import java.time.LocalDateTime;

public record TranscriptDto(Long id,
                            RecognitionServiceProvider recognitionServiceProvider,
                            String resultText,
                            boolean successful,
                            String errorMessage,
                            LocalDateTime createdOn) {

    @Override
    @JsonSerialize(using = RecognitionServiceProviderSerializer.class)
    public RecognitionServiceProvider recognitionServiceProvider() {
        return recognitionServiceProvider;
    }
}
