# Backend Setup - Database Connection Fix

## ❌ Error
```
Access denied for user 'root'@'localhost' (using password: NO)
```

## ✅ Solution

The backend is stopping because MySQL requires a password, but it's not configured.

### Option 1: Set Password in application.properties (Quick Fix)

Edit `backend/src/main/resources/application.properties` and set your MySQL password:

```properties
spring.datasource.password=your_mysql_password_here
```

### Option 2: Use Environment Variables (Recommended)

1. **Set environment variable before running:**
   ```bash
   export DB_PASSWORD=your_mysql_password_here
   cd backend
   mvn spring-boot:run
   ```

2. **Or create a `.env` file** (if using a tool like `dotenv`):
   ```bash
   DB_PASSWORD=your_mysql_password_here
   ```

### Option 3: Create application-local.properties

Create `backend/src/main/resources/application-local.properties`:

```properties
spring.datasource.password=your_mysql_password_here
```

Then run with profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## 🔧 MySQL Setup Checklist

1. **Check if MySQL is running:**
   ```bash
   mysql --version
   # or
   brew services list  # on macOS
   ```

2. **Start MySQL if not running:**
   ```bash
   # macOS
   brew services start mysql
   
   # Linux
   sudo systemctl start mysql
   
   # Windows
   # Start MySQL service from Services
   ```

3. **Verify MySQL connection:**
   ```bash
   mysql -u root -p
   # Enter your password when prompted
   ```

4. **Create database (if needed):**
   ```sql
   CREATE DATABASE IF NOT EXISTS dairy_farm_erp;
   ```

5. **Grant permissions (if needed):**
   ```sql
   GRANT ALL PRIVILEGES ON dairy_farm_erp.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

## 🚀 Quick Start

1. **Set your MySQL password:**
   ```bash
   export DB_PASSWORD=your_password
   ```

2. **Run the backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Or if MySQL has no password** (not recommended for production):
   - Leave `spring.datasource.password=` empty in `application.properties`
   - Make sure MySQL allows passwordless login for root

## 📝 Notes

- The application will automatically create the database if it doesn't exist (due to `createDatabaseIfNotExist=true`)
- Flyway will run migrations automatically on startup
- If you change the password, restart the backend

