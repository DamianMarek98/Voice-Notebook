import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranscriptGroupComponent } from './transcript-group.component';

describe('TranscriptGroupComponent', () => {
  let component: TranscriptGroupComponent;
  let fixture: ComponentFixture<TranscriptGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TranscriptGroupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TranscriptGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
