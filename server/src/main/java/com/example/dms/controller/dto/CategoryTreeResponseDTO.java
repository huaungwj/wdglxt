package com.example.dms.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller 层的树节点响应 DTO
 */
@Data
public class CategoryTreeResponseDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer level;
    private Long fileCount;
    private List<CategoryTreeResponseDTO> children = new ArrayList<>();
}

