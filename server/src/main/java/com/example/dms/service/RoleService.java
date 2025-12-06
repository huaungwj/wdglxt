package com.example.dms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dms.repository.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    /**
     * 获取角色的菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);
    
    /**
     * 分配角色菜单权限
     */
    void assignRoleMenus(Long roleId, List<Long> menuIds);
    
    /**
     * 获取角色的部门ID列表（用于自定义数据权限）
     */
    List<Long> getRoleDeptIds(Long roleId);
    
    /**
     * 分配角色部门权限（自定义数据权限）
     */
    void assignRoleDepts(Long roleId, List<Long> deptIds);
}
