package com.example.dms.repository;

import com.example.dms.repository.entity.DocFile;
import java.util.List;
import java.util.Optional;

/**
 * 文档文件仓储接口
 */
public interface DocFileRepository {
    /**
     * 保存文档文件
     */
    DocFile save(DocFile docFile);

    /**
     * 根据ID查找
     */
    Optional<DocFile> findById(Long id);

    /**
     * 根据文件编号查找
     */
    Optional<DocFile> findByFileNo(String fileNo);

    /**
     * 更新文档文件
     */
    void update(DocFile docFile);

    /**
     * 删除文档文件（逻辑删除）
     */
    void deleteById(Long id);

    /**
     * 根据创建人ID查找
     */
    List<DocFile> findByCreatorId(Long creatorId);

    /**
     * 根据部门ID查找
     */
    List<DocFile> findByDeptId(Long deptId);

    /**
     * 根据分类ID查找
     */
    List<DocFile> findByCategoryId(Long categoryId);

    /**
     * 统计某个分类下的文件数量（只统计未删除的）
     */
    long countByCategoryId(Long categoryId);

    /**
     * 查找所有未删除的文档
     */
    List<DocFile> findAllActive();

    /**
     * 根据创建人ID列表查找（用于权限控制：查找自己及下级的文档）
     */
    List<DocFile> findByCreatorIds(List<Long> creatorIds);

    /**
     * 根据条件查询（用于搜索）
     */
    List<DocFile> search(String fileNo, Long creatorId, String keyword, Integer status, Long categoryId);
}

