# Setup Guide - Milk Management System

Complete step-by-step guide to set up and run the Milk Management System.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17 or higher** - [Download](https://adoptium.net/)
- **Node.js 18+ and npm** - [Download](https://nodejs.org/)
- **MySQL 8.0+** - [Download](https://dev.mysql.com/downloads/)
- **Maven 3.6+** (optional, included with Spring Boot) - [Download](https://maven.apache.org/)

## Step 1: Database Setup

1. **Start MySQL Server**
   ```bash
   # On macOS/Linux
   mysql.server start
   
   # On Windows, start MySQL service from Services
   ```

2. **Create Database**
   ```bash
   mysql -u root -p
   ```
   
   Then run:
   ```sql
   CREATE DATABASE milk_management;
   ```

3. **Run Schema Script**
   ```bash
   mysql -u root -p milk_management < database/schema.sql
   ```
   
   Or manually execute the SQL file from `database/schema.sql` in your MySQL client.

4. **Verify Database**
   ```sql
   USE milk_management;
   SHOW TABLES;
   ```
   
   You should see all tables created.

## Step 2: Backend Setup (Spring Boot)

1. **Navigate to Backend Directory**
   ```bash
   cd backend
   ```

2. **Update Database Configuration**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

3. **Build and Run**
   ```bash
   # Using Maven Wrapper (if available)
   ./mvnw spring-boot:run
   
   # Or using Maven
   mvn clean install
   mvn spring-boot:run
   ```

4. **Verify Backend**
   
   Open browser: `http://localhost:8080`
   
   You should see a Whitelabel Error Page (this is normal - no root endpoint defined).

## Step 3: Frontend Setup (Angular)

1. **Navigate to Frontend Directory**
   ```bash
   cd frontend
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```
   
   This may take a few minutes.

3. **Start Development Server**
   ```bash
   npm start
   # or
   ng serve
   ```

4. **Verify Frontend**
   
   Open browser: `http://localhost:4200`
   
   You should see the login page.

## Step 4: Login and Test

1. **Default Credentials**
   - **Admin**: 
     - Username: `admin`
     - Password: `admin123`
   - **Manager**:
     - Username: `manager`
     - Password: `manager123`

2. **Test Features**
   - Login with admin credentials
   - View dashboard with statistics
   - Add a new customer
   - Create a milk delivery
   - View delivery list

## Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```bash
# Change port in application.properties
server.port=8081
```

**Database connection error:**
- Verify MySQL is running
- Check username/password in `application.properties`
- Ensure database `milk_management` exists

**JWT Secret too short:**
- The JWT secret in `application.properties` must be at least 256 bits (32 characters)
- Current default is sufficient, but you can change it

### Frontend Issues

**Port 4200 already in use:**
```bash
ng serve --port 4201
```

**npm install fails:**
```bash
# Clear cache and retry
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

**CORS errors:**
- Ensure backend is running on port 8080
- Check CORS configuration in `SecurityConfig.java`
- Verify API URL in services (should be `http://localhost:8080`)

**Chart not displaying:**
- Ensure `ng2-charts` and `chart.js` are installed
- Check browser console for errors
- Verify `BaseChartDirective` is imported in module

### Database Issues

**Tables not created:**
- Manually run `database/schema.sql` script
- Check MySQL user has CREATE privileges

**Default users not working:**
- Password hash might be incorrect
- Re-run the INSERT statements from `schema.sql`
- Or create new user via registration endpoint

## Project Structure

```
milkManagement/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/milkmanagement/
│   │   │   │       ├── controller/    # REST controllers
│   │   │   │       ├── service/        # Business logic
│   │   │   │       ├── repository/     # Data access
│   │   │   │       ├── entity/         # JPA entities
│   │   │   │       ├── dto/            # Data transfer objects
│   │   │   │       ├── security/       # Security config
│   │   │   │       └── util/           # Utilities
│   │   │   └── resources/
│   │   │       └── application.properties
│   └── pom.xml
├── frontend/               # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/     # Angular components
│   │   │   ├── services/       # API services
│   │   │   ├── guards/         # Route guards
│   │   │   └── interceptors/   # HTTP interceptors
│   │   └── index.html
│   └── package.json
├── database/
│   └── schema.sql          # MySQL schema
├── README.md
├── API_DOCUMENTATION.md
└── SETUP_GUIDE.md
```

## Next Steps

1. **Customize Configuration**
   - Update JWT secret key
   - Configure email settings (if needed)
   - Adjust CORS origins for production

2. **Add More Features**
   - Payment tracking
   - Invoice generation (PDF)
   - Email notifications
   - Reports and exports

3. **Deploy to Production**
   - Build Angular for production: `ng build --prod`
   - Package Spring Boot as JAR: `mvn clean package`
   - Configure production database
   - Set up reverse proxy (nginx)

## Support

For issues or questions:
1. Check the API documentation: `API_DOCUMENTATION.md`
2. Review error logs in console/terminal
3. Verify all prerequisites are installed correctly

## Default Data

After running the schema script, you'll have:
- 2 default users (admin, manager)
- 3 default products (Milk, Curd, Butter)
- Default product prices
- Empty stock entries

You can start adding customers and deliveries immediately after login.
