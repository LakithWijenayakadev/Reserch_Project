// Exam Monitoring JavaScript
// This file contains the client-side logic for the exam monitoring system

class ExamMonitoring {
    constructor() {
        this.video = null;
        this.canvas = null;
        this.ctx = null;
        this.isMonitoring = false;
        this.alertCooldowns = {};
        this.lastAlertTime = {};
        this.alertCooldownSeconds = 5;
        this.init();
    }

    init() {
        this.setupVideo();
        this.setupEventListeners();
        this.startMonitoring();
    }

    setupVideo() {
        this.video = document.getElementById('video');
        this.canvas = document.getElementById('canvas');
        this.ctx = this.canvas.getContext('2d');
        
        if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
            navigator.mediaDevices.getUserMedia({ video: true })
                .then(stream => {
                    this.video.srcObject = stream;
                    this.video.play();
                })
                .catch(err => {
                    console.error('Error accessing camera:', err);
                    this.showAlert('Camera access denied. Please allow camera access to continue.', 'error');
                });
        } else {
            console.error('getUserMedia not supported');
            this.showAlert('Camera not supported on this device.', 'error');
        }
    }

    setupEventListeners() {
        // Tab switching detection
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                this.handleTabSwitch();
            }
        });

        // Window focus/blur detection
        window.addEventListener('blur', () => {
            this.handleTabSwitch();
        });

        // Copy/paste detection
        document.addEventListener('copy', (e) => {
            this.handleCopyPaste('copy');
        });

        document.addEventListener('paste', (e) => {
            this.handleCopyPaste('paste');
        });

        // Keyboard shortcuts detection
        document.addEventListener('keydown', (e) => {
            this.handleKeyboardShortcuts(e);
        });

        // Right-click detection
        document.addEventListener('contextmenu', (e) => {
            e.preventDefault();
            this.handleRightClick();
        });
    }

    startMonitoring() {
        if (!this.isMonitoring) {
            this.isMonitoring = true;
            this.monitorLoop();
        }
    }

    stopMonitoring() {
        this.isMonitoring = false;
    }

    monitorLoop() {
        if (!this.isMonitoring) return;

        if (this.video && this.video.readyState === this.video.HAVE_ENOUGH_DATA) {
            this.captureFrame();
        }

        setTimeout(() => this.monitorLoop(), 1000); // Check every second
    }

    captureFrame() {
        if (!this.video || !this.canvas || !this.ctx) return;

        this.ctx.drawImage(this.video, 0, 0, this.canvas.width, this.canvas.height);
        
        const imageData = this.canvas.toDataURL('image/jpeg', 0.8);
        this.analyzeFrame(imageData);
    }

    analyzeFrame(imageData) {
        fetch('/api/detection/analyze', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ image: imageData })
        })
        .then(response => response.json())
        .then(data => {
            if (data.alert) {
                this.handleDetection(data);
            }
        })
        .catch(error => {
            console.error('Error analyzing frame:', error);
        });
    }

    handleDetection(data) {
        const detectionType = data.sound_type;
        const message = data.message;
        
        if (this.shouldSendAlert(detectionType)) {
            this.showAlert(message, detectionType);
            this.updateAlertTime(detectionType);
        }
    }

    handleTabSwitch() {
        fetch('/api/detection/tab_switch', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.alert) {
                this.showAlert(data.message, 'tab_switching');
            }
        })
        .catch(error => {
            console.error('Error reporting tab switch:', error);
        });
    }

    handleCopyPaste(action) {
        this.showAlert(`${action.charAt(0).toUpperCase() + action.slice(1)} detected! Please focus on the exam.`, 'warning');
    }

    handleKeyboardShortcuts(e) {
        // Detect common shortcuts that might be used for cheating
        if (e.ctrlKey || e.metaKey) {
            const key = e.key.toLowerCase();
            if (['c', 'v', 'x', 'a', 'z', 'y', 'f'].includes(key)) {
                this.showAlert('Keyboard shortcut detected! Please focus on the exam.', 'warning');
            }
        }
    }

    handleRightClick() {
        this.showAlert('Right-click disabled during exam. Please focus on the exam.', 'warning');
    }

    shouldSendAlert(detectionType) {
        const now = Date.now();
        const lastAlert = this.lastAlertTime[detectionType];
        
        if (!lastAlert) return true;
        
        const timeSinceLastAlert = now - lastAlert;
        return timeSinceLastAlert >= (this.alertCooldownSeconds * 1000);
    }

    updateAlertTime(detectionType) {
        this.lastAlertTime[detectionType] = Date.now();
    }

    showAlert(message, type = 'info') {
        // Create alert element
        const alert = document.createElement('div');
        alert.className = `alert alert-${type}`;
        alert.innerHTML = `
            <div class="alert-content">
                <span class="alert-icon">⚠️</span>
                <span class="alert-message">${message}</span>
                <button class="alert-close" onclick="this.parentElement.parentElement.remove()">×</button>
            </div>
        `;

        // Add styles
        alert.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${this.getAlertColor(type)};
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
            z-index: 10000;
            max-width: 400px;
            animation: slideIn 0.3s ease-out;
        `;

        // Add to page
        document.body.appendChild(alert);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (alert.parentElement) {
                alert.remove();
            }
        }, 5000);

        // Play sound
        this.playAlertSound(type);

        // Speak the message
        this.speakMessage(message);
    }

    getAlertColor(type) {
        const colors = {
            'looking_away': '#ff6b6b',
            'multiple_people': '#ff6b6b',
            'no_face': '#ff6b6b',
            'blur_screen': '#ffa726',
            'tab_switching': '#ff6b6b',
            'warning': '#ffa726',
            'error': '#f44336',
            'info': '#2196f3'
        };
        return colors[type] || colors.info;
    }

    playAlertSound(type) {
        // Create audio context for sound generation
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();

        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);

        // Different frequencies for different alert types
        const frequencies = {
            'looking_away': 800,
            'multiple_people': 1000,
            'no_face': 600,
            'blur_screen': 400,
            'tab_switching': 1200,
            'warning': 500,
            'error': 300
        };

        oscillator.frequency.setValueAtTime(frequencies[type] || 500, audioContext.currentTime);
        oscillator.type = 'sine';

        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);

        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.5);
    }

    speakMessage(message) {
        if ('speechSynthesis' in window) {
            const utterance = new SpeechSynthesisUtterance(message);
            utterance.rate = 0.9;
            utterance.pitch = 1;
            utterance.volume = 0.8;
            speechSynthesis.speak(utterance);
        }
    }
}

// Initialize monitoring when page loads
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('video')) {
        window.examMonitoring = new ExamMonitoring();
    }
});

// Add CSS for alerts
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    .alert-content {
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    .alert-icon {
        font-size: 20px;
    }
    
    .alert-message {
        flex: 1;
        font-weight: 500;
    }
    
    .alert-close {
        background: none;
        border: none;
        color: white;
        font-size: 20px;
        cursor: pointer;
        padding: 0;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .alert-close:hover {
        background: rgba(255, 255, 255, 0.2);
        border-radius: 50%;
    }
`;
document.head.appendChild(style);
