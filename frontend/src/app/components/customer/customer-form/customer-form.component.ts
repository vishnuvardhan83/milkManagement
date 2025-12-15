import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CustomerService, Customer } from '../../../services/customer.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-customer-form',
  templateUrl: './customer-form.component.html',
  styleUrls: ['./customer-form.component.scss']
})
export class CustomerFormComponent implements OnInit {
  customerForm: FormGroup;
  isEditMode = false;

  milkTypes = ['COW', 'BUFFALO', 'BOTH'];
  deliveryStatuses = ['ACTIVE', 'PAUSED', 'INACTIVE'];

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private dialogRef: MatDialogRef<CustomerFormComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: Customer | null
  ) {
    this.customerForm = this.fb.group({
      name: ['', Validators.required],
      address: [''],
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      email: ['', Validators.email],
      dailyMilkQuantity: [0, [Validators.required, Validators.min(0)]],
      milkType: ['COW'],
      deliveryStatus: ['ACTIVE']
    });

    if (data) {
      this.isEditMode = true;
      this.customerForm.patchValue(data);
    }
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.customerForm.valid) {
      const customerData = this.customerForm.value;
      
      if (this.isEditMode && this.data?.id) {
        this.customerService.updateCustomer(this.data.id, customerData).subscribe({
          next: () => {
            this.snackBar.open('Customer updated successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: () => {
            this.snackBar.open('Error updating customer', 'Close', { duration: 3000 });
          }
        });
      } else {
        this.customerService.createCustomer(customerData).subscribe({
          next: () => {
            this.snackBar.open('Customer created successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: () => {
            this.snackBar.open('Error creating customer', 'Close', { duration: 3000 });
          }
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
