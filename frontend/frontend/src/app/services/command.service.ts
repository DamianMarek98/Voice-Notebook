import {Injectable} from '@angular/core';
import {AuthService} from './auth.service';
import {ToastService} from './toast.service';
import {Observable, Subject} from 'rxjs';
import {Command} from '../models/command.model';

@Injectable({
  providedIn: 'root'
})
export class CommandService {

  private static readonly SAVE = 'SAVE';
  private static readonly NEW = 'NEW';
  private static readonly CHANGE_PROVIDER = 'CHANGE_PROVIDER';
  private static readonly LOGOUT = 'LOGOUT';
  private static readonly DELETE = 'DELETE';
  private static readonly DEFAULT_FALLBACK = 'DEFAULT_FALLBACK';
  private static readonly ERROR = 'ERROR';

  private saveSubject = new Subject();
  private newNoteSubject = new Subject();
  private changeProviderSubject = new Subject<string>();
  private deleteNoteSubject = new Subject();

  constructor(private toastService: ToastService, private authService: AuthService) {
  }

  handleCommand(command: Command) {
    console.log(command);
    const commandName = command.name;
    if (commandName === CommandService.SAVE) {
      this.notify('Rozpoznano komende zapisz');
      this.saveSubject.next(null);
    } else if (commandName === CommandService.NEW) {
      this.notify('Rozpoznano komende nowa notatka');
      this.newNoteSubject.next(null);
    } else if (commandName === CommandService.CHANGE_PROVIDER) {
      this.notify('Rozpoznano komende zmiana stt');
      this.changeProviderSubject.next(command.provider);
    } else if (commandName === CommandService.LOGOUT) {
      this.notify('Rozpoznano komende wyloguj, za chwilę nastąpi wylogowanie', 3000);
      new Promise(res => setTimeout(res, 3000))
        .then(() => this.authService.logout());
    } else if (commandName === CommandService.DELETE) {
      this.toastService.show('Rozpoznano komende usuń notatkę (jeśli posiadasz jedną notatkę, komenda zostanie anulowana)',
        {classname: 'bg-warning text-dark', delay: 2000});
      this.deleteNoteSubject.next(null);
    } else if (commandName === CommandService.DEFAULT_FALLBACK) {
      this.toastService.show('Nie rozpoznano komendy', {classname: 'bg-warning text-dark', delay: 2000});
    } else if (commandName === CommandService.ERROR) {
      this.toastService.show('Błąd podczas rozpoznawania komendy', {classname: 'bg-danger text-dark', delay: 2000});
    }
  }

  private notify(text: string, delay: number = 2000) {
    this.toastService.show(text, {classname: 'bg-info text-dark', delay});
  }

  public newNoteEvent(): Observable<any> {
    return this.newNoteSubject.asObservable();
  }

  public changeProviderEvent(): Observable<string> {
    return this.changeProviderSubject.asObservable();
  }

  public saveNoteEvent(): Observable<any> {
    return this.saveSubject.asObservable();
  }

  public deleteNoteEvent(): Observable<any> {
    return this.deleteNoteSubject.asObservable();
  }
}
