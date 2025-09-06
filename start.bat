@echo off
echo Starting Exam Monitoring System...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or higher
    pause
    exit /b 1
)

REM Check if PostgreSQL is running
echo Checking PostgreSQL connection...
psql -U postgres -c "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo Warning: Could not connect to PostgreSQL
    echo Please ensure PostgreSQL is running and accessible
    echo You may need to update database credentials in application.yml
    echo.
)

echo Building application...
call mvn clean install -DskipTests

if %errorlevel% neq 0 (
    echo Error: Build failed
    pause
    exit /b 1
)

echo.
echo Starting application...
echo Application will be available at: http://localhost:8080
echo.
echo Default credentials:
echo   Admin: admin / admin123
echo   Student 1: student1 / password1
echo   Student 2: student2 / password2
echo.

call mvn spring-boot:run

pause
