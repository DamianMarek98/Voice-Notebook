package pg.masters.backend.recognition.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecognitionServiceProvider {
    GOOGLE("google", AudioMimeType.WEBM, ReplyType.FULL_TEXT),
    AZURE("azure", AudioMimeType.WAV, ReplyType.ADDITIONAL_TEXT),
    REV_AI("rev ai", AudioMimeType.WAV, ReplyType.ADDITIONAL_TEXT),
    SPEECH_TEXT_AI("speech text ai", AudioMimeType.WAV, ReplyType.ADDITIONAL_TEXT),
    BROWSER("browser", AudioMimeType.NONE, ReplyType.ADDITIONAL_TEXT);

    private final String name;
    private final AudioMimeType audioMimeType;
    private final ReplyType replyType;
}
