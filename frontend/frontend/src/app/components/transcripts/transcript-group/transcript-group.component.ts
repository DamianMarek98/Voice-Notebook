import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {TranscriptService} from '../../../services/transcript.service';
import {Transcript} from '../../../models/transcript.model';
import {ActivatedRoute} from '@angular/router';
import {ToastService} from '../../../services/toast.service';

@Component({
  selector: 'app-transcript-group',
  templateUrl: './transcript-group.component.html',
  styleUrls: ['./transcript-group.component.less']
})
export class TranscriptGroupComponent implements OnInit {

  @ViewChild('note') realTextTextarea: ElementRef;
  googleTranscript: Transcript;
  azureTranscript: Transcript;
  revAiTranscript: Transcript;
  groupName: string;
  realText: string;

  constructor(private route: ActivatedRoute, private transcriptService: TranscriptService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.groupName = params.name;
      this.transcriptService.getGroupTranscripts(this.groupName).subscribe({
        next: (transcripts) => {
          this.googleTranscript = transcripts.find(t => t.recognitionServiceProvider === 'google');
          this.azureTranscript = transcripts.find(t => t.recognitionServiceProvider === 'azure');
          this.revAiTranscript = transcripts.find(t => t.recognitionServiceProvider === 'rev ai');
        }
      });
      this.transcriptService.getGroupRealText(this.groupName).subscribe({
        next: (realText) => this.realText = realText.text
      })
    });
  }

  updateText(): void {
    this.transcriptService.updateRealText(this.groupName, this.realText).subscribe({
      next: () => this.toastService.show('Zapisano rzeczywisty tekst!', {classname: 'bg-success text-dark', delay: 2000}),
      error: () => this.toastService.show('Błąd zapisu!', {classname: 'bg-danger text-dark', delay: 2000})
    })
  }
}
