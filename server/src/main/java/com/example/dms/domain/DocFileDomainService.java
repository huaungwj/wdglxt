package com.example.dms.domain;

import com.example.dms.domain.dto.DocFileDomainDTO;
import com.example.dms.repository.DocFileRepository;
import com.example.dms.repository.entity.DocFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文档文件领域服务层
 * 包含核心业务逻辑
 */
@Service
public class DocFileDomainService {

    private final DocFileRepository docFileRepository;

    public DocFileDomainService(DocFileRepository docFileRepository) {
        this.docFileRepository = docFileRepository;
    }

    /**
     * 创建文档文件
     */
    @Transactional
    public DocFileDomainDTO create(String fileNo, String name, String originalName, String path,
                                   Long size, String type, Long creatorId, Long deptId, Long categoryId, String tags) {
        // 检查文件编号是否已存在
        Optional<DocFile> existing = docFileRepository.findByFileNo(fileNo);
        if (existing.isPresent()) {
            throw new RuntimeException("文件编号已存在: " + fileNo);
        }

        // 使用充血模型创建实体
        DocFile docFile = DocFile.create(fileNo, name, originalName, path, size, type, creatorId, deptId, categoryId, tags);

        // 保存
        DocFile saved = docFileRepository.save(docFile);

        // 转换为 DTO
        return toDTO(saved);
    }

    /**
     * 根据ID查找文档
     */
    public Optional<DocFileDomainDTO> findById(Long id) {
        return docFileRepository.findById(id)
            .filter(file -> !file.isDeleted())
            .map(this::toDTO);
    }

    /**
     * 增加访问次数
     */
    @Transactional
    public void incrementVisitCount(Long id) {
        DocFile docFile = docFileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文档不存在"));
        
        docFile.incrementVisitCount();
        docFileRepository.update(docFile);
    }

    /**
     * 删除文档（逻辑删除）
     */
    @Transactional
    public void delete(Long id) {
        DocFile docFile = docFileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文档不存在"));
        
        docFileRepository.deleteById(id);
    }

    /**
     * 根据创建人ID列表查找（用于权限控制）
     */
    public List<DocFileDomainDTO> findByCreatorIds(List<Long> creatorIds) {
        return docFileRepository.findByCreatorIds(creatorIds).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 搜索文档
     */
    public List<DocFileDomainDTO> search(String fileNo, Long creatorId, String keyword, Integer status, Long categoryId) {
        return docFileRepository.search(fileNo, creatorId, keyword, status, categoryId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 更新标签
     */
    @Transactional
    public void updateTags(Long id, String tags) {
        DocFile docFile = docFileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文档不存在"));
        
        docFile.updateTags(tags);
        docFileRepository.update(docFile);
    }

    /**
     * 更新分类
     */
    @Transactional
    public void updateCategory(Long id, Long categoryId) {
        DocFile docFile = docFileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文档不存在"));
        
        docFile.updateCategory(categoryId);
        docFileRepository.update(docFile);
    }

    /**
     * 实体转 DTO
     */
    private DocFileDomainDTO toDTO(DocFile docFile) {
        DocFileDomainDTO dto = new DocFileDomainDTO();
        dto.setId(docFile.getId());
        dto.setFileNo(docFile.getFileNo());
        dto.setName(docFile.getName());
        dto.setOriginalName(docFile.getOriginalName());
        dto.setPath(docFile.getPath());
        dto.setSize(docFile.getSize());
        dto.setType(docFile.getType());
        dto.setCreatorId(docFile.getCreatorId());
        dto.setDeptId(docFile.getDeptId());
        dto.setCategoryId(docFile.getCategoryId());
        dto.setStatus(docFile.getStatus());
        dto.setVisitCount(docFile.getVisitCount());
        dto.setTags(docFile.getTags());
        dto.setCreatedAt(docFile.getCreatedAt());
        dto.setUpdatedAt(docFile.getUpdatedAt());
        return dto;
    }
}

