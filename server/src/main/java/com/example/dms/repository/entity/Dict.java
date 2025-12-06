package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dict")
public class Dict {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String code;
    private String value;
    private String label;
    private Integer sort;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
