import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService, SignupRequest } from '../../../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  signupForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.signupForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      fullName: [''],
      phone: ['']
    });
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      this.loading = true;
      const signupRequest: SignupRequest = {
        ...this.signupForm.value,
        role: 'ROLE_CUSTOMER'
      };
      
      this.authService.signup(signupRequest).subscribe({
        next: (response) => {
          this.loading = false;
          this.snackBar.open('Registration successful! Please login.', 'Close', { duration: 3000 });
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.loading = false;
          this.snackBar.open('Registration failed. Please try again.', 'Close', { duration: 5000 });
          console.error('Signup error:', error);
        }
      });
    }
  }
}
