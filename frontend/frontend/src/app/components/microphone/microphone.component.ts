import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {IoService} from '../../services/io.service';
import {NoteComponent} from '../note/note.component';
import {MediaRecorder, register} from 'extendable-media-recorder';
import {connect} from 'extendable-media-recorder-wav-encoder';
import {RecognitionService} from '../../services/recognition.service';
import {ToastService} from '../../services/toast.service';
import {ProviderService} from '../../services/provider.service';
import {RecognitionProvider} from '../../models/recognition-provider.model';
import {VoiceRecognitionService} from '../../services/voice-recognition.service';


@Component({
  selector: 'app-microphone',
  templateUrl: './microphone.component.html',
  styleUrls: ['./microphone.component.less']
})
export class MicrophoneComponent implements OnInit {
  @Input() note: NoteComponent;
  @ViewChild('startButton') startButton: ElementRef;
  private mediaRecorder: any;
  private stream: MediaStream;
  private provider: RecognitionProvider;
  public recordingFlag = false;

  constructor(private ioService: IoService, private recognitionService: RecognitionService, private toastService: ToastService,
              private providersService: ProviderService, private voiceRecognitionService: VoiceRecognitionService) {
  }

  async ngOnInit(): Promise<void> {
    // tslint:disable-next-line:max-line-length
    // https://stackoverflow.com/questions/65191193/media-recorder-save-in-wav-format-across-browsers?newreg=6ea9a5dea1974b5da4c53be340214c50
    await register(await connect());
    this.stream = await navigator.mediaDevices.getUserMedia({audio: true});
    this.providersService.loadCurrentProvider();
    this.providersService.getCurrentProviderObservable().subscribe(provider => this.provider = provider);
  }

  start(): void {
    this.recognitionService.startRecognition();
    this.recordingFlag = true;
    if (this.provider.timeSlice === 0) { // avoid record while using in web recognition
      this.voiceRecognitionService.startListeningNote();
      return;
    }
    this.setUpRecorder();
    this.record();
  }

  setUpRecorder(): void {
    this.mediaRecorder = new MediaRecorder(this.stream, {mimeType: this.provider.mimeType});
    this.mediaRecorder.ondataavailable = (event) => this.handleDataAvailable(event);
  }

  handleDataAvailable(event: any): void {
    if (event.data.size > 0) {
      this.ioService.sendBinaryStream(event.data);
    }
  }

  private record() {
    this.mediaRecorder.start();
    setTimeout(() => {
      if (this.recordingFlag === true) {
        this.mediaRecorder.stop();
        this.record();
      }
    }, this.provider.timeSlice);
  }

  stop(): void {
    this.recordingFlag = false;
    if (this.provider.timeSlice === 0) { // avoid record while using in web recognition
      this.voiceRecognitionService.abortListening();
    }
    this.recognitionService.stopRecognition().subscribe((text) => {
      this.showToast(text);
    });
  }

  showToast(text: string) {
    this.toastService.show(text, { classname: 'bg-warning text-dark', delay: 2000 });
  }

  recognitionInProgress = (): boolean => this.recognitionService.inProgress;
}
