package com.exammonitoring.controller;

import com.exammonitoring.entity.Exam;
import com.exammonitoring.entity.ExamSession;
import com.exammonitoring.service.ExamService;
import com.exammonitoring.service.ExamSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    @Autowired
    private ExamSessionService examSessionService;
    
    @GetMapping("/create_exam")
    public String createExamPage(HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return "redirect:/login";
        }
        return "create_exam";
    }
    
    @PostMapping("/create_exam")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createExam(@RequestBody Map<String, Object> examData, 
                                                         HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            String title = (String) examData.get("title");
            String description = (String) examData.get("description");
            Integer durationMinutes = (Integer) examData.get("duration_minutes");
            String createdBy = (String) session.getAttribute("username");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) examData.get("questions");
            
            Exam exam = examService.createExam(title, description, durationMinutes, createdBy, questions);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exam_id", exam.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/exams")
    public String listExams(HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return "redirect:/login";
        }
        
        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        return "admin_exams";
    }
    
    @PostMapping("/start_exam/{examId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> startExam(@PathVariable Long examId, HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            ExamSession sessionData = examSessionService.startExamSession(examId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "session_id", sessionData.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/stop_exam/{sessionId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> stopExam(@PathVariable Long sessionId, HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            examSessionService.stopExamSession(sessionId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/exam_results/{sessionId}")
    public String examResults(@PathVariable Long sessionId, HttpSession session, Model model) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return "redirect:/login";
        }
        
        try {
            ExamSession sessionData = examSessionService.getSessionById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
            
            model.addAttribute("exam", sessionData.getExam());
            model.addAttribute("session", sessionData);
            model.addAttribute("students", sessionData.getStudents());
            
            return "exam_results";
        } catch (Exception e) {
            model.addAttribute("message", "Session not found");
            return "error";
        }
    }
}
