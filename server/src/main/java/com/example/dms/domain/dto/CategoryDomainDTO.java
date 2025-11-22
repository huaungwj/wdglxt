package com.example.dms.domain.dto;

import lombok.Data;

/**
 * Domain Service 层的数据传输对象
 */
@Data
public class CategoryDomainDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer level;
    private Long fileCount;
}

