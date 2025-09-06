#!/usr/bin/env python3
"""
Simple Database Viewer for Exam Monitoring System
This script helps you view data in your PostgreSQL database
"""

import psycopg2
import sys

# Database connection settings
DB_CONFIG = {
    'host': 'localhost',
    'database': 'exam_monitoring',
    'user': 'postgres',
    'password': 'postgres',
    'port': 5432
}

def connect_to_db():
    """Connect to PostgreSQL database"""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        print(f"Error connecting to database: {e}")
        print("\nMake sure PostgreSQL is running and the database exists.")
        return None

def show_tables(conn):
    """Show all tables in the database"""
    cursor = conn.cursor()
    cursor.execute("""
        SELECT table_name 
        FROM information_schema.tables 
        WHERE table_schema = 'public'
        ORDER BY table_name;
    """)
    tables = cursor.fetchall()
    
    print("\n📋 Available Tables:")
    print("=" * 50)
    for table in tables:
        print(f"  • {table[0]}")
    print()

def view_table_data(conn, table_name):
    """View data from a specific table"""
    cursor = conn.cursor()
    
    try:
        # Get row count
        cursor.execute(f"SELECT COUNT(*) FROM {table_name};")
        count = cursor.fetchone()[0]
        print(f"\n📊 Table: {table_name} (Total rows: {count})")
        print("=" * 60)
        
        if count > 0:
            # Get sample data
            cursor.execute(f"SELECT * FROM {table_name} LIMIT 10;")
            rows = cursor.fetchall()
            
            # Get column names
            column_names = [desc[0] for desc in cursor.description]
            
            print(f"Sample data (first 10 rows):")
            print("-" * 60)
            
            # Print header
            print(" | ".join([f"{col:15}" for col in column_names]))
            print("-" * (15 * len(column_names) + 3 * (len(column_names) - 1)))
            
            # Print rows
            for row in rows:
                row_str = " | ".join([f"{str(val)[:15]:15}" for val in row])
                print(row_str)
        else:
            print("Table is empty.")
            
    except Exception as e:
        print(f"Error viewing table {table_name}: {e}")

def show_user_stats(conn):
    """Show user statistics"""
    cursor = conn.cursor()
    
    print("\n👥 User Statistics:")
    print("=" * 50)
    
    # Total users
    cursor.execute("SELECT COUNT(*) FROM users;")
    total_users = cursor.fetchone()[0]
    print(f"Total Users: {total_users}")
    
    # Users by role
    cursor.execute("""
        SELECT role, COUNT(*) 
        FROM users 
        GROUP BY role;
    """)
    roles = cursor.fetchall()
    for role, count in roles:
        print(f"  {role}: {count}")
    
    # Recent logins
    cursor.execute("""
        SELECT username, name, last_login 
        FROM users 
        WHERE last_login IS NOT NULL 
        ORDER BY last_login DESC 
        LIMIT 5;
    """)
    recent_logins = cursor.fetchall()
    if recent_logins:
        print(f"\nRecent Logins:")
        for username, name, last_login in recent_logins:
            print(f"  {name} ({username}) - {last_login}")

def show_exam_stats(conn):
    """Show exam statistics"""
    cursor = conn.cursor()
    
    print("\n📚 Exam Statistics:")
    print("=" * 50)
    
    # Total exams
    cursor.execute("SELECT COUNT(*) FROM exams;")
    total_exams = cursor.fetchone()[0]
    print(f"Total Exams: {total_exams}")
    
    # Exams by status
    cursor.execute("""
        SELECT status, COUNT(*) 
        FROM exams 
        GROUP BY status;
    """)
    statuses = cursor.fetchall()
    for status, count in statuses:
        print(f"  {status}: {count}")
    
    # Active sessions
    cursor.execute("SELECT COUNT(*) FROM exam_sessions WHERE status = 'ACTIVE';")
    active_sessions = cursor.fetchone()[0]
    print(f"Active Sessions: {active_sessions}")

def show_detection_stats(conn):
    """Show detection statistics"""
    cursor = conn.cursor()
    
    print("\n🔍 Detection Statistics:")
    print("=" * 50)
    
    # Total detections
    cursor.execute("SELECT COUNT(*) FROM student_detections;")
    total_detections = cursor.fetchone()[0]
    print(f"Total Detections: {total_detections}")
    
    # Detections by type
    cursor.execute("""
        SELECT detection_type, COUNT(*) 
        FROM student_detections 
        GROUP BY detection_type 
        ORDER BY COUNT(*) DESC;
    """)
    detection_types = cursor.fetchall()
    for det_type, count in detection_types:
        print(f"  {det_type}: {count}")

def main():
    """Main function"""
    print("🗄️  Exam Monitoring Database Viewer")
    print("=" * 50)
    
    # Connect to database
    conn = connect_to_db()
    if not conn:
        return
    
    try:
        while True:
            print("\nChoose an option:")
            print("1. Show all tables")
            print("2. View table data")
            print("3. Show user statistics")
            print("4. Show exam statistics")
            print("5. Show detection statistics")
            print("6. Exit")
            
            choice = input("\nEnter your choice (1-6): ").strip()
            
            if choice == '1':
                show_tables(conn)
            elif choice == '2':
                table_name = input("Enter table name: ").strip()
                view_table_data(conn, table_name)
            elif choice == '3':
                show_user_stats(conn)
            elif choice == '4':
                show_exam_stats(conn)
            elif choice == '5':
                show_detection_stats(conn)
            elif choice == '6':
                print("Goodbye!")
                break
            else:
                print("Invalid choice. Please try again.")
                
    except KeyboardInterrupt:
        print("\n\nGoodbye!")
    finally:
        conn.close()

if __name__ == "__main__":
    main()
