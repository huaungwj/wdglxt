package com.example.dms.controller;

import com.example.dms.common.ApiResponse;
import com.example.dms.repository.entity.Dict;
import com.example.dms.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证
 * 3. 调用 Service 层处理业务逻辑
 * 4. 返回统一的 API 响应格式
 */
@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    /**
     * 获取所有字典
     */
    @GetMapping
    public ApiResponse<List<Dict>> list() {
        return ApiResponse.ok(dictService.list());
    }

    /**
     * 根据类型获取字典
     */
    @GetMapping("/type/{type}")
    public ApiResponse<List<Dict>> getByType(@PathVariable String type) {
        // TODO: 实现按类型查询
        return ApiResponse.ok(dictService.list());
    }

    /**
     * 根据ID获取字典
     */
    @GetMapping("/{id}")
    public ApiResponse<Dict> getById(@PathVariable Long id) {
        Dict dict = dictService.getById(id);
        if (dict == null) {
            return ApiResponse.fail("字典不存在");
        }
        return ApiResponse.ok(dict);
    }

    /**
     * 创建字典
     */
    @PostMapping
    public ApiResponse<Dict> create(@RequestBody Dict dict) {
        boolean success = dictService.save(dict);
        if (success) {
            return ApiResponse.ok(dict);
        } else {
            return ApiResponse.fail("创建失败");
        }
    }

    /**
     * 更新字典
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Dict dict) {
        dict.setId(id);
        boolean success = dictService.updateById(dict);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("更新失败");
        }
    }

    /**
     * 删除字典
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        boolean success = dictService.removeById(id);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("删除失败");
        }
    }
}
