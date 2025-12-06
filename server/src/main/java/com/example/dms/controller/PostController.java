package com.example.dms.controller;

import com.example.dms.common.ApiResponse;
import com.example.dms.repository.entity.Post;
import com.example.dms.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理控制器
 * 参考 RuoYi 设计
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 获取所有岗位
     */
    @GetMapping
    public ApiResponse<List<Post>> list() {
        return ApiResponse.ok(postService.list());
    }

    /**
     * 根据ID获取岗位
     */
    @GetMapping("/{id}")
    public ApiResponse<Post> getById(@PathVariable Long id) {
        Post post = postService.getById(id);
        if (post == null) {
            return ApiResponse.fail("岗位不存在");
        }
        return ApiResponse.ok(post);
    }

    /**
     * 创建岗位
     */
    @PostMapping
    public ApiResponse<Post> create(@RequestBody Post post) {
        boolean success = postService.save(post);
        if (success) {
            return ApiResponse.ok(post);
        } else {
            return ApiResponse.fail("创建失败");
        }
    }

    /**
     * 更新岗位
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Post post) {
        post.setId(id);
        boolean success = postService.updateById(post);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("更新失败");
        }
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        boolean success = postService.removeById(id);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.fail("删除失败");
        }
    }
}

