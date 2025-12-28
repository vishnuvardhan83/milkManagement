import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatMenuModule
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Dairy Farm ERP';
  @ViewChild('sidenav') sidenav!: MatSidenav;
  sidenavOpened = false;
  private hoverTimeout: any;
  private leaveTimeout: any;

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
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
    return this.authService.hasRole('ROLE_ADMIN') || this.authService.hasRole('ROLE_FARM_ADMIN');
  }

  isCustomer(): boolean {
    return this.authService.hasRole('ROLE_CUSTOMER');
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
