package com.example.dms.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeNode {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer level;
    private Long fileCount;
    private List<CategoryTreeNode> children = new ArrayList<>();
}
