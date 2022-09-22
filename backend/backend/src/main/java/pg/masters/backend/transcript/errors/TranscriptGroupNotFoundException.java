package pg.masters.backend.transcript.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TranscriptGroupNotFoundException extends Exception {
    public TranscriptGroupNotFoundException(String name) {
        super("Transcript group with name: " + name + " not found!");
    }
}
