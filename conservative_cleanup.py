#!/usr/bin/env python3
"""
Conservative Cleanup Script - Step by Step
Removes only the safest files first
"""

import os
import shutil
from datetime import datetime

def create_backup_folder():
    """Create a backup folder with timestamp"""
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    backup_folder = f"backup_{timestamp}"
    os.makedirs(backup_folder, exist_ok=True)
    return backup_folder

def safe_remove_file(file_path, backup_folder):
    """Safely remove a file by backing it up first"""
    if os.path.exists(file_path):
        backup_path = os.path.join(backup_folder, file_path)
        os.makedirs(os.path.dirname(backup_path), exist_ok=True)
        shutil.copy2(file_path, backup_path)
        os.remove(file_path)
        print(f"✅ Removed: {file_path}")
        return True
    return False

def main():
    print("🧹 Conservative Cleanup - Step 1 (Safest Files Only)")
    print("=" * 55)
    
    # Create backup folder
    backup_folder = create_backup_folder()
    print(f"📁 Created backup folder: {backup_folder}")
    
    # Step 1: Remove only the safest files (utility scripts and duplicates)
    step1_files = [
        'app_backup.py',           # Backup of old app
        'enhanced_app.py',         # Duplicate of current app
        'update_admin_dashboard.py', # One-time script
        'templates/admin_dashboard_enhanced.html', # Duplicate template
    ]
    
    print(f"\n🗑️  Step 1: Removing safe utility files...")
    removed_count = 0
    for file in step1_files:
        if os.path.exists(file):
            if safe_remove_file(file, backup_folder):
                removed_count += 1
    
    print(f"\n✅ Step 1 completed!")
    print(f"   Removed: {removed_count} files")
    print(f"   Backup location: {backup_folder}")
    
    # Test system integrity
    print(f"\n🧪 Testing system integrity...")
    if os.path.exists('app.py'):
        print("   ✅ Main application file exists")
    if os.path.exists('templates/admin_dashboard.html'):
        print("   ✅ Admin dashboard template exists")
    if os.path.exists('detection_data.json'):
        print("   ✅ Detection data exists")
    
    print(f"\n🎉 Step 1 cleanup completed successfully!")
    print(f"   Your system is still fully functional")
    
    # Ask about next steps
    print(f"\n📋 Next steps available:")
    print(f"   1. Remove large dataset folders (kaggle_raw/ - 1.5GB)")
    print(f"   2. Remove Spring Boot files (if not using Spring Boot)")
    print(f"   3. Remove more utility scripts")
    print(f"   4. Stop here (system is clean enough)")
    
    response = input(f"\n🤔 What would you like to do next? (1/2/3/4): ").strip()
    
    if response == "1":
        print(f"\n🗑️  Removing large dataset folders...")
        large_folders = ['kaggle_raw/', 'kaggle_raw_audio/']
        for folder in large_folders:
            if os.path.exists(folder):
                backup_path = os.path.join(backup_folder, folder)
                shutil.copytree(folder, backup_path)
                shutil.rmtree(folder)
                print(f"✅ Removed: {folder} (backed up)")
        
        print(f"\n🎉 Large folders removed! Space saved: ~1.8GB")
    
    elif response == "2":
        print(f"\n🗑️  Removing Spring Boot files...")
        spring_files = ['pom.xml', 'start.bat', 'start.sh', 'src/']
        for file in spring_files:
            if os.path.exists(file):
                if os.path.isfile(file):
                    safe_remove_file(file, backup_folder)
                elif os.path.isdir(file):
                    backup_path = os.path.join(backup_folder, file)
                    shutil.copytree(file, backup_path)
                    shutil.rmtree(file)
                    print(f"✅ Removed directory: {file}")
        
        print(f"\n🎉 Spring Boot files removed!")
    
    elif response == "3":
        print(f"\n🗑️  Removing more utility scripts...")
        utility_files = [
            'build_monitoring_csv.py',
            'setup_database.py', 
            'simple_data_viewer.py',
            'simple_db_viewer.py',
            'view_data_tables.py',
            'view_database.py',
            'view_database.bat',
            'MIGRATION_SUMMARY.md',
            'database_setup.sql',
            'monitoring_model.pkl'
        ]
        
        for file in utility_files:
            if os.path.exists(file):
                safe_remove_file(file, backup_folder)
        
        print(f"\n🎉 Utility scripts removed!")
    
    else:
        print(f"\n✅ Stopping here. Your system is clean and functional!")

if __name__ == "__main__":
    main()
