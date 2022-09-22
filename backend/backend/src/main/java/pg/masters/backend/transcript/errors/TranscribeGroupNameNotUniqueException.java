package pg.masters.backend.transcript.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class TranscribeGroupNameNotUniqueException extends Exception {
    public TranscribeGroupNameNotUniqueException(String name) {
        super("Transcribe group with name: " + name + " already exists!");
    }
}
