package com.example.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dms.dto.CategoryTreeNode;
import com.example.dms.entity.Category;
import com.example.dms.mapper.CategoryMapper;
import com.example.dms.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryTreeNode> getTreeWithCounts() {
        List<Category> all = this.list();

        // Build nodes map
        Map<Long, CategoryTreeNode> nodeMap = new HashMap<>();
        for (Category c : all) {
            CategoryTreeNode n = new CategoryTreeNode();
            n.setId(c.getId());
            n.setName(c.getName());
            n.setParentId(c.getParentId());
            n.setSort(Optional.ofNullable(c.getSort()).orElse(0));
            n.setLevel(Optional.ofNullable(c.getLevel()).orElse(0));
            n.setFileCount(0L);
            nodeMap.put(c.getId(), n);
        }
        // assemble tree
        List<CategoryTreeNode> roots = new ArrayList<>();
        for (Category c : all) {
            CategoryTreeNode n = nodeMap.get(c.getId());
            Long pid = Optional.ofNullable(c.getParentId()).orElse(0L);
            if (pid == 0) {
                roots.add(n);
            } else {
                CategoryTreeNode p = nodeMap.get(pid);
                if (p != null) p.getChildren().add(n);
                else roots.add(n); // broken parent, treat as root
            }
        }
        // sort children
        nodeMap.values().forEach(x -> x.getChildren().sort(Comparator.comparing(CategoryTreeNode::getSort).thenComparing(CategoryTreeNode::getId)));
        roots.sort(Comparator.comparing(CategoryTreeNode::getSort).thenComparing(CategoryTreeNode::getId));

        // aggregate counts including descendants
        aggregateCounts(roots);
        return roots;
    }

    private long aggregateCounts(List<CategoryTreeNode> nodes) {
        long sum = 0;
        for (CategoryTreeNode n : nodes) {
            long child = aggregateCounts(n.getChildren());
            long self = Optional.ofNullable(n.getFileCount()).orElse(0L);
            long total = self + child;
            n.setFileCount(total);
            sum += total;
        }
        return sum;
    }

    @Override
    @Transactional
    public Category create(String name, Long parentId) {
        Category c = new Category();
        c.setName(name);
        c.setParentId(parentId);
        c.setSort(0);
        c.setLevel(calcLevel(parentId));
        this.save(c);
        return c;
    }

    @Override
    @Transactional
    public void update(Long id, String name, Long parentId, Integer sort) {
        Category c = this.getById(id);
        if (c == null) throw new RuntimeException("Category not found");
        if (name != null) c.setName(name);
        if (sort != null) c.setSort(sort);
        if (parentId != null && !Objects.equals(parentId, c.getParentId())) {
            // prevent setting self or descendant as parent
            if (Objects.equals(parentId, id)) throw new RuntimeException("Parent cannot be self");
            if (isDescendant(parentId, id)) throw new RuntimeException("Parent cannot be a descendant");
            c.setParentId(parentId);
            c.setLevel(calcLevel(parentId));
        }
        this.updateById(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        long childCount = this.count(new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount > 0) throw new RuntimeException("Cannot delete: has sub categories");
        this.removeById(id);
    }

    private int calcLevel(Long parentId) {
        if (parentId == null) return 0;
        Category p = this.getById(parentId);
        return p == null ? 0 : Optional.ofNullable(p.getLevel()).orElse(0) + 1;
    }

    private boolean isDescendant(Long candidateParentId, Long id) {
        if (candidateParentId == null) return false;
        Long cur = candidateParentId;
        while (cur != null) {
            if (Objects.equals(cur, id)) return true;
            Category c = this.getById(cur);
            cur = c == null ? null : c.getParentId();
        }
        return false;
    }
}
