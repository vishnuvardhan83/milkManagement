import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-payment-form',
  templateUrl: './payment-form.component.html',
  styleUrls: ['./payment-form.component.scss']
})
export class PaymentFormComponent implements OnInit {
  paymentForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PaymentFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private snackBar: MatSnackBar
  ) {
    this.paymentForm = this.fb.group({
      name: ['', [Validators.required]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      pendingAmount: [this.data?.totalAmount || 0, [Validators.required, Validators.min(0)]],
      paymentDate: [new Date(), [Validators.required]],
      paymentMethod: ['CASH', [Validators.required]],
      notes: ['']
    });
  }

  ngOnInit(): void {
    if (this.data?.totalAmount) {
      this.paymentForm.patchValue({
        pendingAmount: this.data.totalAmount
      });
    }
  }

  onSubmit(): void {
    if (this.paymentForm.valid) {
      const paymentData = {
        ...this.paymentForm.value,
        totalAmount: this.data?.totalAmount || 0
      };
      this.dialogRef.close({ success: true, paymentData });
    } else {
      this.snackBar.open('Please fill all required fields correctly', 'Close', { duration: 3000 });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(controlName: string): string {
    const control = this.paymentForm.get(controlName);
    if (control?.hasError('required')) {
      return `${controlName} is required`;
    }
    if (control?.hasError('pattern')) {
      return 'Invalid phone number (10 digits required)';
    }
    if (control?.hasError('min')) {
      return 'Amount must be positive';
    }
    return '';
  }
}
