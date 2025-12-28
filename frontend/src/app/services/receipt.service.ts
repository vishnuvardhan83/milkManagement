import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/receipts';

export interface Receipt {
  id?: number;
  receiptNumber?: string;
  customerId: number;
  customerName?: string;
  customerMobile?: string;
  receiptDate: string;
  quantityLiters: number;
  milkRate: number;
  totalAmount?: number;
  paymentStatus?: 'PENDING' | 'PARTIAL' | 'PAID';
  paidAmount?: number;
  pendingAmount?: number;
  paymentDate?: string;
  paymentMethod?: string;
  notes?: string;
}

@Injectable({ providedIn: 'root' })
export class ReceiptService {
  constructor(private http: HttpClient) {}

  getAllReceipts(): Observable<Receipt[]> {
    return this.http.get<Receipt[]>(API_URL);
  }

  getReceiptsByCustomer(customerId: number): Observable<Receipt[]> {
    return this.http.get<Receipt[]>(`${API_URL}/customer/${customerId}`);
  }

  getReceiptById(id: number): Observable<Receipt> {
    return this.http.get<Receipt>(`${API_URL}/${id}`);
  }

  createReceipt(receipt: Receipt): Observable<Receipt> {
    return this.http.post<Receipt>(API_URL, receipt);
  }

  updateReceipt(id: number, receipt: Receipt): Observable<Receipt> {
    return this.http.put<Receipt>(`${API_URL}/${id}`, receipt);
  }

  deleteReceipt(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }

  recordPayment(receiptId: number, amount: number, paymentMethod: string = 'CASH'): Observable<Receipt> {
    const params = new HttpParams()
      .set('amount', amount.toString())
      .set('paymentMethod', paymentMethod);
    return this.http.post<Receipt>(`${API_URL}/${receiptId}/payment`, null, { params });
  }
}

