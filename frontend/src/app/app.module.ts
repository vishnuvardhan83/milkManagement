import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

// Angular Material
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatCheckboxModule } from '@angular/material/checkbox';

// Charts
import { NgChartsModule } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthComponent } from './components/auth/auth/auth.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { CustomerListComponent } from './components/customer/customer-list/customer-list.component';
import { CustomerFormComponent } from './components/customer/customer-form/customer-form.component';
import { DeliveryListComponent } from './components/delivery/delivery-list/delivery-list.component';
import { DeliveryFormComponent } from './components/delivery/delivery-form/delivery-form.component';
import { ProductListComponent } from './components/product/product-list/product-list.component';
import { ProductFormComponent } from './components/product/product-form/product-form.component';
import { PaymentListComponent } from './components/payment/payment-list/payment-list.component';
import { UserListComponent } from './components/user/user-list/user-list.component';
import { UserFormComponent } from './components/user/user-form/user-form.component';
import { RoleAssignmentComponent } from './components/user/role-assignment/role-assignment.component';
import { CartComponent } from './components/cart/cart.component';
import { OrderHistoryComponent } from './components/order/order-history/order-history.component';
import { PaymentFormComponent } from './components/payment/payment-form/payment-form.component';
import { InventoryComponent } from './components/inventory/inventory.component';
import { ConfirmDialogComponent } from './components/shared/confirm-dialog.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { AuthService } from './services/auth.service';
import { CustomerService } from './services/customer.service';
import { DeliveryService } from './services/delivery.service';
import { DashboardService } from './services/dashboard.service';
import { ProductService } from './services/product.service';
import { PaymentService } from './services/payment.service';
import { UserService } from './services/user.service';
import { OrderService } from './services/order.service';
import { InventoryService } from './services/inventory.service';

@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    DashboardComponent,
    CustomerListComponent,
    CustomerFormComponent,
    DeliveryListComponent,
    DeliveryFormComponent,
    ProductListComponent,
    ProductFormComponent,
    PaymentListComponent,
    UserListComponent,
    UserFormComponent,
    RoleAssignmentComponent,
    CartComponent,
    OrderHistoryComponent,
    PaymentFormComponent,
    InventoryComponent,
    ConfirmDialogComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatTableModule,
    MatIconModule,
    MatMenuModule,
    MatSidenavModule,
    MatListModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatChipsModule,
    MatCheckboxModule,
    NgChartsModule
  ],
  providers: [
    AuthService,
    CustomerService,
    DeliveryService,
    DashboardService,
    ProductService,
    PaymentService,
    UserService,
    OrderService,
    InventoryService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
