import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/customers';

export interface Customer {
  id?: number;
  name: string;
  address?: string;
  mobileNumber: string;
  email?: string;
  dailyMilkQuantity: number;
  milkType?: 'COW' | 'BUFFALO' | 'BOTH';
  deliveryStatus?: 'ACTIVE' | 'PAUSED' | 'INACTIVE';
}

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(private http: HttpClient) { }

  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(API_URL);
  }

  getActiveCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${API_URL}/active`);
  }

  getCustomerById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${API_URL}/${id}`);
  }

  createCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(API_URL, customer);
  }

  updateCustomer(id: number, customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${API_URL}/${id}`, customer);
  }

  deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
