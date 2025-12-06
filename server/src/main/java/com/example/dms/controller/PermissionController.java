package com.example.dms.controller;

import com.example.dms.common.ApiResponse;
import com.example.dms.common.util.JwtUtil;
import com.example.dms.application.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 权限管理控制器
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证
 * 3. 调用 Application Service 层处理业务逻辑
 * 4. 返回统一的 API 响应格式
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final JwtUtil jwtUtil;

    /**
     * 获取当前用户的所有权限
     */
    @GetMapping("/current")
    public ApiResponse<Set<String>> getCurrentUserPermissions(
            @RequestHeader("Authorization") String token) {
        String authToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(authToken);
        Set<String> permissions = permissionService.getUserPermissions(userId);
        return ApiResponse.ok(permissions);
    }

    /**
     * 检查当前用户是否有某个权限
     */
    @GetMapping("/check")
    public ApiResponse<Boolean> checkPermission(
            @RequestParam String permissionCode,
            @RequestHeader("Authorization") String token) {
        String authToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(authToken);
        boolean hasPermission = permissionService.hasPermission(userId, permissionCode);
        return ApiResponse.ok(hasPermission);
    }
}

