package com.exammonitoring.repository;

import com.exammonitoring.entity.DetectionHistory;
import com.exammonitoring.entity.DetectionType;
import com.exammonitoring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetectionHistoryRepository extends JpaRepository<DetectionHistory, Long> {
    
    List<DetectionHistory> findByUser(User user);
    
    List<DetectionHistory> findByUserUsername(String username);
    
    Optional<DetectionHistory> findByUserAndDetectionType(User user, DetectionType detectionType);
    
    Optional<DetectionHistory> findByUserUsernameAndDetectionType(String username, DetectionType detectionType);
    
    @Query("SELECT dh FROM DetectionHistory dh WHERE dh.user.username = :username ORDER BY dh.lastDetection DESC")
    List<DetectionHistory> findDetectionHistoryByUsernameOrderByLastDetectionDesc(@Param("username") String username);
    
    @Query("SELECT SUM(dh.detectionCount) FROM DetectionHistory dh WHERE dh.user.username = :username")
    Integer getTotalDetectionCountByUsername(@Param("username") String username);
}
