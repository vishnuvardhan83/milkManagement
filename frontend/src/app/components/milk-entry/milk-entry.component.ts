import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-milk-entry',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Milk Entry</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Milk entry feature coming soon...</p>
        </mat-card-content>
      </mat-card>
    </div>
  `
})
export class MilkEntryComponent implements OnInit {
  ngOnInit(): void {}
}

