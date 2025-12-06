package com.example.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dms.repository.entity.Role;
import com.example.dms.repository.entity.RoleDept;
import com.example.dms.repository.entity.RoleMenu;
import com.example.dms.repository.mapper.RoleDeptMapper;
import com.example.dms.repository.mapper.RoleMapper;
import com.example.dms.repository.mapper.RoleMenuMapper;
import com.example.dms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    
    @Autowired
    private RoleDeptMapper roleDeptMapper;
    
    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuMapper.selectList(
            new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId)
        ).stream()
        .map(RoleMenu::getMenuId)
        .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        // 先删除该角色的所有菜单权限
        roleMenuMapper.delete(
            new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId)
        );
        
        // 再添加新的菜单权限
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }
    
    @Override
    public List<Long> getRoleDeptIds(Long roleId) {
        return roleDeptMapper.selectList(
            new LambdaQueryWrapper<RoleDept>()
                .eq(RoleDept::getRoleId, roleId)
        ).stream()
        .map(RoleDept::getDeptId)
        .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void assignRoleDepts(Long roleId, List<Long> deptIds) {
        // 先删除该角色的所有部门权限
        roleDeptMapper.delete(
            new LambdaQueryWrapper<RoleDept>()
                .eq(RoleDept::getRoleId, roleId)
        );
        
        // 再添加新的部门权限
        if (deptIds != null && !deptIds.isEmpty()) {
            for (Long deptId : deptIds) {
                RoleDept roleDept = new RoleDept();
                roleDept.setRoleId(roleId);
                roleDept.setDeptId(deptId);
                roleDeptMapper.insert(roleDept);
            }
        }
    }
}
