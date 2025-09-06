#!/usr/bin/env python3
"""
Simple Data Viewer for Exam Monitoring System
This script displays your existing JSON data in table format
"""

import json
import os

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

def print_table(data, headers, title=""):
    """Print data in a simple table format"""
    if title:
        print(f"\n{title}")
        print("=" * len(title))
    
    if not data:
        print("No data found")
        return
    
    # Calculate column widths
    col_widths = []
    for i, header in enumerate(headers):
        max_width = len(header)
        for row in data:
            if i < len(row):
                max_width = max(max_width, len(str(row[i])))
        col_widths.append(max_width + 2)
    
    # Print header
    header_line = "|"
    for i, header in enumerate(headers):
        header_line += f" {header:<{col_widths[i]-1}}|"
    print(header_line)
    
    # Print separator
    separator = "|"
    for width in col_widths:
        separator += "-" * width + "|"
    print(separator)
    
    # Print data rows
    for row in data:
        row_line = "|"
        for i, cell in enumerate(row):
            if i < len(row):
                row_line += f" {str(cell):<{col_widths[i]-1}}|"
        print(row_line)

def display_users_table():
    """Display users data in table format"""
    # Default users from the system
    users_data = {
        'student1': {'password': 'password1', 'name': 'John Doe', 'role': 'student'},
        'student2': {'password': 'password2', 'name': 'Jane Smith', 'role': 'student'},
        'admin': {'password': 'admin123', 'name': 'Administrator', 'role': 'admin'}
    }
    
    headers = ['Username', 'Name', 'Role', 'Password']
    rows = []
    for username, data in users_data.items():
        rows.append([username, data['name'], data['role'], data['password']])
    
    print_table(rows, headers, "👥 USERS TABLE")

def display_exams_table():
    """Display exams data in table format"""
    exams_data = load_json_data('exams_data.json')
    
    if not exams_data:
        print("\n📚 EXAMS TABLE")
        print("=" * 50)
        print("No exam data found")
        return
    
    headers = ['Exam ID', 'Title', 'Description', 'Duration (min)', 'Status', 'Created By']
    rows = []
    
    for exam_id, exam in exams_data.items():
        description = exam.get('description', 'N/A')
        if len(description) > 30:
            description = description[:30] + '...'
        
        rows.append([
            exam_id,
            exam.get('title', 'N/A'),
            description,
            exam.get('duration_minutes', 'N/A'),
            exam.get('status', 'N/A'),
            exam.get('created_by', 'N/A')
        ])
    
    print_table(rows, headers, "📚 EXAMS TABLE")
    
    # Show questions for each exam
    print("\n📝 EXAM QUESTIONS")
    print("=" * 50)
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

def display_exam_sessions_table():
    """Display exam sessions data in table format"""
    sessions_data = load_json_data('exam_sessions.json')
    
    if not sessions_data:
        print("\n🎯 EXAM SESSIONS TABLE")
        print("=" * 50)
        print("No exam session data found")
        return
    
    headers = ['Session ID', 'Exam ID', 'Status', 'Students Count', 'Started At']
    rows = []
    
    for session_id, session in sessions_data.items():
        students_count = len(session.get('students', []))
        started_at = session.get('started_at', 'N/A')
        if started_at and started_at != 'N/A':
            started_at = started_at[:19]
        
        rows.append([
            session_id,
            session.get('exam_id', 'N/A'),
            session.get('status', 'N/A'),
            students_count,
            started_at
        ])
    
    print_table(rows, headers, "🎯 EXAM SESSIONS TABLE")
    
    # Show detailed session info
    print("\n📊 SESSION DETAILS")
    print("=" * 50)
    for session_id, session in sessions_data.items():
        print(f"\nSession: {session_id}")
        print("-" * 30)
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

def display_detection_data_table():
    """Display detection data in table format"""
    detection_data = load_json_data('detection_data.json')
    
    if not detection_data:
        print("\n🔍 DETECTION DATA TABLE")
        print("=" * 50)
        print("No detection data found")
        return
    
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
    
    print_table(rows, headers, "🔍 DETECTION DATA TABLE")
    
    # Show alert history
    print("\n🚨 ALERT HISTORY")
    print("=" * 50)
    for username, data in detection_data.items():
        if 'alert_history' in data and data['alert_history']:
            print(f"\n{username}:")
            print("-" * 30)
            for alert in data['alert_history'][-10:]:  # Last 10 alerts
                print(f"  {alert.get('timestamp', 'N/A')[:19]} - {alert.get('type', 'N/A')}: {alert.get('message', 'N/A')}")

def display_monitoring_csv():
    """Display monitoring CSV data if available"""
    print("\n📈 MONITORING CSV DATA")
    print("=" * 50)
    
    if os.path.exists('monitoring.csv'):
        try:
            with open('monitoring.csv', 'r') as f:
                lines = f.readlines()
            
            print(f"CSV file has {len(lines)} lines")
            print("\nFirst 10 lines:")
            for i, line in enumerate(lines[:10]):
                print(f"{i+1:2}: {line.strip()}")
            
            if len(lines) > 10:
                print(f"... and {len(lines) - 10} more lines")
                
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
    main()
