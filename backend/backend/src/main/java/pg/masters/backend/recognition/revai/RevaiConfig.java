package pg.masters.backend.recognition.revai;

import ai.rev.speechtotext.models.streaming.SessionConfig;
import ai.rev.speechtotext.models.streaming.StreamContentType;

public record RevaiConfig(StreamContentType streamContentType, SessionConfig sessionConfig, String token) {
}
