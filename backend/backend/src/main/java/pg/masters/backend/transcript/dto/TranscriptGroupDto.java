package pg.masters.backend.transcript.dto;

import pg.masters.backend.transcript.Status;

import java.time.LocalDateTime;

public record TranscriptGroupDto(String name, boolean finished, LocalDateTime createdOn, String status, String errorMessage) {
    public TranscriptGroupDto(String name, boolean finished, LocalDateTime createdOn, Status status, String errorMessage) {
        this(name, finished, createdOn, status.getText(), errorMessage);
    }
}
