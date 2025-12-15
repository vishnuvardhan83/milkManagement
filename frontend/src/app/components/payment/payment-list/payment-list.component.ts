import { Component, OnInit } from '@angular/core';
import { PaymentService, Payment } from '../../../services/payment.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-payment-list',
  templateUrl: './payment-list.component.html',
  styleUrls: ['./payment-list.component.scss']
})
export class PaymentListComponent implements OnInit {
  payments: Payment[] = [];
  filteredPayments: Payment[] = [];
  allPayments: Payment[] = [];
  displayedColumns: string[] = ['customerName', 'invoiceId', 'amount', 'paymentDate', 'paymentMethod', 'status', 'actions'];
  loading = false;
  
  // Search and filter controls
  searchControl = new FormControl('');
  filterStatusControl = new FormControl('');
  filterMethodControl = new FormControl('');
  filterDateFromControl = new FormControl('');
  filterDateToControl = new FormControl('');
  minAmountControl = new FormControl('');
  maxAmountControl = new FormControl('');
  showFilters = false;
  
  constructor(
    private paymentService: PaymentService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadPayments();
    
    // Setup search with debounce
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.applyFilters();
    });

    // Setup filter changes
    this.filterStatusControl.valueChanges.subscribe(() => this.applyFilters());
    this.filterMethodControl.valueChanges.subscribe(() => this.applyFilters());
    this.filterDateFromControl.valueChanges.subscribe(() => this.applyFilters());
    this.filterDateToControl.valueChanges.subscribe(() => this.applyFilters());
    this.minAmountControl.valueChanges.subscribe(() => this.applyFilters());
    this.maxAmountControl.valueChanges.subscribe(() => this.applyFilters());
  }

  loadPayments(): void {
    this.loading = true;
    this.paymentService.getAllPayments().subscribe({
      next: (data) => {
        this.allPayments = data;
        this.payments = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading payments:', error);
        this.snackBar.open('Error loading payments', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.allPayments];
    const searchTerm = this.searchControl.value?.toLowerCase() || '';
    const filterStatus = this.filterStatusControl.value || '';
    const filterMethod = this.filterMethodControl.value || '';
    const dateFrom = this.filterDateFromControl.value;
    const dateTo = this.filterDateToControl.value;
    const minAmount = this.minAmountControl.value ? parseFloat(this.minAmountControl.value) : null;
    const maxAmount = this.maxAmountControl.value ? parseFloat(this.maxAmountControl.value) : null;

    // Apply search
    if (searchTerm) {
      filtered = filtered.filter(p => 
        (p.customerName && p.customerName.toLowerCase().includes(searchTerm)) ||
        (p.invoiceId && p.invoiceId.toString().includes(searchTerm))
      );
    }

    // Apply status filter
    if (filterStatus) {
      filtered = filtered.filter(p => p.status === filterStatus);
    }

    // Apply method filter
    if (filterMethod) {
      filtered = filtered.filter(p => p.paymentMethod === filterMethod);
    }

    // Apply date filters
    if (dateFrom) {
      const fromDate = new Date(dateFrom);
      filtered = filtered.filter(p => {
        const paymentDate = new Date(p.paymentDate);
        return paymentDate >= fromDate;
      });
    }
    if (dateTo) {
      const toDate = new Date(dateTo);
      filtered = filtered.filter(p => {
        const paymentDate = new Date(p.paymentDate);
        return paymentDate <= toDate;
      });
    }

    // Apply amount filters
    if (minAmount !== null) {
      filtered = filtered.filter(p => p.amount >= minAmount);
    }
    if (maxAmount !== null) {
      filtered = filtered.filter(p => p.amount <= maxAmount);
    }

    this.filteredPayments = filtered;
    this.payments = filtered;
  }

  clearFilters(): void {
    this.searchControl.setValue('');
    this.filterStatusControl.setValue('');
    this.filterMethodControl.setValue('');
    this.filterDateFromControl.setValue('');
    this.filterDateToControl.setValue('');
    this.minAmountControl.setValue('');
    this.maxAmountControl.setValue('');
    this.applyFilters();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  updatePaymentStatus(payment: Payment, status: 'PENDING' | 'PAID' | 'PARTIAL'): void {
    if (payment.id) {
      this.paymentService.updatePayment(payment.id, { status }).subscribe({
        next: () => {
          this.snackBar.open('Payment status updated successfully', 'Close', { duration: 3000 });
          this.loadPayments();
        },
        error: (error) => {
          this.snackBar.open('Error updating payment status', 'Close', { duration: 3000 });
        }
      });
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PAID':
        return 'green';
      case 'PENDING':
        return 'orange';
      case 'PARTIAL':
        return 'blue';
      default:
        return 'gray';
    }
  }
}
