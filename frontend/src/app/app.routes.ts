import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'signup',
    loadComponent: () => import('./components/auth/signup/signup.component').then(m => m.SignupComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'animals',
    loadComponent: () => import('./components/animals/animal-list/animal-list.component').then(m => m.AnimalListComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_WORKER'] }
  },
  {
    path: 'milk-entries',
    loadComponent: () => import('./components/milk-entry/milk-entry.component').then(m => m.MilkEntryComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_WORKER'] }
  },
  {
    path: 'customers',
    loadComponent: () => import('./components/customer/customer-list/customer-list.component').then(m => m.CustomerListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'inventory',
    loadComponent: () => import('./components/inventory/inventory.component').then(m => m.InventoryComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_WORKER'] }
  },
  {
    path: 'expenses',
    loadComponent: () => import('./components/expenses/expenses.component').then(m => m.ExpensesComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
  },
  {
    path: 'salaries',
    loadComponent: () => import('./components/salaries/salaries.component').then(m => m.SalariesComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
  },
  {
    path: 'reports',
    loadComponent: () => import('./components/reports/reports.component').then(m => m.ReportsComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
  },
  {
    path: 'notifications',
    loadComponent: () => import('./components/notifications/notifications.component').then(m => m.NotificationsComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];

