import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  isFormValid(): boolean {
    return this.loginForm.valid;
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    if (!this.isFormValid()) {
      return;
    }

    const email = this.loginForm.get('email')?.value;
    const password = this.loginForm.get('password')?.value;

    // Backend expects username/password; we send email as username
    const payload = {
      username: email,
      password: password
    };

    this.loading = true;
    this.authService.login(payload as any).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
        this.loading = false;
      },
      error: (error) => {
        const errorMessage = error.error?.error || 'Invalid email or password';
        this.snackBar.open(errorMessage, 'Close', {
          duration: 3000
        });
        this.loading = false;
      }
    });
  }

  useAdminDemo(): void {
    this.loginForm.patchValue({
      email: 'admin@example.com',
      password: 'admin123'
    });
  }

  useCustomerDemo(): void {
    this.loginForm.patchValue({
      email: 'customer@example.com',
      password: 'customer123'
    });
  }
}