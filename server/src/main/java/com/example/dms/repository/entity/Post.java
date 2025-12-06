package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位实体类
 * 参考 RuoYi 设计
 */
@Data
@TableName("sys_post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 岗位编码 */
    private String postCode;
    
    /** 岗位名称 */
    private String postName;
    
    /** 岗位顺序 */
    private Integer postSort;
    
    /** 状态（0正常 1停用） */
    private String status;
    
    /** 备注 */
    private String remark;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

