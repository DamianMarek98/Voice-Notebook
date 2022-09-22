import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.less']
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  submitted = false;
  error = false;
  errorMessage!: string;
  returnUrl!: string;

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
  }

  // getter to easy access form fields
  get formControls() {
    return this.form.controls;
  }

  onSubmit() {
    this.submitted = true;
    this.error = false;

    if (this.form.invalid) {
      return;
    }
    this.loading = true;
    this.authService.login(this.formControls.username.value, this.formControls.password.value)
      .subscribe({
        next: () => this.router.navigate([this.returnUrl]).then(),
        error: (error) => {
          this.loading = false;
          this.error = true;
          this.errorMessage = 'Podano błędne dane, spróbuj ponownie!';
          console.log(error.statusText);
        }
      });
  }

}
