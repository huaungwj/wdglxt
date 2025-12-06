package com.example.dms.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 * 用于生成BCrypt密码哈希值
 * 
 * 使用方法：
 * 1. 运行此类的main方法
 * 2. 复制输出的BCrypt哈希值
 * 3. 更新data.sql或直接执行SQL更新数据库
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        // 生成新的哈希
        String encoded = encoder.encode(password);
        System.out.println("========================================");
        System.out.println("密码: " + password);
        System.out.println("新生成的BCrypt哈希: " + encoded);
        System.out.println("========================================");
        
        // 验证新生成的哈希
        boolean matches = encoder.matches(password, encoded);
        System.out.println("新哈希验证结果: " + matches);
        
        // 验证现有哈希（data.sql中的）
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lVZ5mUyHH7HQd/L9u";
        boolean existingMatches = encoder.matches(password, existingHash);
        System.out.println("现有哈希验证结果: " + existingMatches);
        
        // 测试一个已知可以验证admin123的哈希值
        String knownHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
        boolean knownMatches = encoder.matches(password, knownHash);
        System.out.println("已知哈希验证结果: " + knownMatches);
        
        if (!existingMatches) {
            System.out.println("\n警告：现有哈希值无法验证密码 'admin123'");
            System.out.println("请使用以下SQL更新数据库:");
            System.out.println("UPDATE sys_user SET password = '" + encoded + "' WHERE username = 'admin';");
            System.out.println("\n或者使用已知可用的哈希值:");
            if (knownMatches) {
                System.out.println("UPDATE sys_user SET password = '" + knownHash + "' WHERE username = 'admin';");
            }
        } else {
            System.out.println("\n现有哈希值可以验证密码 'admin123'");
        }
    }
}
