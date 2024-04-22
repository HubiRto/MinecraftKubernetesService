import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {RouterLink} from "@angular/router";
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    NgClass,
    NgIf
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  passwordInputType: string = 'password';
  passwordIcon: string = 'fa-eye-slash';
  registerForm!: FormGroup;

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      repeatPassword: ['', [Validators.required]]
    });
  }

  isFieldHaveError(field: string, errorType: string): boolean {
    return this.registerForm.controls[field].dirty && this.registerForm.hasError(errorType, field);
  }

  isFirstNameHaveError(): boolean {
    return this.isFieldHaveError('firstName', 'required');
  }

  isLastNameHaveError(): boolean {
    return this.isFieldHaveError('lastName', 'required');
  }

  isEmailHaveError(): boolean {
    return this.isFieldHaveError('email', 'required')
      || this.isFieldHaveError('email', 'email');
  }

  isEmailHaveTypeError(errorType: string): boolean {
    return this.isFieldHaveError('email', errorType);
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
    if (this.registerForm.valid) {
      console.log(this.registerForm.value);
    } else {
      this.validateAllFormFields(this.registerForm);
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
