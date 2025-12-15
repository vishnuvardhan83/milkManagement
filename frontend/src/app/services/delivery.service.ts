import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/deliveries';

export interface MilkDelivery {
  id?: number;
  customerId: number;
  productId: number;
  deliveryDate: string;
  quantityDelivered: number;
  pricePerUnit?: number;
  totalAmount?: number;
  notes?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {

  constructor(private http: HttpClient) { }

  getAllDeliveries(): Observable<MilkDelivery[]> {
    return this.http.get<MilkDelivery[]>(API_URL);
  }

  getDeliveriesByCustomer(customerId: number): Observable<MilkDelivery[]> {
    return this.http.get<MilkDelivery[]>(`${API_URL}/customer/${customerId}`);
  }

  getDeliveriesByDate(date: string): Observable<MilkDelivery[]> {
    return this.http.get<MilkDelivery[]>(`${API_URL}/date/${date}`);
  }

  getDeliveriesByDateRange(customerId: number, startDate: string, endDate: string): Observable<MilkDelivery[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<MilkDelivery[]>(`${API_URL}/customer/${customerId}/range`, { params });
  }

  createDelivery(delivery: MilkDelivery): Observable<MilkDelivery> {
    return this.http.post<MilkDelivery>(API_URL, delivery);
  }
}
