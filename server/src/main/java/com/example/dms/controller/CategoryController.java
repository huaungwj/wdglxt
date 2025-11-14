package com.example.dms.controller;

import com.example.dms.common.R;
import com.example.dms.dto.CategoryTreeNode;
import com.example.dms.entity.Category;
import com.example.dms.service.CategoryService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public R<List<CategoryTreeNode>> tree(){
        return R.ok(categoryService.getTreeWithCounts());
    }

    @PostMapping
    public R<Category> create(@RequestBody Map<String, Object> body){
        String name = (String) body.get("name");
        if (name == null || name.isBlank()) throw new RuntimeException("分类名称不能为空");
        Long parentId = body.get("parentId") == null ? null : Long.valueOf(body.get("parentId").toString());
        return R.ok(categoryService.create(name, parentId));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body){
        String name = body.get("name") == null ? null : body.get("name").toString();
        Long parentId = body.get("parentId") == null ? null : Long.valueOf(body.get("parentId").toString());
        Integer sort = body.get("sort") == null ? null : Integer.valueOf(body.get("sort").toString());
        categoryService.update(id, name, parentId, sort);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id){
        categoryService.delete(id);
        return R.ok();
    }
}
