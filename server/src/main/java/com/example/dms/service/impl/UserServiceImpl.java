package com.example.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dms.application.service.PermissionService;
import com.example.dms.common.util.DataScopeUtil;
import com.example.dms.controller.dto.UserInfoDTO;
import com.example.dms.repository.entity.Dept;
import com.example.dms.repository.entity.Post;
import com.example.dms.repository.entity.Role;
import com.example.dms.repository.entity.User;
import com.example.dms.repository.entity.UserRole;
import com.example.dms.repository.mapper.DeptMapper;
import com.example.dms.repository.mapper.PostMapper;
import com.example.dms.repository.mapper.RoleMapper;
import com.example.dms.repository.mapper.UserMapper;
import com.example.dms.repository.mapper.UserRoleMapper;
import com.example.dms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private DeptMapper deptMapper;
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    @Lazy
    private PermissionService permissionService;
    
    @Autowired
    private DataScopeUtil dataScopeUtil;
    
    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
    
    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        ).stream()
        .map(UserRole::getRoleId)
        .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        // 先删除该用户的所有角色
        userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );
        
        // 再添加新的角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }
    
    @Override
    public List<User> listUsers(String username, String phone, Integer status, Long deptId, String startDate, String endDate) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 用户名模糊查询
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        
        // 手机号模糊查询
        if (StringUtils.hasText(phone)) {
            wrapper.like(User::getPhone, phone);
        }
        
        // 状态精确查询
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        // 部门查询（包括子部门）
        if (deptId != null) {
            // 获取该部门及其所有子部门的ID列表
            List<Long> deptIds = getDeptAndChildrenIds(deptId);
            if (!deptIds.isEmpty()) {
                wrapper.in(User::getDeptId, deptIds);
            }
        }
        
        // 创建时间范围查询
        if (StringUtils.hasText(startDate)) {
            LocalDateTime start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE).atStartOfDay();
            wrapper.ge(User::getCreatedAt, start);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).atTime(23, 59, 59);
            wrapper.le(User::getCreatedAt, end);
        }
        
        return list(wrapper);
    }
    
    /**
     * 获取部门及其所有子部门的ID列表
     */
    private List<Long> getDeptAndChildrenIds(Long deptId) {
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(deptId);
        
        // 递归获取所有子部门
        List<Dept> children = deptMapper.selectList(
            new LambdaQueryWrapper<Dept>()
                .eq(Dept::getParentId, deptId)
        );
        
        for (Dept child : children) {
            deptIds.addAll(getDeptAndChildrenIds(child.getId()));
        }
        
        return deptIds;
    }
    
    @Override
    public UserInfoDTO getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return null;
        }
        
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setDeptId(user.getDeptId());
        dto.setPostId(user.getPostId());
        dto.setStatus(user.getStatus());
        dto.setRemark(user.getRemark());
        
        // 格式化创建时间
        if (user.getCreatedAt() != null) {
            dto.setCreatedAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // 获取部门名称
        if (user.getDeptId() != null) {
            Dept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                dto.setDeptName(dept.getName());
            }
        }
        
        // 获取岗位名称
        if (user.getPostId() != null) {
            Post post = postMapper.selectById(user.getPostId());
            if (post != null) {
                dto.setPostName(post.getPostName());
            }
        }
        
        // 获取角色列表
        List<Long> roleIds = getUserRoleIds(userId);
        if (!roleIds.isEmpty()) {
            List<Role> roles = roleMapper.selectBatchIds(roleIds);
            List<UserInfoDTO.RoleInfo> roleInfos = roles.stream().map(role -> {
                UserInfoDTO.RoleInfo roleInfo = new UserInfoDTO.RoleInfo();
                roleInfo.setId(role.getId());
                roleInfo.setName(role.getName());
                roleInfo.setCode(role.getCode());
                roleInfo.setDataScope(role.getDataScope());
                return roleInfo;
            }).collect(Collectors.toList());
            dto.setRoles(roleInfos);
        }
        
        // 获取权限列表
        Set<String> permissions = permissionService.getUserPermissions(userId);
        dto.setPermissions(new ArrayList<>(permissions));
        
        // 获取数据权限范围
        String dataScope = dataScopeUtil.getDataScope(userId);
        dto.setDataScope(dataScope);
        
        // 数据权限范围描述
        Map<String, String> dataScopeMap = Map.of(
            "1", "全部数据权限",
            "2", "自定义数据权限",
            "3", "本部门数据权限",
            "4", "本部门及以下数据权限",
            "5", "仅本人数据权限"
        );
        dto.setDataScopeText(dataScopeMap.getOrDefault(dataScope, "未知"));
        
        return dto;
    }
}
