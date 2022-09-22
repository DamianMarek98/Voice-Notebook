import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranscriptBodyComponent } from './transcript-body.component';

describe('TranscriptBodyComponent', () => {
  let component: TranscriptBodyComponent;
  let fixture: ComponentFixture<TranscriptBodyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TranscriptBodyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TranscriptBodyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
