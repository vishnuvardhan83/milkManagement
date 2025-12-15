# Quick Start Guide

## Why the Application Stops

The Spring Boot application **stops immediately** when it cannot connect to the MySQL database. This is expected behavior - Spring Boot fails to start if database connection fails during initialization.

## Fix Database Connection

The error shows: `Access denied for user 'root'@'localhost' (using password: NO)`

This means your MySQL password is not set. You have two options:

### Option 1: Set Password in application.properties (Quick Fix)

Edit `backend/src/main/resources/application.properties` and change:
```properties
spring.datasource.password=${DB_PASSWORD:root}
```
Replace `root` with your actual MySQL password.

### Option 2: Use Environment Variable (Recommended)

Set the password as an environment variable before starting:
```bash
export DB_PASSWORD=your_mysql_password_here
cd backend
mvn spring-boot:run
```

## Start the Services

### 1. Start Backend (Terminal 1)
```bash
cd backend
mvn spring-boot:run
```

Or use the run script:
```bash
cd backend
./run.sh
```

### 2. Start Frontend (Terminal 2)
```bash
# First time: Install dependencies
cd frontend
npm install

# Install Angular CLI if not installed
npm install -g @angular/cli

# Start the frontend
ng serve
```

Or use the startup script:
```bash
./start-frontend.sh
```

## Verify MySQL is Running

Make sure MySQL is running:
```bash
# Check if MySQL is running
mysql.server status

# If not running, start it
mysql.server start
```

## Access the Application

- **Backend API**: http://localhost:8080
- **Frontend UI**: http://localhost:4200

## Troubleshooting

### If MySQL password is wrong:
1. Check your MySQL password: `mysql -u root -p`
2. Update `application.properties` with the correct password
3. Restart the backend

### If Angular CLI not found:
```bash
npm install -g @angular/cli
```

### If port 8080 or 4200 is already in use:
- Change backend port in `application.properties`: `server.port=8081`
- Change frontend port: `ng serve --port 4201`
