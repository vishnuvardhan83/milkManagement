import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/milk-collections';

export interface DailyMilkCollection {
  id?: number;
  animalId: number;
  animalTagNumber?: string;
  animalName?: string;
  animalType?: string;
  collectionDate: string;
  morningQuantity?: number;
  eveningQuantity?: number;
  totalQuantity?: number;
  qualityGrade?: string;
  notes?: string;
}

@Injectable({ providedIn: 'root' })
export class MilkCollectionService {
  constructor(private http: HttpClient) {}

  getAllCollections(date?: string, startDate?: string, endDate?: string): Observable<DailyMilkCollection[]> {
    let params = new HttpParams();
    if (date) params = params.set('date', date);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get<DailyMilkCollection[]>(API_URL, { params });
  }

  getCollectionById(id: number): Observable<DailyMilkCollection> {
    return this.http.get<DailyMilkCollection>(`${API_URL}/${id}`);
  }

  createCollection(collection: DailyMilkCollection): Observable<DailyMilkCollection> {
    return this.http.post<DailyMilkCollection>(API_URL, collection);
  }

  updateCollection(id: number, collection: DailyMilkCollection): Observable<DailyMilkCollection> {
    return this.http.put<DailyMilkCollection>(`${API_URL}/${id}`, collection);
  }

  deleteCollection(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}

