package com.example.dms;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    
    @Test
    public void testPasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        // 生成新的哈希
        String newHash = encoder.encode(password);
        System.out.println("新生成的BCrypt哈希: " + newHash);
        
        // 验证现有哈希
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lVZ5mUyHH7HQd/L9u";
        boolean matches = encoder.matches(password, existingHash);
        System.out.println("现有哈希验证结果: " + matches);
        
        // 验证新生成的哈希
        boolean newMatches = encoder.matches(password, newHash);
        System.out.println("新哈希验证结果: " + newMatches);
    }
}

