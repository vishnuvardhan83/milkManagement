import { Component, OnInit } from '@angular/core';
import { CustomerService, Customer } from '../../../services/customer.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CustomerFormComponent } from '../customer-form/customer-form.component';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.scss']
})
export class CustomerListComponent implements OnInit {
  customers: Customer[] = [];
  filteredCustomers: Customer[] = [];
  allCustomers: Customer[] = [];
  displayedColumns: string[] = ['name', 'mobileNumber', 'email', 'dailyMilkQuantity', 'milkType', 'deliveryStatus', 'actions'];
  loading = false;
  
  // Search and filter controls
  searchControl = new FormControl('');
  filterNameControl = new FormControl('');
  filterStatusControl = new FormControl('');
  filterMilkTypeControl = new FormControl('');
  showFilters = false;
  
  constructor(
    private customerService: CustomerService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    
    // Setup search with debounce
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.applyFilters();
    });

    // Setup filter changes
    this.filterNameControl.valueChanges.subscribe(() => this.applyFilters());
    this.filterStatusControl.valueChanges.subscribe(() => this.applyFilters());
    this.filterMilkTypeControl.valueChanges.subscribe(() => this.applyFilters());
  }

  loadCustomers(): void {
    this.loading = true;
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.allCustomers = data;
        this.customers = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading customers:', error);
        this.snackBar.open('Error loading customers', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.allCustomers];
    const searchTerm = this.searchControl.value?.toLowerCase() || '';
    const filterName = this.filterNameControl.value?.toLowerCase() || '';
    const filterStatus = this.filterStatusControl.value || '';
    const filterMilkType = this.filterMilkTypeControl.value || '';

    // Apply search
    if (searchTerm) {
      filtered = filtered.filter(c => 
        c.name.toLowerCase().includes(searchTerm) ||
        c.mobileNumber?.toLowerCase().includes(searchTerm) ||
        (c.email && c.email.toLowerCase().includes(searchTerm))
      );
    }

    // Apply name filter
    if (filterName) {
      filtered = filtered.filter(c => c.name.toLowerCase().includes(filterName));
    }

    // Apply status filter
    if (filterStatus) {
      filtered = filtered.filter(c => (c.deliveryStatus || 'ACTIVE') === filterStatus);
    }

    // Apply milk type filter
    if (filterMilkType) {
      filtered = filtered.filter(c => (c.milkType || 'COW') === filterMilkType);
    }

    this.filteredCustomers = filtered;
    this.customers = filtered;
  }

  clearFilters(): void {
    this.searchControl.setValue('');
    this.filterNameControl.setValue('');
    this.filterStatusControl.setValue('');
    this.filterMilkTypeControl.setValue('');
    this.applyFilters();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(CustomerFormComponent, {
      width: '500px',
      data: null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadCustomers();
      }
    });
  }

  openEditDialog(customer: Customer): void {
    const dialogRef = this.dialog.open(CustomerFormComponent, {
      width: '500px',
      data: customer
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadCustomers();
      }
    });
  }

  deleteCustomer(id: number): void {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.customerService.deleteCustomer(id).subscribe({
        next: () => {
          this.snackBar.open('Customer deleted successfully', 'Close', { duration: 3000 });
          this.loadCustomers();
        },
        error: (error) => {
          this.snackBar.open('Error deleting customer', 'Close', { duration: 3000 });
        }
      });
    }
  }
}
