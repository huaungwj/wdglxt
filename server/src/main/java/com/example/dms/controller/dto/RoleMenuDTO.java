package com.example.dms.controller.dto;

import lombok.Data;
import java.util.List;

/**
 * 角色菜单权限DTO
 */
@Data
public class RoleMenuDTO {
    /** 角色ID */
    private Long roleId;
    
    /** 菜单ID列表 */
    private List<Long> menuIds;
}

