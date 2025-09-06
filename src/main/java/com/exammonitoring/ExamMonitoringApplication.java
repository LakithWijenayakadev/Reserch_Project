package com.exammonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ExamMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamMonitoringApplication.class, args);
        System.out.println("🎓 Student Behavior Monitoring System — http://127.0.0.1:8080");
        System.out.println("Admin: admin/admin123 | Students: student1/password1, student2/password2");
    }
}
