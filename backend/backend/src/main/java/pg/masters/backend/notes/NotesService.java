package pg.masters.backend.notes;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.masters.backend.auth.UserService;
import pg.masters.backend.notes.exceptions.NoteNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesService {

    private final NotesRepository notesRepository;
    private final UserService userService;

    public List<Note> getUserNotes(Long userId) {
        return notesRepository.getUserNotes(userId);
    }

    public String getNoteText(Long id) throws NoteNotFoundException {
        return notesRepository.findById(id)
                .map(Note::getText)
                .orElseThrow(() -> new NoteNotFoundException(id));
    }

    public Note findNote(Long id) throws NoteNotFoundException {
        return notesRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
    }

    @Transactional
    public Note createNote(String title, Long userId) {
        var user = userService.findUserWithNotes(userId);
        var note = new Note();
        note.setTitle(title);
        Hibernate.initialize(user.getNotes());
        user.addNote(note);
        note.setCreatedOn(LocalDate.now());
        note.setLastModification(LocalDateTime.now());

        return notesRepository.save(note);
    }

    public Note updateNote(Long id, String title, String text) throws NoteNotFoundException {
        var note = notesRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        note.setTitle(title);
        note.setText(text);
        note.setLastModification(LocalDateTime.now());
        return notesRepository.save(note);
    }

    public Note updateNote(Long id, String text) throws NoteNotFoundException {
        var note = notesRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        note.setText(text);
        return updateNote(note);
    }

    private Note updateNote(Note note) {
        note.setLastModification(LocalDateTime.now());
        return notesRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long id, Long userId) throws NoteNotFoundException {
        var user = userService.findUserWithNotes(userId);
        var note = notesRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        Hibernate.initialize(user.getNotes());
        user.removeNote(note);
        notesRepository.delete(note);
    }


}
