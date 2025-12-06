package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档文件实体（充血模型）
 * 包含业务逻辑方法
 */
@Data
@TableName("doc_file")
public class DocFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fileNo;
    private String name;
    private String originalName;
    private String path;
    private Long size;
    private String type;
    private Long creatorId;
    private Long deptId;
    private Long categoryId;
    private Integer status;
    private Integer visitCount;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 创建新的文档文件
     */
    public static DocFile create(String fileNo, String name, String originalName, String path, 
                                 Long size, String type, Long creatorId, Long deptId, Long categoryId, String tags) {
        DocFile docFile = new DocFile();
        docFile.setFileNo(fileNo);
        docFile.setName(name);
        docFile.setOriginalName(originalName);
        docFile.setPath(path);
        docFile.setSize(size);
        docFile.setType(type);
        docFile.setCreatorId(creatorId);
        docFile.setDeptId(deptId);
        docFile.setCategoryId(categoryId);
        docFile.setTags(tags);
        docFile.setStatus(1);
        docFile.setVisitCount(0);
        docFile.setCreatedAt(LocalDateTime.now());
        docFile.setUpdatedAt(LocalDateTime.now());
        return docFile;
    }

    /**
     * 增加访问次数
     */
    public void incrementVisitCount() {
        this.visitCount = (this.visitCount == null ? 0 : this.visitCount) + 1;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 标记为已删除
     */
    public void markAsDeleted() {
        this.status = 0;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 检查是否已删除
     */
    public boolean isDeleted() {
        return this.status != null && this.status == 0;
    }

    /**
     * 更新标签
     */
    public void updateTags(String tags) {
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新分类
     */
    public void updateCategory(Long categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now();
    }
}
