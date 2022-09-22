import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {NotesService} from '../../services/notes.service';
import {ToastService} from '../../services/toast.service';
import {Note} from '../../models/note.model';
import {RecognitionService} from '../../services/recognition.service';
import {CommandService} from '../../services/command.service';

@Component({
  selector: 'app-notes-list',
  templateUrl: './notes-list.component.html',
  styleUrls: ['./notes-list.component.less']
})
export class NotesListComponent implements OnInit {

  @Output() noteSelected = new EventEmitter<Note>();
  notes: Note[] = [];
  title = '';
  toShortTitle = false;

  constructor(private notesService: NotesService, private toastService: ToastService, private recognitionService: RecognitionService,
              private commandService: CommandService) {
  }

  ngOnInit(): void {
    this.loadNotes();
    this.notesService.noteSaveAsObservable().subscribe({
      next: (id) => {
        this.loadUpdatedNote(id);
      }
    })

    this.commandService.newNoteEvent().subscribe({
      next: () => {
        this.title = 'Nowa notatka';
        this.createNote();
      }
    })

    this.notesService.noteDeleteAsObservable().subscribe({
      next: (note) => {
        if (this.notes.length > 1) {
          this.deleteNote(note);
        }
      }
    })
  }

  private loadUpdatedNote(id: number) {
    const note = this.notes.find(n => n.id === id);
    if (note) {
      const indexOf = this.notes.indexOf(note);
      this.notesService.getNote(note.id).subscribe({
        next: (updatedNote) => this.notes[indexOf] = updatedNote
      });
    }
  }

  private loadNotes() {
    this.notesService.getAllNotes().subscribe({
        next: (notes) => {
          this.notes = notes;
          if (notes.length > 0) {
            this.newNoteSelected(notes[0]);
          } else {
            this.generateInitialNote();
          }
        },
        error: () => this.toastService.show('Błąd podczas pobierania notatek',
          {classname: 'bg-danger text-dark', delay: 2000})
      }
    );
  }

  private generateInitialNote() {
    this.notesService.addInitialNote().subscribe({
      next: (initialNote) => {
        this.notes.push(initialNote);
        this.newNoteSelected(initialNote);
      }
    });
  }

  newNoteSelected(note: Note): void {
    this.noteSelected.emit(note);
  }

  deleteNote(note: Note): void {
    this.notesService.deleteNote(note.id).subscribe({
      next: () => {
        const index = this.notes.indexOf(note);
        if (index > -1) {
          this.notes.splice(index, 1);
          this.newNoteSelected(this.notes[0]);
        } else {
          console.log('Note not found!');
        }
      },
      error: (err) => console.log(err)
    });
  }

  createNote(): void {
    this.toShortTitle = (this.title.length < 5);
    if (this.toShortTitle) {
      return;
    }

    this.notesService.addNote(this.title).subscribe({
      next: (note) => {
        this.noteSelected.emit(note);
        this.notes.unshift(note);
        this.title = '';
      },
      error: (err) => console.log(err)
    });
  }

  recognitionInProgress = (): boolean => this.recognitionService.inProgress;
  lastNote = (): boolean => this.notes.length === 1;
}
