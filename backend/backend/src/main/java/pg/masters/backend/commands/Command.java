package pg.masters.backend.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Command {
    SAVE("save.note"),
    NEW("new.note"),
    CHANGE_PROVIDER("change.provider"),
    LOGOUT("logout"),
    DELETE("delete"),
    DEFAULT_FALLBACK("Default Fallback Intent"),
    ERROR("error");


    private final String intent;
}
