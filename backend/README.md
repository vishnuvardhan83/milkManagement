# Milk Management System - Backend

Spring Boot backend application for Milk Selling & Daily Delivery Management System.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Node.js (for frontend, if running full stack)

## Setup Instructions

### 1. Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE milk_management;
```

2. The application will automatically create tables on first run (using `spring.jpa.hibernate.ddl-auto=update`)

### 2. Configuration

The application uses `src/main/resources/application.properties` for configuration.

**Important:** Update the database credentials in `application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_password_here
```

Or use environment variables:
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password_here
```

### 3. Build the Project

**Option 1: Using the wrapper script (Recommended)**
```bash
./mvn.sh clean install
```

**Option 2: Manual build with Java 17**
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean install
```

**Note:** If you have multiple Java versions installed, make sure to use Java 17. The wrapper script (`mvn.sh`) automatically sets Java 17.

### 4. Run the Application

**Option 1: Using the run script (Recommended)**
```bash
chmod +x run.sh
./run.sh
```

**Option 2: Using Maven**
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn spring-boot:run
```

**Option 3: Run the JAR file**
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
java -jar target/milk-management-backend-1.0.0.jar
```

## API Endpoints

The application runs on `http://localhost:8080` by default.

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Customers
- `GET /api/customers` - Get all customers
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

### Deliveries
- `GET /api/deliveries` - Get all deliveries
- `GET /api/deliveries/customer/{customerId}` - Get deliveries by customer
- `GET /api/deliveries/date/{date}` - Get deliveries by date
- `POST /api/deliveries` - Create delivery

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics

## Default Roles

The system supports the following roles:
- `ROLE_ADMIN` - Full access
- `ROLE_MANAGER` - Management access
- `ROLE_CUSTOMER` - Customer access

## Troubleshooting

### Java Version Issues

If you encounter compilation errors related to Java version:
1. Check your Java version: `java -version`
2. Ensure Java 17 is installed: `/usr/libexec/java_home -V`
3. Use the wrapper script: `./mvn.sh` instead of `mvn`

### Database Connection Issues

1. Ensure MySQL is running: `mysql.server start` (macOS)
2. Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`
3. Check credentials in `application.properties`

### Port Already in Use

If port 8080 is already in use, change it in `application.properties`:
```properties
server.port=8081
```

## Development

### Project Structure
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/milkmanagement/
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── service/        # Business logic
│   │   │   ├── repository/     # Data access
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── dto/            # Data transfer objects
│   │   │   ├── security/       # Security configuration
│   │   │   └── util/           # Utility classes
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvn.sh                      # Maven wrapper with Java 17
└── run.sh                      # Run script
```

## License

This project is part of the Milk Management System.
