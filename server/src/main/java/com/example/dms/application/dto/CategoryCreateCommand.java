package com.example.dms.application.dto;

import lombok.Data;

/**
 * Application Service 层的创建命令
 */
@Data
public class CategoryCreateCommand {
    private String name;
    private Long parentId;
}

