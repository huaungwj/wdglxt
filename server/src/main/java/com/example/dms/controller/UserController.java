package com.example.dms.controller;

import com.example.dms.common.ApiResponse;
import com.example.dms.repository.entity.User;
import com.example.dms.service.AuthService;
import com.example.dms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证
 * 3. 调用 Service 层处理业务逻辑
 * 4. 返回统一的 API 响应格式
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final com.example.dms.common.util.JwtUtil jwtUtil;

    /**
     * 获取所有用户（支持查询参数）
     */
    @GetMapping
    public ApiResponse<List<User>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<User> users = userService.listUsers(username, phone, status, deptId, startDate, endDate);
        return ApiResponse.ok(users);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ApiResponse.fail("用户不存在");
        }
        return ApiResponse.ok(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ApiResponse<User> create(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            User existUser = userService.getByUsername(user.getUsername());
            if (existUser != null) {
                return ApiResponse.fail("用户名已存在");
            }
            
            // 加密密码
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            // 设置默认值
            if (user.getStatus() == null) {
                user.setStatus(1);
            }
            if (user.getGender() == null || user.getGender().isEmpty()) {
                user.setGender("2");
            }
            
            userService.save(user);
            return ApiResponse.ok(user);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        
        // 如果提供了新密码，则加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 如果没有提供密码，则不更新密码字段
            User existingUser = userService.getById(id);
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
            }
        }
        
        boolean success = userService.updateById(user);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("更新失败");
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("删除失败");
        }
    }
    
    /**
     * 获取用户的角色ID列表
     */
    @GetMapping("/{id}/roles")
    public ApiResponse<List<Long>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = userService.getUserRoleIds(id);
        return ApiResponse.ok(roleIds);
    }
    
    /**
     * 分配用户角色
     */
    @PostMapping("/{id}/roles")
    public ApiResponse<Void> assignUserRoles(@PathVariable Long id, @RequestBody com.example.dms.controller.dto.UserRoleDTO dto) {
        dto.setUserId(id);
        userService.assignUserRoles(id, dto.getRoleIds());
        return ApiResponse.ok();
    }
    
    /**
     * 获取当前用户信息（包含部门、岗位、角色等）
     */
    @GetMapping("/current")
    public ApiResponse<com.example.dms.controller.dto.UserInfoDTO> getCurrentUser(
            @RequestHeader("Authorization") String token) {
        try {
            String authToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(authToken);
            com.example.dms.controller.dto.UserInfoDTO userInfo = userService.getUserInfo(userId);
            return ApiResponse.ok(userInfo);
        } catch (Exception e) {
            return ApiResponse.fail("获取用户信息失败：" + e.getMessage());
        }
    }
}
