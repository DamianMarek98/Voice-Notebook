import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {TranscriptGroup} from '../models/transcript-group.model';
import {Transcript} from '../models/transcript.model';
import {RealText} from '../models/real-text.model';

@Injectable({providedIn: 'root'})
export class TranscriptService {


  constructor(private http: HttpClient) {
  }

  public createTranscriptGroup(name: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>('/api/transcript/new/' + name, formData);
  }

  public getTranscriptGroups(): Observable<TranscriptGroup[]> {
    return this.http.get<TranscriptGroup[]>('/api/transcript/groups');
  }

  public getGroupTranscripts(name: string): Observable<Transcript[]> {
    return this.http.get<Transcript[]>('/api/transcript/' + name + '/all')
  }

  public getGroupRealText(name: string): Observable<RealText> {
    return this.http.get<RealText>('/api/transcript/' + name + '/real-text')
  }

  public updateRealText(name: string, text: string): Observable<any> {
    return this.http.put('/api/transcript/' + name + '/update-real-text', text);
  }
}
