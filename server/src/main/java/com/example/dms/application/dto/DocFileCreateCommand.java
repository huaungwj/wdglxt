package com.example.dms.application.dto;

import lombok.Data;

/**
 * Application Service 层的文档创建命令
 */
@Data
public class DocFileCreateCommand {
    private String fileNo;
    private String name;
    private String originalName;
    private String path;
    private Long size;
    private String type;
    private Long creatorId;
    private Long deptId;
    private Long categoryId;
    private String tags;
}

