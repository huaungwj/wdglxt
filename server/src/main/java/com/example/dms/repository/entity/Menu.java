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
 * 菜单实体类
 * 参考 RuoYi 设计
 * type: 0=目录, 1=菜单, 2=按钮
 */
@Data
@TableName("sys_menu")
public class Menu {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 菜单名称 */
    private String name;
    
    /** 父菜单ID */
    private Long parentId;
    
    /** 路由地址 */
    private String path;
    
    /** 组件路径 */
    private String component;
    
    /** 权限标识 */
    private String permCode;
    
    /** 菜单类型（0目录 1菜单 2按钮） */
    private Integer type;
    
    /** 显示排序 */
    private Integer sort;
    
    /** 菜单图标 */
    private String icon;
    
    /** 是否外链（0是 1否） */
    private Integer isFrame;
    
    /** 显示状态（0显示 1隐藏） */
    private String visible;
    
    /** 菜单状态（0正常 1停用） */
    private String status;
    
    /** 子菜单列表（用于树形结构，不映射到数据库） */
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Menu> children;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
