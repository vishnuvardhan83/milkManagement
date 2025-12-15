import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DeliveryService, MilkDelivery } from '../../../services/delivery.service';
import { Customer } from '../../../services/customer.service';
import { ProductService } from '../../../services/product.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-delivery-form',
  templateUrl: './delivery-form.component.html',
  styleUrls: ['./delivery-form.component.scss']
})
export class DeliveryFormComponent implements OnInit {
  deliveryForm: FormGroup;
  customers: Customer[] = [];

  constructor(
    private fb: FormBuilder,
    private deliveryService: DeliveryService,
    private productService: ProductService,
    private dialogRef: MatDialogRef<DeliveryFormComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { customers: Customer[] }
  ) {
    this.customers = data?.customers || [];
    
    this.deliveryForm = this.fb.group({
      customerId: ['', Validators.required],
      productId: [1, Validators.required], // Default to Milk (product ID 1)
      deliveryDate: [new Date(), Validators.required],
      quantityDelivered: [0, [Validators.required, Validators.min(0.01)]],
      pricePerUnit: [50], // Default price
      notes: ['']
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.deliveryForm.valid) {
      const deliveryData: MilkDelivery = {
        ...this.deliveryForm.value,
        deliveryDate: this.deliveryForm.value.deliveryDate.toISOString().split('T')[0]
      };
      
      this.deliveryService.createDelivery(deliveryData).subscribe({
        next: () => {
          this.snackBar.open('Delivery created successfully', 'Close', { duration: 3000 });
          // Refresh product quantities after delivery
          this.productService.loadProductQuantities();
          this.dialogRef.close(true);
        },
        error: () => {
          this.snackBar.open('Error creating delivery', 'Close', { duration: 3000 });
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
