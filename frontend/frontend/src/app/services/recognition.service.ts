import {Injectable} from '@angular/core';
import {first, Observable, of, Subject} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable({providedIn: 'root'})
export class RecognitionService {

  recognitionInProgressSubject = new Subject<boolean>();
  inProgress = false;

  headers = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) {
  }

  private setInProgress(progress: boolean): void {
    this.inProgress = progress;
    this.recognitionInProgressSubject.next(this.inProgress);
  }

  public startRecognition(): void {
    this.setInProgress(true);
  }

  public stopRecognition(): Observable<any> {
    return this.http.post('/api/recognition/stop', {}, {responseType: 'text'}).pipe(first(() => {
      this.setInProgress(false);
      return true;
    }));
  }

  public subscribeToProgressChange(): Observable<boolean> {
    return this.recognitionInProgressSubject;
  }
}
