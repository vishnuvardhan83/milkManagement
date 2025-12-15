import { Component, OnInit } from '@angular/core';
import { DeliveryService, MilkDelivery } from '../../../services/delivery.service';
import { CustomerService, Customer } from '../../../services/customer.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DeliveryFormComponent } from '../delivery-form/delivery-form.component';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-delivery-list',
  templateUrl: './delivery-list.component.html',
  styleUrls: ['./delivery-list.component.scss']
})
export class DeliveryListComponent implements OnInit {
  deliveries: MilkDelivery[] = [];
  filteredDeliveries: MilkDelivery[] = [];
  allDeliveries: MilkDelivery[] = [];
  customers: Customer[] = [];
  displayedColumns: string[] = ['deliveryDate', 'customerId', 'quantityDelivered', 'pricePerUnit', 'totalAmount', 'actions'];
  loading = false;
  
  // Search and filter controls
  searchControl = new FormControl('');
  filterCustomerControl = new FormControl('');
  filterDateFromControl = new FormControl('');
  filterDateToControl = new FormControl('');
  minAmountControl = new FormControl('');
  maxAmountControl = new FormControl('');
  showFilters = false;
  
  constructor(
    private deliveryService: DeliveryService,
    private customerService: CustomerService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDeliveries();
    this.loadCustomers();
    
    // Setup search with debounce
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.applyFilters();
    });

    // Setup filter changes
    this.filterCustomerControl.valueChanges.subscribe(() => this.applyFilters());
    this.filterDateFromControl.valueChanges.subscribe(() => this.applyFilters());
    this.filterDateToControl.valueChanges.subscribe(() => this.applyFilters());
    this.minAmountControl.valueChanges.subscribe(() => this.applyFilters());
    this.maxAmountControl.valueChanges.subscribe(() => this.applyFilters());
  }

  loadDeliveries(): void {
    this.loading = true;
    this.deliveryService.getAllDeliveries().subscribe({
      next: (data) => {
        this.allDeliveries = data;
        this.deliveries = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading deliveries:', error);
        this.snackBar.open('Error loading deliveries', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.allDeliveries];
    const searchTerm = this.searchControl.value?.toLowerCase() || '';
    const filterCustomer = this.filterCustomerControl.value;
    const dateFrom = this.filterDateFromControl.value;
    const dateTo = this.filterDateToControl.value;
    const minAmount = this.minAmountControl.value ? parseFloat(this.minAmountControl.value) : null;
    const maxAmount = this.maxAmountControl.value ? parseFloat(this.maxAmountControl.value) : null;

    // Apply search
    if (searchTerm) {
      filtered = filtered.filter(d => {
        const customerName = this.getCustomerName(d.customerId).toLowerCase();
        return customerName.includes(searchTerm);
      });
    }

    // Apply customer filter
    if (filterCustomer) {
      filtered = filtered.filter(d => d.customerId === parseInt(filterCustomer));
    }

    // Apply date filters
    if (dateFrom) {
      const fromDate = new Date(dateFrom);
      filtered = filtered.filter(d => {
        const deliveryDate = new Date(d.deliveryDate);
        return deliveryDate >= fromDate;
      });
    }
    if (dateTo) {
      const toDate = new Date(dateTo);
      filtered = filtered.filter(d => {
        const deliveryDate = new Date(d.deliveryDate);
        return deliveryDate <= toDate;
      });
    }

    // Apply amount filters
    if (minAmount !== null) {
      filtered = filtered.filter(d => (d.totalAmount || 0) >= minAmount);
    }
    if (maxAmount !== null) {
      filtered = filtered.filter(d => (d.totalAmount || 0) <= maxAmount);
    }

    this.filteredDeliveries = filtered;
    this.deliveries = filtered;
  }

  clearFilters(): void {
    this.searchControl.setValue('');
    this.filterCustomerControl.setValue('');
    this.filterDateFromControl.setValue('');
    this.filterDateToControl.setValue('');
    this.minAmountControl.setValue('');
    this.maxAmountControl.setValue('');
    this.applyFilters();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  loadCustomers(): void {
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers = data;
      }
    });
  }

  getCustomerName(customerId: number): string {
    const customer = this.customers.find(c => c.id === customerId);
    return customer ? customer.name : 'Unknown';
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(DeliveryFormComponent, {
      width: '500px',
      data: { customers: this.customers }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadDeliveries();
      }
    });
  }
}
