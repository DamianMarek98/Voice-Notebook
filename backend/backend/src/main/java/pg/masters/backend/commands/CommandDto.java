package pg.masters.backend.commands;

public record CommandDto(String name, String provider) {
    public CommandDto(String name) {
        this(name, "");
    }
}
