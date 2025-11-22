package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体（充血模型）
 * 包含业务逻辑方法
 */
@Data
@TableName("doc_category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 创建新的分类
     */
    public static Category create(String name, Long parentId, Integer level) {
        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        category.setSort(0);
        category.setLevel(level);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    /**
     * 更新分类名称
     */
    public void updateName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 更新排序
     */
    public void updateSort(Integer sort) {
        if (sort != null) {
            this.sort = sort;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 更新父分类
     */
    public void updateParent(Long parentId, Integer level) {
        if (parentId != null) {
            this.parentId = parentId;
            this.level = level;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 验证是否可以删除（检查是否有子分类）
     */
    public boolean canDelete(long childCount) {
        return childCount == 0;
    }

    /**
     * 验证父分类是否有效（不能是自己或自己的后代）
     */
    public boolean isValidParent(Long candidateParentId, Long selfId) {
        if (candidateParentId == null) return true;
        if (candidateParentId.equals(selfId)) return false;
        return true; // 具体检查是否为自己的后代需要在 Repository 层实现
    }
}

