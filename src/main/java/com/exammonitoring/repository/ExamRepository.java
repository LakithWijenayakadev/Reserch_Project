package com.exammonitoring.repository;

import com.exammonitoring.entity.Exam;
import com.exammonitoring.entity.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    List<Exam> findByStatus(ExamStatus status);
    
    List<Exam> findByCreatedBy(String createdBy);
    
    @Query("SELECT e FROM Exam e WHERE e.status = :status ORDER BY e.createdAt DESC")
    List<Exam> findExamsByStatusOrderByCreatedAtDesc(@Param("status") ExamStatus status);
    
    @Query("SELECT e FROM Exam e WHERE e.createdBy = :createdBy ORDER BY e.createdAt DESC")
    List<Exam> findExamsByCreatedByOrderByCreatedAtDesc(@Param("createdBy") String createdBy);
}
