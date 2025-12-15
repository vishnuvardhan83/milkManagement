# Milk Selling & Daily Delivery Management System

A complete full-stack application for managing daily milk delivery business built with Angular (Frontend), Spring Boot (Backend), and MySQL (Database).

## 🏗️ Project Structure

```
milkManagement/
├── backend/          # Spring Boot application
├── frontend/         # Angular application
└── database/         # MySQL schema scripts
```

## 🚀 Tech Stack

- **Frontend**: Angular 16+, Angular Material, Chart.js
- **Backend**: Java 17+, Spring Boot, Spring Security, JWT
- **Database**: MySQL 8.0+
- **ORM**: Spring Data JPA

## 📋 Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (ADMIN/MANAGER, CUSTOMER)
- Secure password hashing with BCrypt

### Admin/Manager Dashboard
- Total customers overview
- Daily/monthly milk delivery tracking
- Revenue analytics
- Pending payments monitoring
- Dynamic milk price management
- Customer CRUD operations

### Customer Management
- Customer registration with address and contact details
- Daily milk quantity assignment
- Milk type selection (cow/buffalo)
- Pause/resume delivery functionality

### Milk Delivery Module
- Daily delivery entry and tracking
- Automatic cost calculation
- Stock management
- Monthly delivery summaries

### Billing & Payment System
- Auto-generated daily/monthly bills
- Payment tracking
- Invoice generation (PDF)
- Outstanding balance management

### Product & Stock Management
- Multiple product support (Milk, Curd, Butter)
- Stock quantity tracking
- Auto stock deduction on delivery

## 🛠️ Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 18+ and npm
- MySQL 8.0+
- Maven 3.6+

### Backend Setup
1. Navigate to `backend` directory
2. Update `application.properties` with your MySQL credentials
3. Run the database schema script from `database/schema.sql`
4. Build and run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Frontend Setup
1. Navigate to `frontend` directory
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start development server:
   ```bash
   ng serve
   ```

### Database Setup
1. Create MySQL database:
   ```sql
   CREATE DATABASE milk_management;
   ```
2. Run the schema script from `database/schema.sql`

## 📡 API Endpoints

See `API_DOCUMENTATION.md` for complete API documentation.

## 👥 Default Users

- **Admin**: admin@milk.com / admin123
- **Manager**: manager@milk.com / manager123

## 📝 License

This project is for educational and commercial use.
# milkManagement
