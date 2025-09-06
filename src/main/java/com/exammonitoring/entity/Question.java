package com.exammonitoring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Entity
@Table(name = "questions")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    
    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @MapKeyColumn(name = "option_key")
    @Column(name = "option_value", columnDefinition = "TEXT")
    private Map<String, String> options;
    
    @NotBlank
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
    
    // Constructors
    public Question() {}
    
    public Question(String text, Map<String, String> options, String correctAnswer) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Map<String, String> getOptions() {
        return options;
    }
    
    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public Exam getExam() {
        return exam;
    }
    
    public void setExam(Exam exam) {
        this.exam = exam;
    }
}
