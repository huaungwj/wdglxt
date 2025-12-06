package com.example.dms;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码测试类
 * 用于生成和验证BCrypt密码哈希值
 */
public class PasswordTest {
    
    @Test
    public void generateAndTestPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        // 生成多个哈希值，确保至少有一个可以工作
        System.out.println("========================================");
        System.out.println("密码: " + password);
        System.out.println("========================================");
        
        for (int i = 0; i < 5; i++) {
            String hash = encoder.encode(password);
            boolean matches = encoder.matches(password, hash);
            System.out.println("哈希 " + (i + 1) + ": " + hash);
            System.out.println("验证结果: " + matches);
            System.out.println("SQL: UPDATE sys_user SET password = '" + hash + "' WHERE username = 'admin';");
            System.out.println("---");
        }
        
        // 测试一些常见的哈希值
        String[] testHashes = {
            "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lVZ5mUyHH7HQd/L9u",
            "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi",
            "$2a$10$rKqJqJqJqJqJqJqJqJqJ.OqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ"
        };
        
        System.out.println("\n测试现有哈希值:");
        for (String hash : testHashes) {
            boolean matches = encoder.matches(password, hash);
            System.out.println("哈希: " + hash);
            System.out.println("验证结果: " + matches);
            if (matches) {
                System.out.println("✓ 这个哈希值可以用于更新数据库!");
            }
            System.out.println("---");
        }
    }
}

