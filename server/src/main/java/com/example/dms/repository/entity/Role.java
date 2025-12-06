package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体类
 * 参考 RuoYi 设计
 */
@Data
@TableName("sys_role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 角色名称 */
    private String name;
    
    /** 权限字符（如：admin） */
    private String code;
    
    /** 角色顺序 */
    private Integer roleSort;
    
    /** 角色状态（0正常 1停用） */
    private String status;
    
    /** 数据权限范围（1全部数据权限 2自定义数据权限 3本部门数据权限 4本部门及以下数据权限 5仅本人数据权限） */
    private String dataScope;
    
    /** 备注 */
    private String remark;
    
    /** 描述（保留兼容性） */
    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
