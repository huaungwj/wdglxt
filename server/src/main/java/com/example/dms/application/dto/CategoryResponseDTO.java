package com.example.dms.application.dto;

import lombok.Data;

/**
 * Application Service 层的响应 DTO
 */
@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer level;
}

