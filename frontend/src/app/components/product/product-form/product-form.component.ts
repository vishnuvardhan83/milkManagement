import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ProductService, Product } from '../../../services/product.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.scss']
})
export class ProductFormComponent implements OnInit {
  productForm: FormGroup;
  isEditMode = false;
  productTypes = ['COW_MILK', 'BUFFALO_MILK', 'CURD'];
  units = ['L', 'kg', 'LITRE'];

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private dialogRef: MatDialogRef<ProductFormComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { product?: Product }
  ) {
    this.isEditMode = !!data.product;

    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      type: ['COW_MILK', Validators.required],
      quantity: [0, [Validators.required, Validators.min(0)]],
      pricePerUnit: [0, [Validators.required, Validators.min(0.01)]],
      unit: ['L', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    if (this.isEditMode && this.data.product) {
      this.productForm.patchValue({
        name: this.data.product.name,
        type: this.data.product.type || 'COW_MILK',
        quantity: this.data.product.quantity,
        pricePerUnit: this.data.product.pricePerUnit,
        unit: this.data.product.unit || 'L',
        description: ''
      });
    }
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const formValue = this.productForm.value;
      const productData: Product = {
        name: formValue.name,
        type: formValue.type,
        quantity: formValue.quantity,
        pricePerUnit: formValue.pricePerUnit,
        unit: formValue.unit,
        description: formValue.description
      };

      if (this.isEditMode && this.data.product?.id) {
        this.productService.updateProduct(this.data.product.id, productData).subscribe({
          next: () => {
            this.snackBar.open('Product updated successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            console.error('Error updating product:', error);
            this.snackBar.open('Error updating product', 'Close', { duration: 3000 });
          }
        });
      } else {
        this.productService.createProduct(productData).subscribe({
          next: () => {
            this.snackBar.open('Product created successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            console.error('Error creating product:', error);
            this.snackBar.open('Error creating product', 'Close', { duration: 3000 });
          }
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
