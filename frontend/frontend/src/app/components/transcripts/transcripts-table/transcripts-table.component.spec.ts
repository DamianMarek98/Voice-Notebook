import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranscriptsTableComponent } from './transcripts-table.component';

describe('TranscriptsTableComponent', () => {
  let component: TranscriptsTableComponent;
  let fixture: ComponentFixture<TranscriptsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TranscriptsTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TranscriptsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
