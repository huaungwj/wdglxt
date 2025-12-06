package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName; // 用户昵称/真实姓名
    private String phone; // 手机号码
    private String email; // 邮箱
    private String gender; // 性别：0=男，1=女，2=未知
    private Long deptId; // 归属部门
    private Long postId; // 岗位ID
    private Long parentId; // 上级用户ID
    private String departmentPath; // 部门路径，例如 1/3/8
    private String remark; // 备注
    private Integer status; // 状态：1=正常，0=停用
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
