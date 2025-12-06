package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限点实体
 */
@Data
@TableName("sys_permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code; // 权限标识，如 file:upload
    private String name; // 权限名称
    private String description; // 权限描述
    private Integer type; // 1: 菜单权限, 2: 按钮权限
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

