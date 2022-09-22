import {Component, Input, OnInit} from '@angular/core';
import {Transcript} from '../../../../models/transcript.model';

@Component({
  selector: 'app-transcript-body',
  templateUrl: './transcript-body.component.html',
  styleUrls: ['./transcript-body.component.less']
})
export class TranscriptBodyComponent {

  @Input() transcript: Transcript;

  constructor() { }

}
