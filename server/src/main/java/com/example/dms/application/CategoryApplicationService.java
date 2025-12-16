package com.example.dms.application;

import com.example.dms.application.dto.CategoryCreateCommand;
import com.example.dms.application.dto.CategoryResponseDTO;
import com.example.dms.application.dto.CategoryUpdateCommand;
import com.example.dms.domain.CategoryDomainService;
import com.example.dms.domain.dto.CategoryDomainDTO;
import com.example.dms.dto.CategoryTreeNode;
import com.example.dms.repository.DocFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用服务层
 * 负责业务编排和跨领域协调
 */
@Service
public class CategoryApplicationService {

    private final CategoryDomainService categoryDomainService;
    private final DocFileRepository docFileRepository;

    public CategoryApplicationService(CategoryDomainService categoryDomainService, DocFileRepository docFileRepository) {
        this.categoryDomainService = categoryDomainService;
        this.docFileRepository = docFileRepository;
    }

    /**
     * 获取分类树（带文件计数）
     */
    public List<CategoryTreeNode> getTreeWithCounts() {
        List<CategoryDomainDTO> all = categoryDomainService.findAll();

        // 构建节点映射
        Map<Long, CategoryTreeNode> nodeMap = new HashMap<>();
        for (CategoryDomainDTO dto : all) {
            CategoryTreeNode node = new CategoryTreeNode();
            node.setId(dto.getId());
            node.setName(dto.getName());
            node.setParentId(dto.getParentId());
            node.setSort(Optional.ofNullable(dto.getSort()).orElse(0));
            node.setLevel(Optional.ofNullable(dto.getLevel()).orElse(0));
            // 查询该分类下的实际文件数量
            long fileCount = docFileRepository.countByCategoryId(dto.getId());
            node.setFileCount(fileCount);
            nodeMap.put(dto.getId(), node);
        }

        // 组装树结构
        List<CategoryTreeNode> roots = new ArrayList<>();
        for (CategoryDomainDTO dto : all) {
            CategoryTreeNode node = nodeMap.get(dto.getId());
            Long pid = Optional.ofNullable(dto.getParentId()).orElse(0L);
            if (pid == 0) {
                roots.add(node);
            } else {
                CategoryTreeNode parent = nodeMap.get(pid);
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    roots.add(node); // 父节点不存在，作为根节点
                }
            }
        }

        // 排序
        nodeMap.values().forEach(x -> x.getChildren().sort(
            Comparator.comparing(CategoryTreeNode::getSort)
                .thenComparing(CategoryTreeNode::getId)
        ));
        roots.sort(
            Comparator.comparing(CategoryTreeNode::getSort)
                .thenComparing(CategoryTreeNode::getId)
        );

        // 聚合文件计数（包括后代）
        aggregateCounts(roots);
        return roots;
    }

    /**
     * 创建分类
     */
    @Transactional
    public CategoryResponseDTO create(CategoryCreateCommand command) {
        CategoryDomainDTO dto = categoryDomainService.create(
            command.getName(),
            command.getParentId()
        );
        return toResponseDTO(dto);
    }

    /**
     * 更新分类
     */
    @Transactional
    public void update(Long id, CategoryUpdateCommand command) {
        categoryDomainService.update(
            id,
            command.getName(),
            command.getParentId(),
            command.getSort()
        );
    }

    /**
     * 删除分类
     */
    @Transactional
    public void delete(Long id) {
        // 递归检查该分类及其所有子分类中是否有文件
        long totalFileCount = countFilesInCategoryAndChildren(id);
        if (totalFileCount > 0) {
            throw new RuntimeException("该分类或其子分类中存在文件，无法删除");
        }
        
        // 检查通过后，调用领域服务删除
        categoryDomainService.delete(id);
    }
    
    /**
     * 递归统计分类及其所有子分类中的文件数量
     */
    private long countFilesInCategoryAndChildren(Long categoryId) {
        // 统计当前分类中的文件数量
        long fileCount = docFileRepository.countByCategoryId(categoryId);
        
        // 查找所有子分类
        List<CategoryDomainDTO> allCategories = categoryDomainService.findAll();
        List<Long> childCategoryIds = findAllChildCategoryIds(categoryId, allCategories);
        
        // 递归统计所有子分类中的文件数量
        for (Long childId : childCategoryIds) {
            fileCount += docFileRepository.countByCategoryId(childId);
        }
        
        return fileCount;
    }
    
    /**
     * 查找所有子分类ID（递归）
     */
    private List<Long> findAllChildCategoryIds(Long parentId, List<CategoryDomainDTO> allCategories) {
        List<Long> result = new ArrayList<>();
        
        for (CategoryDomainDTO category : allCategories) {
            if (parentId.equals(category.getParentId())) {
                result.add(category.getId());
                // 递归查找子分类的子分类
                result.addAll(findAllChildCategoryIds(category.getId(), allCategories));
            }
        }
        
        return result;
    }

    /**
     * 聚合文件计数
     */
    private long aggregateCounts(List<CategoryTreeNode> nodes) {
        long sum = 0;
        for (CategoryTreeNode node : nodes) {
            long childCount = aggregateCounts(node.getChildren());
            long selfCount = Optional.ofNullable(node.getFileCount()).orElse(0L);
            long total = selfCount + childCount;
            node.setFileCount(total);
            sum += total;
        }
        return sum;
    }

    /**
     * Domain DTO 转 Response DTO
     */
    private CategoryResponseDTO toResponseDTO(CategoryDomainDTO dto) {
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(dto.getId());
        response.setName(dto.getName());
        response.setParentId(dto.getParentId());
        response.setSort(dto.getSort());
        response.setLevel(dto.getLevel());
        return response;
    }
}

