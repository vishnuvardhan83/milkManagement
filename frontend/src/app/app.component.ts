import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Milk Management System';
  @ViewChild('sidenav') sidenav!: MatSidenav;
  sidenavOpened = false;
  private hoverTimeout: any;
  private leaveTimeout: any;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Close sidenav after navigation
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.closeSidenav();
      });
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ADMIN') || this.authService.hasRole('ROLE_ADMIN');
  }

  isCustomer(): boolean {
    return this.authService.hasRole('CUSTOMER') || this.authService.hasRole('ROLE_CUSTOMER');
  }

  onSidenavHover(): void {
    if (this.leaveTimeout) {
      clearTimeout(this.leaveTimeout);
      this.leaveTimeout = null;
    }
    if (!this.sidenavOpened) {
      this.sidenavOpened = true;
    }
  }

  onSidenavLeave(): void {
    // Delay closing to allow moving to content
    if (this.leaveTimeout) {
      clearTimeout(this.leaveTimeout);
    }
    this.leaveTimeout = setTimeout(() => {
      this.closeSidenav();
    }, 300);
  }

  onNavItemClick(): void {
    this.closeSidenav();
  }

  closeSidenav(): void {
    this.sidenavOpened = false;
  }

  logout(): void {
    this.authService.logout();
  }
}
