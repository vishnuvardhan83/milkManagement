import { Component, OnInit } from '@angular/core';
import { ProductService, Product, ProductQuantity } from '../../../services/product.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../../services/auth.service';
import { ProductFormComponent } from '../product-form/product-form.component';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  allProducts: Product[] = [];
  quantities: ProductQuantity = {
    cowMilk: 100,
    buffaloMilk: 80,
    curd: 50
  };
  loading = false;
  
  // Search and filter controls
  searchControl = new FormControl('');
  filterNameControl = new FormControl('');
  filterTypeControl = new FormControl('');
  minPriceControl = new FormControl('');
  maxPriceControl = new FormControl('');
  
  // Filter options
  productTypes = ['', 'COW_MILK', 'BUFFALO_MILK', 'CURD'];
  showFilters = false;
  
  constructor(
    private productService: ProductService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadQuantities();
    
    // Subscribe to quantity updates
    this.productService.productQuantity$.subscribe(quantities => {
      this.quantities = quantities;
      // Update product quantities in the products array
      this.products.forEach(product => {
        if (product.type === 'COW_MILK') {
          product.quantity = quantities.cowMilk;
        } else if (product.type === 'BUFFALO_MILK') {
          product.quantity = quantities.buffaloMilk;
        } else if (product.type === 'CURD') {
          product.quantity = quantities.curd;
        }
      });
      this.applyFilters();
    });

    // Setup search with debounce
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.applyFilters();
    });

    // Setup filter changes
    this.filterNameControl.valueChanges.subscribe(() => this.applyFilters());
    this.filterTypeControl.valueChanges.subscribe(() => this.applyFilters());
    this.minPriceControl.valueChanges.subscribe(() => this.applyFilters());
    this.maxPriceControl.valueChanges.subscribe(() => this.applyFilters());

    // Refresh quantities periodically
    setInterval(() => {
      this.loadQuantities();
    }, 30000);
  }

  isAdminOrManager(): boolean {
    return this.authService.isAdminOrManager();
  }

  isCustomer(): boolean {
    return this.authService.hasRole('CUSTOMER') || this.authService.hasRole('ROLE_CUSTOMER');
  }

  addToCart(product: Product): void {
    const cartData = localStorage.getItem('cart');
    let cartItems: any[] = [];
    if (cartData) {
      cartItems = JSON.parse(cartData);
    }
    
    const existingItem = cartItems.find((item: any) => item.product.id === product.id);
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      cartItems.push({ product, quantity: 1 });
    }
    
    localStorage.setItem('cart', JSON.stringify(cartItems));
    this.snackBar.open(`${product.name} added to cart`, 'Close', { duration: 2000 });
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        this.allProducts = data;
        this.products = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        // If API fails, show default products
        this.allProducts = [
          { id: 1, name: 'Cow Milk', type: 'COW_MILK', quantity: this.quantities.cowMilk, pricePerUnit: 50, unit: 'L' },
          { id: 2, name: 'Buffalo Milk', type: 'BUFFALO_MILK', quantity: this.quantities.buffaloMilk, pricePerUnit: 60, unit: 'L' },
          { id: 3, name: 'Curd', type: 'CURD', quantity: this.quantities.curd, pricePerUnit: 40, unit: 'kg' }
        ];
        this.products = this.allProducts;
        this.applyFilters();
        this.loading = false;
      }
    });
  }


  applyFilters(): void {
    let filtered = [...this.allProducts];
    const searchTerm = this.searchControl.value?.toLowerCase() || '';
    const filterName = this.filterNameControl.value?.toLowerCase() || '';
    const filterType = this.filterTypeControl.value || '';
    const minPrice = this.minPriceControl.value ? parseFloat(this.minPriceControl.value) : null;
    const maxPrice = this.maxPriceControl.value ? parseFloat(this.maxPriceControl.value) : null;

    // Apply search
    if (searchTerm) {
      filtered = filtered.filter(p => 
        p.name.toLowerCase().includes(searchTerm) ||
        (p.type && p.type.toLowerCase().includes(searchTerm))
      );
    }

    // Apply name filter
    if (filterName) {
      filtered = filtered.filter(p => p.name.toLowerCase().includes(filterName));
    }

    // Apply type filter
    if (filterType) {
      filtered = filtered.filter(p => p.type === filterType);
    }

    // Apply price filters
    if (minPrice !== null) {
      filtered = filtered.filter(p => p.pricePerUnit >= minPrice);
    }
    if (maxPrice !== null) {
      filtered = filtered.filter(p => p.pricePerUnit <= maxPrice);
    }

    this.filteredProducts = filtered;
    this.products = filtered;
  }

  clearFilters(): void {
    this.searchControl.setValue('');
    this.filterNameControl.setValue('');
    this.filterTypeControl.setValue('');
    this.minPriceControl.setValue('');
    this.maxPriceControl.setValue('');
    this.applyFilters();
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(ProductFormComponent, {
      width: '500px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts();
      }
    });
  }

  openEditDialog(product: Product): void {
    const dialogRef = this.dialog.open(ProductFormComponent, {
      width: '500px',
      data: { product }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts();
      }
    });
  }

  deleteProduct(id: number): void {
    if (confirm('Are you sure you want to delete this product?')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.snackBar.open('Product deleted successfully', 'Close', { duration: 3000 });
          this.loadProducts();
        },
        error: (error) => {
          this.snackBar.open('Error deleting product', 'Close', { duration: 3000 });
        }
      });
    }
  }

  loadQuantities(): void {
    this.productService.getProductQuantities().subscribe({
      next: (quantities) => {
        this.quantities = quantities;
      },
      error: (error) => {
        console.error('Error loading quantities, using defaults:', error);
        // Use default values
        this.quantities = this.productService.getCurrentQuantities();
      }
    });
  }

  getCowMilkQuantity(): number {
    const product = this.products.find(p => p.type === 'COW_MILK');
    return product ? product.quantity : this.quantities.cowMilk;
  }

  getBuffaloMilkQuantity(): number {
    const product = this.products.find(p => p.type === 'BUFFALO_MILK');
    return product ? product.quantity : this.quantities.buffaloMilk;
  }

  getCurdQuantity(): number {
    const product = this.products.find(p => p.type === 'CURD');
    return product ? product.quantity : this.quantities.curd;
  }

  getCowMilkCount(): number {
    return this.allProducts.filter(p => p.type === 'COW_MILK').length;
  }

  getBuffaloMilkCount(): number {
    return this.allProducts.filter(p => p.type === 'BUFFALO_MILK').length;
  }

  getCurdCount(): number {
    return this.allProducts.filter(p => p.type === 'CURD').length;
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }
}
