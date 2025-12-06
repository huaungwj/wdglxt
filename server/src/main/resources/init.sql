/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80025 (8.0.25)
 Source Host           : localhost:3306
 Source Schema         : dms

 Target Server Type    : MySQL
 Target Server Version : 80025 (8.0.25)
 File Encoding         : 65001

 Date: 03/12/2025 16:00:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for doc_category
-- ----------------------------
DROP TABLE IF EXISTS `doc_category`;
CREATE TABLE `doc_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `sort` int DEFAULT '0',
  `level` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_permission` varchar(100) DEFAULT NULL COMMENT '上传权限标识',
  `view_permission` varchar(100) DEFAULT NULL COMMENT '查看权限标识',
  `download_permission` varchar(100) DEFAULT NULL COMMENT '下载权限标识',
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of doc_category
-- ----------------------------
BEGIN;
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (1, '企管', NULL, 0, 0, '2025-11-13 15:42:09', '2025-11-22 10:54:50', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (3, '生产', NULL, 0, 0, '2025-11-13 15:42:29', '2025-11-13 15:42:29', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (4, '技术', NULL, 0, 0, '2025-11-13 15:42:34', '2025-11-13 15:42:34', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (5, '质量', NULL, 0, 0, '2025-11-13 15:42:39', '2025-11-13 15:42:39', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (6, '设备', NULL, 0, 0, '2025-11-13 15:42:48', '2025-11-13 15:42:48', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (7, '采购', NULL, 0, 0, '2025-11-13 15:42:56', '2025-11-13 15:42:56', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (9, '企管-1', 1, 0, 1, '2025-11-13 15:44:24', '2025-11-13 15:44:24', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (10, '企管-2', 1, 0, 1, '2025-11-13 15:44:35', '2025-11-13 15:44:35', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (11, '企管-1-1', 9, 0, 2, '2025-11-13 15:53:08', '2025-11-13 15:53:08', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (14, 'asdas', NULL, 0, 0, '2025-11-22 10:21:18', '2025-11-22 10:21:18', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (15, '技术文档', NULL, 1, 0, '2025-12-03 11:31:43', '2025-12-03 11:31:43', NULL, NULL, NULL);
INSERT INTO `doc_category` (`id`, `name`, `parent_id`, `sort`, `level`, `created_at`, `updated_at`, `upload_permission`, `view_permission`, `download_permission`) VALUES (16, '产品文档', NULL, 2, 0, '2025-12-03 11:31:43', '2025-12-03 11:31:43', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for doc_file
-- ----------------------------
DROP TABLE IF EXISTS `doc_file`;
CREATE TABLE `doc_file` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_no` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `original_name` varchar(255) DEFAULT NULL,
  `path` varchar(500) NOT NULL,
  `size` bigint DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `creator_id` bigint NOT NULL,
  `dept_id` bigint DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `status` int DEFAULT '1' COMMENT '1: Normal, 0: Deleted',
  `visit_count` int DEFAULT '0',
  `tags` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_no` (`file_no`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of doc_file
-- ----------------------------
BEGIN;
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (1, 'F1764737800682', '3c2454c42ea2d1772ef682352eebdcf0', '3c2454c42ea2d1772ef682352eebdcf0.jpg', 'uploads/b2ff189a-7d6f-4550-ba53-94484e4a0922.jpg', 671661, 'image/jpeg', 1, NULL, 1, 0, 1, NULL, '2025-12-03 12:56:41', '2025-12-03 13:20:17');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (2, 'F1764737800793', '厨+启动图9', '厨+启动图9.zip', 'uploads/245feec9-0d80-48df-a871-ff1d4b2deb50.zip', 780180, 'application/zip', 1, NULL, 1, 0, 0, NULL, '2025-12-03 12:56:41', '2025-12-03 13:20:18');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (3, 'F1764737906399', '3c2454c42ea2d1772ef682352eebdcf0', '3c2454c42ea2d1772ef682352eebdcf0.jpg', 'uploads/ca44653e-5e1e-412b-903f-6ca529e49a71.jpg', 671661, 'image/jpeg', 1, NULL, NULL, 0, 1, NULL, '2025-12-03 12:58:26', '2025-12-03 13:20:15');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (4, 'F1764737906451', '厨+启动图9', '厨+启动图9.zip', 'uploads/feafb864-cd0a-4230-b229-05266be5b2bc.zip', 780180, 'application/zip', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 12:58:26', '2025-12-03 13:20:16');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (5, 'F1764737906620', '交易所综合问题20251125', '交易所综合问题20251125.docx', 'uploads/7070b3f6-da20-430e-a1af-950e5bb45c4c.docx', 5880129, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 12:58:27', '2025-12-03 13:20:12');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (6, 'F1764737906764', '金掌柜交易所问题20251201', '金掌柜交易所问题20251201.docx', 'uploads/38efaf79-37f4-4f4f-a3fb-b3112cbc4c23.docx', 4055082, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 12:58:27', '2025-12-03 13:20:13');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (7, 'F1764737906798', '屏保', '屏保.png', 'uploads/3696452a-806d-4be6-a9fd-4e06a35cf9f0.png', 61541, 'image/png', 1, NULL, NULL, 0, 2, NULL, '2025-12-03 12:58:27', '2025-12-03 13:20:14');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (8, 'F1764738681561', 'trailer_hd', 'trailer_hd.mp4', 'uploads/c30f70d2-642a-4fe3-b65e-0fa1ca0d1ee1.mp4', 14621544, 'video/mp4', 1, NULL, NULL, 0, 5, NULL, '2025-12-03 13:11:22', '2025-12-03 13:19:38');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (9, 'F1764739252529', '3c2454c42ea2d1772ef682352eebdcf0', '3c2454c42ea2d1772ef682352eebdcf0.jpg', 'uploads/25d500fd-580d-4a3c-aea5-889e5e3f6690.jpg', 671661, 'image/jpeg', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:20:53', '2025-12-03 13:24:20');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (10, 'F1764739252577', '厨+启动图9', '厨+启动图9.zip', 'uploads/b5609b9b-4593-40df-8d9d-3cd2316aa52d.zip', 780180, 'application/zip', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:20:53', '2025-12-03 13:24:23');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (11, 'F1764739252745', '交易所综合问题20251125', '交易所综合问题20251125.docx', 'uploads/df61edde-98fa-4fe2-815f-9205ef1084bb.docx', 5880129, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:20:53', '2025-12-03 13:24:22');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (12, 'F1764739252921', '金掌柜交易所问题20251201', '金掌柜交易所问题20251201.docx', 'uploads/2154e337-c093-4b01-8ebc-869967925bc7.docx', 4055082, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:20:53', '2025-12-03 13:26:20');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (13, 'F1764739252990', '屏保', '屏保.png', 'uploads/450db245-bebd-4e46-b7e3-0d170d5897c8.png', 61541, 'image/png', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:20:53', '2025-12-03 13:26:21');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (14, 'F1764739264551', 'trailer_hd', 'trailer_hd.mp4', 'uploads/42f734ff-4336-4b93-b2b5-db4b4241384f.mp4', 14621544, 'video/mp4', 1, NULL, NULL, 0, 1, NULL, '2025-12-03 13:21:05', '2025-12-03 13:24:19');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (15, 'F1764739615720', '3c2454c42ea2d1772ef682352eebdcf0', '3c2454c42ea2d1772ef682352eebdcf0.jpg', 'uploads/2025/12/03/108b4f51-782e-4a9b-8bc8-bf0ca1af81f1.jpg', 671661, 'image/jpeg', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:26:56', '2025-12-03 13:35:49');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (16, 'F1764739615779', '厨+启动图9', '厨+启动图9.zip', 'uploads/2025/12/03/2a383e20-d5cf-4326-b24b-4db78c358da8.zip', 780180, 'application/zip', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:26:56', '2025-12-03 13:35:50');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (17, 'F1764739615965', '交易所综合问题20251125', '交易所综合问题20251125.docx', 'uploads/2025/12/03/f570b1dc-f826-4541-9755-055bd2e18e06.docx', 5880129, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:26:56', '2025-12-03 13:35:51');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (18, 'F1764739616091', '金掌柜交易所问题20251201', '金掌柜交易所问题20251201.docx', 'uploads/2025/12/03/7848afdb-e1e3-4f6c-8f6b-1245d0b7b03a.docx', 4055082, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:26:56', '2025-12-03 13:35:54');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (19, 'F1764739616123', '屏保', '屏保.png', 'uploads/2025/12/03/1cf9859c-2a6b-42d8-8250-8c1ea4b11c03.png', 61541, 'image/png', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:26:56', '2025-12-03 13:35:52');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (20, 'F1764739616556', 'trailer_hd', 'trailer_hd.mp4', 'uploads/2025/12/03/81e88b56-14ca-4ffc-be69-e53d519ef250.mp4', 14621544, 'video/mp4', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:26:57', '2025-12-03 13:35:46');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (21, 'F1764740020592', '金掌柜交易所问题20251201', '金掌柜交易所问题20251201.docx', 'uploads/2025/12/03/8a8b6c0e-ba39-4fa4-b54b-c90f4af613a8.docx', 4055082, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:33:41', '2025-12-03 13:35:48');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (22, 'F1764740021889', '金掌柜交易所问题20251201', '金掌柜交易所问题20251201.docx', 'uploads/2025/12/03/2852c4b2-ae11-43cb-9455-2cd9356eb776.docx', 4055082, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:33:42', '2025-12-03 13:35:45');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (23, 'F1764740022759', '金掌柜交易所问题20251201', '金掌柜交易所问题20251201.docx', 'uploads/2025/12/03/ab69aec7-de94-42eb-b365-32af02554703.docx', 4055082, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:33:43', '2025-12-03 13:35:44');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (24, 'F1764740095313', '3c2454c42ea2d1772ef682352eebdcf0', '3c2454c42ea2d1772ef682352eebdcf0.jpg', 'uploads/2025/12/03/3a65bc39-192b-4564-919f-a4b001be4854.jpg', 671661, 'image/jpeg', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:34:55', '2025-12-03 13:35:41');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (25, 'F1764740095385', '厨+启动图9', '厨+启动图9.zip', 'uploads/2025/12/03/4b9755b5-2bfd-487b-bb56-650a103735e4.zip', 780180, 'application/zip', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:34:55', '2025-12-03 13:35:43');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (26, 'F1764740095548', '交易所综合问题20251125', '交易所综合问题20251125.docx', 'uploads/2025/12/03/c61874f2-0e68-4224-a28c-d4a5be08b178.docx', 5880129, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:34:56', '2025-12-03 13:35:38');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (27, 'F1764740095669', '金掌柜交易所问题20251201', '金掌柜交易所问题20251201.docx', 'uploads/2025/12/03/9fbc1024-3a0e-4357-bcaa-74a7cbf2b34e.docx', 4055082, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:34:56', '2025-12-03 13:35:39');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (28, 'F1764740095696', '屏保', '屏保.png', 'uploads/2025/12/03/83c6fbb2-0659-4322-9d60-0648a9ee2930.png', 61541, 'image/png', 1, NULL, NULL, 0, 0, NULL, '2025-12-03 13:34:56', '2025-12-03 13:35:40');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (29, 'F1764746470690', '3c2454c42ea2d1772ef682352eebdcf0', '3c2454c42ea2d1772ef682352eebdcf0.jpg', 'uploads/2025/12/03/df7317fb-bb93-40b8-8aad-c6d9d8cc948e.jpg', 671661, 'image/jpeg', 1, NULL, 1, 1, 0, NULL, '2025-12-03 15:21:11', '2025-12-03 15:21:11');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (30, 'F1764746498237', '屏保', '屏保.png', 'uploads/2025/12/03/b27f1967-5520-473d-91fa-0ecae8069122.png', 61541, 'image/png', 27, NULL, 1, 1, 1, NULL, '2025-12-03 15:21:38', '2025-12-03 15:21:57');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (31, 'F1764746601453', '交易所综合问题20251125', '交易所综合问题20251125.docx', 'uploads/2025/12/03/15624a93-d675-4fe5-948c-cb0c1e86395f.docx', 5880129, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 28, NULL, 1, 0, 0, NULL, '2025-12-03 15:23:21', '2025-12-03 15:54:46');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (32, 'F1764746601487', '厨+启动图9', '厨+启动图9.zip', 'uploads/2025/12/03/14fedcbd-66fe-4f41-a05d-1d4a25618392.zip', 780180, 'application/zip', 28, NULL, 1, 1, 0, NULL, '2025-12-03 15:23:21', '2025-12-03 15:23:21');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (33, 'F1764748445497', 'cloud-computing-3d', 'cloud-computing-3d.jpg', 'uploads/2025/12/03/989d0485-8ded-4a2b-a81e-d91db0347200.jpg', 110778, 'image/jpeg', 31, NULL, 10, 0, 0, NULL, '2025-12-03 15:54:05', '2025-12-03 15:54:59');
INSERT INTO `doc_file` (`id`, `file_no`, `name`, `original_name`, `path`, `size`, `type`, `creator_id`, `dept_id`, `category_id`, `status`, `visit_count`, `tags`, `created_at`, `updated_at`) VALUES (34, 'F1764748528848', 'cloud-computing-3d', 'cloud-computing-3d.jpg', 'uploads/2025/12/03/fed72e20-5f77-4503-8914-414cf64610bb.jpg', 110778, 'image/jpeg', 31, NULL, 5, 1, 0, NULL, '2025-12-03 15:55:29', '2025-12-03 15:55:29');
COMMIT;

-- ----------------------------
-- Table structure for doc_visit_log
-- ----------------------------
DROP TABLE IF EXISTS `doc_visit_log`;
CREATE TABLE `doc_visit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `visit_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_file` (`file_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of doc_visit_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `parent_id` bigint DEFAULT '0',
  `sort` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` (`id`, `name`, `parent_id`, `sort`, `created_at`, `updated_at`, `leader`, `phone`, `email`, `status`) VALUES (1, '总公司', 0, 1, '2025-12-03 11:31:43', '2025-12-03 15:39:11', '', '', '', '0');
INSERT INTO `sys_dept` (`id`, `name`, `parent_id`, `sort`, `created_at`, `updated_at`, `leader`, `phone`, `email`, `status`) VALUES (2, '技术部', 1, 1, '2025-12-03 11:31:43', '2025-12-03 15:39:01', '', '', '', '0');
INSERT INTO `sys_dept` (`id`, `name`, `parent_id`, `sort`, `created_at`, `updated_at`, `leader`, `phone`, `email`, `status`) VALUES (3, '市场部', 1, 2, '2025-12-03 11:31:43', '2025-12-03 11:31:43', NULL, NULL, NULL, '0');
INSERT INTO `sys_dept` (`id`, `name`, `parent_id`, `sort`, `created_at`, `updated_at`, `leader`, `phone`, `email`, `status`) VALUES (4, '财务部', 1, 3, '2025-12-03 11:31:43', '2025-12-03 11:31:43', NULL, NULL, NULL, '0');
INSERT INTO `sys_dept` (`id`, `name`, `parent_id`, `sort`, `created_at`, `updated_at`, `leader`, `phone`, `email`, `status`) VALUES (5, '开发组', 2, 1, '2025-12-03 14:54:42', '2025-12-03 14:54:42', '孙七', '010-12345682', 'sunqi@example.com', '0');
INSERT INTO `sys_dept` (`id`, `name`, `parent_id`, `sort`, `created_at`, `updated_at`, `leader`, `phone`, `email`, `status`) VALUES (6, '测试组', 2, 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42', '周八', '010-12345683', 'zhouba@example.com', '0');
COMMIT;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL,
  `code` varchar(50) NOT NULL,
  `value` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `sort` int DEFAULT '0',
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission_code` varchar(100) DEFAULT NULL COMMENT '权限标识，控制谁可以访问此字典项',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_permission` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `parent_id` bigint DEFAULT '0',
  `path` varchar(255) DEFAULT NULL,
  `component` varchar(255) DEFAULT NULL,
  `perm_code` varchar(100) DEFAULT NULL,
  `type` int DEFAULT '1' COMMENT '1: Menu, 2: Button',
  `sort` int DEFAULT '0',
  `icon` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_frame` int DEFAULT '0' COMMENT '是否外链（0是 1否）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=204 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (1, '系统管理', 0, 'system', NULL, NULL, 0, 1, 'Setting', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (2, '用户管理', 1, 'users', 'views/UserManage', 'menu:user', 1, 1, 'User', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (3, '部门管理', 1, 'depts', 'views/DeptManage', 'menu:dept', 1, 2, 'OfficeBuilding', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (4, '角色管理', 1, 'roles', 'views/RoleManage', 'menu:role', 1, 3, 'UserFilled', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (5, '菜单管理', 1, 'menus', 'views/MenuManage', 'menu:menu', 1, 4, 'Menu', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (6, '字典管理', 1, 'dicts', 'views/DictManage', 'menu:dict', 1, 5, 'Collection', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (7, '权限管理', 1, 'permissions', 'views/PermissionManage', 'menu:permission', 1, 6, 'Lock', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (8, '岗位管理', 1, 'posts', 'views/PostManage', 'menu:post', 1, 7, 'Briefcase', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (10, '分类管理', 0, 'categories', 'views/Categories', 'menu:category', 1, 2, 'Folder', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (20, '文档信息', 0, 'docs', 'views/DocList', 'menu:document', 1, 3, 'Document', '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (100, '用户新增', 2, NULL, NULL, 'user:add', 2, 1, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (101, '用户修改', 2, NULL, NULL, 'user:edit', 2, 2, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (102, '用户删除', 2, NULL, NULL, 'user:delete', 2, 3, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (103, '用户查询', 2, NULL, NULL, 'user:query', 2, 4, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (110, '部门新增', 3, NULL, NULL, 'dept:add', 2, 1, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (111, '部门修改', 3, NULL, NULL, 'dept:edit', 2, 2, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (112, '部门删除', 3, NULL, NULL, 'dept:delete', 2, 3, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (113, '部门查询', 3, NULL, NULL, 'dept:query', 2, 4, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (120, '角色新增', 4, NULL, NULL, 'role:add', 2, 1, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (121, '角色修改', 4, NULL, NULL, 'role:edit', 2, 2, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (122, '角色删除', 4, NULL, NULL, 'role:delete', 2, 3, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (123, '角色查询', 4, NULL, NULL, 'role:query', 2, 4, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (130, '菜单新增', 5, NULL, NULL, 'menu:add', 2, 1, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (131, '菜单修改', 5, NULL, NULL, 'menu:edit', 2, 2, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (132, '菜单删除', 5, NULL, NULL, 'menu:delete', 2, 3, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (133, '菜单查询', 5, NULL, NULL, 'menu:query', 2, 4, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (200, '文件上传', 20, NULL, NULL, 'file:upload', 2, 1, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (201, '文件下载', 20, NULL, NULL, 'file:download', 2, 2, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (202, '文件预览', 20, NULL, NULL, 'file:preview', 2, 3, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `perm_code`, `type`, `sort`, `icon`, `created_at`, `updated_at`, `is_frame`, `visible`, `status`) VALUES (203, '文件删除', 20, NULL, NULL, 'file:delete', 2, 4, NULL, '2025-12-03 14:51:12', '2025-12-03 14:51:12', 1, '0', '0');
COMMIT;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL COMMENT '权限标识，如 file:upload',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
  `type` int DEFAULT '1' COMMENT '1: 菜单权限, 2: 按钮权限',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_code` (`code`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (1, 'file:upload', '文件上传', '允许上传文件', 2, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (2, 'file:download', '文件下载', '允许下载文件', 2, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (3, 'file:preview', '文件预览', '允许预览文件', 2, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (4, 'file:delete', '文件删除', '允许删除文件', 2, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (5, 'menu:document', '文档管理菜单', '文档管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (6, 'menu:category', '分类管理菜单', '分类管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (7, 'menu:system', '系统管理菜单', '系统管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (8, 'menu:user', '用户管理菜单', '用户管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (9, 'menu:dept', '部门管理菜单', '部门管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (10, 'menu:role', '角色管理菜单', '角色管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (11, 'menu:menu', '菜单管理菜单', '菜单管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (12, 'menu:dict', '字典管理菜单', '字典管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (13, 'menu:permission', '权限管理菜单', '权限管理菜单权限', 1, '2025-12-03 13:56:14', '2025-12-03 13:56:14');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (14, 'menu:post', '岗位管理菜单', '岗位管理菜单权限', 1, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (15, 'user:add', '用户新增', '允许新增用户', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (16, 'user:edit', '用户修改', '允许修改用户', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (17, 'user:delete', '用户删除', '允许删除用户', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (18, 'user:query', '用户查询', '允许查询用户', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (19, 'dept:add', '部门新增', '允许新增部门', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (20, 'dept:edit', '部门修改', '允许修改部门', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (21, 'dept:delete', '部门删除', '允许删除部门', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (22, 'dept:query', '部门查询', '允许查询部门', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (23, 'role:add', '角色新增', '允许新增角色', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (24, 'role:edit', '角色修改', '允许修改角色', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (25, 'role:delete', '角色删除', '允许删除角色', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (26, 'role:query', '角色查询', '允许查询角色', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (27, 'menu:add', '菜单新增', '允许新增菜单', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (28, 'menu:edit', '菜单修改', '允许修改菜单', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (29, 'menu:delete', '菜单删除', '允许删除菜单', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
INSERT INTO `sys_permission` (`id`, `code`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES (30, 'menu:query', '菜单查询', '允许查询菜单', 2, '2025-12-03 14:54:42', '2025-12-03 14:54:42');
COMMIT;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_code` varchar(50) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int DEFAULT '0' COMMENT '岗位顺序',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `post_code` (`post_code`),
  KEY `idx_post_code` (`post_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
BEGIN;
INSERT INTO `sys_post` (`id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `created_at`, `updated_at`) VALUES (1, 'ceo', '董事长', 1, '0', NULL, '2025-12-03 14:33:18', '2025-12-03 14:33:18');
INSERT INTO `sys_post` (`id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `created_at`, `updated_at`) VALUES (2, 'cto', '技术总监', 2, '0', NULL, '2025-12-03 14:33:18', '2025-12-03 14:33:18');
INSERT INTO `sys_post` (`id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `created_at`, `updated_at`) VALUES (3, 'manager', '部门经理', 3, '0', NULL, '2025-12-03 14:33:18', '2025-12-03 14:33:18');
INSERT INTO `sys_post` (`id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `created_at`, `updated_at`) VALUES (4, 'staff', '普通员工', 4, '0', NULL, '2025-12-03 14:33:18', '2025-12-03 14:33:18');
INSERT INTO `sys_post` (`id`, `post_code`, `post_name`, `post_sort`, `status`, `remark`, `created_at`, `updated_at`) VALUES (9, 'leader', '组长', 4, '0', '小组负责人', '2025-12-03 14:54:42', '2025-12-03 14:54:42');
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `code` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `role_sort` int DEFAULT '0' COMMENT '角色顺序',
  `status` char(1) DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据权限范围（1全部数据权限 2自定义数据权限 3本部门数据权限 4本部门及以下数据权限 5仅本人数据权限）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `created_at`, `updated_at`, `role_sort`, `status`, `remark`, `data_scope`) VALUES (1, '超级管理员', 'admin', '系统超级管理员', '2025-12-03 11:31:43', '2025-12-03 14:54:42', 1, '0', '超级管理员拥有所有权限', '1');
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `created_at`, `updated_at`, `role_sort`, `status`, `remark`, `data_scope`) VALUES (2, '普通角色', 'common', '普通用户', '2025-12-03 11:31:43', '2025-12-03 14:54:42', 2, '0', '普通角色，只能访问本部门数据', '3');
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `created_at`, `updated_at`, `role_sort`, `status`, `remark`, `data_scope`) VALUES (41, '普通用户', 'USER', '普通用户', '2025-12-03 15:05:50', '2025-12-03 15:05:50', 0, '0', NULL, '1');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`),
  KEY `idx_role` (`role_id`),
  KEY `idx_dept` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 1);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 2);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 3);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 4);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 5);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 6);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 7);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 8);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 10);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 20);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 100);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 101);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 102);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 103);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 110);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 111);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 112);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 113);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 120);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 121);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 122);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 123);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 130);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 131);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 132);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 133);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 200);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 201);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 202);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 203);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 10);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 20);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 200);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 201);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 202);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (2, 203);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (41, 10);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (41, 20);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (41, 200);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (41, 201);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (41, 202);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (41, 203);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `idx_role` (`role_id`),
  KEY `idx_permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 1);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 2);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 3);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 4);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 5);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 6);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 7);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 8);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 9);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 10);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 11);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 12);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 13);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 14);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 15);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 16);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 17);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 18);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 19);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 20);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 21);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 22);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 23);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 24);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 25);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 26);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 27);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 28);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 29);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 30);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (2, 1);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (2, 2);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (2, 3);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (2, 4);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (2, 5);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (2, 6);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (41, 4);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `dept_id` bigint DEFAULT NULL,
  `status` int DEFAULT '1' COMMENT '1: Normal, 0: Disabled',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `parent_id` bigint DEFAULT NULL COMMENT '上级用户ID',
  `department_path` varchar(500) DEFAULT NULL COMMENT '部门路径，例如 1/3/8',
  `post_id` bigint DEFAULT NULL COMMENT '岗位ID',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `gender` char(1) DEFAULT '2' COMMENT '性别（0男 1女 2未知）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_dept` (`dept_id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_dept_path` (`department_path`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `dept_id`, `status`, `created_at`, `updated_at`, `parent_id`, `department_path`, `post_id`, `phone`, `email`, `gender`, `remark`) VALUES (1, 'admin', '$2a$10$7NBw291ztoIcszQKCLQR9.W/ICj98U8wtU4aH9EIkszrC/R1L70bC', '系统管理员', 1, 1, '2025-12-03 15:16:46', '2025-12-03 15:16:46', NULL, NULL, 1, NULL, NULL, '2', NULL);
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `dept_id`, `status`, `created_at`, `updated_at`, `parent_id`, `department_path`, `post_id`, `phone`, `email`, `gender`, `remark`) VALUES (27, 'zhangsan', '$2a$10$myjgxNGpGFlSGSGw6hO5MOpancwQ/NiOcNtFhuWmYsHBQ7y6sK8T2', '张三', 2, 1, '2025-12-03 15:20:49', '2025-12-03 15:20:49', NULL, NULL, 3, '13800138000', '123456@qq.com', '0', '');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `dept_id`, `status`, `created_at`, `updated_at`, `parent_id`, `department_path`, `post_id`, `phone`, `email`, `gender`, `remark`) VALUES (28, 'lisi', '$2a$10$H8Mf/.ymGeYtuhqZDc4wteZCECOGAnvS9NBivBelxI6qP5I.yWJxC', '李四', 5, 1, '2025-12-03 15:22:33', '2025-12-03 15:38:02', NULL, NULL, 4, '13800138001', '123456@qq.com', '0', '');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `dept_id`, `status`, `created_at`, `updated_at`, `parent_id`, `department_path`, `post_id`, `phone`, `email`, `gender`, `remark`) VALUES (31, 'wangwu', '$2a$10$1ZJF0oocoerN1ibI/bcicOTFE9acZKocBToYP6xI9o2rnlEJOzxpy', '王五', 3, 1, '2025-12-03 15:53:42', '2025-12-03 15:53:42', NULL, NULL, 3, '13800138002', '1234588@qq.com', '0', '123');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (27, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (27, 41);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (28, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (28, 41);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (31, 1);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (31, 41);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
