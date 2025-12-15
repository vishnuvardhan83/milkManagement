import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/dashboard';

export interface DashboardStats {
  totalCustomers: number;
  activeCustomers: number;
  totalMilkDeliveredToday: number;
  totalRevenueToday: number;
  totalRevenueThisMonth: number;
  pendingPayments: number;
  pendingInvoices: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private http: HttpClient) { }

  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${API_URL}/stats`);
  }
}
