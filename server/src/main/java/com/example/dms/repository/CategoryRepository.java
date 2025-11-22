package com.example.dms.repository;

import com.example.dms.repository.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    void update(Category category);
    void deleteById(Long id);
    long countByParentId(Long parentId);
}

