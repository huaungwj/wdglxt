package com.example.dms.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Domain Service 层的文档文件数据传输对象
 */
@Data
public class DocFileDomainDTO {
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
}

