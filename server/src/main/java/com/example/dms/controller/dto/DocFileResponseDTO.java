package com.example.dms.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Controller 层的文档响应 DTO
 */
@Data
public class DocFileResponseDTO {
    private Long id;
    private String fileNo;
    private String name;
    private String originalName;
    private String path;
    private Long size;
    private String type;
    private Long creatorId;
    private String creatorName;
    private Long deptId;
    private String deptName;
    private Long categoryId;
    private String categoryName;
    private Integer status;
    private Integer visitCount;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 预览和下载的完整URL
    private String previewUrl;
    private String downloadUrl;
}

