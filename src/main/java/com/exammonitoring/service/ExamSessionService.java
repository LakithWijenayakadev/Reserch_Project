package com.exammonitoring.service;

import com.exammonitoring.entity.Exam;
import com.exammonitoring.entity.ExamSession;
import com.exammonitoring.entity.SessionStatus;
import com.exammonitoring.repository.ExamRepository;
import com.exammonitoring.repository.ExamSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ExamSessionService {
    
    @Autowired
    private ExamSessionRepository examSessionRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    public ExamSession startExamSession(Long examId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));
        
        if (exam.getStatus() != com.exammonitoring.entity.ExamStatus.ACTIVE) {
            throw new IllegalStateException("Exam is not active");
        }
        
        ExamSession session = new ExamSession(exam);
        return examSessionRepository.save(session);
    }
    
    public ExamSession stopExamSession(Long sessionId) {
        ExamSession session = examSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));
        
        session.setStatus(SessionStatus.COMPLETED);
        session.setEndedAt(LocalDateTime.now());
        
        // Update exam status to completed
        Exam exam = session.getExam();
        exam.setStatus(com.exammonitoring.entity.ExamStatus.COMPLETED);
        examRepository.save(exam);
        
        return examSessionRepository.save(session);
    }
    
    public List<ExamSession> getAllSessions() {
        return examSessionRepository.findAll();
    }
    
    public List<ExamSession> getSessionsByStatus(SessionStatus status) {
        return examSessionRepository.findByStatus(status);
    }
    
    public List<ExamSession> getSessionsByExamId(Long examId) {
        return examSessionRepository.findByExamId(examId);
    }
    
    public Optional<ExamSession> getSessionById(Long id) {
        return examSessionRepository.findById(id);
    }
    
    public ExamSession addStudentToSession(Long sessionId, String studentUsername) {
        ExamSession session = examSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));
        
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Session is not active");
        }
        
        if (!session.getStudents().contains(studentUsername)) {
            session.getStudents().add(studentUsername);
            session.getStudentAnswers().put(studentUsername, "");
        }
        
        return examSessionRepository.save(session);
    }
    
    public ExamSession submitAnswer(Long sessionId, String studentUsername, String questionId, String answer) {
        ExamSession session = examSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));
        
        if (!session.getStudents().contains(studentUsername)) {
            throw new IllegalArgumentException("Student not in this session");
        }
        
        String currentAnswers = session.getStudentAnswers().get(studentUsername);
        if (currentAnswers == null || currentAnswers.isEmpty()) {
            currentAnswers = questionId + ":" + answer;
        } else {
            currentAnswers += ";" + questionId + ":" + answer;
        }
        session.getStudentAnswers().put(studentUsername, currentAnswers);
        
        return examSessionRepository.save(session);
    }
    
    public List<ExamSession> getActiveSessions() {
        return examSessionRepository.findByStatus(SessionStatus.ACTIVE);
    }
}
