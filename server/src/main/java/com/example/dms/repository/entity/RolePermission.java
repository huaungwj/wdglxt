package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色权限关联实体
 */
@Data
@TableName("sys_role_permission")
public class RolePermission {
    private Long roleId;
    private Long permissionId;
}

