package pg.masters.backend.transcript;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    NEW("Nowe"),
    IN_PROGRESS("W trakcie"),
    FINISHED("Zakończono");

    private final String text;
}
