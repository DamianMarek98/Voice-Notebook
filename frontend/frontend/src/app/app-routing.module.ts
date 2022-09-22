import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './services/auth.guard';
import {TranscriptsComponent} from './components/transcripts/transcripts.component';
import {TranscriptGroupComponent} from './components/transcripts/transcript-group/transcript-group.component';


const routes: Routes = [
  {path: '', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'transcripts', component: TranscriptsComponent, canActivate: [AuthGuard]},
  {path: 'transcripts-group/:name', component: TranscriptGroupComponent, canActivate: [AuthGuard]},
  // otherwise redirect to home
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
