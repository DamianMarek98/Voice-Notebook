package pg.masters.backend.recognition.google.errors;

public class KeyNotSetException extends RuntimeException {
    public KeyNotSetException(String keyName) {
        super(keyName + " is not set in application");
    }
}
