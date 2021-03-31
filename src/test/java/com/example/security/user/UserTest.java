package com.example.security.user;

import com.example.security.Entity.User;
import com.example.security.Entity.UserRepository;
import com.example.security.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class UserTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ReportService reportService;

    @Test
    public void load() {
        // 잘 돌아가는지 확인용
    }

    @Test
    public void run() {
        User byUsername = userRepository.findByUsername("admin@pharmcadd.com");
        System.out.println(byUsername);
        System.out.println(byUsername.getPassword());
    }

    @Test
    public void changePassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String current = "qwer";
        String newPassword = "1234";
        User byUsername = reportService.authReturn("admin@pharmcadd.com");
        if (passwordEncoder.matches(current, byUsername.getPassword())) {
            System.out.println("===match===");
            String encrypted = passwordEncoder.encode(newPassword);
            byUsername.setPassword(encrypted);
            userRepository.save(byUsername);
        } else {
            System.out.println("===not match===");
        }
    }
}
