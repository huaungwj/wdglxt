package com.example.dms.domain;

import com.example.dms.domain.dto.CategoryDomainDTO;
import com.example.dms.repository.CategoryRepository;
import com.example.dms.repository.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 领域服务层
 * 包含核心业务逻辑
 */
@Service
public class CategoryDomainService {

    private final CategoryRepository categoryRepository;

    public CategoryDomainService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 创建分类
     */
    @Transactional
    public CategoryDomainDTO create(String name, Long parentId) {
        // 验证名称
        if (name == null || name.isBlank()) {
            throw new RuntimeException("分类名称不能为空");
        }

        // 计算层级
        Integer level = calculateLevel(parentId);

        // 创建实体（使用充血模型的方法）
        Category category = Category.create(name, parentId, level);

        // 保存
        Category saved = categoryRepository.save(category);

        // 转换为 DTO
        return toDTO(saved);
    }

    /**
     * 更新分类
     */
    @Transactional
    public void update(Long id, String name, Long parentId, Integer sort) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));

        // 使用充血模型的方法更新
        category.updateName(name);
        category.updateSort(sort);

        // 如果父分类改变，需要验证和更新
        if (parentId != null && !Objects.equals(parentId, category.getParentId())) {
            // 验证父分类不能是自己
            if (Objects.equals(parentId, id)) {
                throw new RuntimeException("Parent cannot be self");
            }
            // 验证父分类不能是自己的后代
            if (isDescendant(parentId, id)) {
                throw new RuntimeException("Parent cannot be a descendant");
            }
            // 计算新层级
            Integer newLevel = calculateLevel(parentId);
            category.updateParent(parentId, newLevel);
        }

        categoryRepository.update(category);
    }

    /**
     * 删除分类
     */
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));

        // 检查是否有子分类
        long childCount = categoryRepository.countByParentId(id);
        if (!category.canDelete(childCount)) {
            throw new RuntimeException("Cannot delete: has sub categories");
        }

        categoryRepository.deleteById(id);
    }

    /**
     * 获取所有分类
     */
    public List<CategoryDomainDTO> findAll() {
        return categoryRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 根据ID查找分类
     */
    public Optional<CategoryDomainDTO> findById(Long id) {
        return categoryRepository.findById(id).map(this::toDTO);
    }

    /**
     * 计算层级
     */
    private Integer calculateLevel(Long parentId) {
        if (parentId == null) {
            return 0;
        }
        return categoryRepository.findById(parentId)
            .map(p -> Optional.ofNullable(p.getLevel()).orElse(0) + 1)
            .orElse(0);
    }

    /**
     * 检查 candidateParentId 是否是 id 的后代
     */
    private boolean isDescendant(Long candidateParentId, Long id) {
        if (candidateParentId == null) return false;
        Long cur = candidateParentId;
        while (cur != null) {
            if (Objects.equals(cur, id)) return true;
            Optional<Category> c = categoryRepository.findById(cur);
            cur = c.map(Category::getParentId).orElse(null);
        }
        return false;
    }

    /**
     * 实体转 DTO
     */
    private CategoryDomainDTO toDTO(Category category) {
        CategoryDomainDTO dto = new CategoryDomainDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setParentId(category.getParentId());
        dto.setSort(category.getSort());
        dto.setLevel(category.getLevel());
        return dto;
    }
}

