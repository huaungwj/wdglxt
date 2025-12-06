package com.example.dms.application.service;

import com.example.dms.repository.entity.Dept;
import com.example.dms.repository.entity.Permission;
import com.example.dms.repository.entity.RolePermission;
import com.example.dms.repository.entity.User;
import com.example.dms.repository.mapper.PermissionMapper;
import com.example.dms.repository.mapper.RolePermissionMapper;
import com.example.dms.repository.mapper.UserRoleMapper;
import com.example.dms.service.DeptService;
import com.example.dms.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限服务
 * 用于处理权限相关的业务逻辑，包括：
 * 1. 文件访问权限（自己或上级可查看）
 * 2. 功能操作权限（上传、下载、预览）
 * 3. 菜单权限
 * 4. 字典权限
 */
@Service
public class PermissionService {

    private final UserService userService;
    private final DeptService deptService;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    public PermissionService(
            UserService userService, 
            DeptService deptService,
            PermissionMapper permissionMapper,
            RolePermissionMapper rolePermissionMapper,
            UserRoleMapper userRoleMapper) {
        this.userService = userService;
        this.deptService = deptService;
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * 获取当前用户可见的用户ID列表
     * 包括：自己 + 下级部门的用户
     */
    public List<Long> getVisibleUserIds(Long currentUserId) {
        User currentUser = userService.getById(currentUserId);
        if (currentUser == null) {
            return List.of();
        }

        List<Long> userIds = new ArrayList<>();
        userIds.add(currentUserId); // 自己

        // 获取下级部门的用户
        if (currentUser.getDeptId() != null) {
            List<Long> subordinateDeptIds = getSubordinateDeptIds(currentUser.getDeptId());
            if (!subordinateDeptIds.isEmpty()) {
                List<User> subordinateUsers = userService.list().stream()
                    .filter(user -> user.getDeptId() != null && subordinateDeptIds.contains(user.getDeptId()))
                    .collect(Collectors.toList());
                userIds.addAll(subordinateUsers.stream().map(User::getId).collect(Collectors.toList()));
            }
        }

        return userIds;
    }

    /**
     * 递归获取下级部门ID列表
     */
    private List<Long> getSubordinateDeptIds(Long deptId) {
        List<Long> deptIds = new ArrayList<>();
        List<Dept> allDepts = deptService.list();
        
        // 递归查找所有下级部门
        findSubordinateDepts(deptId, allDepts, deptIds);
        
        return deptIds;
    }

    /**
     * 递归查找下级部门
     */
    private void findSubordinateDepts(Long parentId, List<Dept> allDepts, List<Long> result) {
        for (Dept dept : allDepts) {
            if (dept.getParentId() != null && dept.getParentId().equals(parentId)) {
                result.add(dept.getId());
                // 递归查找该部门的子部门
                findSubordinateDepts(dept.getId(), allDepts, result);
            }
        }
    }

    /**
     * 检查用户是否有权限查看文档
     * 规则：
     * 1. 自己只能查看自己的：file.created_by === user.id
     * 2. 上级能够查看下级的：user.department_path 是 file.created_by.department_path 的前缀
     * 3. 其他情况默认不可查看
     */
    public boolean canViewDocument(Long currentUserId, Long documentCreatorId) {
        // 如果是自己的文档，直接允许
        if (currentUserId.equals(documentCreatorId)) {
            return true;
        }

        User currentUser = userService.getById(currentUserId);
        User documentCreator = userService.getById(documentCreatorId);
        
        if (currentUser == null || documentCreator == null) {
            return false;
        }

        // 使用 department_path 判断是否为上级
        if (currentUser.getDepartmentPath() != null && 
            documentCreator.getDepartmentPath() != null) {
            // 如果文档创建者的部门路径以当前用户的部门路径开头，则当前用户为上级
            if (documentCreator.getDepartmentPath().startsWith(currentUser.getDepartmentPath() + "/") ||
                documentCreator.getDepartmentPath().equals(currentUser.getDepartmentPath())) {
                return true;
            }
        }

        // 如果没有 department_path，使用部门层级关系判断
        if (currentUser.getDeptId() != null && documentCreator.getDeptId() != null) {
            List<Long> subordinateDeptIds = getSubordinateDeptIds(currentUser.getDeptId());
            if (subordinateDeptIds.contains(documentCreator.getDeptId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查用户是否有权限执行某个操作
     * @param userId 用户ID
     * @param permissionCode 权限标识，如 file:upload
     * @return 是否有权限
     */
    public boolean hasPermission(Long userId, String permissionCode) {
        // 获取用户的所有角色ID
        List<Long> roleIds = userRoleMapper.selectList(
            com.baomidou.mybatisplus.core.toolkit.Wrappers.<com.example.dms.repository.entity.UserRole>lambdaQuery()
                .eq(com.example.dms.repository.entity.UserRole::getUserId, userId)
        ).stream()
        .map(com.example.dms.repository.entity.UserRole::getRoleId)
        .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return false;
        }

        // 获取权限ID
        Permission permission = permissionMapper.selectOne(
            com.baomidou.mybatisplus.core.toolkit.Wrappers.<Permission>lambdaQuery()
                .eq(Permission::getCode, permissionCode)
        );

        if (permission == null) {
            return false;
        }

        // 检查角色是否有该权限
        long count = rolePermissionMapper.selectCount(
            com.baomidou.mybatisplus.core.toolkit.Wrappers.<RolePermission>lambdaQuery()
                .in(RolePermission::getRoleId, roleIds)
                .eq(RolePermission::getPermissionId, permission.getId())
        );

        return count > 0;
    }

    /**
     * 获取用户的所有权限标识列表
     */
    public Set<String> getUserPermissions(Long userId) {
        // 获取用户的所有角色ID
        List<Long> roleIds = userRoleMapper.selectList(
            com.baomidou.mybatisplus.core.toolkit.Wrappers.<com.example.dms.repository.entity.UserRole>lambdaQuery()
                .eq(com.example.dms.repository.entity.UserRole::getUserId, userId)
        ).stream()
        .map(com.example.dms.repository.entity.UserRole::getRoleId)
        .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return new HashSet<>();
        }

        // 获取所有角色对应的权限ID
        List<Long> permissionIds = rolePermissionMapper.selectList(
            com.baomidou.mybatisplus.core.toolkit.Wrappers.<RolePermission>lambdaQuery()
                .in(RolePermission::getRoleId, roleIds)
        ).stream()
        .map(RolePermission::getPermissionId)
        .distinct()
        .collect(Collectors.toList());

        if (permissionIds.isEmpty()) {
            return new HashSet<>();
        }

        // 获取权限标识列表
        return permissionMapper.selectList(
            com.baomidou.mybatisplus.core.toolkit.Wrappers.<Permission>lambdaQuery()
                .in(Permission::getId, permissionIds)
        ).stream()
        .map(Permission::getCode)
        .collect(Collectors.toSet());
    }

    /**
     * 检查用户是否有文件上传权限
     */
    public boolean canUpload(Long userId) {
        return hasPermission(userId, "file:upload");
    }

    /**
     * 检查用户是否有文件下载权限
     */
    public boolean canDownload(Long userId, Long documentCreatorId) {
        // 先检查是否有下载权限点
        if (!hasPermission(userId, "file:download")) {
            return false;
        }
        // 再检查是否有权限访问该文档
        return canViewDocument(userId, documentCreatorId);
    }

    /**
     * 检查用户是否有文件预览权限
     */
    public boolean canPreview(Long userId, Long documentCreatorId) {
        // 先检查是否有预览权限点
        if (!hasPermission(userId, "file:preview")) {
            return false;
        }
        // 再检查是否有权限访问该文档
        return canViewDocument(userId, documentCreatorId);
    }

    /**
     * 检查用户是否有文件删除权限
     */
    public boolean canDelete(Long userId, Long documentCreatorId) {
        // 先检查是否有删除权限点
        if (!hasPermission(userId, "file:delete")) {
            return false;
        }
        // 再检查是否有权限访问该文档
        return canViewDocument(userId, documentCreatorId);
    }
}
