# Dairy Farm ERP - Complete System Documentation

## 🧀 Project Overview

Complete production-ready Dairy Farm ERP system for managing cows & buffaloes, daily milk production, inventory, customer sales, billing, notifications, and profit-loss analytics.

## 🏗️ System Architecture

- **Backend**: Spring Boot 3.x (Java 17) + MySQL + JWT Authentication
- **Frontend**: Angular 18 Standalone Components + Angular Material + Tailwind CSS
- **Database**: MySQL 8.0+
- **Authentication**: JWT with refresh tokens
- **Deployment**: Docker + Docker Compose

## 📋 Prerequisites

- Java 17 or higher
- Node.js 18+ and npm
- MySQL 8.0+
- Maven 3.8+
- Docker & Docker Compose (optional)

## 🚀 Quick Start

### 1. Database Setup

```bash
cd database
mysql -u root -p < schema.sql
mysql -u root -p < seed.sql
```

### 2. Backend Setup

```bash
cd backend
# Update application.properties with your MySQL credentials
mvn clean install
mvn spring-boot:run
```

Backend will run on: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Frontend Setup

```bash
cd frontend
npm install
ng serve
```

Frontend will run on: `http://localhost:4200`

### 4. Docker Deployment (Optional)

```bash
cd backend
docker-compose up -d
```

## 🔐 Default Credentials

- **Admin**: username: `admin`, password: `admin123`
- **Manager**: username: `manager`, password: `admin123`
- **Worker**: username: `worker1`, password: `admin123`

## 📚 API Documentation

All APIs are documented via Swagger UI at `/swagger-ui.html`

### Authentication Endpoints
- `POST /api/auth/signin` - Login
- `POST /api/auth/signup` - Register

### Core Modules
- Animals: `/api/animals`
- Milk Entries: `/api/milk-entries`
- Customers: `/api/customers`
- Receipts: `/api/receipts`
- Payments: `/api/payments`
- Inventory: `/api/inventory`
- Expenses: `/api/expenses`
- Salaries: `/api/salaries`
- Notifications: `/api/notifications`
- Dashboard: `/api/dashboard/stats`
- Profit/Loss: `/api/dashboard/profit-loss`

## 🧪 Testing APIs

See `CURL_COMMANDS_COMPLETE.md` for comprehensive API testing commands.

## 📦 Project Structure

```
milkManagement/
├── backend/
│   ├── src/main/java/com/dairyfarm/
│   │   ├── controller/     # REST Controllers
│   │   ├── service/        # Business Logic
│   │   ├── repository/     # Data Access
│   │   ├── entity/         # JPA Entities
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── security/       # JWT & Security
│   │   ├── util/           # Utilities (PDF, JWT)
│   │   └── exception/      # Exception Handlers
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── Dockerfile
│   └── docker-compose.yml
├── frontend/
│   ├── src/app/
│   │   ├── components/     # Angular Components
│   │   ├── services/       # API Services
│   │   ├── guards/         # Route Guards
│   │   └── interceptors/   # HTTP Interceptors
│   └── package.json
└── database/
    ├── schema.sql
    └── seed.sql
```

## 🔧 Configuration

### Backend Configuration (`application.properties`)

Update these values:
- MySQL connection details
- JWT secret key
- Email SMTP settings (for notifications)
- Twilio credentials (for SMS)

### Frontend Configuration (`environment.ts`)

Update API base URL:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## 📊 Features

### ✅ Implemented Features

1. **Authentication & Authorization**
   - JWT-based authentication
   - Role-based access control (Admin, Manager, Worker, Customer)
   - Refresh token support

2. **Animal Management**
   - CRUD operations for cows & buffaloes
   - Track health status, breed, purchase details

3. **Milk Entry Management**
   - Daily morning/evening milk collection
   - Auto-calculation of total quantity
   - Quality tracking

4. **Customer Management**
   - Customer registration & profile
   - Track pending balances
   - Delivery status management

5. **Receipt & Billing**
   - Generate receipts for milk sales
   - Track payment status (Pending/Partial/Paid)
   - PDF receipt download
   - Auto-update customer balance

6. **Inventory Management**
   - Track fodder, medicines, packaging, cans
   - Low stock alerts
   - Usage history tracking
   - Cost calculation

7. **Expense & Salary Management**
   - Record daily expenses
   - Employee salary management
   - Payment tracking

8. **Notifications**
   - Email notifications (SMTP)
   - SMS notifications (Twilio)
   - Payment reminders
   - Low stock alerts

9. **Dashboard & Analytics**
   - Real-time dashboard metrics
   - Profit/Loss calculation
   - Sales vs Expense graphs
   - Milk production charts

10. **Audit Logging**
    - Track all user actions
    - Module-wise activity logs

## 🐳 Docker Commands

```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild after code changes
docker-compose up -d --build
```

## 📝 Development Notes

- Backend uses Spring Boot 3.x with Java 17
- Frontend uses Angular 18 standalone components
- All APIs follow RESTful conventions
- CORS is enabled for frontend communication
- Global exception handler for error management
- Swagger/OpenAPI for API documentation

## 🧪 Testing

### Backend Testing
```bash
cd backend
mvn test
```

### Frontend Testing
```bash
cd frontend
ng test
```

## 📄 License

This is a production-ready system for dairy farm management.

## 🆘 Support

For issues or questions, refer to:
- API Documentation: `/swagger-ui.html`
- CURL Commands: `CURL_COMMANDS_COMPLETE.md`
- Database Schema: `database/schema.sql`
