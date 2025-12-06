package com.example.dms.controller.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户角色关联DTO
 */
@Data
public class UserRoleDTO {
    /** 用户ID */
    private Long userId;
    
    /** 角色ID列表 */
    private List<Long> roleIds;
}

