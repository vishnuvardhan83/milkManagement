import { Component, OnInit } from '@angular/core';
import { ProductService, Product } from '../../services/product.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../services/auth.service';
import { PaymentFormComponent } from '../payment/payment-form/payment-form.component';
import { OrderService } from '../../services/order.service';

export interface CartItem {
  product: Product;
  quantity: number;
}

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  products: Product[] = [];
  loading = false;
  isCustomer = false;

  constructor(
    private productService: ProductService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private authService: AuthService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.isCustomer = this.authService.hasRole('CUSTOMER') || this.authService.hasRole('ROLE_CUSTOMER');
    this.loadProducts();
    this.loadCart();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getAllProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.snackBar.open('Error loading products', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadCart(): void {
    const cartData = localStorage.getItem('cart');
    if (cartData) {
      this.cartItems = JSON.parse(cartData);
    }
  }

  saveCart(): void {
    localStorage.setItem('cart', JSON.stringify(this.cartItems));
  }

  addToCart(product: Product): void {
    const existingItem = this.cartItems.find(item => item.product.id === product.id);
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      this.cartItems.push({ product, quantity: 1 });
    }
    this.saveCart();
    this.snackBar.open(`${product.name} added to cart`, 'Close', { duration: 2000 });
  }

  removeFromCart(item: CartItem): void {
    const index = this.cartItems.indexOf(item);
    if (index > -1) {
      this.cartItems.splice(index, 1);
      this.saveCart();
      this.snackBar.open('Item removed from cart', 'Close', { duration: 2000 });
    }
  }

  updateQuantity(item: CartItem, change: number): void {
    item.quantity += change;
    if (item.quantity <= 0) {
      this.removeFromCart(item);
    } else {
      this.saveCart();
    }
  }

  getTotalPrice(): number {
    return this.cartItems.reduce((total, item) => {
      return total + (item.product.pricePerUnit * item.quantity);
    }, 0);
  }

  getTotalItems(): number {
    return this.cartItems.reduce((total, item) => total + item.quantity, 0);
  }

  clearCart(): void {
    this.cartItems = [];
    this.saveCart();
    this.snackBar.open('Cart cleared', 'Close', { duration: 2000 });
  }

  checkout(): void {
    if (this.cartItems.length === 0) {
      this.snackBar.open('Cart is empty', 'Close', { duration: 2000 });
      return;
    }

    // Open payment form dialog
    const dialogRef = this.dialog.open(PaymentFormComponent, {
      width: '500px',
      data: {
        totalAmount: this.getTotalPrice(),
        cartItems: this.cartItems
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.success) {
        // Create order
        this.createOrder(result.paymentData);
      }
    });
  }

  createOrder(paymentData: any): void {
    this.loading = true;
    const user = this.authService.getCurrentUser();
    const orderData = {
      customerId: paymentData.customerId || null,
      items: this.cartItems
        .filter(item => item.product.id != null)
        .map(item => ({
          productId: item.product.id!,
          quantity: item.quantity,
          price: item.product.pricePerUnit
        })),
      totalAmount: this.getTotalPrice(),
      paymentData: paymentData
    };

    this.orderService.createOrder(orderData).subscribe({
      next: (order) => {
        this.snackBar.open('Order placed successfully!', 'Close', { duration: 3000 });
        this.clearCart();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error creating order:', error);
        this.snackBar.open('Error placing order: ' + (error.error?.message || error.message), 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }
}
