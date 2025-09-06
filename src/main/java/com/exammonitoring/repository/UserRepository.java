package com.exammonitoring.repository;

import com.exammonitoring.entity.User;
import com.exammonitoring.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    List<User> findByRole(UserRole role);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.name")
    List<User> findUsersByRoleOrderByName(@Param("role") UserRole role);
}
