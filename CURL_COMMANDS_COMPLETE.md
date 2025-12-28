# Complete CURL Commands for Dairy Farm ERP APIs

## Base URL
```
http://localhost:8080/api
```

## Authentication

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. Signup
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "role": "ROLE_CUSTOMER",
    "fullName": "New User",
    "phone": "1234567890"
  }'
```

## Animals

### 3. Get All Animals
```bash
curl -X GET http://localhost:8080/api/animals \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Get Animal by ID
```bash
curl -X GET http://localhost:8080/api/animals/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Create Animal
```bash
curl -X POST http://localhost:8080/api/animals \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tagNumber": "COW003",
    "name": "Radha",
    "animalType": "COW",
    "breed": "HOLSTEIN",
    "dateOfBirth": "2021-01-15",
    "purchaseDate": "2021-01-20",
    "purchasePrice": 48000.00,
    "status": "ACTIVE",
    "healthStatus": "Healthy"
  }'
```

### 6. Update Animal
```bash
curl -X PUT http://localhost:8080/api/animals/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tagNumber": "COW001",
    "name": "Lakshmi Updated",
    "animalType": "COW",
    "status": "ACTIVE"
  }'
```

### 7. Delete Animal
```bash
curl -X DELETE http://localhost:8080/api/animals/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Milk Entries

### 8. Get All Milk Entries
```bash
curl -X GET http://localhost:8080/api/milk-entries \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 9. Get Milk Entries by Date
```bash
curl -X GET "http://localhost:8080/api/milk-entries?date=2024-01-15" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 10. Create Milk Entry
```bash
curl -X POST http://localhost:8080/api/milk-entries \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "animalId": 1,
    "entryDate": "2024-01-15",
    "morningQuantity": 8.50,
    "eveningQuantity": 7.20,
    "qualityGrade": "A",
    "temperature": 4.5
  }'
```

### 11. Update Milk Entry
```bash
curl -X PUT http://localhost:8080/api/milk-entries/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "morningQuantity": 9.00,
    "eveningQuantity": 7.50
  }'
```

## Customers

### 12. Get All Customers
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 13. Create Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "address": "123 Street, City",
    "mobileNumber": "9876543213",
    "email": "john@example.com",
    "dailyMilkQuantity": 6.00,
    "milkType": "COW",
    "deliveryStatus": "ACTIVE"
  }'
```

### 14. Get Customers with Pending Amounts
```bash
curl -X GET http://localhost:8080/api/customers/pending-amounts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Receipts

### 15. Get All Receipts
```bash
curl -X GET http://localhost:8080/api/receipts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 16. Get Receipts by Customer
```bash
curl -X GET http://localhost:8080/api/receipts/customer/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 17. Create Receipt
```bash
curl -X POST http://localhost:8080/api/receipts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "receiptDate": "2024-01-15",
    "quantityLiters": 5.00,
    "milkRate": 60.00,
    "paymentMethod": "CASH"
  }'
```

### 18. Download Receipt PDF
```bash
curl -X GET http://localhost:8080/api/receipts/1/download \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output receipt.pdf
```

### 19. Record Payment on Receipt
```bash
curl -X POST "http://localhost:8080/api/receipts/1/payment?amount=150.00&paymentMethod=CASH" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Inventory

### 20. Get All Inventory Items
```bash
curl -X GET http://localhost:8080/api/inventory \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 21. Get Low Stock Items
```bash
curl -X GET http://localhost:8080/api/inventory/low-stock \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 22. Create Inventory Item
```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Vitamin Supplements",
    "category": "SUPPLEMENTS",
    "quantity": 100.00,
    "unit": "packets",
    "costPerUnit": 50.00,
    "supplierName": "Supplier Co.",
    "purchaseDate": "2024-01-10",
    "lowStockThreshold": 20.00
  }'
```

### 23. Use Inventory Item
```bash
curl -X POST http://localhost:8080/api/inventory/1/use \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usageDate": "2024-01-15",
    "quantityUsed": 10.00,
    "purpose": "Daily feeding"
  }'
```

### 24. Get Usage History
```bash
curl -X GET http://localhost:8080/api/inventory/1/usage-history \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Expenses

### 25. Get All Expenses
```bash
curl -X GET http://localhost:8080/api/expenses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 26. Create Expense
```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "expenseDate": "2024-01-15",
    "category": "FEED",
    "description": "Cattle feed purchase",
    "amount": 5000.00,
    "paymentMethod": "CASH"
  }'
```

## Salaries

### 27. Get All Salaries
```bash
curl -X GET http://localhost:8080/api/salaries \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 28. Create Salary
```bash
curl -X POST http://localhost:8080/api/salaries \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 3,
    "salaryMonth": "2024-01-01",
    "baseSalary": 15000.00,
    "bonus": 2000.00,
    "deductions": 500.00,
    "paymentStatus": "PAID",
    "paymentDate": "2024-01-05"
  }'
```

## Payments

### 29. Get All Payments
```bash
curl -X GET http://localhost:8080/api/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 30. Create Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "receiptId": 1,
    "paymentDate": "2024-01-15",
    "amount": 300.00,
    "paymentMethod": "CASH",
    "referenceNumber": "PAY001"
  }'
```

## Notifications

### 31. Send Email
```bash
curl -X POST "http://localhost:8080/api/notifications/send-email?customerId=1&subject=Payment%20Reminder&message=Please%20clear%20your%20dues" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 32. Send SMS
```bash
curl -X POST "http://localhost:8080/api/notifications/send-sms?customerId=1&message=Payment%20reminder" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 33. Send Payment Reminder
```bash
curl -X POST http://localhost:8080/api/notifications/payment-reminder/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 34. Get Pending Notifications
```bash
curl -X GET http://localhost:8080/api/notifications/pending \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Dashboard

### 35. Get Dashboard Stats
```bash
curl -X GET http://localhost:8080/api/dashboard/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 36. Get Profit/Loss Report
```bash
curl -X GET "http://localhost:8080/api/dashboard/profit-loss?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Notes

1. Replace `YOUR_JWT_TOKEN` with the actual JWT token received from login
2. All dates should be in `YYYY-MM-DD` format
3. All monetary values are in decimal format (e.g., 60.00)
4. For file downloads (PDF), use `--output` flag to save the file
