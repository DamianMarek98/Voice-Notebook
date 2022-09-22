package pg.masters.backend.notes;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.masters.backend.auth.AuthService;
import pg.masters.backend.auth.exceptions.NotAuthenticatedException;
import pg.masters.backend.notes.exceptions.NoteNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NotesController {

    private final NotesService notesService;
    private final AuthService authService;

    @GetMapping("/all")
    public List<NoteDto> getNotes() throws NotAuthenticatedException {
        var user = authService.getCurrentContextUser();
        return notesService.getUserNotes(user.getId()).stream()
                .map(NoteDto::map)
                .toList();
    }

    @GetMapping("/{noteId}")
    public NoteDto getNote(@PathVariable("noteId") Long noteId) throws NoteNotFoundException {
        return NoteDto.map(notesService.findNote(noteId));
    }

    @GetMapping("/text/{noteId}")
    public NoteTextDto getNoteText(@PathVariable("noteId") Long noteId) throws NoteNotFoundException {
        return new NoteTextDto(notesService.getNoteText(noteId));
    }

    @PostMapping("/add/{title}")
    public NoteDto addNote(@PathVariable("title") String title) throws NotAuthenticatedException {
        var user = authService.getCurrentContextUser();
        return NoteDto.map(notesService.createNote(title, user.getId()));
    }

    @GetMapping("/add-initial")
    public NoteDto addInitial() throws NotAuthenticatedException {
        var user = authService.getCurrentContextUser();
        return NoteDto.map(notesService.createNote("Pierwsza notatka - " + user.getUsername(), user.getId()));
    }

    @PatchMapping("/update/{noteId}")
    public NoteDto updateNote(@PathVariable("noteId") Long id, @RequestBody String text) throws NoteNotFoundException {
        return NoteDto.map(notesService.updateNote(id, text));
    }

    @PatchMapping("/update/{noteId}/{title}")
    public NoteDto updateNoteTitle(@PathVariable("noteId") Long id, @PathVariable("title") String title,
                                   @RequestBody String text) throws NoteNotFoundException {
        return NoteDto.map(notesService.updateNote(id, title, text));
    }

    @DeleteMapping("/remove/{noteId}")
    public void deleteNote(@PathVariable("noteId") Long noteId) throws NoteNotFoundException, NotAuthenticatedException {
        var user = authService.getCurrentContextUser();
        notesService.deleteNote(noteId, user.getId());
    }
}
