package com.example.dms.controller;

import com.example.dms.common.ApiResponse;
import com.example.dms.repository.entity.Dept;
import com.example.dms.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证
 * 3. 调用 Service 层处理业务逻辑
 * 4. 返回统一的 API 响应格式
 */
@RestController
@RequestMapping("/api/depts")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    /**
     * 获取所有部门
     */
    @GetMapping
    public ApiResponse<List<Dept>> list() {
        return ApiResponse.ok(deptService.list());
    }
    
    /**
     * 获取部门树形结构
     */
    @GetMapping("/tree")
    public ApiResponse<List<Dept>> tree() {
        return ApiResponse.ok(deptService.getDeptTree());
    }

    /**
     * 根据ID获取部门
     */
    @GetMapping("/{id}")
    public ApiResponse<Dept> getById(@PathVariable Long id) {
        Dept dept = deptService.getById(id);
        if (dept == null) {
            return ApiResponse.fail("部门不存在");
        }
        return ApiResponse.ok(dept);
    }

    /**
     * 创建部门
     */
    @PostMapping
    public ApiResponse<Dept> create(@RequestBody Dept dept) {
        boolean success = deptService.save(dept);
        if (success) {
            return ApiResponse.ok(dept);
        } else {
            return ApiResponse.fail("创建失败");
        }
    }

    /**
     * 更新部门
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Dept dept) {
        dept.setId(id);
        boolean success = deptService.updateById(dept);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("更新失败");
        }
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        boolean success = deptService.removeById(id);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("删除失败");
        }
    }
}
