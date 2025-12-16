import { Component, OnInit } from '@angular/core';
import { OrderService, Order } from '../../../services/order.service';
import { AuthService } from '../../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-order-history',
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.scss']
})
export class OrderHistoryComponent implements OnInit {
  orders: Order[] = [];
  loading = false;
  currentUserId: number | null = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    const user = this.authService.getCurrentUser();
    if (user && user.id) {
      this.currentUserId = user.id;
      this.orderService.getUserOrders(user.id).subscribe({
        next: (orders) => {
          this.orders = orders;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading user orders:', error);
          this.orderService.getOrders().subscribe({
            next: (allOrders) => {
              this.orders = allOrders.filter(order => order.userId === user.id);
              this.loading = false;
            },
            error: (err) => {
              console.error('Error loading all orders:', err);
              this.snackBar.open('Error loading orders', 'Close', { duration: 3000 });
              this.loading = false;
            }
          });
        }
      });
    } else {
      // Load all orders if admin/manager
      this.orderService.getOrders().subscribe({
        next: (orders) => {
          this.orders = orders;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading orders:', error);
          this.snackBar.open('Error loading orders', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  getTotalItems(order: Order): number {
    return order.items.reduce((total, item) => total + item.quantity, 0);
  }

  getStatusColor(status: string | undefined): string {
    switch (status?.toUpperCase()) {
      case 'COMPLETED':
      case 'PAID':
        return 'green';
      case 'PENDING':
        return 'orange';
      case 'CANCELLED':
        return 'red';
      default:
        return 'gray';
    }
  }
}
