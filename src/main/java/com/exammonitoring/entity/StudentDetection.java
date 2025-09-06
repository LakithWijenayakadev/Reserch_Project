package com.exammonitoring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_detections")
public class StudentDetection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ExamSession examSession;
    
    @NotBlank
    @Column(name = "student_username", nullable = false)
    private String studentUsername;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DetectionType detectionType;
    
    @Column(name = "detected_at")
    private LocalDateTime detectedAt;
    
    @Column(name = "alert_sent")
    private Boolean alertSent = false;
    
    @Column(name = "alert_message", columnDefinition = "TEXT")
    private String alertMessage;
    
    // Constructors
    public StudentDetection() {
        this.detectedAt = LocalDateTime.now();
    }
    
    public StudentDetection(ExamSession examSession, String studentUsername, DetectionType detectionType) {
        this();
        this.examSession = examSession;
        this.studentUsername = studentUsername;
        this.detectionType = detectionType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ExamSession getExamSession() {
        return examSession;
    }
    
    public void setExamSession(ExamSession examSession) {
        this.examSession = examSession;
    }
    
    public String getStudentUsername() {
        return studentUsername;
    }
    
    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }
    
    public DetectionType getDetectionType() {
        return detectionType;
    }
    
    public void setDetectionType(DetectionType detectionType) {
        this.detectionType = detectionType;
    }
    
    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }
    
    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
    
    public Boolean getAlertSent() {
        return alertSent;
    }
    
    public void setAlertSent(Boolean alertSent) {
        this.alertSent = alertSent;
    }
    
    public String getAlertMessage() {
        return alertMessage;
    }
    
    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }
}
