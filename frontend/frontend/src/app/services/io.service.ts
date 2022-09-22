import {Injectable} from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {TextService} from './text.service';
import {CommandService} from './command.service';
import {Command} from '../models/command.model';
import {AuthService} from './auth.service';
import {Observable, Subject} from 'rxjs';
import {ToastService} from './toast.service';

const io = require('socket.io-client');

@Injectable({providedIn: 'root'})
export class IoService {
  public socket: any;
  public lang: string;
  private client: any;
  private reader = new FileReader();
  private transcriptionStatusUpdate = new Subject<any>();
  webSocketEndPoint = 'http://localhost:8080/voice-websocket';

  constructor(private textService: TextService, private commandService: CommandService, private authService: AuthService,
              private toastService: ToastService) {
    this.socket = io('connect', () => {
      console.log('connected');
    });
    this.socket.binaryType = 'arraybuffer';
    this.lang = 'pl-PL';

    this.connectWithServer();
    this.authService.getLoginSubjectAsObservable().subscribe({
      next: () => this.connectWithServer()
    })
  }

  private connectWithServer(): void {
    const socket = new SockJS('http://localhost:8080/voice-websocket');
    this.client = Stomp.over(socket);

    this.client.connect({}, (frame) => {
      this.client.subscribe('/user/topic/text', (message) => {
        if (message.body) {
          this.textService.notify(message.body);
        }
      });

      this.client.subscribe('/user/topic/command', (message) => {
        if (message.body) {
          const command: Command = JSON.parse(message.body);
          console.log(command);
          this.commandService.handleCommand(command);
        }
      });

      const username = this.authService.getUserLogin();
      this.client.subscribe('/topic/transcript-status/' + username, (message) => {
        this.transcriptionStatusUpdate.next(undefined);
        this.toastService.show(message.body, {classname: 'bg-success text-dark', delay: 3000})
      });
    });
  }

  getTranscriptionStatusUpdateAsObservable(): Observable<any> {
    return this.transcriptionStatusUpdate.asObservable();
  }

  sendPossibleCommand(command: string): void {
    this.client.send('/app/possible-command', {}, command);
  }

  sendBinaryStream(blob: any) {
    this.reader.readAsDataURL(blob);
    this.reader.onloadend = () => {
      this.client.send('/app/speech', {}, this.reader.result);
    };
  }
}
