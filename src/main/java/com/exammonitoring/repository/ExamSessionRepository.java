package com.exammonitoring.repository;

import com.exammonitoring.entity.ExamSession;
import com.exammonitoring.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    
    List<ExamSession> findByStatus(SessionStatus status);
    
    List<ExamSession> findByExamId(Long examId);
    
    @Query("SELECT es FROM ExamSession es WHERE es.status = :status ORDER BY es.startedAt DESC")
    List<ExamSession> findSessionsByStatusOrderByStartedAtDesc(@Param("status") SessionStatus status);
    
    @Query("SELECT es FROM ExamSession es WHERE es.exam.id = :examId ORDER BY es.startedAt DESC")
    List<ExamSession> findSessionsByExamIdOrderByStartedAtDesc(@Param("examId") Long examId);
}
