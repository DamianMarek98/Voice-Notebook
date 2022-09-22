import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {first, Observable, Subject} from 'rxjs';
import {Note, NoteText} from '../models/note.model';

@Injectable({providedIn: 'root'})
export class NotesService {

  private noteSaveSubject = new Subject<number>();
  private noteDeleteSubject = new Subject<Note>();

  constructor(private http: HttpClient) {
  }

  public getAllNotes(): Observable<Note[]> {
    return this.http.get<Note[]>('/api/notes/all');
  }

  public getNote(id: number): Observable<Note> {
    return this.http.get<Note>('/api/notes/' + id);
  }

  public addNote(title: string): Observable<Note> {
    return this.http.post<Note>('/api/notes/add/' + title, {});
  }

  public addInitialNote(): Observable<Note> {
    return this.http.get<Note>('/api/notes/add-initial');
  }

  public updateNote(id: number, text: string): Observable<Note> {
    return this.http.patch<Note>('/api/notes/update/' + id, text).pipe(first(() => {
      this.noteSaveSubject.next(id);
      return true;
    }));
  }

  public updateNoteAndTitle(id: number, title: string, text: string): Observable<Note> {
    return this.http.patch<Note>('/api/notes/update/' + id + '/' + title, text).pipe(first(() => {
      this.noteSaveSubject.next(id);
      return true;
    }));
  }

  public getNoteText(id: number): Observable<NoteText> {
    return this.http.get<NoteText>('/api/notes/text/' + id);
  }

  public deleteNote(id: number): Observable<any> {
    return this.http.delete('/api/notes/remove/' + id);
  }

  public noteSaveAsObservable(): Observable<number> {
    return this.noteSaveSubject.asObservable();
  }

  public noteDeleteAsObservable(): Observable<Note> {
    return this.noteDeleteSubject.asObservable();
  }

  public eventDeleteNote(note: Note): void {
    this.noteDeleteSubject.next(note);
  }
}
