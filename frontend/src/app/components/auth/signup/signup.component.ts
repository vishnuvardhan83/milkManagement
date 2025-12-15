import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {
  signupForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.signupForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      name: ['', [Validators.required]],
      address: [''],
      mobileNumber: ['', [Validators.required]],
      dailyMilkQuantity: ['', [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      this.loading = true;
      this.authService.signup(this.signupForm.value).subscribe({
        next: (response) => {
          this.snackBar.open('Account created successfully! Please login.', 'Close', {
            duration: 3000
          });
          this.loading = false;
          this.router.navigate(['/login']);
        },
        error: (error) => {
          const errorMessage = error.error?.error || error.error || 'Failed to create account';
          this.snackBar.open(errorMessage, 'Close', {
            duration: 5000
          });
          this.loading = false;
        }
      });
    }
  }
}
