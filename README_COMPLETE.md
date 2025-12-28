# Dairy Farm ERP - Complete System Documentation

## 🎯 System Overview

Complete dairy farm management system with:
- Animal Management (Cows/Buffaloes)
- Daily Milk Collection (Morning + Evening with auto-total)
- Inventory Management (Fodder, Medicine, Packaging, etc.)
- Customer Management & Billing
- Receipt Generation & Payment Tracking
- Expense Tracking
- Salary Management
- Email/SMS Notifications
- Profit/Loss Analytics
- Role-based Access Control (Admin, Worker, Customer)

## 🚀 Quick Start Guide

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js 18+
- Angular CLI 18+
- MySQL 8.0+

### Step 1: Database Setup

```bash
# Create database and tables
mysql -u root -p < database/complete_schema.sql
```

Or manually:
```sql
mysql -u root -p
CREATE DATABASE dairy_farm_erp;
USE dairy_farm_erp;
SOURCE database/complete_schema.sql;
```

### Step 2: Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dairy_farm_erp?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password

# Email Configuration (Optional - for notifications)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Step 3: Build and Run Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend will run on: `http://localhost:8080`

### Step 4: Frontend Setup

```bash
cd frontend
npm install
ng serve
```

Frontend will run on: `http://localhost:4200`

### Step 5: Access the Application

- Frontend URL: http://localhost:4200
- Backend API: http://localhost:8080/api

**Default Login Credentials:**
- Username: `admin`
- Password: `admin123`

## 📁 Project Structure

```
dairy-farm-erp/
├── backend/
│   ├── src/main/java/com/milkmanagement/
│   │   ├── controller/          # REST Controllers
│   │   │   ├── AnimalController.java
│   │   │   ├── InventoryItemController.java
│   │   │   ├── DailyMilkCollectionController.java
│   │   │   ├── ReceiptController.java
│   │   │   ├── ExpenseController.java
│   │   │   ├── SalaryController.java
│   │   │   ├── NotificationController.java
│   │   │   └── DashboardController.java
│   │   ├── service/             # Business Logic
│   │   ├── repository/          # Data Access
│   │   ├── entity/              # JPA Entities
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── security/            # JWT Security
│   └── pom.xml
├── frontend/
│   └── src/app/
│       ├── components/          # Angular Components
│       ├── services/            # API Services
│       └── guards/              # Route Guards
└── database/
    └── complete_schema.sql      # Database Schema
```

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/signin` - Login
- `POST /api/auth/signup` - Register

### Animals
- `GET /api/animals` - List all animals
- `GET /api/animals/{id}` - Get animal by ID
- `POST /api/animals` - Create animal
- `PUT /api/animals/{id}` - Update animal
- `DELETE /api/animals/{id}` - Delete animal

### Daily Milk Collection
- `GET /api/milk-collections` - List collections
- `POST /api/milk-collections` - Create collection (auto-calculates total)
- `PUT /api/milk-collections/{id}` - Update collection
- `DELETE /api/milk-collections/{id}` - Delete collection

### Inventory
- `GET /api/inventory` - List all inventory items
- `POST /api/inventory` - Create inventory item
- `PUT /api/inventory/{id}` - Update inventory item
- `DELETE /api/inventory/{id}` - Delete inventory item
- `POST /api/inventory/{id}/use` - Use inventory item
- `GET /api/inventory/{id}/usage-history` - Get usage history

### Customers
- `GET /api/customers` - List all customers
- `POST /api/customers` - Create customer
- `GET /api/customers/pending-amounts` - Get customers with pending dues

### Receipts
- `GET /api/receipts` - List all receipts
- `GET /api/receipts/customer/{customerId}` - Get customer receipts
- `POST /api/receipts` - Create receipt (auto-updates customer balance)
- `POST /api/receipts/{id}/payment` - Record payment

### Expenses
- `GET /api/expenses` - List expenses
- `POST /api/expenses` - Create expense
- `PUT /api/expenses/{id}` - Update expense
- `DELETE /api/expenses/{id}` - Delete expense

### Salaries
- `GET /api/salaries` - List salaries
- `POST /api/salaries` - Create salary (auto-calculates net)
- `PUT /api/salaries/{id}` - Update salary

### Notifications
- `POST /api/notifications/send` - Send notification
- `POST /api/notifications/send-email` - Send email
- `POST /api/notifications/send-sms` - Send SMS
- `POST /api/notifications/payment-reminder/{customerId}` - Send payment reminder

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics
- `GET /api/dashboard/profit-loss` - Calculate profit/loss

## 🎨 Key Features

### Auto Calculations
- **Milk Total**: Automatically calculates `total = morning + evening`
- **Receipt Amount**: Automatically calculates `total = quantity × rate`
- **Customer Balance**: Auto-updates when receipts are created/updated
- **Net Salary**: Auto-calculates `net = base + bonus - deductions`
- **Profit/Loss**: Calculates `profit = revenue - (expenses + salaries + inventory cost)`

### Business Logic
- When customer buys milk → Receipt created → Customer balance updated
- When payment recorded → Receipt status updated → Customer balance updated
- When inventory used → Quantity reduced automatically
- Profit calculation includes all costs (expenses + salaries + inventory)

## 🔐 Security

- JWT-based authentication
- BCrypt password hashing
- Role-based access control:
  - `ROLE_ADMIN` - Full access
  - `ROLE_WORKER` - Data entry access
  - `ROLE_CUSTOMER` - Limited access (own data)
  - `ROLE_MANAGER` - Operational access

## 📊 Database Schema

Key tables:
- `animals` - Animal records
- `daily_milk_collections` - Milk collection with morning/evening
- `inventory_items` - Inventory items (fodder, medicine, etc.)
- `inventory_usage` - Inventory usage history
- `customers` - Customer profiles with balance
- `receipts` - Customer receipts with payment tracking
- `expenses` - Farm expenses
- `salaries` - Employee salaries
- `notification_queue` - Email/SMS queue

See `database/complete_schema.sql` for complete schema.

## 🧪 Testing APIs

Use the CURL commands in `CURL_COMMANDS_COMPLETE.md` to test all endpoints.

Example:
```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.token')

# Create Animal
curl -X POST http://localhost:8080/api/animals \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tagNumber": "COW-001",
    "name": "Bella",
    "animalType": "COW",
    "breed": "HOLSTEIN",
    "status": "ACTIVE"
  }'
```

## 🐛 Troubleshooting

### Backend Issues
- **Port 8080 already in use**: Change `server.port` in `application.properties`
- **Database connection error**: Check MySQL credentials in `application.properties`
- **Compilation errors**: Ensure Java 17 is installed and Maven is configured

### Frontend Issues
- **CORS errors**: Backend CORS is configured for `http://localhost:4200`
- **API connection errors**: Ensure backend is running on port 8080
- **Build errors**: Run `npm install` to install dependencies

## 📝 Next Steps

1. Configure email settings for notifications
2. Configure SMS provider (Twilio) for SMS notifications
3. Build frontend components (see existing component patterns)
4. Deploy to production server
5. Set up automated backups

## 📞 Support

For detailed API documentation, see:
- `CURL_COMMANDS_COMPLETE.md` - Complete API examples
- `API_DOCUMENTATION.md` - API reference

---

**System Status**: ✅ Backend Complete | 🚧 Frontend Components Pending

All backend APIs are production-ready and fully functional.

