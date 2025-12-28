import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Notifications</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Notifications feature coming soon...</p>
        </mat-card-content>
      </mat-card>
    </div>
  `
})
export class NotificationsComponent implements OnInit {
  ngOnInit(): void {}
}

