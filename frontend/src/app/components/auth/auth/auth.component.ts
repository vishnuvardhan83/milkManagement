import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent implements OnInit {
  isLoginMode = true;
  loginForm: FormGroup;
  signupForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });

    this.signupForm = this.fb.group({
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
    
    // Check route to determine initial mode
    const path = this.route.snapshot.routeConfig?.path;
    this.isLoginMode = path !== 'signup';
  }

  switchMode(): void {
    this.isLoginMode = !this.isLoginMode;
  }

  onLogin(): void {
    if (this.loginForm.valid) {
      this.loading = true;
      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          this.router.navigate(['/dashboard']);
          this.loading = false;
        },
        error: (error) => {
          const errorMessage = error.error?.error || 'Invalid username or password';
          this.snackBar.open(errorMessage, 'Close', {
            duration: 3000
          });
          this.loading = false;
        }
      });
    }
  }

  onSignup(): void {
    if (this.signupForm.valid) {
      this.loading = true;
      this.authService.signup(this.signupForm.value).subscribe({
        next: () => {
          this.snackBar.open('Account created successfully! Please login.', 'Close', {
            duration: 3000
          });
          this.loading = false;
          this.isLoginMode = true;
          this.signupForm.reset();
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
