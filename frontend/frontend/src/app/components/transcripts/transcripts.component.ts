import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TranscriptService} from '../../services/transcript.service';
import {ToastService} from '../../services/toast.service';
import { faCircleInfo } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-transcripts',
  templateUrl: './transcripts.component.html',
  styleUrls: ['./transcripts.component.less']
})
export class TranscriptsComponent implements OnInit {

  form!: FormGroup;
  loading = false;
  error = false;
  errorMessage!: string;
  faInfo = faCircleInfo;

  constructor(private formBuilder: FormBuilder, private transcriptService: TranscriptService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      file: [File, Validators.required],
      name: ['', Validators.required]
    });
  }

  onFileChange(event) {
    if (event.target.files && event.target.files.length > 0) {
      const file = (event.target.files[0] as File);
      this.form.get('file').setValue(file);
    }
  }

  // getter to easy access form fields
  get formControls() {
    return this.form.controls;
  }

  onSubmit(): void {
    this.error = false;
    if (this.form.invalid) {
      this.error = true;
      this.errorMessage = 'Uzupełnij dane!';
      return;
    }
    this.loading = true;
    this.transcriptService.createTranscriptGroup(this.formControls.name.value, this.formControls.file.value)
      .subscribe({
        next: () => {
          this.form.reset();
          this.loading = false;
          this.toastService.show('Utworzono grupę, transkrypcja w trakcie!', {classname: 'bg-success text-dark', delay: 2000})
        },
        error: (error) => {
          console.log(error.status)
          this.loading = false;
          this.error = true;
          this.errorMessage = error.status === 409 ? 'Transkrypcja o podanej nazwie istnieje' : 'Podano błędne dane, spróbuj ponownie!';
          console.log(error.statusText);
        }
      });
  }
}
