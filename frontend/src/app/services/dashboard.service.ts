import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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

  getDashboardStats(fromDate?: string | null, toDate?: string | null): Observable<DashboardStats> {
    let params = new HttpParams();
    if (fromDate) {
      params = params.set('fromDate', fromDate);
    }
    if (toDate) {
      params = params.set('toDate', toDate);
    }
    return this.http.get<DashboardStats>(`${API_URL}/stats`, { params });
  }
}
