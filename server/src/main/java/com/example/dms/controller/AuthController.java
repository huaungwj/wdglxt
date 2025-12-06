package com.example.dms.controller;

import com.example.dms.common.ApiResponse;
import com.example.dms.dto.LoginRequest;
import com.example.dms.dto.LoginResponse;
import com.example.dms.repository.entity.User;
import com.example.dms.service.AuthService;
import com.example.dms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证
 * 3. 调用 Service 层处理业务逻辑
 * 4. 返回统一的 API 响应格式
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getUsername(), request.getPassword());
            User user = userService.getByUsername(request.getUsername());
            LoginResponse response = new LoginResponse(token, user.getUsername(), user.getRealName());
            return ApiResponse.ok(response);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 重置admin用户密码（临时接口，用于修复密码问题）
     * 密码固定为：admin123
     * 支持GET和POST方法，方便浏览器访问
     */
    @GetMapping("/reset-admin-password")
    @PostMapping("/reset-admin-password")
    public ApiResponse<String> resetAdminPassword() {
        try {
            authService.resetPassword("admin", "admin123");
            return ApiResponse.ok("admin用户密码已重置为：admin123");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail("重置失败：" + e.getMessage());
        }
    }

    /**
     * 测试密码验证（临时接口，用于调试）
     */
    @PostMapping("/test-password")
    public ApiResponse<String> testPassword(@RequestBody LoginRequest request) {
        try {
            User user = userService.getByUsername(request.getUsername());
            if (user == null) {
                return ApiResponse.fail("用户不存在");
            }
            
            org.springframework.security.crypto.password.PasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            
            boolean matches = encoder.matches(request.getPassword(), user.getPassword());
            
            String result = String.format(
                "用户名: %s\n输入的密码: [%s]\n数据库哈希: [%s]\n验证结果: %s",
                request.getUsername(),
                request.getPassword(),
                user.getPassword(),
                matches
            );
            
            return ApiResponse.ok(result);
        } catch (Exception e) {
            return ApiResponse.fail("测试失败：" + e.getMessage());
        }
    }
}
