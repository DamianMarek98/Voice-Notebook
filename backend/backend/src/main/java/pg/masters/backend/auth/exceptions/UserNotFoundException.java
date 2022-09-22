package pg.masters.backend.auth.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String username) {
        super("User with username " + username + " was not found");
    }
}
