import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/expenses';

export interface Expense {
  id?: number;
  expenseDate: string;
  category: 'FEED' | 'MEDICINE' | 'LABOR' | 'MAINTENANCE' | 'UTILITIES' | 'TRANSPORT' | 'INSURANCE' | 'OTHER';
  description: string;
  amount: number;
  paymentMethod?: string;
  receiptNumber?: string;
  notes?: string;
}

@Injectable({ providedIn: 'root' })
export class ExpenseService {
  constructor(private http: HttpClient) {}

  getAllExpenses(startDate?: string, endDate?: string): Observable<Expense[]> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get<Expense[]>(API_URL, { params });
  }

  getExpenseById(id: number): Observable<Expense> {
    return this.http.get<Expense>(`${API_URL}/${id}`);
  }

  createExpense(expense: Expense): Observable<Expense> {
    return this.http.post<Expense>(API_URL, expense);
  }

  updateExpense(id: number, expense: Expense): Observable<Expense> {
    return this.http.put<Expense>(`${API_URL}/${id}`, expense);
  }

  deleteExpense(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}

