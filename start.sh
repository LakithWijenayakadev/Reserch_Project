#!/bin/bash

echo "Starting Exam Monitoring System..."
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher"
    exit 1
fi

# Check if PostgreSQL is running
echo "Checking PostgreSQL connection..."
if ! psql -U postgres -c "SELECT 1;" &> /dev/null; then
    echo "Warning: Could not connect to PostgreSQL"
    echo "Please ensure PostgreSQL is running and accessible"
    echo "You may need to update database credentials in application.yml"
    echo
fi

echo "Building application..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

echo
echo "Starting application..."
echo "Application will be available at: http://localhost:8080"
echo
echo "Default credentials:"
echo "  Admin: admin / admin123"
echo "  Student 1: student1 / password1"
echo "  Student 2: student2 / password2"
echo

mvn spring-boot:run
