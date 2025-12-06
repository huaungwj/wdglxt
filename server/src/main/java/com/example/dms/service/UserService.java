package com.example.dms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dms.repository.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    User getByUsername(String username);
    
    /**
     * 获取用户的角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
    
    /**
     * 分配用户角色
     */
    void assignUserRoles(Long userId, List<Long> roleIds);
    
    /**
     * 条件查询用户列表
     */
    List<User> listUsers(String username, String phone, Integer status, Long deptId, String startDate, String endDate);
    
    /**
     * 获取用户详细信息（包含部门、岗位、角色等）
     */
    com.example.dms.controller.dto.UserInfoDTO getUserInfo(Long userId);
}
