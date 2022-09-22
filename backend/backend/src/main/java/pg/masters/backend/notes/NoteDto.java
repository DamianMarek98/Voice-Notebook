package pg.masters.backend.notes;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NoteDto(Long id, String title, String contentBeginning, LocalDate createdOn, LocalDateTime lastModification) {

    public static NoteDto map(Note note) {
        var text = note.getText();
        if (text.length() > 40) {
            text = text.substring(0, 40) + "...";
        }

        return new NoteDto(note.getId(), note.getTitle(), text, note.getCreatedOn(), note.getLastModification());
    }
}
