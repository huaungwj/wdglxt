package com.example.dms.service;

import com.example.dms.dto.CategoryTreeNode;
import com.example.dms.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryTreeNode> getTreeWithCounts();
    Category create(String name, Long parentId);
    void update(Long id, String name, Long parentId, Integer sort);
    void delete(Long id);
}
