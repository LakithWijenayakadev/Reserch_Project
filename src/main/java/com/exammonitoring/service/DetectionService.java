package com.exammonitoring.service;

import com.exammonitoring.entity.DetectionHistory;
import com.exammonitoring.entity.DetectionType;
import com.exammonitoring.entity.StudentDetection;
import com.exammonitoring.entity.User;
import com.exammonitoring.repository.DetectionHistoryRepository;
import com.exammonitoring.repository.StudentDetectionRepository;
import com.exammonitoring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class DetectionService {
    
    @Autowired
    private DetectionHistoryRepository detectionHistoryRepository;
    
    @Autowired
    private StudentDetectionRepository studentDetectionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public DetectionHistory recordDetection(String username, DetectionType detectionType) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Optional<DetectionHistory> existingHistory = detectionHistoryRepository
            .findByUserAndDetectionType(user, detectionType);
        
        DetectionHistory history;
        if (existingHistory.isPresent()) {
            history = existingHistory.get();
            history.setDetectionCount(history.getDetectionCount() + 1);
            history.setLastDetection(LocalDateTime.now());
        } else {
            history = new DetectionHistory(user, detectionType);
        }
        
        return detectionHistoryRepository.save(history);
    }
    
    public StudentDetection recordStudentDetection(Long sessionId, String studentUsername, 
                                                  DetectionType detectionType, String alertMessage) {
        StudentDetection detection = new StudentDetection();
        detection.setStudentUsername(studentUsername);
        detection.setDetectionType(detectionType);
        detection.setAlertMessage(alertMessage);
        detection.setAlertSent(alertMessage != null && !alertMessage.isEmpty());
        
        // Set exam session
        // Note: In a real implementation, you'd fetch the session and set it
        // For now, we'll handle this in the controller
        
        return studentDetectionRepository.save(detection);
    }
    
    public List<DetectionHistory> getDetectionHistoryByUsername(String username) {
        return detectionHistoryRepository.findDetectionHistoryByUsernameOrderByLastDetectionDesc(username);
    }
    
    public List<StudentDetection> getStudentDetectionsBySession(Long sessionId) {
        return studentDetectionRepository.findByExamSessionId(sessionId);
    }
    
    public List<StudentDetection> getStudentDetectionsBySessionAndStudent(Long sessionId, String studentUsername) {
        return studentDetectionRepository.findByExamSessionIdAndStudentUsername(sessionId, studentUsername);
    }
    
    public Map<String, Integer> getDetectionCountsByUsername(String username) {
        List<DetectionHistory> histories = detectionHistoryRepository.findByUserUsername(username);
        Map<String, Integer> counts = new java.util.HashMap<>();
        
        for (DetectionHistory history : histories) {
            counts.put(history.getDetectionType().name().toLowerCase(), history.getDetectionCount());
        }
        
        // Initialize missing detection types with 0
        for (DetectionType type : DetectionType.values()) {
            counts.putIfAbsent(type.name().toLowerCase(), 0);
        }
        
        return counts;
    }
    
    public boolean shouldSendAlert(String username, DetectionType detectionType, int cooldownSeconds) {
        Optional<DetectionHistory> history = detectionHistoryRepository
            .findByUserUsernameAndDetectionType(username, detectionType);
        
        if (history.isPresent()) {
            LocalDateTime lastAlert = history.get().getLastAlert();
            if (lastAlert != null) {
                LocalDateTime cooldownEnd = lastAlert.plusSeconds(cooldownSeconds);
                return LocalDateTime.now().isAfter(cooldownEnd);
            }
        }
        
        return true; // No previous alert, so we can send one
    }
    
    public void updateAlertTime(String username, DetectionType detectionType) {
        Optional<DetectionHistory> history = detectionHistoryRepository
            .findByUserUsernameAndDetectionType(username, detectionType);
        
        if (history.isPresent()) {
            DetectionHistory detectionHistory = history.get();
            detectionHistory.setLastAlert(LocalDateTime.now());
            detectionHistory.setAlertCount(detectionHistory.getAlertCount() + 1);
            detectionHistoryRepository.save(detectionHistory);
        }
    }
    
    public void resetAlertCooldowns(String username) {
        List<DetectionHistory> histories = detectionHistoryRepository.findByUserUsername(username);
        for (DetectionHistory history : histories) {
            history.setLastAlert(null);
        }
        detectionHistoryRepository.saveAll(histories);
    }
}
