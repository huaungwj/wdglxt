-- 手动执行此脚本以创建表（如果自动建表失败）
-- Execute this script manually if auto table creation fails

USE dms;

CREATE TABLE IF NOT EXISTS doc_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  parent_id BIGINT DEFAULT NULL,
  sort INT DEFAULT 0,
  level INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
