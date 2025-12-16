import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardService, DashboardStats } from '../../services/dashboard.service';
import { AuthService } from '../../services/auth.service';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  loading = true;
  
  // Date range filters for dashboard
  dateFromControl = new FormControl('');
  dateToControl = new FormControl('');

  // Chart configurations
  revenueChartData: ChartData<'line'> = {
    labels: [],
    datasets: []
  };
  revenueChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: true
      },
      title: {
        display: true,
        text: 'Revenue Overview'
      }
    }
  };
  revenueChartType: ChartType = 'line';

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboardStats();
  }

  loadDashboardStats(): void {
    const from = this.formatDate(this.dateFromControl.value);
    const to = this.formatDate(this.dateToControl.value);

    this.dashboardService.getDashboardStats(from, to).subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
        this.updateCharts();
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
        this.loading = false;
      }
    });
  }

  private formatDate(value: any): string | null {
    if (!value) {
      return null;
    }
    const date = value instanceof Date ? value : new Date(value);
    if (isNaN(date.getTime())) {
      return null;
    }
    // Backend expects ISO date (yyyy-MM-dd)
    return date.toISOString().substring(0, 10);
  }

  updateCharts(): void {
    if (this.stats) {
      this.revenueChartData = {
        labels: ['Today', 'This Month'],
        datasets: [
          {
            label: 'Revenue',
            data: [this.stats.totalRevenueToday, this.stats.totalRevenueThisMonth],
            borderColor: '#1976d2',
            backgroundColor: 'rgba(25, 118, 210, 0.1)',
            tension: 0.4
          }
        ]
      };
    }
  }

  applyDateFilter(): void {
    this.loadDashboardStats();
  }

  clearDateFilter(): void {
    this.dateFromControl.setValue(null);
    this.dateToControl.setValue(null);
    this.loadDashboardStats();
  }

}
