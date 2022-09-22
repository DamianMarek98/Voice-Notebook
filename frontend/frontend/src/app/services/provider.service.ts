import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {RecognitionProvider} from '../models/recognition-provider.model';

@Injectable({providedIn: 'root'})
export class ProviderService {

  private currentProviderSubject: Subject<RecognitionProvider> = new Subject<RecognitionProvider>();

  constructor(private http: HttpClient) {
    this.loadCurrentProvider();
  }

  private readonly API_PROVIDERS_PREFIX = '/api/providers/';

  public changeProvider(name: string): void {
    this.http.post<RecognitionProvider>(this.API_PROVIDERS_PREFIX + 'change/' + name, {})
      .subscribe(provider => this.currentProviderSubject.next(provider));
  }

  public getAvailable(): Observable<string[]> {
    return this.http.get<string[]>(this.API_PROVIDERS_PREFIX + 'available');
  }

  loadCurrentProvider(): void {
    this.http.get<RecognitionProvider>(this.API_PROVIDERS_PREFIX + 'currently-in-use')
      .subscribe(provider => this.currentProviderSubject.next(provider));
  }

  getCurrentProviderObservable(): Observable<RecognitionProvider> {
    return this.currentProviderSubject.asObservable();
  }
}
