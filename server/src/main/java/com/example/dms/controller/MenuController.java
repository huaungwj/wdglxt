package com.example.dms.controller;

import com.example.dms.common.ApiResponse;
import com.example.dms.repository.entity.Menu;
import com.example.dms.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证
 * 3. 调用 Service 层处理业务逻辑
 * 4. 返回统一的 API 响应格式
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 获取所有菜单
     */
    @GetMapping
    public ApiResponse<List<Menu>> list() {
        return ApiResponse.ok(menuService.list());
    }

    /**
     * 获取菜单树形结构
     */
    @GetMapping("/tree")
    public ApiResponse<List<Menu>> tree() {
        return ApiResponse.ok(menuService.getMenuTree());
    }

    /**
     * 根据ID获取菜单
     */
    @GetMapping("/{id}")
    public ApiResponse<Menu> getById(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        if (menu == null) {
            return ApiResponse.fail("菜单不存在");
        }
        return ApiResponse.ok(menu);
    }

    /**
     * 创建菜单
     */
    @PostMapping
    public ApiResponse<Menu> create(@RequestBody Menu menu) {
        boolean success = menuService.save(menu);
        if (success) {
            return ApiResponse.ok(menu);
        } else {
            return ApiResponse.fail("创建失败");
        }
    }

    /**
     * 更新菜单
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Menu menu) {
        menu.setId(id);
        boolean success = menuService.updateById(menu);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("更新失败");
        }
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        boolean success = menuService.removeById(id);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("删除失败");
        }
    }
}
