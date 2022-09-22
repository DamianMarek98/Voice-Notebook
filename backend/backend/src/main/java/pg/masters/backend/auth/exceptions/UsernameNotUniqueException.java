package pg.masters.backend.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UsernameNotUniqueException extends Exception {
    public UsernameNotUniqueException(String username) {
        super("User with given username already exists: " + username);
    }
}
