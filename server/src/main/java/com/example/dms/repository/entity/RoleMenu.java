package com.example.dms.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_menu")
public class RoleMenu {
    private Long roleId;
    private Long menuId;
}
