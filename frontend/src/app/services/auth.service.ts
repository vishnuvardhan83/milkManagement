import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

const API_URL = `${environment.apiUrl}/auth`;

export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  password: string;
  email: string;
  role?: string;
  fullName?: string;
  phone?: string;
}

export interface JwtResponse {
  token: string;
  type: string;
  refreshToken?: string;
  id: number;
  username: string;
  email: string;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<JwtResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  login(loginRequest: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${API_URL}/signin`, loginRequest).pipe(
      tap(response => {
        localStorage.setItem('currentUser', JSON.stringify(response));
        localStorage.setItem('token', response.token);
        if (response.refreshToken) {
          localStorage.setItem('refreshToken', response.refreshToken);
        }
        this.currentUserSubject.next(response);
      })
    );
  }

  signup(signupRequest: SignupRequest): Observable<any> {
    return this.http.post(`${API_URL}/signup`, signupRequest);
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    if (!user) return false;
    return user.roles.some(r => r === role || r === `ROLE_${role}`);
  }

  isAdminOrManager(): boolean {
    return this.hasRole('ROLE_ADMIN') || this.hasRole('ROLE_MANAGER');
  }

  getCurrentUser(): JwtResponse | null {
    return this.currentUserSubject.value;
  }
}
