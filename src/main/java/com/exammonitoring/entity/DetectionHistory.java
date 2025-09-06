package com.exammonitoring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "detection_history")
public class DetectionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DetectionType detectionType;
    
    @Column(name = "detection_count")
    private Integer detectionCount = 0;
    
    @Column(name = "last_detection")
    private LocalDateTime lastDetection;
    
    @Column(name = "alert_count")
    private Integer alertCount = 0;
    
    @Column(name = "last_alert")
    private LocalDateTime lastAlert;
    
    // Constructors
    public DetectionHistory() {}
    
    public DetectionHistory(User user, DetectionType detectionType) {
        this.user = user;
        this.detectionType = detectionType;
        this.lastDetection = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public DetectionType getDetectionType() {
        return detectionType;
    }
    
    public void setDetectionType(DetectionType detectionType) {
        this.detectionType = detectionType;
    }
    
    public Integer getDetectionCount() {
        return detectionCount;
    }
    
    public void setDetectionCount(Integer detectionCount) {
        this.detectionCount = detectionCount;
    }
    
    public LocalDateTime getLastDetection() {
        return lastDetection;
    }
    
    public void setLastDetection(LocalDateTime lastDetection) {
        this.lastDetection = lastDetection;
    }
    
    public Integer getAlertCount() {
        return alertCount;
    }
    
    public void setAlertCount(Integer alertCount) {
        this.alertCount = alertCount;
    }
    
    public LocalDateTime getLastAlert() {
        return lastAlert;
    }
    
    public void setLastAlert(LocalDateTime lastAlert) {
        this.lastAlert = lastAlert;
    }
}
