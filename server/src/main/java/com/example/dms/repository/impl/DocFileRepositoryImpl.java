package com.example.dms.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dms.repository.DocFileRepository;
import com.example.dms.repository.entity.DocFile;
import com.example.dms.repository.mapper.DocFileMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 文档文件仓储实现
 */
@Repository
public class DocFileRepositoryImpl implements DocFileRepository {

    private final DocFileMapper docFileMapper;

    public DocFileRepositoryImpl(DocFileMapper docFileMapper) {
        this.docFileMapper = docFileMapper;
    }

    @Override
    public DocFile save(DocFile docFile) {
        docFileMapper.insert(docFile);
        return docFile;
    }

    @Override
    public Optional<DocFile> findById(Long id) {
        return Optional.ofNullable(docFileMapper.selectById(id));
    }

    @Override
    public Optional<DocFile> findByFileNo(String fileNo) {
        return Optional.ofNullable(
            docFileMapper.selectOne(
                new LambdaQueryWrapper<DocFile>()
                    .eq(DocFile::getFileNo, fileNo)
            )
        );
    }

    @Override
    public void update(DocFile docFile) {
        docFileMapper.updateById(docFile);
    }

    @Override
    public void deleteById(Long id) {
        Optional<DocFile> docFile = findById(id);
        docFile.ifPresent(file -> {
            file.markAsDeleted();
            update(file);
        });
    }

    @Override
    public List<DocFile> findByCreatorId(Long creatorId) {
        return docFileMapper.selectList(
            new LambdaQueryWrapper<DocFile>()
                .eq(DocFile::getCreatorId, creatorId)
                .eq(DocFile::getStatus, 1)
        );
    }

    @Override
    public List<DocFile> findByDeptId(Long deptId) {
        return docFileMapper.selectList(
            new LambdaQueryWrapper<DocFile>()
                .eq(DocFile::getDeptId, deptId)
                .eq(DocFile::getStatus, 1)
        );
    }

    @Override
    public List<DocFile> findByCategoryId(Long categoryId) {
        return docFileMapper.selectList(
            new LambdaQueryWrapper<DocFile>()
                .eq(DocFile::getCategoryId, categoryId)
                .eq(DocFile::getStatus, 1)
        );
    }

    @Override
    public List<DocFile> findAllActive() {
        return docFileMapper.selectList(
            new LambdaQueryWrapper<DocFile>()
                .eq(DocFile::getStatus, 1)
        );
    }

    @Override
    public List<DocFile> findByCreatorIds(List<Long> creatorIds) {
        if (creatorIds == null || creatorIds.isEmpty()) {
            return List.of();
        }
        return docFileMapper.selectList(
            new LambdaQueryWrapper<DocFile>()
                .in(DocFile::getCreatorId, creatorIds)
                .eq(DocFile::getStatus, 1)
        );
    }

    @Override
    public List<DocFile> search(String fileNo, Long creatorId, String keyword, Integer status, Long categoryId) {
        LambdaQueryWrapper<DocFile> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(fileNo)) {
            wrapper.eq(DocFile::getFileNo, fileNo);
        }
        
        if (creatorId != null) {
            wrapper.eq(DocFile::getCreatorId, creatorId);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(DocFile::getName, keyword)
                .or()
                .like(DocFile::getOriginalName, keyword)
                .or()
                .like(DocFile::getTags, keyword)
            );
        }
        
        if (status != null) {
            wrapper.eq(DocFile::getStatus, status);
        } else {
            wrapper.eq(DocFile::getStatus, 1); // 默认只查询未删除的
        }
        
        if (categoryId != null) {
            wrapper.eq(DocFile::getCategoryId, categoryId);
        }
        
        wrapper.orderByDesc(DocFile::getCreatedAt);
        
        return docFileMapper.selectList(wrapper);
    }
}

