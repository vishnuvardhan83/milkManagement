#!/bin/bash
# Script to start the Spring Boot backend

cd "$(dirname "$0")/backend"

# Set Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null)
if [ -z "$JAVA_HOME" ]; then
    echo "Error: Java 17 not found. Please install Java 17."
    exit 1
fi

export PATH=$JAVA_HOME/bin:$PATH

echo "========================================="
echo "Starting Milk Management Backend"
echo "========================================="
echo "Using Java:"
java -version
echo ""
echo "Server will start on: http://localhost:8080"
echo "API endpoints: http://localhost:8080/api"
echo ""
echo "Press Ctrl+C to stop the server"
echo "========================================="
echo ""

# Check if MySQL is running (optional check)
if ! pgrep -x "mysqld" > /dev/null && ! pgrep -x "mysql" > /dev/null; then
    echo "Warning: MySQL might not be running. Make sure MySQL is started."
    echo ""
fi

mvn spring-boot:run
