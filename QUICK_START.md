# 🚀 Quick Start Guide

## ⚠️ Current Issue: Database Password Not Set

The backend is stopping because MySQL requires a password. Here's how to fix it:

## ✅ Solution 1: Use the Startup Script (Easiest)

```bash
cd backend
./start-backend.sh your_mysql_password
```

## ✅ Solution 2: Set Environment Variable

```bash
# Set your MySQL password
export DB_PASSWORD=your_mysql_password

# Start backend
cd backend
mvn spring-boot:run
```

## ✅ Solution 3: Edit application.properties Directly

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.password=your_mysql_password
```

Then start:
```bash
cd backend
mvn spring-boot:run
```

## 🔍 Check MySQL Status

```bash
# Check if MySQL is running
brew services list | grep mysql

# Start MySQL if not running
brew services start mysql

# Test connection
mysql -u root -p
```

## 📝 Complete Setup Steps

1. **Set MySQL Password:**
   ```bash
   export DB_PASSWORD=your_password
   ```

2. **Start Backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Start Frontend (in another terminal):**
   ```bash
   cd frontend
   npm install --legacy-peer-deps
   ng serve
   ```

4. **Access Application:**
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

## 🐛 Troubleshooting

### MySQL Connection Error
- Make sure MySQL is running: `brew services start mysql`
- Verify password is correct
- Check if MySQL allows passwordless login (not recommended)

### Port Already in Use
- Backend (8080): Change `server.port` in `application.properties`
- Frontend (4200): Use `ng serve --port 4201`

### Database Not Created
- The app will auto-create the database if `createDatabaseIfNotExist=true` is in the URL
- Make sure MySQL user has CREATE DATABASE permission

## 📚 More Help

See `BACKEND_SETUP_FIX.md` for detailed database setup instructions.
