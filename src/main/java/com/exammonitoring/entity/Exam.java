package com.exammonitoring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exams")
public class Exam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Positive
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @NotBlank
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamStatus status;
    
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamSession> examSessions;
    
    // Constructors
    public Exam() {
        this.createdAt = LocalDateTime.now();
        this.status = ExamStatus.DRAFT;
    }
    
    public Exam(String title, String description, Integer durationMinutes, String createdBy) {
        this();
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public ExamStatus getStatus() {
        return status;
    }
    
    public void setStatus(ExamStatus status) {
        this.status = status;
    }
    
    public List<ExamSession> getExamSessions() {
        return examSessions;
    }
    
    public void setExamSessions(List<ExamSession> examSessions) {
        this.examSessions = examSessions;
    }
}
