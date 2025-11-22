package com.example.dms.controller.dto;

import lombok.Data;

/**
 * Controller 层的创建响应 DTO
 */
@Data
public class CategoryCreateResponseDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer level;
}

