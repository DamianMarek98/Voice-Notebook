import {EventEmitter, Injectable} from '@angular/core';
import {Observable} from 'rxjs';


@Injectable({providedIn: 'root'})
export class TextService {

  private textNotification = new EventEmitter<string>();

  constructor() {
  }

  observe(): Observable<string> {
    return this.textNotification.asObservable();
  }

  notify(text: string): void {
    this.textNotification.emit(text);
  }
}
