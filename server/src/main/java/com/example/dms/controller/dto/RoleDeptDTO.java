package com.example.dms.controller.dto;

import lombok.Data;
import java.util.List;

/**
 * 角色部门权限DTO（用于自定义数据权限）
 */
@Data
public class RoleDeptDTO {
    /** 角色ID */
    private Long roleId;
    
    /** 部门ID列表 */
    private List<Long> deptIds;
}

