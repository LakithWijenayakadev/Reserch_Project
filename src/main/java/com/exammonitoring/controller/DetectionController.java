package com.exammonitoring.controller;

import com.exammonitoring.entity.DetectionType;
import com.exammonitoring.service.DetectionService;
import com.exammonitoring.service.ComputerVisionService;
import jakarta.servlet.http.HttpSession;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/detection")
public class DetectionController {
    
    @Autowired
    private DetectionService detectionService;
    
    @Autowired
    private ComputerVisionService computerVisionService;
    
    @Value("${app.monitoring.alert-cooldown-seconds:5}")
    private int alertCooldownSeconds;
    
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeFrame(@RequestBody Map<String, String> payload, 
                                                          HttpSession session) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        String imageData = payload.get("image");
        if (imageData == null || imageData.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No image data provided"));
        }
        
        try {
            // Decode base64 image
            String base64Data = imageData.split(",")[1];
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            
            // Convert to OpenCV Mat
            Mat imageBgr = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
            
            // Analyze frame
            String decision = computerVisionService.analyzeFrame(imageBgr);
            
            Map<String, Object> response = new HashMap<>();
            response.put("alert", false);
            response.put("message", "Everything looks good. Keep focusing on your exam!");
            response.put("sound_type", null);
            response.put("screen_blurred", false);
            
            if (!"none".equals(decision)) {
                DetectionType detectionType = mapDecisionToDetectionType(decision);
                
                // Check if we should send alert
                boolean shouldAlert = detectionService.shouldSendAlert(username, detectionType, alertCooldownSeconds);
                
                // Always record detection
                detectionService.recordDetection(username, detectionType);
                
                if (shouldAlert) {
                    String alertMessage = getAlertMessage(detectionType);
                    detectionService.updateAlertTime(username, detectionType);
                    
                    response.put("alert", true);
                    response.put("message", alertMessage);
                    response.put("sound_type", decision);
                    response.put("screen_blurred", "blur_screen".equals(decision));
                }
            } else {
                // Reset cooldowns when face is properly detected
                detectionService.resetAlertCooldowns(username);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error processing image: " + e.getMessage()));
        }
    }
    
    @PostMapping("/tab_switch")
    public ResponseEntity<Map<String, Object>> tabSwitch(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        DetectionType detectionType = DetectionType.TAB_SWITCHING;
        boolean shouldAlert = detectionService.shouldSendAlert(username, detectionType, alertCooldownSeconds);
        
        // Always record detection
        detectionService.recordDetection(username, detectionType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("alert", false);
        
        if (shouldAlert) {
            String alertMessage = "Please stay on the exam page. Tab switching detected!";
            detectionService.updateAlertTime(username, detectionType);
            
            response.put("alert", true);
            response.put("message", alertMessage);
            response.put("sound_type", "tab_switching");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history/{username}")
    public ResponseEntity<Map<String, Object>> getDetectionHistory(@PathVariable String username, 
                                                                 HttpSession session) {
        String sessionUsername = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (sessionUsername == null || !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        Map<String, Integer> counts = detectionService.getDetectionCountsByUsername(username);
        return ResponseEntity.ok(counts);
    }
    
    private DetectionType mapDecisionToDetectionType(String decision) {
        return switch (decision) {
            case "looking_away" -> DetectionType.LOOKING_AWAY;
            case "multiple_people" -> DetectionType.MULTIPLE_PEOPLE;
            case "no_face" -> DetectionType.NO_FACE;
            case "blur_screen" -> DetectionType.BLUR_SCREEN;
            default -> DetectionType.LOOKING_AWAY;
        };
    }
    
    private String getAlertMessage(DetectionType detectionType) {
        return switch (detectionType) {
            case LOOKING_AWAY -> "Looking away from screen detected!";
            case MULTIPLE_PEOPLE -> "Multiple people detected!";
            case NO_FACE -> "No face detected!";
            case BLUR_SCREEN -> "Screen blur detected!";
            case TAB_SWITCHING -> "Tab switching detected!";
        };
    }
}
