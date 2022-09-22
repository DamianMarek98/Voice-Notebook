import {Component, Input, OnInit} from '@angular/core';
import {ProviderService} from '../../services/provider.service';
import {RecognitionService} from '../../services/recognition.service';
import {CommandService} from '../../services/command.service';

@Component({
  selector: 'app-provider-selection',
  templateUrl: './provider-selection.component.html',
  styleUrls: ['./provider-selection.component.less']
})
export class ProviderSelectionComponent implements OnInit {
  providers: string[] = [];
  provider = '';

  constructor(private providersService: ProviderService, private recognitionService: RecognitionService,
              private commandService: CommandService) {
  }

  ngOnInit(): void {
    this.providersService.getAvailable().subscribe(providers => {
      this.providers = providers;
    });
    this.providersService.getCurrentProviderObservable()
      .subscribe(provider => {
        this.provider = provider.name;
      });
    this.commandService.changeProviderEvent().subscribe({
      next: (provider) => this.providersService.changeProvider(provider)
    });
  }

  changeProvider(): void {
    this.providersService.changeProvider(this.provider);
  }

  recognitionInProgress = (): boolean => this.recognitionService.inProgress;
}
