package pg.masters.backend.recognition;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RecognitionProviderMappingNotImplemented extends Error {
    public RecognitionProviderMappingNotImplemented() {
        super("Cannot map this recognition provider");
    }
}
