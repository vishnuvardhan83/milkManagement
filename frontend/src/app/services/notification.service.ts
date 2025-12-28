import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/notifications';

export interface NotificationRequest {
  customerId?: number;
  notificationType: 'PAYMENT_REMINDER' | 'RECEIPT_GENERATED' | 'BALANCE_UPDATE' | 'CUSTOM_MESSAGE';
  channel: 'EMAIL' | 'SMS' | 'BOTH';
  subject?: string;
  message: string;
}

export interface NotificationQueue {
  id: number;
  customerId?: number;
  notificationType: string;
  channel: string;
  recipient: string;
  subject?: string;
  message: string;
  status: 'PENDING' | 'SENT' | 'FAILED' | 'CANCELLED';
  sentAt?: string;
  errorMessage?: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private http: HttpClient) {}

  sendNotification(request: NotificationRequest): Observable<NotificationQueue> {
    return this.http.post<NotificationQueue>(`${API_URL}/send`, request);
  }

  sendEmail(customerId: number, subject: string, message: string): Observable<NotificationQueue> {
    const params = new HttpParams()
      .set('customerId', customerId.toString())
      .set('subject', subject)
      .set('message', message);
    return this.http.post<NotificationQueue>(`${API_URL}/send-email`, null, { params });
  }

  sendSMS(customerId: number, message: string): Observable<NotificationQueue> {
    const params = new HttpParams()
      .set('customerId', customerId.toString())
      .set('message', message);
    return this.http.post<NotificationQueue>(`${API_URL}/send-sms`, null, { params });
  }

  sendPaymentReminder(customerId: number): Observable<any> {
    return this.http.post(`${API_URL}/payment-reminder/${customerId}`, null);
  }

  getPendingNotifications(): Observable<NotificationQueue[]> {
    return this.http.get<NotificationQueue[]>(`${API_URL}/pending`);
  }
}

