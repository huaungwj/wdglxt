package com.example.dms.service;

import com.example.dms.common.util.JwtUtil;
import com.example.dms.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    public String login(String username, String password) {
        log.info("登录请求 - 用户名: {}, 密码长度: {}", username, password != null ? password.length() : 0);
        
        User user = userService.getByUsername(username);
        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new RuntimeException("用户不存在");
        }

        log.info("查询到用户 - ID: {}, 用户名: {}, 密码哈希: {}", user.getId(), user.getUsername(), user.getPassword());
        log.info("输入的密码: [{}]", password);
        log.info("数据库中的密码哈希: [{}]", user.getPassword());
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        log.info("密码验证结果: {}", matches);
        
        if (!matches) {
            log.error("密码验证失败 - 用户名: {}, 输入密码: [{}], 数据库哈希: [{}]", username, password, user.getPassword());
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            log.error("用户已被禁用: {}", username);
            throw new RuntimeException("用户已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("登录成功 - 用户名: {}, 生成Token", username);
        return token;
    }

    /**
     * 用户注册（创建用户）
     */
    public User register(String username, String password, String realName, Long deptId) {
        User existUser = userService.getByUsername(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setDeptId(deptId);
        user.setStatus(1);

        userService.save(user);
        return user;
    }

    /**
     * 重置用户密码
     */
    public void resetPassword(String username, String newPassword) {
        log.info("重置密码 - 用户名: {}, 新密码: [{}]", username, newPassword);
        
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        log.info("生成的新密码哈希: [{}]", encodedPassword);
        
        // 验证新生成的哈希
        boolean testMatch = passwordEncoder.matches(newPassword, encodedPassword);
        log.info("新密码哈希验证测试: {}", testMatch);
        
        user.setPassword(encodedPassword);
        boolean updated = userService.updateById(user);
        log.info("密码更新结果: {}", updated);
        
        // 再次验证更新后的密码
        User updatedUser = userService.getByUsername(username);
        boolean verifyMatch = passwordEncoder.matches(newPassword, updatedUser.getPassword());
        log.info("更新后密码验证测试: {}", verifyMatch);
    }
}
