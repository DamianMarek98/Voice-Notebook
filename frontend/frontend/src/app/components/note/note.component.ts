import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {TextService} from '../../services/text.service';
import {ProviderService} from '../../services/provider.service';
import {Note} from '../../models/note.model';
import {NotesService} from '../../services/notes.service';
import {RecognitionService} from '../../services/recognition.service';
import {timer} from 'rxjs';
import {ToastService} from '../../services/toast.service';
import {VoiceRecognitionService} from '../../services/voice-recognition.service';
import {CommandService} from '../../services/command.service';

@Component({
  selector: 'app-note',
  templateUrl: './note.component.html',
  styleUrls: ['./note.component.less']
})
export class NoteComponent implements OnInit {
  @ViewChild('note') note: ElementRef;
  text = '';
  replyType: string;
  firstIncomingText = true;
  savedText = '';
  title = '';
  private lastUpdateText = '';
  private _selectedNote: Note;

  @Input() set selectedNote(value: Note) {
    this._selectedNote = value;
    this.title = value.title;
    this.loadNoteText();
  }

  get selectedNote(): Note {
    return this._selectedNote;
  }

  constructor(private textService: TextService, private providersService: ProviderService, private notesService: NotesService,
              private recognitionService: RecognitionService, private toastService: ToastService,
              private voiceRecognitionService: VoiceRecognitionService, private commandService: CommandService) {
  }

  ngOnInit(): void {
    this.providersService.getCurrentProviderObservable()
      .subscribe(provider => this.replyType = provider.replyType);

    this.textService.observe().subscribe((text) => {
      this.handleIncomingText(text);
      this.firstIncomingText = false;
    });

    this.recognitionService.subscribeToProgressChange().subscribe((inProgress) => {
      this.handleStartDisabledChange(inProgress);
    });

    this.voiceRecognitionService.noteTranscriptionText().subscribe({
      next: (text) => this.handleIncomingText(text)
    });

    timer(10000, 10000).subscribe(() => {
      if (this.lastUpdateText === this.text) {
        return;
      }

      this.lastUpdateText = this.text;
      this.notesService.updateNoteAndTitle(this._selectedNote.id, this.title, this.text).subscribe({
        next: () => this.toastService.show('Zsynchronizowano zmiany!', {classname: 'bg-success text-dark', delay: 2000})
      });
    });

    this.commandService.saveNoteEvent().subscribe({
      next: () => this.saveNote()
    })
    this.commandService.deleteNoteEvent().subscribe({
      next: () => this.notesService.eventDeleteNote(this._selectedNote)
    })
  }

  private handleStartDisabledChange(inProgress: boolean) {
    if (inProgress) { // recognition started
      this.firstIncomingText = true;
      if (this.text !== '') {
        this.savedText = this.text + ' ';
      }
    } else { // recognition finished
      this.savedText = '';
    }
  }

  private loadNoteText(): void {
    if (this._selectedNote) {
      this.notesService.getNoteText(this._selectedNote.id).subscribe({
        next: (noteText) => {
          this.text = noteText.text;
          this.lastUpdateText = noteText.text;
        },
        error: (err) => console.log(err)
      });
    }
  }

  private handleIncomingText(text: string): void {
    if (this.replyType === 'ADDITIONAL_TEXT') {
      this.text += text;
    } else if (this.replyType === 'FULL_TEXT') {
      if (text !== '') {
        this.text = this.savedText + text;
      }
    }

    this.notesService.updateNote(this._selectedNote.id, this.text);
  }

  public saveNote(): void {
    this.lastUpdateText = this.text;
    this.notesService.updateNoteAndTitle(this._selectedNote.id, this.title, this.text).subscribe({
      next: () => this.toastService.show('Zapisano notatkę!', {classname: 'bg-success text-dark', delay: 2000})
    });
  }

  recognitionInProgress = (): boolean => this.recognitionService.inProgress;
  saveValid = (): boolean => this.recognitionInProgress() || this.title.length < 5;
}
