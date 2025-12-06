-- MySQL schema for DMS
-- 根据现有数据库结构生成

-- Users
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  real_name VARCHAR(50),
  dept_id BIGINT,
  status INT DEFAULT 1 COMMENT '1: Normal, 0: Disabled',
  parent_id BIGINT DEFAULT NULL COMMENT '上级用户ID',
  department_path VARCHAR(500) DEFAULT NULL COMMENT '部门路径，例如 1/3/8',
  post_id BIGINT DEFAULT NULL COMMENT '岗位ID',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  gender CHAR(1) DEFAULT '2' COMMENT '性别（0男 1女 2未知）',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_username (username),
  INDEX idx_dept (dept_id),
  INDEX idx_parent (parent_id),
  INDEX idx_dept_path (department_path)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Roles
CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  code VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  role_sort INT DEFAULT 0 COMMENT '角色顺序',
  status CHAR(1) DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  data_scope CHAR(1) DEFAULT '1' COMMENT '数据权限范围（1全部数据权限 2自定义数据权限 3本部门数据权限 4本部门及以下数据权限 5仅本人数据权限）',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User Role Mapping
CREATE TABLE IF NOT EXISTS sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Departments
CREATE TABLE IF NOT EXISTS sys_dept (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  sort INT DEFAULT 0,
  leader VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  status CHAR(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Menus
CREATE TABLE IF NOT EXISTS sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  path VARCHAR(255),
  component VARCHAR(255),
  perm_code VARCHAR(100),
  type INT DEFAULT 1 COMMENT '1: Menu, 2: Button',
  sort INT DEFAULT 0,
  icon VARCHAR(50),
  is_frame INT DEFAULT 0 COMMENT '是否外链（0是 1否）',
  visible CHAR(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  status CHAR(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Role Menu Mapping
CREATE TABLE IF NOT EXISTS sys_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Permissions
CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限标识，如 file:upload',
  name VARCHAR(100) NOT NULL COMMENT '权限名称',
  description VARCHAR(255) DEFAULT NULL COMMENT '权限描述',
  type INT DEFAULT 1 COMMENT '1: 菜单权限, 2: 按钮权限',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_code (code),
  INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Role Permission Mapping
CREATE TABLE IF NOT EXISTS sys_role_permission (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  INDEX idx_role (role_id),
  INDEX idx_permission (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Posts
CREATE TABLE IF NOT EXISTS sys_post (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_code VARCHAR(50) NOT NULL UNIQUE COMMENT '岗位编码',
  post_name VARCHAR(50) NOT NULL COMMENT '岗位名称',
  post_sort INT DEFAULT 0 COMMENT '岗位顺序',
  status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_post_code (post_code),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息表';

-- Role Dept Mapping (for custom data scope)
CREATE TABLE IF NOT EXISTS sys_role_dept (
  role_id BIGINT NOT NULL COMMENT '角色ID',
  dept_id BIGINT NOT NULL COMMENT '部门ID',
  PRIMARY KEY (role_id, dept_id),
  INDEX idx_role (role_id),
  INDEX idx_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表';

-- Dictionaries
CREATE TABLE IF NOT EXISTS sys_dict (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(50) NOT NULL,
  code VARCHAR(50) NOT NULL,
  value VARCHAR(255) NOT NULL,
  label VARCHAR(255) NOT NULL,
  sort INT DEFAULT 0,
  description VARCHAR(255),
  permission_code VARCHAR(100) DEFAULT NULL COMMENT '权限标识，控制谁可以访问此字典项',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_type (type),
  INDEX idx_permission (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Document Categories
CREATE TABLE IF NOT EXISTS doc_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  parent_id BIGINT DEFAULT NULL,
  sort INT DEFAULT 0,
  level INT DEFAULT 0,
  upload_permission VARCHAR(100) DEFAULT NULL COMMENT '上传权限标识',
  view_permission VARCHAR(100) DEFAULT NULL COMMENT '查看权限标识',
  download_permission VARCHAR(100) DEFAULT NULL COMMENT '下载权限标识',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Documents
CREATE TABLE IF NOT EXISTS doc_file (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  file_no VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  original_name VARCHAR(255),
  path VARCHAR(500) NOT NULL,
  size BIGINT,
  type VARCHAR(255),
  creator_id BIGINT NOT NULL,
  dept_id BIGINT,
  category_id BIGINT,
  status INT DEFAULT 1 COMMENT '1: Normal, 0: Deleted',
  visit_count INT DEFAULT 0,
  tags VARCHAR(255),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_creator (creator_id),
  INDEX idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Document Visit Logs
CREATE TABLE IF NOT EXISTS doc_visit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  file_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  visit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_file (file_id),
  INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
