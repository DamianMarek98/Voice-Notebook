package pg.masters.backend.recognition.google.errors;

public class CredentialsException extends RuntimeException {
    public CredentialsException(String credentialsName) {
        super("Unable to obtain " + credentialsName + "credentials");
    }
}

