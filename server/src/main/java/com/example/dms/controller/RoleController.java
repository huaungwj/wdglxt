package com.example.dms.controller;

import com.example.dms.common.ApiResponse;
import com.example.dms.controller.dto.RoleMenuDTO;
import com.example.dms.repository.entity.Role;
import com.example.dms.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 * 参考 RuoYi 设计
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证
 * 3. 调用 Service 层处理业务逻辑
 * 4. 返回统一的 API 响应格式
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取所有角色
     */
    @GetMapping
    public ApiResponse<List<Role>> list() {
        return ApiResponse.ok(roleService.list());
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    public ApiResponse<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return ApiResponse.fail("角色不存在");
        }
        return ApiResponse.ok(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public ApiResponse<Role> create(@RequestBody Role role) {
        boolean success = roleService.save(role);
        if (success) {
            return ApiResponse.ok(role);
        } else {
            return ApiResponse.fail("创建失败");
        }
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        boolean success = roleService.updateById(role);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("更新失败");
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        boolean success = roleService.removeById(id);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("删除失败");
        }
    }
    
    /**
     * 获取角色的菜单ID列表
     */
    @GetMapping("/{id}/menus")
    public ApiResponse<List<Long>> getRoleMenus(@PathVariable Long id) {
        List<Long> menuIds = roleService.getRoleMenuIds(id);
        return ApiResponse.ok(menuIds);
    }
    
    /**
     * 分配角色菜单权限
     */
    @PostMapping("/{id}/menus")
    public ApiResponse<Void> assignRoleMenus(@PathVariable Long id, @RequestBody RoleMenuDTO dto) {
        dto.setRoleId(id);
        roleService.assignRoleMenus(id, dto.getMenuIds());
        return ApiResponse.ok();
    }
    
    /**
     * 获取角色的部门ID列表（用于自定义数据权限）
     */
    @GetMapping("/{id}/depts")
    public ApiResponse<List<Long>> getRoleDepts(@PathVariable Long id) {
        List<Long> deptIds = roleService.getRoleDeptIds(id);
        return ApiResponse.ok(deptIds);
    }
    
    /**
     * 分配角色部门权限（自定义数据权限）
     */
    @PostMapping("/{id}/depts")
    public ApiResponse<Void> assignRoleDepts(@PathVariable Long id, @RequestBody com.example.dms.controller.dto.RoleDeptDTO dto) {
        dto.setRoleId(id);
        roleService.assignRoleDepts(id, dto.getDeptIds());
        return ApiResponse.ok();
    }
}
