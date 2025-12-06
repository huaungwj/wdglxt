package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门实体类
 * 参考 RuoYi 设计
 */
@Data
@TableName("sys_dept")
public class Dept {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 部门名称 */
    private String name;
    
    /** 父部门ID */
    private Long parentId;
    
    /** 显示顺序 */
    private Integer sort;
    
    /** 负责人 */
    private String leader;
    
    /** 联系电话 */
    private String phone;
    
    /** 邮箱 */
    private String email;
    
    /** 部门状态（0正常 1停用） */
    private String status;
    
    /** 子部门列表（用于树形结构，不映射到数据库） */
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Dept> children;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
