#!/usr/bin/env python3
"""
Data Table Viewer for Exam Monitoring System
This script displays your existing JSON data in table format
"""

import json
import os
from tabulate import tabulate
import sys

def load_json_data(filename):
    """Load data from JSON file"""
    try:
        if os.path.exists(filename):
            with open(filename, 'r') as f:
                return json.load(f)
        else:
            return {}
    except Exception as e:
        print(f"Error loading {filename}: {e}")
        return {}

def display_users_table():
    """Display users data in table format"""
    print("\n👥 USERS TABLE")
    print("=" * 80)
    
    # Default users from the system
    users_data = {
        'student1': {'password': 'password1', 'name': 'John Doe', 'role': 'student'},
        'student2': {'password': 'password2', 'name': 'Jane Smith', 'role': 'student'},
        'admin': {'password': 'admin123', 'name': 'Administrator', 'role': 'admin'}
    }
    
    if users_data:
        headers = ['Username', 'Name', 'Role', 'Password']
        rows = []
        for username, data in users_data.items():
            rows.append([username, data['name'], data['role'], data['password']])
        
        print(tabulate(rows, headers=headers, tablefmt='grid'))
    else:
        print("No user data found")

def display_exams_table():
    """Display exams data in table format"""
    print("\n📚 EXAMS TABLE")
    print("=" * 80)
    
    exams_data = load_json_data('exams_data.json')
    
    if exams_data:
        headers = ['Exam ID', 'Title', 'Description', 'Duration (min)', 'Status', 'Created By', 'Created At']
        rows = []
        
        for exam_id, exam in exams_data.items():
            rows.append([
                exam_id,
                exam.get('title', 'N/A'),
                exam.get('description', 'N/A')[:50] + '...' if len(exam.get('description', '')) > 50 else exam.get('description', 'N/A'),
                exam.get('duration_minutes', 'N/A'),
                exam.get('status', 'N/A'),
                exam.get('created_by', 'N/A'),
                exam.get('created_at', 'N/A')[:19] if exam.get('created_at') else 'N/A'
            ])
        
        print(tabulate(rows, headers=headers, tablefmt='grid'))
        
        # Show questions for each exam
        print("\n📝 EXAM QUESTIONS")
        print("=" * 80)
        for exam_id, exam in exams_data.items():
            if 'questions' in exam and exam['questions']:
                print(f"\nExam: {exam.get('title', exam_id)}")
                print("-" * 40)
                for i, question in enumerate(exam['questions'], 1):
                    print(f"Q{i}. {question.get('text', 'N/A')}")
                    if 'options' in question:
                        for key, value in question['options'].items():
                            print(f"   {key}) {value}")
                    print(f"   Correct Answer: {question.get('correct_answer', 'N/A')}")
                    print()
    else:
        print("No exam data found")

def display_exam_sessions_table():
    """Display exam sessions data in table format"""
    print("\n🎯 EXAM SESSIONS TABLE")
    print("=" * 80)
    
    sessions_data = load_json_data('exam_sessions.json')
    
    if sessions_data:
        headers = ['Session ID', 'Exam ID', 'Status', 'Started At', 'Students Count', 'Ended At']
        rows = []
        
        for session_id, session in sessions_data.items():
            students_count = len(session.get('students', []))
            ended_at = session.get('ended_at', 'N/A')
            if ended_at and ended_at != 'N/A':
                ended_at = ended_at[:19]
            
            rows.append([
                session_id,
                session.get('exam_id', 'N/A'),
                session.get('status', 'N/A'),
                session.get('started_at', 'N/A')[:19] if session.get('started_at') else 'N/A',
                students_count,
                ended_at
            ])
        
        print(tabulate(rows, headers=headers, tablefmt='grid'))
        
        # Show detailed session info
        print("\n📊 SESSION DETAILS")
        print("=" * 80)
        for session_id, session in sessions_data.items():
            print(f"\nSession: {session_id}")
            print("-" * 40)
            print(f"Status: {session.get('status', 'N/A')}")
            print(f"Started: {session.get('started_at', 'N/A')}")
            if session.get('ended_at'):
                print(f"Ended: {session.get('ended_at', 'N/A')}")
            
            if 'students' in session and session['students']:
                print(f"Students: {', '.join(session['students'])}")
            
            if 'student_answers' in session and session['student_answers']:
                print("Student Answers:")
                for student, answers in session['student_answers'].items():
                    print(f"  {student}: {answers}")
            
            if 'student_detections' in session and session['student_detections']:
                print("Detections:")
                for student, detections in session['student_detections'].items():
                    print(f"  {student}: {len(detections)} detections")
                    # Show last few detections
                    for detection in detections[-3:]:  # Last 3 detections
                        print(f"    - {detection.get('type', 'N/A')} at {detection.get('timestamp', 'N/A')[:19]}")
    else:
        print("No exam session data found")

