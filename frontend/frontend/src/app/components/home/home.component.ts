import {Component, OnInit} from '@angular/core';
import {Note} from '../../models/note.model';
import {VoiceRecognitionService} from '../../services/voice-recognition.service';

export let webkitSpeechRecognition: any;
export let finalTranscript: string;
export let recognizing: boolean;

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.less']
})
export class HomeComponent implements OnInit {

  currentNote: Note;
  recognition: any;

  constructor(private voiceRecognitionService: VoiceRecognitionService) {
  }

  ngOnInit(): void {
    this.voiceRecognitionService.init();
    this.voiceRecognitionService.startListeningInBackground();
  }

  noteSelected(note: Note) {
    if (!this.currentNote) {
      this.currentNote = note;
      return;
    }

    if (this.currentNote.id === note.id) {
      return;
    }

    this.currentNote = note;
  }
}
