import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/salaries';

export interface Salary {
  id?: number;
  employeeId: number;
  employeeName?: string;
  salaryMonth: string;
  baseSalary: number;
  bonus?: number;
  deductions?: number;
  netSalary?: number;
  paymentStatus: 'PENDING' | 'PAID' | 'PARTIAL';
  paymentDate?: string;
  notes?: string;
}

@Injectable({ providedIn: 'root' })
export class SalaryService {
  constructor(private http: HttpClient) {}

  getAllSalaries(): Observable<Salary[]> {
    return this.http.get<Salary[]>(API_URL);
  }

  getSalaryById(id: number): Observable<Salary> {
    return this.http.get<Salary>(`${API_URL}/${id}`);
  }

  createSalary(salary: Salary): Observable<Salary> {
    return this.http.post<Salary>(API_URL, salary);
  }

  updateSalary(id: number, salary: Salary): Observable<Salary> {
    return this.http.put<Salary>(`${API_URL}/${id}`, salary);
  }

  deleteSalary(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}

