package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色和部门关联表
 * 用于自定义数据权限
 */
@Data
@TableName("sys_role_dept")
public class RoleDept {
    /** 角色ID */
    private Long roleId;
    
    /** 部门ID */
    private Long deptId;
}

