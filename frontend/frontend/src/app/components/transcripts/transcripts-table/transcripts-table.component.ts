import {Component, OnInit} from '@angular/core';
import {TranscriptService} from '../../../services/transcript.service';
import {TranscriptGroup} from '../../../models/transcript-group.model';
import moment from 'moment/moment';
import {Router} from '@angular/router';
import {IoService} from '../../../services/io.service';

@Component({
  selector: 'app-transcripts-table',
  templateUrl: './transcripts-table.component.html',
  styleUrls: ['./transcripts-table.component.less']
})
export class TranscriptsTableComponent implements OnInit {

  transcriptGroups: TranscriptGroup[];

  constructor(private transcriptService: TranscriptService, private router: Router, private ioService: IoService) {
  }

  ngOnInit(): void {
    this.loadTranscripts();
    this.ioService.getTranscriptionStatusUpdateAsObservable().subscribe({
      next: () => this.loadTranscripts()
    });
  }

  private loadTranscripts() {
    this.transcriptService.getTranscriptGroups().subscribe({
      next: (groups) => this.transcriptGroups = groups
    });
  }

  goToTranscripts(group: TranscriptGroup): void {
    this.router.navigate(['/transcripts-group', group.name]).then();
  }

  getCreationDate = (date: string): string => moment.utc(date).local().format('DD MM YYYY hh:mm:ss');

  getStatusClass(group: TranscriptGroup): string {
    if (group.errorMessage && group.errorMessage !== '') {
      return 'error';
    }
    const status = group.status;

    if (status === 'Nowe') {
      return 'new';
    } else if (status === 'W trakcie') {
      return 'in-progress';
    }

    return 'end';
  }
}
