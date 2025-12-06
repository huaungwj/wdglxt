package com.example.dms.controller.dto;

import lombok.Data;

/**
 * Controller 层的文档请求 DTO
 */
@Data
public class DocFileRequestDTO {
    private String fileNo;
    private String name;
    private Long categoryId;
    private String tags;
    private String keyword;
    private Long creatorId;
    private Integer status;
}

