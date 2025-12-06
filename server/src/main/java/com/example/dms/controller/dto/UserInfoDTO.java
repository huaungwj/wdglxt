package com.example.dms.controller.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户信息DTO（包含部门、岗位、角色等信息）
 */
@Data
public class UserInfoDTO {
    /** 用户ID */
    private Long id;
    
    /** 用户名 */
    private String username;
    
    /** 用户昵称 */
    private String realName;
    
    /** 手机号码 */
    private String phone;
    
    /** 邮箱 */
    private String email;
    
    /** 性别 */
    private String gender;
    
    /** 部门ID */
    private Long deptId;
    
    /** 部门名称 */
    private String deptName;
    
    /** 岗位ID */
    private Long postId;
    
    /** 岗位名称 */
    private String postName;
    
    /** 角色列表 */
    private List<RoleInfo> roles;
    
    /** 权限列表 */
    private List<String> permissions;
    
    /** 数据权限范围 */
    private String dataScope;
    
    /** 数据权限范围描述 */
    private String dataScopeText;
    
    /** 状态 */
    private Integer status;
    
    /** 备注 */
    private String remark;
    
    /** 创建时间 */
    private String createdAt;
    
    @Data
    public static class RoleInfo {
        private Long id;
        private String name;
        private String code;
        private String dataScope;
    }
}

