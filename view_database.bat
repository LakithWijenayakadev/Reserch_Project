@echo off
echo Database Viewer for Exam Monitoring System
echo ==========================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Python is not installed or not in PATH
    echo Please install Python 3.6 or higher
    pause
    exit /b 1
)

REM Check if psycopg2 is installed
python -c "import psycopg2" >nul 2>&1
if %errorlevel% neq 0 (
    echo Installing required Python package...
    pip install psycopg2-binary
    if %errorlevel% neq 0 (
        echo Error: Failed to install psycopg2-binary
        echo Please install it manually: pip install psycopg2-binary
        pause
        exit /b 1
    )
)

echo Starting database viewer...
echo.
python simple_db_viewer.py

pause
