import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/inventory';

export interface InventoryStatus {
  productId: number;
  productName: string;
  date: string;
  totalReceived: number;
  available: number;
  pricePerLitre: number;
}

export interface InventoryUpdateRequest {
  productId: number;
  date: string; // ISO yyyy-MM-dd
  totalLitersReceived: number;
  pricePerLitre: number;
}

export interface InventoryEntry {
  id: number;
  productId: number;
  productName: string;
  entryDate: string;
  totalLitersReceived: number;
  pricePerLitre: number;
  createdAt: string;
  updatedAt: string;
}

@Injectable({ providedIn: 'root' })
export class InventoryService {
  constructor(private http: HttpClient) {}

  getStatus(productId?: number): Observable<InventoryStatus> {
    let params = new HttpParams();
    if (productId != null) {
      params = params.set('productId', productId.toString());
    }
    return this.http.get<InventoryStatus>(`${API_URL}/status`, { params });
  }

  updateInventory(request: InventoryUpdateRequest): Observable<InventoryStatus> {
    return this.http.post<InventoryStatus>(`${API_URL}/update`, request);
  }

  getAllInventoryEntries(productId?: number): Observable<InventoryEntry[]> {
    let params = new HttpParams();
    if (productId != null) {
      params = params.set('productId', productId.toString());
    }
    return this.http.get<InventoryEntry[]>(`${API_URL}/entries`, { params });
  }

  getInventoryEntryById(id: number): Observable<InventoryEntry> {
    return this.http.get<InventoryEntry>(`${API_URL}/entries/${id}`);
  }

  updateInventoryEntry(id: number, request: InventoryUpdateRequest): Observable<InventoryEntry> {
    return this.http.put<InventoryEntry>(`${API_URL}/entries/${id}`, request);
  }

  deleteInventoryEntry(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/entries/${id}`);
  }
}
