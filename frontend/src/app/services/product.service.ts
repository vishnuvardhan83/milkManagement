import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

const API_URL = 'http://localhost:8080/api/products';

export interface Product {
  id?: number;
  name: string;
  type: 'COW_MILK' | 'BUFFALO_MILK' | 'CURD';
  quantity: number;
  pricePerUnit: number;
  unit: string;
  description?: string;
}

export interface ProductQuantity {
  cowMilk: number;
  buffaloMilk: number;
  curd: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private productQuantitySubject = new BehaviorSubject<ProductQuantity>({
    cowMilk: 100, // Default value
    buffaloMilk: 80, // Default value
    curd: 50 // Default value
  });
  public productQuantity$ = this.productQuantitySubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadProductQuantities();
  }

  getAllProducts(search?: string, filters?: { name?: string; minPrice?: number; maxPrice?: number; type?: string }): Observable<Product[]> {
    let params = new HttpParams();
    if (search) {
      params = params.set('search', search);
    }
    if (filters) {
      if (filters.name) params = params.set('name', filters.name);
      if (filters.minPrice) params = params.set('minPrice', filters.minPrice.toString());
      if (filters.maxPrice) params = params.set('maxPrice', filters.maxPrice.toString());
      if (filters.type) params = params.set('type', filters.type);
    }
    return this.http.get<Product[]>(API_URL, { params });
  }

  getProductCount(): Observable<number> {
    return this.http.get<number>(`${API_URL}/count`);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${API_URL}/${id}`);
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(API_URL, product);
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${API_URL}/${id}`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }

  getProductQuantities(): Observable<ProductQuantity> {
    return this.http.get<ProductQuantity>(`${API_URL}/quantities`);
  }

  updateProductQuantity(type: 'COW_MILK' | 'BUFFALO_MILK' | 'CURD', quantity: number): Observable<ProductQuantity> {
    return this.http.put<ProductQuantity>(`${API_URL}/quantities/${type}`, { quantity }).pipe(
      tap(quantities => this.productQuantitySubject.next(quantities))
    );
  }

  loadProductQuantities(): void {
    this.getProductQuantities().subscribe({
      next: (quantities) => {
        this.productQuantitySubject.next(quantities);
      },
      error: (error) => {
        console.error('Error loading product quantities, using defaults:', error);
        // Keep default values on error
      }
    });
  }

  getCurrentQuantities(): ProductQuantity {
    return this.productQuantitySubject.value;
  }
}
