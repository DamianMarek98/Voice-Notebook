package pg.masters.backend.commands;

public class CommandNotFoundException extends Exception {
    public CommandNotFoundException(String intent) {
        super("Intent not found: " + intent);
    }
}
