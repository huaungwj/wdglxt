package com.example.dms.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Controller 层的请求 DTO
 */
@Data
public class CategoryRequestDTO {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private Long parentId;
    private Integer sort;
}

