package com.example.dms.common.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dms.repository.entity.Role;
import com.example.dms.repository.entity.RoleDept;
import com.example.dms.repository.entity.User;
import com.example.dms.repository.entity.UserRole;
import com.example.dms.repository.mapper.RoleDeptMapper;
import com.example.dms.repository.mapper.RoleMapper;
import com.example.dms.repository.mapper.UserMapper;
import com.example.dms.repository.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限工具类
 * 参考 RuoYi 设计
 * 
 * 数据权限范围：
 * 1 - 全部数据权限
 * 2 - 自定义数据权限（通过 sys_role_dept 表配置）
 * 3 - 本部门数据权限
 * 4 - 本部门及以下数据权限
 * 5 - 仅本人数据权限
 */
@Component
public class DataScopeUtil {
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private RoleDeptMapper roleDeptMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取用户的数据权限范围
     * 如果用户有多个角色，返回权限范围最大的（数字最小的）
     */
    public String getDataScope(Long userId) {
        // 获取用户的所有角色ID
        List<Long> roleIds = userRoleMapper.selectList(
            com.baomidou.mybatisplus.core.toolkit.Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, userId)
        ).stream()
        .map(UserRole::getRoleId)
        .collect(Collectors.toList());
        
        if (roleIds.isEmpty()) {
            return "5"; // 默认仅本人数据权限
        }
        
        // 获取所有角色的数据权限范围
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        
        // 返回权限范围最大的（数字最小的）
        return roles.stream()
            .map(Role::getDataScope)
            .filter(scope -> scope != null && !scope.isEmpty())
            .min(String::compareTo)
            .orElse("5"); // 默认仅本人数据权限
    }
    
    /**
     * 获取用户的数据权限部门ID列表
     * 根据数据权限范围返回可访问的部门ID列表
     */
    public List<Long> getDataScopeDeptIds(Long userId) {
        String dataScope = getDataScope(userId);
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            return List.of();
        }
        
        switch (dataScope) {
            case "1": // 全部数据权限
                return List.of(); // 空列表表示不限制
                
            case "2": // 自定义数据权限
                // 获取用户所有角色的自定义部门
                List<Long> roleIds = userRoleMapper.selectList(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getUserId, userId)
                ).stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
                
                return roleDeptMapper.selectList(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<RoleDept>lambdaQuery()
                        .in(RoleDept::getRoleId, roleIds)
                ).stream()
                .map(RoleDept::getDeptId)
                .distinct()
                .collect(Collectors.toList());
                
            case "3": // 本部门数据权限
                if (user.getDeptId() == null) {
                    return List.of();
                }
                return List.of(user.getDeptId());
                
            case "4": // 本部门及以下数据权限
                if (user.getDeptId() == null) {
                    return List.of();
                }
                // 返回本部门及所有子部门
                // 这里简化处理，实际应该递归查询所有子部门
                // TODO: 实现递归查询子部门
                return List.of(user.getDeptId());
                
            case "5": // 仅本人数据权限
            default:
                // 返回空列表，表示只能访问自己的数据
                return List.of();
        }
    }
    
    /**
     * 检查用户是否有权限访问指定部门的数据
     */
    public boolean canAccessDept(Long userId, Long deptId) {
        String dataScope = getDataScope(userId);
        User user = userMapper.selectById(userId);
        
        if (user == null || deptId == null) {
            return false;
        }
        
        switch (dataScope) {
            case "1": // 全部数据权限
                return true;
                
            case "2": // 自定义数据权限
                List<Long> allowedDeptIds = getDataScopeDeptIds(userId);
                return allowedDeptIds.contains(deptId);
                
            case "3": // 本部门数据权限
                return user.getDeptId() != null && user.getDeptId().equals(deptId);
                
            case "4": // 本部门及以下数据权限
                if (user.getDeptId() == null) {
                    return false;
                }
                // 简化处理：检查是否是本部门或子部门
                // TODO: 实现递归检查子部门
                return user.getDeptId().equals(deptId);
                
            case "5": // 仅本人数据权限
            default:
                return false;
        }
    }
    
    /**
     * 检查用户是否有权限访问指定用户的数据
     */
    public boolean canAccessUser(Long currentUserId, Long targetUserId) {
        String dataScope = getDataScope(currentUserId);
        User currentUser = userMapper.selectById(currentUserId);
        User targetUser = userMapper.selectById(targetUserId);
        
        if (currentUser == null || targetUser == null) {
            return false;
        }
        
        switch (dataScope) {
            case "1": // 全部数据权限
                return true;
                
            case "2": // 自定义数据权限
            case "3": // 本部门数据权限
            case "4": // 本部门及以下数据权限
                // 检查目标用户是否在可访问的部门中
                if (targetUser.getDeptId() == null) {
                    return false;
                }
                return canAccessDept(currentUserId, targetUser.getDeptId());
                
            case "5": // 仅本人数据权限
            default:
                return currentUserId.equals(targetUserId);
        }
    }
}

