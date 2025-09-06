package com.exammonitoring.service;

import com.exammonitoring.entity.User;
import com.exammonitoring.entity.UserRole;
import com.exammonitoring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User createUser(String username, String password, String name, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        User user = new User(username, passwordEncoder.encode(password), name, role);
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User updateLastLogin(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public boolean authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
    
    public void initializeDefaultUsers() {
        // Create default admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            createUser("admin", "admin123", "Administrator", UserRole.ADMIN);
        }
        
        // Create default student users if not exist
        if (!userRepository.existsByUsername("student1")) {
            createUser("student1", "password1", "John Doe", UserRole.STUDENT);
        }
        
        if (!userRepository.existsByUsername("student2")) {
            createUser("student2", "password2", "Jane Smith", UserRole.STUDENT);
        }
    }
}
