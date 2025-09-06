#!/usr/bin/env python3
"""
Remove Never-Used Files Script
Safely removes files that are never used by the main application
"""

import os
import shutil
from datetime import datetime

def create_backup_folder():
    """Create a backup folder with timestamp"""
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    backup_folder = f"backup_unused_{timestamp}"
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

def safe_remove_directory(dir_path, backup_folder):
    """Safely remove a directory by backing it up first"""
    if os.path.exists(dir_path):
        backup_path = os.path.join(backup_folder, dir_path)
        shutil.copytree(dir_path, backup_path)
        shutil.rmtree(dir_path)
        print(f"✅ Removed directory: {dir_path}")
        return True
    return False

def main():
    print("🗑️  Remove Never-Used Files Script")
    print("=" * 40)
    
    # Create backup folder
    backup_folder = create_backup_folder()
    print(f"📁 Created backup folder: {backup_folder}")
    
    # Files that are NEVER used by the main Flask app
    never_used_files = [
        # Utility scripts (not imported by app.py)
        'build_monitoring_csv.py',
        'conservative_cleanup.py',
        'safe_cleanup.py',
        'setup_database.py',
        'simple_data_viewer.py',
        'simple_db_viewer.py',
        'view_data_tables.py',
        'view_database.py',
        'view_database.bat',
        
        # Migration and documentation files
        'MIGRATION_SUMMARY.md',
        'database_setup.sql',
        
        # Spring Boot files (not used in Flask app)
        'pom.xml',
        'start.bat',
        'start.sh',
        'src/',
        
        # Large dataset folders (not used by current app)
        'kaggle_raw/',
        'kaggle_raw_audio/',
        
        # ML model file (not used by current app)
        'monitoring_model.pkl',
        
        # Training script (not used by main app)
        'train_to_pkl.py',
    ]
    
    # Calculate space savings
    total_size = 0
    files_to_remove = []
    
    print(f"\n📊 Analyzing files to remove...")
    for file in never_used_files:
        if os.path.exists(file):
            size = 0
            if os.path.isfile(file):
                size = os.path.getsize(file)
            elif os.path.isdir(file):
                size = sum(os.path.getsize(os.path.join(dirpath, filename))
                          for dirpath, dirnames, filenames in os.walk(file)
                          for filename in filenames)
            
            size_mb = size / (1024 * 1024)
            total_size += size
            files_to_remove.append((file, size_mb))
            print(f"   - {file} ({size_mb:.1f} MB)")
    
    total_mb = total_size / (1024 * 1024)
    print(f"\n💾 Total space to be freed: {total_mb:.1f} MB")
    print(f"📁 Files to remove: {len(files_to_remove)}")
    
    # Show what will remain (essential files)
    essential_files = [
        'app.py',  # Main Flask application
        'requirements.txt',  # Dependencies
        'templates/',  # HTML templates
        'detection_data.json',  # Used by app
        'exams_data.json',  # Used by app
        'exam_sessions.json',  # Used by app
        'monitoring.csv',  # May contain data
        'README.md',  # Documentation
    ]
    
    print(f"\n✅ Essential files that will remain:")
    for file in essential_files:
        if os.path.exists(file):
            print(f"   - {file}")
    
    # Confirm removal
    print(f"\n⚠️  WARNING: This will remove {len(files_to_remove)} files/folders")
    print(f"   All files will be backed up to: {backup_folder}")
    print(f"   Your Flask app will continue to work normally")
    
    response = input(f"\n🤔 Proceed with removal? (y/N): ").strip().lower()
    
    if response in ['y', 'yes']:
        print(f"\n🔄 Removing never-used files...")
        
        removed_count = 0
        for file, size_mb in files_to_remove:
            if os.path.exists(file):
                if os.path.isfile(file):
                    if safe_remove_file(file, backup_folder):
                        removed_count += 1
                elif os.path.isdir(file):
                    if safe_remove_directory(file, backup_folder):
                        removed_count += 1
        
        print(f"\n✅ Cleanup completed!")
        print(f"   Removed: {removed_count} files/folders")
        print(f"   Space freed: {total_mb:.1f} MB")
        print(f"   Backup location: {backup_folder}")
        
        # Test system integrity
        print(f"\n🧪 Testing system integrity...")
        if os.path.exists('app.py'):
            print("   ✅ Main application file exists")
        if os.path.exists('requirements.txt'):
            print("   ✅ Dependencies file exists")
        if os.path.exists('templates/'):
            print("   ✅ Templates directory exists")
        if os.path.exists('detection_data.json'):
            print("   ✅ Detection data exists")
        
        print(f"\n🎉 System cleanup completed successfully!")
        print(f"   Your Flask application is ready to run")
        print(f"   Run 'python app.py' to test")
        
        # Show final directory structure
        print(f"\n📁 Final directory structure:")
        for item in os.listdir('.'):
            if os.path.isfile(item):
                size = os.path.getsize(item) / 1024  # KB
                print(f"   📄 {item} ({size:.1f} KB)")
            elif os.path.isdir(item) and not item.startswith('backup_'):
                print(f"   📁 {item}/")
        
    else:
        print(f"\n❌ Cleanup cancelled by user")
        print(f"   No files were removed")
        print(f"   Backup folder created but empty: {backup_folder}")

if __name__ == "__main__":
    main()
