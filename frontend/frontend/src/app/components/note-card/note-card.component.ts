import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Note} from '../../models/note.model';
import moment from 'moment';
import {RecognitionService} from '../../services/recognition.service';


@Component({
  selector: 'app-note-card',
  templateUrl: './note-card.component.html',
  styleUrls: ['./note-card.component.less']
})
export class NoteCardComponent {

  @Input() note: Note;
  @Input() lastNote = false;
  @Output() noteSelected = new EventEmitter<Note>();
  @Output() noteToDelete = new EventEmitter<Note>();

  constructor(private recognitionService: RecognitionService) { }

  selectNote(): void {
    if (!this.recognitionInProgress()) {
      this.noteSelected.emit(this.note);
    }
  }

  noteModificationTime(): string {
    return moment(this.note?.lastModification, moment.ISO_8601).format('DD-MM-YYYY HH:mm:ss');
  }

  deleteNote(): void {
    this.noteToDelete.emit(this.note);
  }

  recognitionInProgress = (): boolean => this.recognitionService.inProgress;
}
