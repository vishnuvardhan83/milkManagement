import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { InventoryService, InventoryStatus, InventoryEntry } from '../../services/inventory.service';
import { ProductService, Product } from '../../services/product.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../shared/confirm-dialog.component';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {
  inventoryForm: FormGroup;
  products: Product[] = [];
  selectedProductId: number | null = null;
  status: InventoryStatus | null = null;
  loading = false;
  inventoryEntries: InventoryEntry[] = [];
  displayedColumns: string[] = ['entryDate', 'productName', 'totalLitersReceived', 'pricePerLitre', 'actions'];
  editingEntry: InventoryEntry | null = null;

  constructor(
    private fb: FormBuilder,
    private inventoryService: InventoryService,
    private productService: ProductService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    const today = new Date();
    this.inventoryForm = this.fb.group({
      date: [today, Validators.required],
      productId: [null, Validators.required],
      totalLitersReceived: [100, [Validators.required, Validators.min(0.01)]],
      pricePerLitre: [55.5, [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    this.loadProducts();
    this.loadInventoryEntries();
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (products) => {
        this.products = products;
        if (products.length > 0 && !this.selectedProductId) {
          const defaultProduct = products[0];
          this.selectedProductId = defaultProduct.id!;
          this.inventoryForm.patchValue({ productId: defaultProduct.id });
          this.loadStatus();
        }
      },
      error: () => {
        this.snackBar.open('Error loading products', 'Close', { duration: 3000 });
      }
    });
  }

  onProductChange(): void {
    this.selectedProductId = this.inventoryForm.value.productId;
    this.loadStatus();
  }

  loadStatus(): void {
    if (this.selectedProductId == null) {
      return;
    }
    this.inventoryService.getStatus(this.selectedProductId).subscribe({
      next: (status) => {
        this.status = status;
      },
      error: () => {
        // Silently fail - status might not exist
      }
    });
  }

  loadInventoryEntries(): void {
    this.loading = true;
    this.inventoryService.getAllInventoryEntries(this.selectedProductId || undefined).subscribe({
      next: (entries) => {
        this.inventoryEntries = entries;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open('Error loading inventory entries', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  saveInventory(): void {
    if (this.inventoryForm.invalid || this.selectedProductId == null) {
      return;
    }

    this.loading = true;
    const value = this.inventoryForm.value;
    const date = value.date instanceof Date ? value.date : new Date(value.date);
    const payload = {
      productId: this.selectedProductId,
      date: date.toISOString().substring(0, 10),
      totalLitersReceived: Number(value.totalLitersReceived),
      pricePerLitre: Number(value.pricePerLitre)
    };

    if (this.editingEntry) {
      // Update existing entry
      this.inventoryService.updateInventoryEntry(this.editingEntry.id, payload).subscribe({
        next: () => {
          this.snackBar.open('Inventory entry updated successfully', 'Close', { duration: 3000 });
          this.loadStatus();
          this.loadInventoryEntries();
          this.cancelEdit();
          this.loading = false;
        },
        error: () => {
          this.snackBar.open('Error updating inventory entry', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    } else {
      // Create new entry
      this.inventoryService.updateInventory(payload).subscribe({
        next: () => {
          this.snackBar.open('Inventory updated successfully', 'Close', { duration: 3000 });
          this.loadStatus();
          this.loadInventoryEntries();
          this.resetForm();
          this.loading = false;
        },
        error: () => {
          this.snackBar.open('Error updating inventory', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  editEntry(entry: InventoryEntry): void {
    this.editingEntry = entry;
    const entryDate = new Date(entry.entryDate);
    this.inventoryForm.patchValue({
      date: entryDate,
      productId: entry.productId,
      totalLitersReceived: entry.totalLitersReceived,
      pricePerLitre: entry.pricePerLitre
    });
    this.selectedProductId = entry.productId;
    this.loadStatus();
    // Scroll to form
    document.querySelector('.update-card')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  deleteEntry(entry: InventoryEntry): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Inventory Entry',
        message: `Are you sure you want to delete this inventory entry for ${entry.productName} on ${entry.entryDate}? This will also reduce the stock quantity.`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading = true;
        this.inventoryService.deleteInventoryEntry(entry.id).subscribe({
          next: () => {
            this.snackBar.open('Inventory entry deleted successfully', 'Close', { duration: 3000 });
            this.loadStatus();
            this.loadInventoryEntries();
            this.loading = false;
          },
          error: () => {
            this.snackBar.open('Error deleting inventory entry', 'Close', { duration: 3000 });
            this.loading = false;
          }
        });
      }
    });
  }

  cancelEdit(): void {
    this.editingEntry = null;
    this.resetForm();
  }

  resetForm(): void {
    const today = new Date();
    this.inventoryForm.reset({
      date: today,
      productId: this.selectedProductId,
      totalLitersReceived: 100,
      pricePerLitre: 55.5
    });
  }

  openAddProductDialog(): void {
    // Import and open product form dialog
    import('../product/product-form/product-form.component').then(module => {
      const dialogRef = this.dialog.open(module.ProductFormComponent, {
        width: '600px',
        data: { product: null }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.loadProducts();
          this.snackBar.open('Product added successfully', 'Close', { duration: 3000 });
        }
      });
    }).catch(() => {
      // Fallback: navigate to product form or show message
      this.snackBar.open('Please use the Products page to add new products', 'Close', { duration: 3000 });
    });
  }
}
