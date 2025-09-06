package com.exammonitoring.service;

import com.exammonitoring.entity.Exam;
import com.exammonitoring.entity.ExamStatus;
import com.exammonitoring.entity.Question;
import com.exammonitoring.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ExamService {
    
    @Autowired
    private ExamRepository examRepository;
    
    public Exam createExam(String title, String description, Integer durationMinutes, 
                          String createdBy, List<Map<String, Object>> questionsData) {
        Exam exam = new Exam(title, description, durationMinutes, createdBy);
        exam = examRepository.save(exam);
        
        // Create questions
        for (Map<String, Object> questionData : questionsData) {
            Question question = new Question();
            question.setText((String) questionData.get("text"));
            question.setOptions((Map<String, String>) questionData.get("options"));
            question.setCorrectAnswer((String) questionData.get("correct_answer"));
            question.setExam(exam);
            exam.getQuestions().add(question);
        }
        
        return examRepository.save(exam);
    }
    
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
    
    public List<Exam> getExamsByStatus(ExamStatus status) {
        return examRepository.findByStatus(status);
    }
    
    public List<Exam> getExamsByCreatedBy(String createdBy) {
        return examRepository.findByCreatedBy(createdBy);
    }
    
    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }
    
    public Exam updateExamStatus(Long examId, ExamStatus status) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));
        exam.setStatus(status);
        return examRepository.save(exam);
    }
    
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new IllegalArgumentException("Exam not found with id: " + id);
        }
        examRepository.deleteById(id);
    }
    
    public List<Exam> getActiveExams() {
        return examRepository.findByStatus(ExamStatus.ACTIVE);
    }
}
