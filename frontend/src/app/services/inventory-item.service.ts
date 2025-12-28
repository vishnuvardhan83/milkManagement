import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/inventory';

export interface InventoryItem {
  id?: number;
  name: string;
  category: 'FODDER' | 'MEDICINE' | 'PACKAGING' | 'CANS' | 'SUPPLEMENTS' | 'EQUIPMENT' | 'OTHER';
  quantity: number;
  unit: string;
  costPerUnit?: number;
  totalCost?: number;
  supplierName?: string;
  purchaseDate?: string;
  expiryDate?: string;
  notes?: string;
}

export interface InventoryUsage {
  id?: number;
  inventoryItemId: number;
  inventoryItemName?: string;
  usageDate: string;
  quantityUsed: number;
  purpose?: string;
}

@Injectable({ providedIn: 'root' })
export class InventoryItemService {
  constructor(private http: HttpClient) {}

  getAllInventoryItems(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(API_URL);
  }

  getInventoryItemById(id: number): Observable<InventoryItem> {
    return this.http.get<InventoryItem>(`${API_URL}/${id}`);
  }

  createInventoryItem(item: InventoryItem): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(API_URL, item);
  }

  updateInventoryItem(id: number, item: InventoryItem): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${API_URL}/${id}`, item);
  }

  deleteInventoryItem(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }

  useInventoryItem(itemId: number, usage: InventoryUsage): Observable<InventoryUsage> {
    return this.http.post<InventoryUsage>(`${API_URL}/${itemId}/use`, usage);
  }

  getUsageHistory(itemId: number): Observable<InventoryUsage[]> {
    return this.http.get<InventoryUsage[]>(`${API_URL}/${itemId}/usage-history`);
  }
}

