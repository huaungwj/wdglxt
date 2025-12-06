package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("doc_visit_log")
public class DocVisitLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long fileId;
    private Long userId;
    private LocalDateTime visitTime;
}
