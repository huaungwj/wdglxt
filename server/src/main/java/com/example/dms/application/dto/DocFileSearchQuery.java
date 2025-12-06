package com.example.dms.application.dto;

import lombok.Data;

/**
 * Application Service 层的文档搜索查询
 */
@Data
public class DocFileSearchQuery {
    private String fileNo;
    private Long creatorId;
    private String keyword;
    private Integer status;
    private Long categoryId;
    private Integer page;
    private Integer pageSize;
}

