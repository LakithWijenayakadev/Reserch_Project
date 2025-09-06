package com.exammonitoring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "exam_sessions")
public class ExamSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;
    
    @ElementCollection
    @CollectionTable(name = "session_students", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "student_username")
    private List<String> students;
    
    @ElementCollection
    @CollectionTable(name = "student_answers", joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "student_username")
    @Column(name = "answers", columnDefinition = "TEXT")
    private Map<String, String> studentAnswers;
    
    @OneToMany(mappedBy = "examSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentDetection> studentDetections;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;
    
    // Constructors
    public ExamSession() {
        this.startedAt = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
    }
    
    public ExamSession(Exam exam) {
        this();
        this.exam = exam;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Exam getExam() {
        return exam;
    }
    
    public void setExam(Exam exam) {
        this.exam = exam;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getEndedAt() {
        return endedAt;
    }
    
    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
    
    public SessionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SessionStatus status) {
        this.status = status;
    }
    
    public List<String> getStudents() {
        return students;
    }
    
    public void setStudents(List<String> students) {
        this.students = students;
    }
    
    public Map<String, String> getStudentAnswers() {
        return studentAnswers;
    }
    
    public void setStudentAnswers(Map<String, String> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }
    
    public List<StudentDetection> getStudentDetections() {
        return studentDetections;
    }
    
    public void setStudentDetections(List<StudentDetection> studentDetections) {
        this.studentDetections = studentDetections;
    }
    
    public User getStudent() {
        return student;
    }
    
    public void setStudent(User student) {
        this.student = student;
    }
}
