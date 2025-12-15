#!/bin/bash
# Script to run Spring Boot with Java 17

# Set Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

# Set database credentials from environment variables if needed
# Example: export DB_PASSWORD=your_password_here

echo "========================================="
echo "Milk Management Backend"
echo "========================================="
echo "Using Java:"
java -version
echo ""
echo "Starting Spring Boot application..."
echo "Server will start on: http://localhost:8080"
echo "API endpoints available at: http://localhost:8080/api"
echo ""
echo "Note: Make sure MySQL is running and database 'milk_management' exists"
echo "========================================="
echo ""

mvn spring-boot:run
