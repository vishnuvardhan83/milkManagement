import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/payments';

export interface Payment {
  id?: number;
  invoiceId: number;
  amount: number;
  paymentDate: string;
  paymentMethod: string;
  status: 'PENDING' | 'PAID' | 'PARTIAL';
  customerId?: number;
  customerName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  constructor(private http: HttpClient) {}

  getAllPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(API_URL);
  }

  getPaymentById(id: number): Observable<Payment> {
    return this.http.get<Payment>(`${API_URL}/${id}`);
  }

  updatePayment(id: number, payment: Partial<Payment>): Observable<Payment> {
    return this.http.put<Payment>(`${API_URL}/${id}`, payment);
  }

  createPayment(payment: Payment): Observable<Payment> {
    return this.http.post<Payment>(API_URL, payment);
  }

  getPendingPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${API_URL}/pending`);
  }
}
