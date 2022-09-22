package pg.masters.backend.recognition.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AudioMimeType {
    WEBM("audio/webm"),
    WAV("audio/wav"),
    NONE("none");

    @Getter
    private final String mimeTypeOption;
}
