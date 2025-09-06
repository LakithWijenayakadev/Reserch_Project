package com.exammonitoring.controller;

import com.exammonitoring.entity.User;
import com.exammonitoring.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password, 
                       HttpSession session, 
                       Model model) {
        if (userService.authenticateUser(username, password)) {
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
                session.setAttribute("username", username);
                session.setAttribute("userRole", user.getRole().name());
                session.setAttribute("userName", user.getName());
                
                // Update last login
                userService.updateLastLogin(username);
                
                if (user.getRole().name().equals("ADMIN")) {
                    return "redirect:/admin";
                } else {
                    return "redirect:/dashboard";
                }
            }
        }
        
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return "redirect:/login";
        }
        
        model.addAttribute("username", username);
        model.addAttribute("name", session.getAttribute("userName"));
        return "dashboard";
    }
    
    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"ADMIN".equals(userRole)) {
            return "redirect:/login";
        }
        
        model.addAttribute("users", userService.getAllUsers());
        return "admin_dashboard";
    }
}
