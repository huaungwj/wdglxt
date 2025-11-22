package com.example.dms.application.dto;

import lombok.Data;

/**
 * Application Service 层的更新命令
 */
@Data
public class CategoryUpdateCommand {
    private String name;
    private Long parentId;
    private Integer sort;
}

