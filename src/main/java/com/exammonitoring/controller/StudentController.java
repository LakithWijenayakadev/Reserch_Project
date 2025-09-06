package com.exammonitoring.controller;

import com.exammonitoring.entity.ExamSession;
import com.exammonitoring.service.ExamSessionService;
import com.exammonitoring.service.DetectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentController {
    
    @Autowired
    private ExamSessionService examSessionService;
    
    @Autowired
    private DetectionService detectionService;
    
    @GetMapping("/exams")
    public String studentExams(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return "redirect:/login";
        }
        
        // Get active exam sessions
        List<ExamSession> activeSessions = examSessionService.getActiveSessions();
        List<Map<String, Object>> sessions = new ArrayList<>();
        
        for (ExamSession sessionData : activeSessions) {
            Map<String, Object> sessionInfo = Map.of(
                "session_id", sessionData.getId(),
                "exam", sessionData.getExam(),
                "started_at", sessionData.getStartedAt()
            );
            sessions.add(sessionInfo);
        }
        
        model.addAttribute("sessions", sessions);
        return "student_exams";
    }
    
    @GetMapping("/start_exam/{sessionId}")
    public String startExamDirect(@PathVariable Long sessionId, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return "redirect:/login";
        }
        
        try {
            ExamSession sessionData = examSessionService.getSessionById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Exam session not found"));
            
            if (sessionData.getStatus().name().equals("COMPLETED")) {
                model.addAttribute("message", "Exam session is not active");
                return "error";
            }
            
            // Add student to session if not already added
            examSessionService.addStudentToSession(sessionId, username);
            
            model.addAttribute("exam", sessionData.getExam());
            model.addAttribute("session_id", sessionId);
            model.addAttribute("student", username);
            
            return "exam_interface";
        } catch (Exception e) {
            model.addAttribute("message", "Exam session not found");
            return "error";
        }
    }
    
    @PostMapping("/submit_answer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitAnswer(@RequestBody Map<String, String> answerData, 
                                                          HttpSession session) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            Long sessionId = Long.parseLong(answerData.get("session_id"));
            String questionId = answerData.get("question_id");
            String answer = answerData.get("answer");
            
            examSessionService.submitAnswer(sessionId, username, questionId, answer);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/exam_detection")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> examDetection(@RequestBody Map<String, String> detectionData, 
                                                           HttpSession session) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            Long sessionId = Long.parseLong(detectionData.get("session_id"));
            String detectionType = detectionData.get("detection_type");
            
            // Record detection in session
            // Note: This would need to be implemented in ExamSessionService
            // For now, we'll just return success
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public String studentHistory(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return "redirect:/login";
        }
        
        Map<String, Integer> counts = detectionService.getDetectionCountsByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("name", session.getAttribute("userName"));
        model.addAttribute("counts", counts);
        
        return "student_history";
    }
}
