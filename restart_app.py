#!/usr/bin/env python3
"""
Restart Flask App with New Routes
"""

import subprocess
import sys
import time
import os

def restart_flask_app():
    """Restart the Flask app to load new routes"""
    print("🔄 Restarting Flask App...")
    
    try:
        # Kill any existing Flask processes
        print("   Stopping existing Flask processes...")
        try:
            subprocess.run(['taskkill', '/f', '/im', 'python.exe'], 
                         capture_output=True, text=True)
        except:
            pass
        
        # Wait a moment
        time.sleep(2)
        
        # Start the Flask app
        print("   Starting Flask app with new routes...")
        print("   🚀 Flask app is now running with all management features!")
        print("   📍 Access at: http://127.0.0.1:8080")
        print("   🔑 Login as: admin/admin123")
        print("   ⏹️  Press Ctrl+C to stop")
        print("   " + "="*50)
        
        # Start the app
        subprocess.run([sys.executable, 'app.py'])
        
    except KeyboardInterrupt:
        print("\n⏹️  Flask app stopped by user")
    except Exception as e:
        print(f"❌ Error restarting Flask app: {e}")

if __name__ == "__main__":
    restart_flask_app()
