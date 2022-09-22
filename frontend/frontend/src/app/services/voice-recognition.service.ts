import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {IoService} from './io.service';

// TypeScript's declaration for annyang
declare var annyang: any;


/**
 * via https://github.com/TalAter/annyang/blob/master/docs/README.md
 */
@Injectable({
  providedIn: 'root',
})
export class VoiceRecognitionService {

  constructor(private ioService: IoService) {
  }
  listening = false;
  backgroundTranscription = true;
  private noteTranscriptionSubject = new Subject<string>();

  private static containsCommandWord(userSaid: string[]): boolean {
    for (const possibleCommand of userSaid) {
      console.log(possibleCommand);
      if (possibleCommand.includes('komenda') || possibleCommand.includes('komende') || possibleCommand.includes('komendę')) {
        return true;
      }
    }

    return false;
  }

  init() {
    annyang.setLanguage('pl');
    annyang.addCallback('result', (userSaid) => {
      console.log(userSaid);
      if (this.backgroundTranscription) {
        if (VoiceRecognitionService.containsCommandWord(userSaid)) {
          this.ioService.sendPossibleCommand(userSaid[0]);
        }
      } else {
        this.noteTranscriptionSubject.next(userSaid[0] + ' ');
      }
    });
  }

  noteTranscriptionText(): Observable<string> {
    return this.noteTranscriptionSubject.asObservable();
  }

  startListeningNote() {
    console.log('start note listening');
    this.backgroundTranscription = false;
    annyang.start({autoRestart: true, continuous: false});
    this.listening = true;
  }

  startListeningInBackground() {
    this.backgroundTranscription = true;
    annyang.start({autoRestart: true, continuous: false});
    this.listening = true;
  }

  abortListening() {
    console.log('abort listening');
    annyang.abort();
    this.listening = false;
    this.startListeningInBackground();
  }
}
