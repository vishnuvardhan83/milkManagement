import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/orders';

export interface OrderItem {
  productId: number;
  quantity: number;
  price: number;
}

export interface Order {
  id?: number;
  customerId?: number;
  items: OrderItem[];
  totalAmount: number;
  orderDate?: string;
  status?: string;
  paymentData?: any;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  constructor(private http: HttpClient) {}

  createOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(API_URL, order);
  }

  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(API_URL);
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${API_URL}/${id}`);
  }

  getCustomerOrders(customerId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${API_URL}/customer/${customerId}`);
  }
}
