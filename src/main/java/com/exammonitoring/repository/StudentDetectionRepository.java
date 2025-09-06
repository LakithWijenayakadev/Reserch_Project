package com.exammonitoring.repository;

import com.exammonitoring.entity.StudentDetection;
import com.exammonitoring.entity.DetectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentDetectionRepository extends JpaRepository<StudentDetection, Long> {
    
    List<StudentDetection> findByExamSessionId(Long sessionId);
    
    List<StudentDetection> findByStudentUsername(String studentUsername);
    
    List<StudentDetection> findByDetectionType(DetectionType detectionType);
    
    List<StudentDetection> findByExamSessionIdAndStudentUsername(Long sessionId, String studentUsername);
    
    @Query("SELECT sd FROM StudentDetection sd WHERE sd.examSession.id = :sessionId AND sd.studentUsername = :username ORDER BY sd.detectedAt DESC")
    List<StudentDetection> findDetectionsBySessionAndStudentOrderByDetectedAtDesc(
        @Param("sessionId") Long sessionId, 
        @Param("username") String username
    );
    
    @Query("SELECT COUNT(sd) FROM StudentDetection sd WHERE sd.examSession.id = :sessionId AND sd.studentUsername = :username AND sd.detectionType = :detectionType")
    Long countDetectionsBySessionStudentAndType(
        @Param("sessionId") Long sessionId, 
        @Param("username") String username, 
        @Param("detectionType") DetectionType detectionType
    );
    
    @Query("SELECT sd FROM StudentDetection sd WHERE sd.detectedAt >= :startTime AND sd.detectedAt <= :endTime")
    List<StudentDetection> findDetectionsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
