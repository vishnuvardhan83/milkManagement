import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/animals';

export interface Animal {
  id?: number;
  tagNumber: string;
  name?: string;
  animalType: 'COW' | 'BUFFALO';
  breed?: 'HOLSTEIN' | 'JERSEY' | 'SAHIWAL' | 'MURRAH' | 'NILI_RAVI' | 'CROSSBREED' | 'OTHER';
  dateOfBirth?: string;
  purchaseDate?: string;
  purchasePrice?: number;
  status: 'ACTIVE' | 'SICK' | 'PREGNANT' | 'DRY' | 'SOLD' | 'DECEASED';
  healthStatus?: string;
  notes?: string;
}

@Injectable({ providedIn: 'root' })
export class AnimalService {
  constructor(private http: HttpClient) {}

  getAllAnimals(): Observable<Animal[]> {
    return this.http.get<Animal[]>(API_URL);
  }

  getAnimalById(id: number): Observable<Animal> {
    return this.http.get<Animal>(`${API_URL}/${id}`);
  }

  createAnimal(animal: Animal): Observable<Animal> {
    return this.http.post<Animal>(API_URL, animal);
  }

  updateAnimal(id: number, animal: Animal): Observable<Animal> {
    return this.http.put<Animal>(`${API_URL}/${id}`, animal);
  }

  deleteAnimal(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}

