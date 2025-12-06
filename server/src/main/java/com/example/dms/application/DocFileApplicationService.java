package com.example.dms.application;

import com.example.dms.application.dto.DocFileCreateCommand;
import com.example.dms.application.dto.DocFileResponseDTO;
import com.example.dms.application.dto.DocFileSearchQuery;
import com.example.dms.application.service.PermissionService;
import com.example.dms.domain.CategoryDomainService;
import com.example.dms.domain.DocFileDomainService;
import com.example.dms.domain.dto.CategoryDomainDTO;
import com.example.dms.domain.dto.DocFileDomainDTO;
import com.example.dms.repository.entity.Dept;
import com.example.dms.repository.entity.User;
import com.example.dms.service.DeptService;
import com.example.dms.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文档文件应用服务层
 * 负责业务编排和跨领域协调
 */
@Service
public class DocFileApplicationService {

    private final DocFileDomainService docFileDomainService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final DeptService deptService;
    private final CategoryDomainService categoryDomainService;

    public DocFileApplicationService(
            DocFileDomainService docFileDomainService,
            PermissionService permissionService,
            UserService userService,
            DeptService deptService,
            CategoryDomainService categoryDomainService) {
        this.docFileDomainService = docFileDomainService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.deptService = deptService;
        this.categoryDomainService = categoryDomainService;
    }

    /**
     * 创建文档文件
     */
    @Transactional
    public DocFileResponseDTO create(DocFileCreateCommand command) {
        DocFileDomainDTO dto = docFileDomainService.create(
            command.getFileNo(),
            command.getName(),
            command.getOriginalName(),
            command.getPath(),
            command.getSize(),
            command.getType(),
            command.getCreatorId(),
            command.getDeptId(),
            command.getCategoryId(),
            command.getTags()
        );
        return toResponseDTO(dto);
    }

    /**
     * 根据ID查找文档（带权限检查）
     */
    public Optional<DocFileResponseDTO> findById(Long id, Long currentUserId) {
        Optional<DocFileDomainDTO> docFile = docFileDomainService.findById(id);
        if (docFile.isEmpty()) {
            return Optional.empty();
        }

        // 权限检查：只能查看自己或下级的文档
        if (!permissionService.canViewDocument(currentUserId, docFile.get().getCreatorId())) {
            throw new RuntimeException("无权查看该文档");
        }

        return Optional.of(toResponseDTO(docFile.get()));
    }

    /**
     * 搜索文档（带权限过滤）
     */
    public List<DocFileResponseDTO> search(DocFileSearchQuery query, Long currentUserId) {
        // 获取当前用户可见的用户ID列表
        List<Long> visibleUserIds = permissionService.getVisibleUserIds(currentUserId);

        // 如果指定了创建人，检查是否有权限
        if (query.getCreatorId() != null && !visibleUserIds.contains(query.getCreatorId())) {
            throw new RuntimeException("无权查看该用户的文档");
        }

        // 搜索文档（如果未指定创建人，会在Domain层过滤）
        List<DocFileDomainDTO> results = docFileDomainService.search(
            query.getFileNo(),
            query.getCreatorId(),
            query.getKeyword(),
            query.getStatus(),
            query.getCategoryId()
        );

        // 权限过滤：只返回可见用户的文档
        return results.stream()
            .filter(dto -> visibleUserIds.contains(dto.getCreatorId()))
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * 增加访问次数
     */
    @Transactional
    public void incrementVisitCount(Long id, Long currentUserId) {
        // 先检查权限
        findById(id, currentUserId);
        docFileDomainService.incrementVisitCount(id);
    }

    /**
     * 删除文档
     */
    @Transactional
    public void delete(Long id, Long currentUserId) {
        // 先检查权限
        Optional<DocFileDomainDTO> docFile = docFileDomainService.findById(id);
        if (docFile.isEmpty()) {
            throw new RuntimeException("文档不存在");
        }

        // 只能删除自己创建的文档
        if (!docFile.get().getCreatorId().equals(currentUserId)) {
            throw new RuntimeException("只能删除自己创建的文档");
        }

        docFileDomainService.delete(id);
    }

    /**
     * 更新标签
     */
    @Transactional
    public void updateTags(Long id, String tags, Long currentUserId) {
        // 先检查权限
        Optional<DocFileDomainDTO> docFile = docFileDomainService.findById(id);
        if (docFile.isEmpty()) {
            throw new RuntimeException("文档不存在");
        }

        // 只能修改自己创建的文档
        if (!docFile.get().getCreatorId().equals(currentUserId)) {
            throw new RuntimeException("只能修改自己创建的文档");
        }

        docFileDomainService.updateTags(id, tags);
    }

    /**
     * 更新分类
     */
    @Transactional
    public void updateCategory(Long id, Long categoryId, Long currentUserId) {
        // 先检查权限
        Optional<DocFileDomainDTO> docFile = docFileDomainService.findById(id);
        if (docFile.isEmpty()) {
            throw new RuntimeException("文档不存在");
        }

        // 只能修改自己创建的文档
        if (!docFile.get().getCreatorId().equals(currentUserId)) {
            throw new RuntimeException("只能修改自己创建的文档");
        }

        docFileDomainService.updateCategory(id, categoryId);
    }

    /**
     * Domain DTO 转 Response DTO（填充关联信息）
     */
    private DocFileResponseDTO toResponseDTO(DocFileDomainDTO dto) {
        DocFileResponseDTO response = new DocFileResponseDTO();
        response.setId(dto.getId());
        response.setFileNo(dto.getFileNo());
        response.setName(dto.getName());
        response.setOriginalName(dto.getOriginalName());
        response.setPath(dto.getPath());
        response.setSize(dto.getSize());
        response.setType(dto.getType());
        response.setCreatorId(dto.getCreatorId());
        response.setDeptId(dto.getDeptId());
        response.setCategoryId(dto.getCategoryId());
        response.setStatus(dto.getStatus());
        response.setVisitCount(dto.getVisitCount());
        response.setTags(dto.getTags());
        response.setCreatedAt(dto.getCreatedAt());
        response.setUpdatedAt(dto.getUpdatedAt());

        // 填充创建人姓名
        if (dto.getCreatorId() != null) {
            User user = userService.getById(dto.getCreatorId());
            if (user != null) {
                response.setCreatorName(user.getRealName());
            }
        }

        // 填充部门名称
        if (dto.getDeptId() != null) {
            Dept dept = deptService.getById(dto.getDeptId());
            if (dept != null) {
                response.setDeptName(dept.getName());
            }
        }

        // 填充分类名称
        if (dto.getCategoryId() != null) {
            Optional<CategoryDomainDTO> category = categoryDomainService.findById(dto.getCategoryId());
            if (category.isPresent()) {
                response.setCategoryName(category.get().getName());
            }
        }

        return response;
    }
}

