package pg.masters.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtils {

    public static final String TEMP_AUDIO_PATH = "C:\\Users\\zmddd\\Desktop\\Magisterka\\voice-web-notebook\\backend\\backend\\src\\main\\resources\\temp\\";

    public static String audioTempFilePath() {
        return TEMP_AUDIO_PATH;
    }
}