def display_detection_data_table():
    """Display detection data in table format"""
    print("\n🔍 DETECTION DATA TABLE")
    print("=" * 80)
    
    detection_data = load_json_data('detection_data.json')
    
    if detection_data:
        headers = ['Username', 'Looking Away', 'Multiple People', 'No Face', 'Blur Screen', 'Tab Switching', 'Total Alerts']
        rows = []
        
        for username, data in detection_data.items():
            total = (data.get('looking_away', 0) + 
                    data.get('multiple_people', 0) + 
                    data.get('no_face', 0) + 
                    data.get('blur_screen', 0) + 
                    data.get('tab_switching', 0))
            
            rows.append([
                username,
                data.get('looking_away', 0),
                data.get('multiple_people', 0),
                data.get('no_face', 0),
                data.get('blur_screen', 0),
                data.get('tab_switching', 0),
                total
            ])
        
        print(tabulate(rows, headers=headers, tablefmt='grid'))
        
        # Show alert history
        print("\n🚨 ALERT HISTORY")
        print("=" * 80)
        for username, data in detection_data.items():
            if 'alert_history' in data and data['alert_history']:
                print(f"\n{username}:")
                print("-" * 30)
                for alert in data['alert_history'][-10:]:  # Last 10 alerts
                    print(f"  {alert.get('timestamp', 'N/A')[:19]} - {alert.get('type', 'N/A')}: {alert.get('message', 'N/A')}")
    else:
        print("No detection data found")

def display_monitoring_csv():
    """Display monitoring CSV data if available"""
    print("\n📈 MONITORING CSV DATA")
    print("=" * 80)
    
    if os.path.exists('monitoring.csv'):
        try:
            import pandas as pd
            df = pd.read_csv('monitoring.csv')
            print(f"CSV file has {len(df)} rows and {len(df.columns)} columns")
            print("\nFirst 10 rows:")
            print(tabulate(df.head(10), headers=df.columns, tablefmt='grid'))
            
            print(f"\nColumn names: {list(df.columns)}")
            print(f"Data types:\n{df.dtypes}")
            
        except ImportError:
            print("pandas not available. Install with: pip install pandas")
        except Exception as e:
            print(f"Error reading CSV: {e}")
    else:
        print("No monitoring.csv file found")

def main():
    """Main function"""
    print("🗄️  Exam Monitoring Data Viewer")
    print("=" * 50)
    print("Viewing your existing JSON data files in table format")
    
    while True:
        print("\nChoose what to view:")
        print("1. Users Table")
        print("2. Exams Table")
        print("3. Exam Sessions Table")
        print("4. Detection Data Table")
        print("5. Monitoring CSV Data")
        print("6. View All Tables")
        print("7. Exit")
        
        choice = input("\nEnter your choice (1-7): ").strip()
        
        if choice == '1':
            display_users_table()
        elif choice == '2':
            display_exams_table()
        elif choice == '3':
            display_exam_sessions_table()
        elif choice == '4':
            display_detection_data_table()
        elif choice == '5':
            display_monitoring_csv()
        elif choice == '6':
            display_users_table()
            display_exams_table()
            display_exam_sessions_table()
            display_detection_data_table()
            display_monitoring_csv()
        elif choice == '7':
            print("Goodbye!")
            break
        else:
            print("Invalid choice. Please try again.")

if __name__ == "__main__":
    # Install tabulate if not available
    try:
        import tabulate
    except ImportError:
        print("Installing required package: tabulate")
        import subprocess
        subprocess.check_call([sys.executable, "-m", "pip", "install", "tabulate"])
        import tabulate
    
    main()
