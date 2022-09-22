package pg.masters.backend.notes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoteNotFoundException extends Exception {
    public NoteNotFoundException(Long id) {
        super("Note with id: " + id + " not found!");
    }
}
