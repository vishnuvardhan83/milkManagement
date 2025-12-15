# API Documentation - Milk Management System

Base URL: `http://localhost:8080/api`

## Authentication

All endpoints except `/api/auth/**` require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <token>
```

---

## Auth Endpoints

### POST /api/auth/signin
Login and get JWT token.

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@milk.com",
  "roles": ["ROLE_ADMIN"]
}
```

### POST /api/auth/signup
Register a new user.

**Query Parameters:**
- `username` (required)
- `email` (required)
- `password` (required)
- `role` (optional, default: "ROLE_CUSTOMER")

---

## Customer Endpoints

### GET /api/customers
Get all customers. Requires ADMIN or MANAGER role.

**Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "address": "123 Main St",
    "mobileNumber": "9876543210",
    "email": "john@example.com",
    "dailyMilkQuantity": 5.0,
    "milkType": "COW",
    "deliveryStatus": "ACTIVE"
  }
]
```

### GET /api/customers/active
Get all active customers.

### GET /api/customers/{id}
Get customer by ID.

### POST /api/customers
Create a new customer.

**Request Body:**
```json
{
  "name": "John Doe",
  "address": "123 Main St",
  "mobileNumber": "9876543210",
  "email": "john@example.com",
  "dailyMilkQuantity": 5.0,
  "milkType": "COW",
  "deliveryStatus": "ACTIVE"
}
```

### PUT /api/customers/{id}
Update customer.

### DELETE /api/customers/{id}
Delete customer.

---

## Delivery Endpoints

### GET /api/deliveries
Get all deliveries. Requires ADMIN or MANAGER role.

**Response:**
```json
[
  {
    "id": 1,
    "customerId": 1,
    "productId": 1,
    "deliveryDate": "2024-01-15",
    "quantityDelivered": 5.0,
    "pricePerUnit": 50.00,
    "totalAmount": 250.00,
    "notes": "Regular delivery"
  }
]
```

### GET /api/deliveries/customer/{customerId}
Get deliveries for a specific customer.

### GET /api/deliveries/date/{date}
Get deliveries for a specific date (format: YYYY-MM-DD).

### GET /api/deliveries/customer/{customerId}/range?startDate={startDate}&endDate={endDate}
Get deliveries for a customer within a date range.

### POST /api/deliveries
Create a new delivery.

**Request Body:**
```json
{
  "customerId": 1,
  "productId": 1,
  "deliveryDate": "2024-01-15",
  "quantityDelivered": 5.0,
  "pricePerUnit": 50.00,
  "notes": "Regular delivery"
}
```

---

## Dashboard Endpoints

### GET /api/dashboard/stats
Get dashboard statistics. Requires ADMIN or MANAGER role.

**Response:**
```json
{
  "totalCustomers": 50,
  "activeCustomers": 45,
  "totalMilkDeliveredToday": 250.5,
  "totalRevenueToday": 12525.00,
  "totalRevenueThisMonth": 375750.00,
  "pendingPayments": 15000.00,
  "pendingInvoices": 12
}
```

---

## Error Responses

All endpoints may return the following error responses:

**400 Bad Request:**
```json
{
  "error": "Validation failed",
  "fieldName": "Error message"
}
```

**401 Unauthorized:**
```json
{
  "error": "Unauthorized"
}
```

**404 Not Found:**
```json
{
  "error": "Resource not found"
}
```

**500 Internal Server Error:**
```json
{
  "error": "An unexpected error occurred",
  "message": "Error details"
}
```

---

## Default Credentials

- **Admin**: username: `admin`, password: `admin123`
- **Manager**: username: `manager`, password: `manager123`

---

## Notes

- All dates should be in ISO format (YYYY-MM-DD)
- All monetary values are in decimal format
- JWT tokens expire after 24 hours (86400000 ms)
- CORS is enabled for `http://localhost:4200`
