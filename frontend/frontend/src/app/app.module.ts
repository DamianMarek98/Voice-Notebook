import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './components/home/home.component';
import {MicrophoneComponent} from './components/microphone/microphone.component';
import {NoteComponent} from './components/note/note.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ToastComponent} from './components/toast/toast-container.component';
import {NgbToastModule} from '@ng-bootstrap/ng-bootstrap';
import {ProviderSelectionComponent} from './components/provider-selection/provider-selection.component';
import {LoginComponent} from './components/login/login.component';
import {AuthInterceptor} from './interceptors/auth.interceptor';
import {NoteCardComponent} from './components/note-card/note-card.component';
import {NotesListComponent} from './components/notes-list/notes-list.component';
import {TranscriptsComponent} from './components/transcripts/transcripts.component';
import {TranscriptsTableComponent} from './components/transcripts/transcripts-table/transcripts-table.component';
import {TranscriptGroupComponent} from './components/transcripts/transcript-group/transcript-group.component';
import {TranscriptBodyComponent} from './components/transcripts/transcript-group/transcript-body/transcript-body.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    MicrophoneComponent,
    NoteComponent,
    ToastComponent,
    ProviderSelectionComponent,
    LoginComponent,
    NoteCardComponent,
    NotesListComponent,
    TranscriptsComponent,
    TranscriptsTableComponent,
    TranscriptGroupComponent,
    TranscriptBodyComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NgbToastModule,
    ReactiveFormsModule,
    FontAwesomeModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
