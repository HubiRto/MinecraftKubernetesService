import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgClass, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    NgClass,
    RouterLink,
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  passwordInputType: string = 'password';
  passwordIcon: string = 'fa-eye-slash';
  loginForm!: FormGroup;

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  isFieldHaveError(field: string, errorType: string): boolean {
    return this.loginForm.controls[field].dirty && this.loginForm.hasError(errorType, field);
  }

  isEmailValid(): boolean {
    return this.isFieldHaveError('email', 'required')
      || this.isFieldHaveError('email', 'email');
  }

  isEmailHaveError(errorType: string): boolean {
    return this.isFieldHaveError('email', errorType);
  }

  isPasswordIsValid(): boolean {
    return this.isFieldHaveError('password', 'required');
  }

  hideShowPass(): void {
    if (this.passwordInputType === 'password') {
      this.passwordIcon = 'fa-eye'
      this.passwordInputType = 'text';
    } else {
      this.passwordIcon = 'fa-eye-slash';
      this.passwordInputType = 'password';
    }
  }

  onSubmit() {
    if (this.loginForm.valid) {
      console.log(this.loginForm.value);
    } else {
      this.validateAllFormFields(this.loginForm);
    }
  }

  private validateAllFormFields(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);
      if (control instanceof FormControl) {
        control.markAsDirty({onlySelf: true});
      } else if (control instanceof FormGroup) {
        this.validateAllFormFields(control);
      }
    });
  }
}
