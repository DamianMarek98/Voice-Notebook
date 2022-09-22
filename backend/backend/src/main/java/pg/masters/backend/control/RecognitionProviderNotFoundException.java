package pg.masters.backend.control;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecognitionProviderNotFoundException extends Exception {
    public RecognitionProviderNotFoundException(String message) {
        super(message);
    }
}
