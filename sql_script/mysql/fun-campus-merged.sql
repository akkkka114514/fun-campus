-- ============================================================
-- Fun Campus 数据库初始化脚本（全量合并版，唯一入口）
-- ============================================================
-- 说明: 本文件由 sql_script/mysql 目录下全部 21 个脚本合并而成，
--       是建库初始化的唯一 SQL 文件；各段内容与来源脚本完全一致。
-- 内容与顺序（即推荐执行顺序）:
--   1. fc_portal.sql                       全量库结构 + 基础数据 (Navicat Dump)
--   2. ActivityFavoriteMenu.sql            activity_favorite 表结构
--   3. update_schema_and_test_data.sql     表结构更新 + 测试数据
--   4. ActivityOrderPayment.sql            Phase 9 活动付费参加（订单/支付/退款）
--   5. 其余 17 个 *Menu.sql                业务菜单与按钮权限 (t_menu)
-- 注意:
--   * 面向全新环境初始化；目标库已有数据时请勿整库执行。
--   * *Menu.sql 段不可重复执行，重复执行会再次插入相同菜单。
--   * MessagePermissionMenu 段自带 NOT EXISTS 保护，可重复执行。
-- ============================================================

-- ----------------------------------------------------------------------------
-- 来源文件: fc_portal.sql —— 全量库结构 + 基础数据（Navicat Dump）
-- ----------------------------------------------------------------------------
/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3308
 Source Schema         : fc_portal

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 23/09/2025 09:06:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动标题',
  `status` tinyint NOT NULL COMMENT '活动状态：0-等待报名 1-报名中 2-报名结束 3-进行中 4-已结束 9-待审核（业务状态，预留）；签到/签退由时间窗口校验，不设状态值',
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动地点',
  `score_can_get` decimal(10, 0) NOT NULL COMMENT '能得到的学分',
  `enroll_num_limit` int NOT NULL COMMENT '报名人数限制',
  `activity_school_id` bigint UNSIGNED NOT NULL COMMENT '活动所属学校',
  `activity_organization_id` bigint UNSIGNED NOT NULL COMMENT '活动所属组织',
  `deleted_flag` bit(1) NOT NULL COMMENT '是否删除',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status_deleted`(`status` ASC, `deleted_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动实体' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES (1, 'test1', 1, 'test', 1, 50, 1, 1, b'0', '2025-05-31 15:14:21', '2025-05-31 15:14:27');

-- ----------------------------
-- Table structure for activity_comment
-- ----------------------------
DROP TABLE IF EXISTS `activity_comment`;
CREATE TABLE `activity_comment`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '评论用户id',
  `activity_id` bigint NOT NULL COMMENT '被评论的活动id',
  `to_comment_id` bigint NULL DEFAULT NULL COMMENT '被回复的评论id，null即为最顶层评论',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` timestamp NOT NULL,
  `root_id` bigint NULL DEFAULT NULL COMMENT '根评论id，为null即为顶层评论',
  `deleted` bit(1) NOT NULL,
  `update_time` timestamp NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_comment
-- ----------------------------

-- ----------------------------
-- Table structure for activity_comment_hot
-- ----------------------------
DROP TABLE IF EXISTS `activity_comment_hot`;
CREATE TABLE `activity_comment_hot`  (
  `comment_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `comment_hot` bigint NOT NULL,
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_comment_hot
-- ----------------------------

-- ----------------------------
-- Table structure for activity_enroll_num
-- ----------------------------
DROP TABLE IF EXISTS `activity_enroll_num`;
CREATE TABLE `activity_enroll_num`  (
  `activity_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `enroll_num` int NOT NULL,
  PRIMARY KEY (`activity_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_enroll_num
-- ----------------------------

-- ----------------------------
-- Table structure for activity_enrollment
-- ----------------------------
DROP TABLE IF EXISTS `activity_enrollment`;
CREATE TABLE `activity_enrollment`  (
  `activity_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `sign_in_status` bit(1) NOT NULL COMMENT '1-》是，0-》否',
  `create_time` timestamp NOT NULL,
  `update_time` timestamp NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`activity_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_enrollment
-- ----------------------------

-- ----------------------------
-- Table structure for activity_schedule
-- ----------------------------
DROP TABLE IF EXISTS `activity_schedule`;
CREATE TABLE `activity_schedule`  (
  `activity_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `enroll_start_time` timestamp NOT NULL COMMENT '报名开始时间',
  `enroll_end_time` timestamp NOT NULL COMMENT '报名结束时间',
  `activity_start_time` timestamp NOT NULL COMMENT '活动开始时间',
  `activity_end_time` timestamp NOT NULL COMMENT '活动结束时间',
  `signin_start_time` timestamp NOT NULL COMMENT '签到开始时间',
  `signin_end_time` timestamp NOT NULL COMMENT '签到结束时间',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '修改时间',
  `deleted_flag` bit(1) NOT NULL COMMENT '是否已删除',
  PRIMARY KEY (`activity_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_schedule
-- ----------------------------

-- ----------------------------
-- Table structure for backend_user
-- ----------------------------
DROP TABLE IF EXISTS `backend_user`;
CREATE TABLE `backend_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted_flag` bit(1) NOT NULL COMMENT '是否已删除',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `disabled_flag` bit(1) NOT NULL COMMENT '是否禁用',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 159 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业关联的员工' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of backend_user
-- ----------------------------
INSERT INTO `backend_user` VALUES (1, '2025-09-12 20:43:32', '2025-09-12 20:43:32', b'0', 1, 'akkkka', '$argon2id$v=19$m=16384,t=2,p=1$Phtz3BRi7aqp9sIPEO9CTg$/Sx/Qug8lYy3ugry5yOLvoHnZ4AIlJsyL6bn065vrFk', b'0', '1109607743@qq.com');

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------

-- ----------------------------
-- Table structure for message_group
-- ----------------------------
DROP TABLE IF EXISTS `message_group`;
CREATE TABLE `message_group`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `deleted` bit(1) NOT NULL,
  `create_time` timestamp NOT NULL,
  `update_time` time NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_group
-- ----------------------------

-- ----------------------------
-- Table structure for message_group_membership
-- ----------------------------
DROP TABLE IF EXISTS `message_group_membership`;
CREATE TABLE `message_group_membership`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_group_membership
-- ----------------------------

-- ----------------------------
-- Table structure for notice_message
-- ----------------------------
DROP TABLE IF EXISTS `notice_message`;
CREATE TABLE `notice_message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `origin` bigint NOT NULL,
  `target` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` timestamp NOT NULL,
  `recall` bit(1) NOT NULL COMMENT '是否撤回',
  `update_time` timestamp NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice_message
-- ----------------------------

-- ----------------------------
-- Table structure for organization_activity
-- ----------------------------
DROP TABLE IF EXISTS `organization_activity`;
CREATE TABLE `organization_activity`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `organization_id` bigint NOT NULL COMMENT '运营者id',
  `deleted_flag` bit(1) NOT NULL COMMENT '是否删除',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of organization_activity
-- ----------------------------

-- ----------------------------
-- Table structure for organization_user_follower_num
-- ----------------------------
DROP TABLE IF EXISTS `organization_user_follower_num`;
CREATE TABLE `organization_user_follower_num`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '前台组织者id',
  `follower_num` bigint UNSIGNED NOT NULL COMMENT '关注者数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of organization_user_follower_num
-- ----------------------------

-- ----------------------------
-- Table structure for portal_organization_user
-- ----------------------------
DROP TABLE IF EXISTS `portal_organization_user`;
CREATE TABLE `portal_organization_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted_flag` bit(1) NOT NULL COMMENT '是否已删除',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` char(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `school_id` bigint NOT NULL COMMENT '学校id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of portal_organization_user
-- ----------------------------

-- ----------------------------
-- Table structure for portal_user
-- ----------------------------
DROP TABLE IF EXISTS `portal_user`;
CREATE TABLE `portal_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted_flag` bit(1) NOT NULL COMMENT '是否已删除',
  `gender` bit(1) NOT NULL COMMENT '性别',
  `phone` char(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `school_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校名',
  `college_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学院名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of portal_user
-- ----------------------------

-- ----------------------------
-- Table structure for portal_user_subscriber
-- ----------------------------
DROP TABLE IF EXISTS `portal_user_subscriber`;
CREATE TABLE `portal_user_subscriber`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `publisher_id` bigint NOT NULL,
  `subscricber_id` bigint NOT NULL,
  `deleted` bit(1) NOT NULL,
  `created_time` timestamp NOT NULL,
  `updated_time` timestamp NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of portal_user_subscriber
-- ----------------------------

-- ----------------------------
-- Table structure for school_info
-- ----------------------------
DROP TABLE IF EXISTS `school_info`;
CREATE TABLE `school_info`  (
  `id` bigint NOT NULL COMMENT '学校ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校名称',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校编码',
  `type` tinyint NULL DEFAULT NULL COMMENT '学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `contact_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '官网',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学校简介',
  `logo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校logo图片URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学校信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of school_info
-- ----------------------------

-- ----------------------------
-- Table structure for t_change_log
-- ----------------------------
DROP TABLE IF EXISTS `t_change_log`;
CREATE TABLE `t_change_log`  (
  `change_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '更新日志id',
  `update_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '版本',
  `type` int NOT NULL COMMENT '更新类型:[1:特大版本功能更新;2:功能更新;3:bug修复]',
  `publish_author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发布人',
  `public_date` date NOT NULL COMMENT '发布日期',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新内容',
  `link` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '跳转链接',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`change_log_id`) USING BTREE,
  UNIQUE INDEX `version_unique`(`update_version` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统更新日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_change_log
-- ----------------------------
INSERT INTO `t_change_log` VALUES (2, 'v1.1.0', 2, '卓大', '2020-05-09', 'SmartAdmin中后台系统 v1.1.0 版本（20200422）正式更新上线，更新内容如下：\n\n1.【新增】增加员工姓名查询\n\n2.【新增】增加文件预览组件\n\n3.【新增】新增四级菜单\n', 'http://smartadmin.1024lab.net/views/1.x/base/About.html', '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (8, 'v1.0.0', 1, '卓大', '2019-11-01', 'SmartAdmin中后台系统 v1.0.0 版本（20191101）正式更新上线，更新内容如下：\n\n1.【新增】人员管理\n\n2.【新增】系统设置\n\n3.【新增】心跳服务\n\n4.【新增】动态加载\n\n5.【新增】缓存策略\n\n6.【新增】定时任务', 'http://smartadmin.1024lab.net/views/1.x/base/About.html', '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (9, 'v1.2.0', 2, '卓大', '2020-05-23', 'SmartAdmin中后台系统 v1.2.0 版本（20200515）正式更新上线，更新内容如下：\n\n1.【新增】增加数据权限\n\n2.【新增】帮助文档', NULL, '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (10, 'v1.2.1', 3, '卓大', '2020-05-24', 'SmartAdmin中后台系统 v1.2.1 版本（20200524）正式更新上线，更新内容如下：\n\n1.【修复】四级菜单权限bug\n\n2.【修复】缓存keepalive的Bug\n\n', NULL, '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (11, 'v1.3.0', 2, '卓大', '2020-06-01', 'SmartAdmin中后台系统 v1.3.0 版本（20200601）正式更新上线，更新内容如下：\n\n1.【新增】工作台看板功能\n\n2.【新增】天气预报功能\n\n3.【新增】记录上次登录IP功能', NULL, '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (12, 'v1.4.0', 2, '卓大', '2020-06-06', 'SmartAdmin中后台系统 v1.4.0 版本（20200606）正式更新上线，更新内容如下：\n\n1.【新增】联系客服功能\n\n2.【新增】意见反馈功能', NULL, '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (13, 'v1.5.0', 2, '卓大', '2020-06-14', 'SmartAdmin中后台系统 v1.5.0 版本（20200614）正式更新上线，更新内容如下：\n\n1.【新增】OA系统\n\n2.【新增】通知公告', NULL, '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (14, 'v1.6.0', 2, '卓大', '2020-06-17', 'SmartAdmin中后台系统 v1.6.0 版本（20200617）正式更新上线，更新内容如下：\n\n1.【新增】代码生成\n\n2.【新增】通知公告', NULL, '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (15, 'v2.0.0', 1, '卓大', '2022-10-22', 'SmartAdmin中后台系统 v2.0.0 版本（20191101）正式更新上线，更新内容如下：\n\n1.【新增】人员管理\n\n2.【新增】系统设置\n\n3.【新增】心跳服务\n\n4.【新增】动态加载\n\n5.【新增】缓存策略\n\n6.【新增】定时任务', 'http://smartadmin.1024lab.net/views/1.x/base/About.html', '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (16, 'v1.7.0', 2, '卓大', '2022-10-22', 'SmartAdmin中后台系统 v1.7.0 版本（20200624）正式更新上线，更新内容如下：\n\n1.【新增】商品管理\n\n2.【新增】商品分类', NULL, '2022-10-04 21:33:50', '2022-10-04 21:33:50');
INSERT INTO `t_change_log` VALUES (18, 'v3.0.0', 1, '卓大', '2024-01-01', 'SmartAdmin中后台系统 v3.0.0 版本（20240101）正式更新上线，更新内容如下：\n\n\n1、【新增】权限从SpringSecurity 转成 Sa-Token\n\n2、【新增】增加接口 加密、解密功能\n\n3、【新增】增加网络安全相关功能：登录限制、密码复杂度、最大在线时长等\n\n4、【新增】ant desgin vue 为 4.x 最新版本\n\n5、【新增】升级 vite5\n\n6、【新增】swagger增加knife4j接口文档\n\n7、【优化】后端sa-common 改名为 sa-base\n\n8、【优化】优化官网文档说明\n', NULL, '2022-10-04 21:33:50', '2022-10-04 21:33:50');

-- ----------------------------
-- Table structure for t_code_generator_config
-- ----------------------------
DROP TABLE IF EXISTS `t_code_generator_config`;
CREATE TABLE `t_code_generator_config`  (
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名',
  `basic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '基础命名信息',
  `fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '字段列表',
  `insert_and_update` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '新建、修改',
  `delete_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '删除',
  `query_fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '查询',
  `table_fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '列表',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详情',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`table_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成器的每个表的配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_code_generator_config
-- ----------------------------
INSERT INTO `t_code_generator_config` VALUES ('activity', '{\"backendAuthor\":\"akkkka114514\",\"backendDate\":1756964502000,\"copyright\":\"akkkka114514\",\"description\":\"活动管理\",\"frontAuthor\":\"akkkka114514\",\"frontDate\":1756964502000,\"javaPackageName\":\"net.lab1024.sa.admin.module.business.funcampus\",\"moduleName\":\"Activity\"}', '[{\"autoIncreaseFlag\":true,\"columnComment\":\"主键\",\"columnName\":\"id\",\"fieldName\":\"id\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"主键\",\"primaryKeyFlag\":true},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动标题\",\"columnName\":\"title\",\"fieldName\":\"title\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"活动标题\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动状态，1-》等待报名，2->报名结束，3-》等待签到，4-》活动结束\",\"columnName\":\"status\",\"dict\":\"ACTIVITY_STATUS\",\"fieldName\":\"status\",\"javaType\":\"Integer\",\"jsType\":\"Number\",\"label\":\"活动状态\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动地点\",\"columnName\":\"position\",\"fieldName\":\"position\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"活动地点\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"能得到的学分\",\"columnName\":\"score_can_get\",\"fieldName\":\"scoreCanGet\",\"javaType\":\"BigDecimal\",\"jsType\":\"Number\",\"label\":\"能得到的学分\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"报名人数限制\",\"columnName\":\"enroll_num_limit\",\"fieldName\":\"enrollNumLimit\",\"javaType\":\"Integer\",\"jsType\":\"Number\",\"label\":\"报名人数限制\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动所属学校\",\"columnName\":\"activity_school_id\",\"fieldName\":\"activitySchoolId\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"活动所属学校\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动所属组织\",\"columnName\":\"activity_organization_id\",\"fieldName\":\"activityOrganizationId\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"活动所属组织\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"是否删除\",\"columnName\":\"deleted_flag\",\"fieldName\":\"deletedFlag\",\"javaType\":\"Boolean\",\"jsType\":\"Boolean\",\"label\":\"是否已删除\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"创建时间\",\"columnName\":\"create_time\",\"fieldName\":\"createTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"创建时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"修改时间\",\"columnName\":\"update_time\",\"fieldName\":\"updateTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"修改时间\",\"primaryKeyFlag\":false}]', '{\"countPerLine\":1,\"fieldList\":[{\"columnName\":\"id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"title\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"status\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"position\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"score_can_get\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"enroll_num_limit\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"activity_school_id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"activity_organization_id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"deleted_flag\",\"frontComponent\":\"BooleanSelect\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"create_time\",\"frontComponent\":\"Date\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"update_time\",\"frontComponent\":\"Date\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false}],\"isSupportInsertAndUpdate\":true,\"pageType\":\"modal\",\"width\":\"30\"}', '{\"deleteEnum\":\"SingleAndBatch\",\"isPhysicallyDeleted\":false,\"isSupportDelete\":true}', '[]', '[{\"columnName\":\"id\",\"ellipsisFlag\":true,\"fieldName\":\"id\",\"label\":\"主键\",\"showFlag\":true},{\"columnName\":\"title\",\"ellipsisFlag\":true,\"fieldName\":\"title\",\"label\":\"活动标题\",\"showFlag\":true},{\"columnName\":\"status\",\"ellipsisFlag\":true,\"fieldName\":\"status\",\"label\":\"活动状态\",\"showFlag\":true},{\"columnName\":\"position\",\"ellipsisFlag\":true,\"fieldName\":\"position\",\"label\":\"活动地点\",\"showFlag\":true},{\"columnName\":\"score_can_get\",\"ellipsisFlag\":true,\"fieldName\":\"scoreCanGet\",\"label\":\"能得到的学分\",\"showFlag\":true},{\"columnName\":\"enroll_num_limit\",\"ellipsisFlag\":true,\"fieldName\":\"enrollNumLimit\",\"label\":\"报名人数限制\",\"showFlag\":true},{\"columnName\":\"activity_school_id\",\"ellipsisFlag\":true,\"fieldName\":\"activitySchoolId\",\"label\":\"活动所属学校\",\"showFlag\":true},{\"columnName\":\"activity_organization_id\",\"ellipsisFlag\":true,\"fieldName\":\"activityOrganizationId\",\"label\":\"活动所属组织\",\"showFlag\":true},{\"columnName\":\"deleted_flag\",\"ellipsisFlag\":true,\"fieldName\":\"deletedFlag\",\"label\":\"是否已删除\",\"showFlag\":true},{\"columnName\":\"create_time\",\"ellipsisFlag\":true,\"fieldName\":\"createTime\",\"label\":\"创建时间\",\"showFlag\":true},{\"columnName\":\"update_time\",\"ellipsisFlag\":true,\"fieldName\":\"updateTime\",\"label\":\"修改时间\",\"showFlag\":true}]', NULL, '2025-09-04 13:47:41', '2025-09-04 13:47:41');
INSERT INTO `t_code_generator_config` VALUES ('activity_schedule', '{\"backendAuthor\":\"akkkka114514\",\"backendDate\":1757145247000,\"copyright\":\"akkkka114514\",\"description\":\"活动时间表\",\"frontAuthor\":\"akkkka114514\",\"frontDate\":1757145247000,\"javaPackageName\":\"net.lab1024.sa.admin.module.business.funcampus\",\"moduleName\":\"ActivitySchedule\"}', '[{\"autoIncreaseFlag\":true,\"columnComment\":\"主键\",\"columnName\":\"activity_id\",\"fieldName\":\"activityId\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"主键\",\"primaryKeyFlag\":true},{\"autoIncreaseFlag\":false,\"columnComment\":\"报名开始时间\",\"columnName\":\"enroll_start_time\",\"fieldName\":\"enrollStartTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"报名开始时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"报名结束时间\",\"columnName\":\"enroll_end_time\",\"fieldName\":\"enrollEndTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"报名结束时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动开始时间\",\"columnName\":\"activity_start_time\",\"fieldName\":\"activityStartTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"活动开始时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动结束时间\",\"columnName\":\"activity_end_time\",\"fieldName\":\"activityEndTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"活动结束时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"签到开始时间\",\"columnName\":\"signin_start_time\",\"fieldName\":\"signinStartTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"签到开始时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"签到结束时间\",\"columnName\":\"signin_end_time\",\"fieldName\":\"signinEndTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"签到结束时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"创建时间\",\"columnName\":\"create_time\",\"fieldName\":\"createTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"创建时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"修改时间\",\"columnName\":\"update_time\",\"fieldName\":\"updateTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"修改时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"是否已删除\",\"columnName\":\"deleted_flag\",\"fieldName\":\"deletedFlag\",\"javaType\":\"Boolean\",\"jsType\":\"Boolean\",\"label\":\"是否已删除\",\"primaryKeyFlag\":false}]', '{\"countPerLine\":1,\"fieldList\":[{\"columnName\":\"activity_id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"enroll_start_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"enroll_end_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"activity_start_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"activity_end_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"signin_start_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"signin_end_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"create_time\",\"frontComponent\":\"Date\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"update_time\",\"frontComponent\":\"Date\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"deleted_flag\",\"frontComponent\":\"BooleanSelect\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false}],\"isSupportInsertAndUpdate\":true,\"pageType\":\"modal\",\"width\":\"30\"}', '{\"deleteEnum\":\"SingleAndBatch\",\"isPhysicallyDeleted\":false,\"isSupportDelete\":true}', '[]', '[{\"columnName\":\"activity_id\",\"ellipsisFlag\":true,\"fieldName\":\"activityId\",\"label\":\"主键\",\"showFlag\":true},{\"columnName\":\"enroll_start_time\",\"ellipsisFlag\":true,\"fieldName\":\"enrollStartTime\",\"label\":\"报名开始时间\",\"showFlag\":true},{\"columnName\":\"enroll_end_time\",\"ellipsisFlag\":true,\"fieldName\":\"enrollEndTime\",\"label\":\"报名结束时间\",\"showFlag\":true},{\"columnName\":\"activity_start_time\",\"ellipsisFlag\":true,\"fieldName\":\"activityStartTime\",\"label\":\"活动开始时间\",\"showFlag\":true},{\"columnName\":\"activity_end_time\",\"ellipsisFlag\":true,\"fieldName\":\"activityEndTime\",\"label\":\"活动结束时间\",\"showFlag\":true},{\"columnName\":\"signin_start_time\",\"ellipsisFlag\":true,\"fieldName\":\"signinStartTime\",\"label\":\"签到开始时间\",\"showFlag\":true},{\"columnName\":\"signin_end_time\",\"ellipsisFlag\":true,\"fieldName\":\"signinEndTime\",\"label\":\"签到结束时间\",\"showFlag\":true},{\"columnName\":\"create_time\",\"ellipsisFlag\":true,\"fieldName\":\"createTime\",\"label\":\"创建时间\",\"showFlag\":true},{\"columnName\":\"update_time\",\"ellipsisFlag\":true,\"fieldName\":\"updateTime\",\"label\":\"修改时间\",\"showFlag\":true},{\"columnName\":\"deleted_flag\",\"ellipsisFlag\":true,\"fieldName\":\"deletedFlag\",\"label\":\"是否已删除\",\"showFlag\":true}]', NULL, '2025-09-06 15:55:41', '2025-09-06 15:55:41');
INSERT INTO `t_code_generator_config` VALUES ('organization_activity', '{\"backendAuthor\":\"akkkka114514\",\"backendDate\":1758262136000,\"copyright\":\"akkkka114514\",\"description\":\"运营者发布活动的对应关系\",\"frontAuthor\":\"akkkka114514\",\"frontDate\":1758262136000,\"javaPackageName\":\"net.lab1024.sa.admin.module.business.funcampus.organizationActivity\",\"moduleName\":\"OrganizationActivity\"}', '[{\"autoIncreaseFlag\":true,\"columnComment\":\"主键\",\"columnName\":\"id\",\"fieldName\":\"id\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"主键\",\"primaryKeyFlag\":true},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动id\",\"columnName\":\"activity_id\",\"fieldName\":\"activityId\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"活动id\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"运营者id\",\"columnName\":\"organization_id\",\"fieldName\":\"organizationId\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"运营者id\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"是否删除\",\"columnName\":\"deleted_flag\",\"fieldName\":\"deletedFlag\",\"javaType\":\"Boolean\",\"jsType\":\"Boolean\",\"label\":\"是否删除\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"更新时间\",\"columnName\":\"update_time\",\"fieldName\":\"updateTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"更新时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"创建时间\",\"columnName\":\"create_time\",\"fieldName\":\"createTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"创建时间\",\"primaryKeyFlag\":false}]', '{\"countPerLine\":1,\"fieldList\":[{\"columnName\":\"id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"activity_id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"organization_id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"deleted_flag\",\"frontComponent\":\"BooleanSelect\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"update_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"create_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false}],\"isSupportInsertAndUpdate\":true,\"pageType\":\"modal\",\"width\":\"30\"}', '{\"deleteEnum\":\"SingleAndBatch\",\"isPhysicallyDeleted\":false,\"isSupportDelete\":true}', '[]', '[{\"columnName\":\"id\",\"ellipsisFlag\":true,\"fieldName\":\"id\",\"label\":\"主键\",\"showFlag\":true},{\"columnName\":\"activity_id\",\"ellipsisFlag\":true,\"fieldName\":\"activityId\",\"label\":\"活动id\",\"showFlag\":true},{\"columnName\":\"organization_id\",\"ellipsisFlag\":true,\"fieldName\":\"organizationId\",\"label\":\"运营者id\",\"showFlag\":true},{\"columnName\":\"deleted_flag\",\"ellipsisFlag\":true,\"fieldName\":\"deletedFlag\",\"label\":\"是否删除\",\"showFlag\":true},{\"columnName\":\"update_time\",\"ellipsisFlag\":true,\"fieldName\":\"updateTime\",\"label\":\"更新时间\",\"showFlag\":true},{\"columnName\":\"create_time\",\"ellipsisFlag\":true,\"fieldName\":\"createTime\",\"label\":\"创建时间\",\"showFlag\":true}]', NULL, '2025-09-19 14:10:11', '2025-09-19 14:10:11');
INSERT INTO `t_code_generator_config` VALUES ('portal_organization_user', '{\"backendAuthor\":\"akkkka114514\",\"backendDate\":1758247959000,\"copyright\":\"akkkka114514\",\"description\":\"组织账号运营者\",\"frontAuthor\":\"akkkka114514\",\"frontDate\":1758247959000,\"javaPackageName\":\"net.lab1024.sa.admin.module.business.funcampus\",\"moduleName\":\"PortalOrganizationUser\"}', '[{\"autoIncreaseFlag\":true,\"columnComment\":\"主键\",\"columnName\":\"id\",\"fieldName\":\"id\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"主键\",\"primaryKeyFlag\":true},{\"autoIncreaseFlag\":false,\"columnComment\":\"用户名\",\"columnName\":\"username\",\"fieldName\":\"username\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"用户名\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"密码\",\"columnName\":\"password\",\"fieldName\":\"password\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"密码\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"创建时间\",\"columnName\":\"create_time\",\"fieldName\":\"createTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"创建时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"修改时间\",\"columnName\":\"update_time\",\"fieldName\":\"updateTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"修改时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"是否已删除\",\"columnName\":\"deleted_flag\",\"fieldName\":\"deletedFlag\",\"javaType\":\"Boolean\",\"jsType\":\"Boolean\",\"label\":\"是否已删除\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"头像\",\"columnName\":\"avatar\",\"fieldName\":\"avatar\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"头像\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"手机号\",\"columnName\":\"phone\",\"fieldName\":\"phone\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"手机号\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"学校名\",\"columnName\":\"school_name\",\"fieldName\":\"schoolName\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"学校名\",\"primaryKeyFlag\":false}]', '{\"countPerLine\":1,\"fieldList\":[{\"columnName\":\"id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":true,\"updateFlag\":true},{\"columnName\":\"username\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":true},{\"columnName\":\"password\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":true},{\"columnName\":\"create_time\",\"frontComponent\":\"DateTime\",\"insertFlag\":false,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"update_time\",\"frontComponent\":\"DateTime\",\"insertFlag\":false,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"deleted_flag\",\"frontComponent\":\"BooleanSelect\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"avatar\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":false,\"updateFlag\":true},{\"columnName\":\"phone\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":false,\"updateFlag\":true},{\"columnName\":\"school_name\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":true}],\"isSupportInsertAndUpdate\":true,\"pageType\":\"modal\",\"width\":\"30\"}', '{\"deleteEnum\":\"SingleAndBatch\",\"isPhysicallyDeleted\":false,\"isSupportDelete\":true}', '[]', '[{\"columnName\":\"id\",\"ellipsisFlag\":true,\"fieldName\":\"id\",\"label\":\"主键\",\"showFlag\":true},{\"columnName\":\"username\",\"ellipsisFlag\":true,\"fieldName\":\"username\",\"label\":\"用户名\",\"showFlag\":true},{\"columnName\":\"password\",\"ellipsisFlag\":true,\"fieldName\":\"password\",\"label\":\"密码\",\"showFlag\":true},{\"columnName\":\"create_time\",\"ellipsisFlag\":true,\"fieldName\":\"createTime\",\"label\":\"创建时间\",\"showFlag\":true},{\"columnName\":\"update_time\",\"ellipsisFlag\":true,\"fieldName\":\"updateTime\",\"label\":\"修改时间\",\"showFlag\":true},{\"columnName\":\"deleted_flag\",\"ellipsisFlag\":true,\"fieldName\":\"deletedFlag\",\"label\":\"是否已删除\",\"showFlag\":true},{\"columnName\":\"avatar\",\"ellipsisFlag\":true,\"fieldName\":\"avatar\",\"label\":\"头像\",\"showFlag\":true},{\"columnName\":\"phone\",\"ellipsisFlag\":true,\"fieldName\":\"phone\",\"label\":\"手机号\",\"showFlag\":true},{\"columnName\":\"school_name\",\"ellipsisFlag\":true,\"fieldName\":\"schoolName\",\"label\":\"学校名\",\"showFlag\":true}]', NULL, '2025-09-19 10:16:29', '2025-09-19 10:16:29');
INSERT INTO `t_code_generator_config` VALUES ('school_info', '{\"backendAuthor\":\"akkkka114514\",\"backendDate\":1758587677000,\"copyright\":\"akkkka114514\",\"description\":\"学校信息表\",\"frontAuthor\":\"akkkka114514\",\"frontDate\":1758587677000,\"javaPackageName\":\"net.lab1024.sa.admin.module.business.funcampus.schoolInfo\",\"moduleName\":\"SchoolInfo\"}', '[{\"autoIncreaseFlag\":false,\"columnComment\":\"学校ID\",\"columnName\":\"id\",\"fieldName\":\"id\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"学校ID\",\"primaryKeyFlag\":true},{\"autoIncreaseFlag\":false,\"columnComment\":\"学校名称\",\"columnName\":\"name\",\"fieldName\":\"name\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"学校名称\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"学校编码\",\"columnName\":\"code\",\"fieldName\":\"code\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"学校编码\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)\",\"columnName\":\"type\",\"fieldName\":\"type\",\"javaType\":\"Integer\",\"jsType\":\"Number\",\"label\":\"学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"详细地址\",\"columnName\":\"address\",\"fieldName\":\"address\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"详细地址\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"联系人\",\"columnName\":\"contact_person\",\"fieldName\":\"contactPerson\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"联系人\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"联系电话\",\"columnName\":\"contact_phone\",\"fieldName\":\"contactPhone\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"联系电话\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"邮箱\",\"columnName\":\"email\",\"fieldName\":\"email\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"邮箱\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"官网\",\"columnName\":\"website\",\"fieldName\":\"website\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"官网\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"学校简介\",\"columnName\":\"description\",\"fieldName\":\"description\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"学校简介\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"学校logo图片URL\",\"columnName\":\"logo_url\",\"fieldName\":\"logoUrl\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"学校logo图片URL\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"状态(0:禁用,1:启用)\",\"columnName\":\"status\",\"fieldName\":\"status\",\"javaType\":\"Integer\",\"jsType\":\"Number\",\"label\":\"状态(0:禁用,1:启用)\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"排序\",\"columnName\":\"sort\",\"fieldName\":\"sort\",\"javaType\":\"Integer\",\"jsType\":\"Number\",\"label\":\"排序\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"创建时间\",\"columnName\":\"create_time\",\"fieldName\":\"createTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"创建时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"更新时间\",\"columnName\":\"update_time\",\"fieldName\":\"updateTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"更新时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"删除标识(0:未删除,1:已删除)\",\"columnName\":\"deleted_flag\",\"fieldName\":\"deletedFlag\",\"javaType\":\"Integer\",\"jsType\":\"Number\",\"label\":\"删除标识(0:未删除,1:已删除)\",\"primaryKeyFlag\":false}]', '{\"countPerLine\":1,\"fieldList\":[{\"columnName\":\"id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":true,\"updateFlag\":true},{\"columnName\":\"name\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"code\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"type\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"address\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"contact_person\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"contact_phone\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"email\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"website\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"description\",\"frontComponent\":\"Textarea\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"logo_url\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"status\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"sort\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"create_time\",\"frontComponent\":\"DateTime\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"update_time\",\"frontComponent\":\"DateTime\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"deleted_flag\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false}],\"isSupportInsertAndUpdate\":true,\"pageType\":\"modal\",\"width\":\"30\"}', '{\"deleteEnum\":\"SingleAndBatch\",\"isPhysicallyDeleted\":false,\"isSupportDelete\":true}', '[{\"columnNameList\":[\"name\"],\"fieldName\":\"keywords\",\"label\":\"关键字\",\"queryTypeEnum\":\"Like\",\"width\":\"200px\"},{\"columnNameList\":[\"type\"],\"fieldName\":\"type\",\"label\":\"学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)\",\"queryTypeEnum\":\"Equal\",\"width\":\"200px\"},{\"columnNameList\":[\"code\"],\"fieldName\":\"code\",\"label\":\"学校编码\",\"queryTypeEnum\":\"Equal\",\"width\":\"200px\"}]', '[{\"columnName\":\"id\",\"ellipsisFlag\":true,\"fieldName\":\"id\",\"label\":\"学校ID\",\"showFlag\":true},{\"columnName\":\"name\",\"ellipsisFlag\":true,\"fieldName\":\"name\",\"label\":\"学校名称\",\"showFlag\":true},{\"columnName\":\"code\",\"ellipsisFlag\":true,\"fieldName\":\"code\",\"label\":\"学校编码\",\"showFlag\":true},{\"columnName\":\"type\",\"ellipsisFlag\":true,\"fieldName\":\"type\",\"label\":\"学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)\",\"showFlag\":true},{\"columnName\":\"address\",\"ellipsisFlag\":true,\"fieldName\":\"address\",\"label\":\"详细地址\",\"showFlag\":true},{\"columnName\":\"contact_person\",\"ellipsisFlag\":true,\"fieldName\":\"contactPerson\",\"label\":\"联系人\",\"showFlag\":true},{\"columnName\":\"contact_phone\",\"ellipsisFlag\":true,\"fieldName\":\"contactPhone\",\"label\":\"联系电话\",\"showFlag\":true},{\"columnName\":\"email\",\"ellipsisFlag\":true,\"fieldName\":\"email\",\"label\":\"邮箱\",\"showFlag\":true},{\"columnName\":\"website\",\"ellipsisFlag\":true,\"fieldName\":\"website\",\"label\":\"官网\",\"showFlag\":true},{\"columnName\":\"description\",\"ellipsisFlag\":true,\"fieldName\":\"description\",\"label\":\"学校简介\",\"showFlag\":true},{\"columnName\":\"logo_url\",\"ellipsisFlag\":true,\"fieldName\":\"logoUrl\",\"label\":\"学校logo图片URL\",\"showFlag\":true},{\"columnName\":\"status\",\"ellipsisFlag\":true,\"fieldName\":\"status\",\"label\":\"状态(0:禁用,1:启用)\",\"showFlag\":true},{\"columnName\":\"sort\",\"ellipsisFlag\":true,\"fieldName\":\"sort\",\"label\":\"排序\",\"showFlag\":true},{\"columnName\":\"create_time\",\"ellipsisFlag\":true,\"fieldName\":\"createTime\",\"label\":\"创建时间\",\"showFlag\":true},{\"columnName\":\"update_time\",\"ellipsisFlag\":true,\"fieldName\":\"updateTime\",\"label\":\"更新时间\",\"showFlag\":true},{\"columnName\":\"deleted_flag\",\"ellipsisFlag\":true,\"fieldName\":\"deletedFlag\",\"label\":\"删除标识(0:未删除,1:已删除)\",\"showFlag\":true}]', NULL, '2025-09-23 08:39:16', '2025-09-23 08:39:16');

-- ----------------------------
-- Table structure for t_config
-- ----------------------------
DROP TABLE IF EXISTS `t_config`;
CREATE TABLE `t_config`  (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数名字',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数key',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次修改时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_config
-- ----------------------------
INSERT INTO `t_config` VALUES (1, '万能密码', 'super_password', '1024ok', '执行示例任务2', '2025-09-04 12:44:28', '2021-12-16 23:32:46');
INSERT INTO `t_config` VALUES (2, '三级等保', 'level3_protect_config', '{\n	\"fileDetectFlag\":true,\n	\"loginActiveTimeoutMinutes\":30,\n	\"loginFailLockMinutes\":30,\n	\"loginFailMaxTimes\":3,\n	\"maxUploadFileSizeMb\":30,\n	\"passwordComplexityEnabled\":true,\n	\"regularChangePasswordMonths\":3,\n	\"regularChangePasswordNotAllowRepeatTimes\":3,\n	\"twoFactorLoginEnabled\":false\n}', 'SmartJob Sample2 update', '2024-09-03 21:49:23', '2024-08-13 11:44:49');

-- ----------------------------
-- Table structure for t_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_dict`;
CREATE TABLE `t_dict`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典id',
  `dict_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典名字',
  `dict_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典编码',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典备注',
  `disabled_flag` tinyint NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `unique_code`(`dict_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_dict
-- ----------------------------
INSERT INTO `t_dict` VALUES (1, '商品地区', 'GOODS_PLACE', '用于商品管理中的商品地区1', 0, '2025-03-27 14:42:26', '2025-03-31 11:23:03');
INSERT INTO `t_dict` VALUES (4, '活动状态', 'ACTIVITY_STATUS', '', 0, '2025-09-06 15:04:33', '2025-09-06 15:04:33');

-- ----------------------------
-- Table structure for t_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `t_dict_data`;
CREATE TABLE `t_dict_data`  (
  `dict_data_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典数据id',
  `dict_id` bigint NOT NULL COMMENT '字典id',
  `data_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典项值',
  `data_label` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典项显示名称',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `sort_order` int NOT NULL COMMENT '排序（越大越靠前）',
  `disabled_flag` tinyint NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`dict_data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_dict_data
-- ----------------------------
INSERT INTO `t_dict_data` VALUES (2, 1, 'LUO_YANG', '洛阳', 'sad', 2, 0, '2025-03-27 15:52:39', '2025-03-27 20:53:21');
INSERT INTO `t_dict_data` VALUES (3, 1, 'ZHENG_ZHOU', '郑州', '', 0, 0, '2025-03-27 18:58:16', '2025-03-27 20:53:32');
INSERT INTO `t_dict_data` VALUES (7, 1, 'BEI_JING', '北京', '', 0, 0, '2025-03-27 20:53:45', '2025-03-27 20:53:45');
INSERT INTO `t_dict_data` VALUES (8, 1, 'SHANG_HAI', '上海', '', 0, 0, '2025-03-27 20:53:45', '2025-03-27 20:53:45');
INSERT INTO `t_dict_data` VALUES (9, 4, '0', '等待报名', '', 5, 0, '2025-09-21 10:00:00', '2025-09-21 10:00:00');
INSERT INTO `t_dict_data` VALUES (10, 4, '1', '报名中', '', 4, 0, '2025-09-21 10:00:00', '2025-09-21 10:00:00');
INSERT INTO `t_dict_data` VALUES (11, 4, '2', '报名结束', '', 3, 0, '2025-09-21 10:00:00', '2025-09-21 10:00:00');
INSERT INTO `t_dict_data` VALUES (12, 4, '3', '进行中', '', 2, 0, '2025-09-21 10:00:00', '2025-09-21 10:00:00');
INSERT INTO `t_dict_data` VALUES (13, 4, '4', '已结束', '', 1, 0, '2025-09-21 10:00:00', '2025-09-21 10:00:00');
INSERT INTO `t_dict_data` VALUES (14, 4, '9', '待审核', '业务状态（预留）：报名需审核时等待审核', 0, 0, '2025-09-21 10:00:00', '2025-09-21 10:00:00');

-- ----------------------------
-- Table structure for t_feedback
-- ----------------------------
DROP TABLE IF EXISTS `t_feedback`;
CREATE TABLE `t_feedback`  (
  `feedback_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `feedback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '反馈内容',
  `feedback_attachment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '反馈图片',
  `user_id` bigint NOT NULL COMMENT '创建人id',
  `user_type` int NOT NULL COMMENT '创建人用户类型',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`feedback_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '意见反馈' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_feedback
-- ----------------------------

-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file`  (
  `file_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `folder_type` tinyint UNSIGNED NOT NULL COMMENT '文件夹类型',
  `file_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `file_size` int NULL DEFAULT NULL COMMENT '文件大小',
  `file_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件key，用于文件下载',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人，即上传人',
  `creator_user_type` int NULL DEFAULT NULL COMMENT '创建人用户类型',
  `creator_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`file_id`) USING BTREE,
  UNIQUE INDEX `uk_file_key`(`file_key` ASC) USING BTREE,
  INDEX `module_id_module_type`(`folder_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 108 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_heart_beat_record
-- ----------------------------
DROP TABLE IF EXISTS `t_heart_beat_record`;
CREATE TABLE `t_heart_beat_record`  (
  `heart_beat_record_id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `project_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
  `server_ip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务器ip',
  `process_no` int NOT NULL COMMENT '进程号',
  `process_start_time` datetime NOT NULL COMMENT '进程开启时间',
  `heart_beat_time` datetime NOT NULL COMMENT '心跳时间',
  PRIMARY KEY (`heart_beat_record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 214 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公用服务 - 服务心跳' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_heart_beat_record
-- ----------------------------
INSERT INTO `t_heart_beat_record` VALUES (188, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 16672, '2025-09-04 12:44:06', '2025-09-04 15:12:07');
INSERT INTO `t_heart_beat_record` VALUES (189, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 22028, '2025-09-05 14:30:56', '2025-09-05 16:02:17');
INSERT INTO `t_heart_beat_record` VALUES (190, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 8712, '2025-09-05 16:03:29', '2025-09-05 20:20:01');
INSERT INTO `t_heart_beat_record` VALUES (191, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 5424, '2025-09-06 14:55:56', '2025-09-06 18:22:04');
INSERT INTO `t_heart_beat_record` VALUES (192, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 20220, '2025-09-07 12:13:47', '2025-09-07 15:35:01');
INSERT INTO `t_heart_beat_record` VALUES (193, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 11372, '2025-09-07 15:35:15', '2025-09-07 20:33:18');
INSERT INTO `t_heart_beat_record` VALUES (194, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 13452, '2025-09-08 15:00:20', '2025-09-08 18:01:34');
INSERT INTO `t_heart_beat_record` VALUES (195, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 14056, '2025-09-12 20:23:26', '2025-09-12 20:39:33');
INSERT INTO `t_heart_beat_record` VALUES (196, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 18360, '2025-09-12 20:44:20', '2025-09-12 21:30:26');
INSERT INTO `t_heart_beat_record` VALUES (197, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 19444, '2025-09-12 21:33:08', '2025-09-12 21:39:14');
INSERT INTO `t_heart_beat_record` VALUES (198, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 6336, '2025-09-12 21:41:52', '2025-09-12 21:52:58');
INSERT INTO `t_heart_beat_record` VALUES (199, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.43.130', 18940, '2025-09-14 13:41:23', '2025-09-14 13:52:31');
INSERT INTO `t_heart_beat_record` VALUES (200, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.43.130', 6552, '2025-09-14 13:55:50', '2025-09-14 15:22:45');
INSERT INTO `t_heart_beat_record` VALUES (201, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1', 19568, '2025-09-14 15:27:04', '2025-09-14 16:23:10');
INSERT INTO `t_heart_beat_record` VALUES (202, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 6248, '2025-09-15 16:12:08', '2025-09-15 16:13:14');
INSERT INTO `t_heart_beat_record` VALUES (203, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 4236, '2025-09-15 16:30:20', '2025-09-15 16:31:26');
INSERT INTO `t_heart_beat_record` VALUES (204, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 8396, '2025-09-15 16:33:01', '2025-09-15 16:54:07');
INSERT INTO `t_heart_beat_record` VALUES (205, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 14632, '2025-09-15 17:00:07', '2025-09-15 17:01:13');
INSERT INTO `t_heart_beat_record` VALUES (206, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 18944, '2025-09-15 17:03:14', '2025-09-15 17:04:20');
INSERT INTO `t_heart_beat_record` VALUES (207, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 10352, '2025-09-15 17:05:05', '2025-09-15 18:49:52');
INSERT INTO `t_heart_beat_record` VALUES (208, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 21532, '2025-09-15 18:52:44', '2025-09-15 19:38:51');
INSERT INTO `t_heart_beat_record` VALUES (209, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 14684, '2025-09-16 13:24:03', '2025-09-16 13:45:10');
INSERT INTO `t_heart_beat_record` VALUES (210, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 16368, '2025-09-16 13:46:29', '2025-09-16 20:53:30');
INSERT INTO `t_heart_beat_record` VALUES (211, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 3512, '2025-09-19 10:09:46', '2025-09-19 10:25:52');
INSERT INTO `t_heart_beat_record` VALUES (212, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 19352, '2025-09-19 14:03:00', '2025-09-19 17:28:15');
INSERT INTO `t_heart_beat_record` VALUES (213, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;10.101.116.254', 15400, '2025-09-23 08:28:49', '2025-09-23 09:05:01');

-- ----------------------------
-- Table structure for t_help_doc
-- ----------------------------
DROP TABLE IF EXISTS `t_help_doc`;
CREATE TABLE `t_help_doc`  (
  `help_doc_id` bigint NOT NULL AUTO_INCREMENT,
  `help_doc_catalog_id` bigint NOT NULL COMMENT '类型1公告 2动态',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文本内容',
  `content_html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'html内容',
  `attachment` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `page_view_count` int NOT NULL DEFAULT 0 COMMENT '页面浏览量，传说中的pv',
  `user_view_count` int NOT NULL DEFAULT 0 COMMENT '用户浏览量，传说中的uv',
  `author` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`help_doc_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助文档' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_help_doc
-- ----------------------------
INSERT INTO `t_help_doc` VALUES (32, 6, '企业名称该写什么？', '需求1：管理公司基本信息，包含：企业名称、Logo、地区、营业执照、联系人 等等，可以 增删拆改需求2：管理公司的银行账户，包含：银行信息、账户名称、账号、类型等，可以 增删拆改需求3：管理公司的发票信息，包含：开票抬头、纳税号、银行账户、开户行、备注等，可以 增删拆改需求4：对于公司信息、银行信息、发票信息 任何的修改，都有记录 数据变动记录；', '<ul><li style=\"text-align: start;\">需求1：管理公司基本信息，包含：企业名称、Logo、地区、营业执照、联系人 等等，可以 增删拆改</li><li style=\"text-align: start;\">需求2：管理公司的银行账户，包含：银行信息、账户名称、账号、类型等，可以 增删拆改</li><li style=\"text-align: start;\">需求3：管理公司的发票信息，包含：开票抬头、纳税号、银行账户、开户行、备注等，可以 增删拆改</li><li style=\"text-align: start;\">需求4：对于公司信息、银行信息、发票信息 任何的修改，都有记录 数据变动记录；</li></ul>', '', 0, 55, 1, '卓大', '2024-07-07 23:15:28', '2022-11-22 10:41:48');
INSERT INTO `t_help_doc` VALUES (33, 6, '谁有权限查看企业信息', '需求1：管理公司基本信息，包含：企业名称、Logo、地区、营业执照、联系人 等等，可以 增删拆改需求2：管理公司的银行账户，包含：银行信息、账户名称、账号、类型等，可以 增删拆改需求3：管理公司的发票信息，包含：开票抬头、纳税号、银行账户、开户行、备注等，可以 增删拆改需求4：对于公司信息、银行信息、发票信息 任何的修改，都有记录 数据变动记录；', '<ul><li style=\"text-align: start;\">需求1：管理公司基本信息，包含：企业名称、Logo、地区、营业执照、联系人 等等，可以 增删拆改</li><li style=\"text-align: start;\">需求2：管理公司的银行账户，包含：银行信息、账户名称、账号、类型等，可以 增删拆改</li><li style=\"text-align: start;\">需求3：管理公司的发票信息，包含：开票抬头、纳税号、银行账户、开户行、备注等，可以 增删拆改</li><li style=\"text-align: start;\">需求4：对于公司信息、银行信息、发票信息 任何的修改，都有记录 数据变动记录；</li></ul>', '', 0, 13, 1, '卓大', '2024-04-10 19:36:55', '2022-11-22 10:42:19');

-- ----------------------------
-- Table structure for t_help_doc_catalog
-- ----------------------------
DROP TABLE IF EXISTS `t_help_doc_catalog`;
CREATE TABLE `t_help_doc_catalog`  (
  `help_doc_catalog_id` bigint NOT NULL AUTO_INCREMENT COMMENT '帮助文档目录',
  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序字段',
  `parent_id` bigint NOT NULL COMMENT '父级id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`help_doc_catalog_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助文档-目录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_help_doc_catalog
-- ----------------------------
INSERT INTO `t_help_doc_catalog` VALUES (6, '企业信息', 0, 0, '2022-11-05 10:52:40', '2022-11-22 10:37:38');
INSERT INTO `t_help_doc_catalog` VALUES (9, '企业信用', 0, 6, '2023-12-01 20:16:54', '2023-12-01 20:16:54');
INSERT INTO `t_help_doc_catalog` VALUES (10, '采购文档', 0, 11, '2023-12-01 20:17:08', '2023-12-01 20:17:29');
INSERT INTO `t_help_doc_catalog` VALUES (11, '进销存', 0, 0, '2023-12-01 20:17:23', '2023-12-01 20:17:23');

-- ----------------------------
-- Table structure for t_help_doc_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_help_doc_relation`;
CREATE TABLE `t_help_doc_relation`  (
  `relation_id` bigint NOT NULL COMMENT '关联id',
  `relation_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联名称',
  `help_doc_id` bigint NOT NULL COMMENT '文档id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`relation_id`, `help_doc_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助文档-关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_help_doc_relation
-- ----------------------------
INSERT INTO `t_help_doc_relation` VALUES (0, '首页', 32, '2023-12-04 13:34:17', '2023-12-04 13:34:17');
INSERT INTO `t_help_doc_relation` VALUES (0, '首页', 33, '2023-12-04 13:34:21', '2023-12-04 13:34:21');

-- ----------------------------
-- Table structure for t_help_doc_view_record
-- ----------------------------
DROP TABLE IF EXISTS `t_help_doc_view_record`;
CREATE TABLE `t_help_doc_view_record`  (
  `help_doc_id` bigint NOT NULL COMMENT '通知公告id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `page_view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `first_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次ip',
  `first_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次用户设备等标识',
  `last_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次ip',
  `last_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次用户设备等标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`help_doc_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助文档-查看记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_help_doc_view_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_login_fail
-- ----------------------------
DROP TABLE IF EXISTS `t_login_fail`;
CREATE TABLE `t_login_fail`  (
  `login_fail_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_type` int NOT NULL COMMENT '用户类型',
  `login_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录名',
  `login_fail_count` int NULL DEFAULT NULL COMMENT '连续登录失败次数',
  `lock_flag` tinyint NULL DEFAULT 0 COMMENT '锁定状态:1锁定，0未锁定',
  `login_lock_begin_time` datetime NULL DEFAULT NULL COMMENT '连续登录失败锁定开始时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`login_fail_id`) USING BTREE,
  UNIQUE INDEX `uid_and_utype`(`user_id` ASC, `user_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 87 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录失败次数记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_login_fail
-- ----------------------------

-- ----------------------------
-- Table structure for t_login_log
-- ----------------------------
DROP TABLE IF EXISTS `t_login_log`;
CREATE TABLE `t_login_log`  (
  `login_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int NOT NULL COMMENT '用户id',
  `user_type` int NOT NULL COMMENT '用户类型',
  `user_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `login_ip` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ip',
  `login_ip_region` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ip地区',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'user-agent信息',
  `login_device` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录设备',
  `login_result` int NOT NULL COMMENT '登录结果：0成功 1失败 2 退出',
  `remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`login_log_id`) USING BTREE,
  INDEX `customer_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1919 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户登录日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_login_log
-- ----------------------------
INSERT INTO `t_login_log` VALUES (1905, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-04 12:57:15', '2025-09-04 12:57:16');
INSERT INTO `t_login_log` VALUES (1906, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-04 13:30:52', '2025-09-04 13:30:52');
INSERT INTO `t_login_log` VALUES (1907, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-04 15:08:45', '2025-09-04 15:08:46');
INSERT INTO `t_login_log` VALUES (1908, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-05 14:35:08', '2025-09-05 14:35:08');
INSERT INTO `t_login_log` VALUES (1909, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-05 18:39:03', '2025-09-05 18:39:04');
INSERT INTO `t_login_log` VALUES (1910, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-06 14:58:08', '2025-09-06 14:58:09');
INSERT INTO `t_login_log` VALUES (1911, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-07 12:18:54', '2025-09-07 12:18:55');
INSERT INTO `t_login_log` VALUES (1912, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-07 15:34:53', '2025-09-07 15:34:53');
INSERT INTO `t_login_log` VALUES (1913, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 0, '', '2025-09-08 15:12:54', '2025-09-08 15:12:54');
INSERT INTO `t_login_log` VALUES (1914, 1, 1, 'akkkka', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 1, '密码错误', '2025-09-12 20:45:18', '2025-09-12 20:45:19');
INSERT INTO `t_login_log` VALUES (1915, 1, 1, 'akkkka', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', '电脑端', 1, '密码错误', '2025-09-12 20:53:59', '2025-09-12 20:54:00');

-- ----------------------------
-- Table structure for t_mail_template
-- ----------------------------
DROP TABLE IF EXISTS `t_mail_template`;
CREATE TABLE `t_mail_template`  (
  `template_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `template_subject` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `template_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板内容',
  `template_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '解析类型 string，freemarker',
  `disable_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否禁用',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`template_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_mail_template
-- ----------------------------
INSERT INTO `t_mail_template` VALUES ('login_verification_code', '登录验证码', '<!DOCTYPE HTML>\r\n<html>\r\n<head>\r\n  <title>登录提醒</title>\r\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\r\n  <style>\r\n      * {\r\n          font-family: SimSun;\r\n          /* 4号字体 */\r\n          font-size: 18px;\r\n          /* 22磅行间距 */\r\n          line-height: 29px;\r\n      }\r\n\r\n      .main_font_size {\r\n          font-size: 12.0pt;\r\n      }\r\n\r\n      .mainContent {\r\n          line-height: 28px;\r\n      }\r\n\r\n      p {\r\n          margin: 0 auto;\r\n          text-align: justify;\r\n      }\r\n  </style>\r\n\r\n</head>\r\n<body>\r\n<div>\r\n  <div style=\"margin: 0px auto;width: 690px;\">\r\n    <div class=\"mainContent\">\r\n      <h1>验证码</h1>\r\n      <p>请在验证页面输入此验证码</p>\r\n      <p><b>${code}</b></p>\r\n      <p>验证码将于此电子邮件发出 5 分钟后过期。</p>\r\n      <p>如果你未曾提出此请求，可以忽略这封电子邮件。</p>\r\n    </div>\r\n\r\n  </div>\r\n</div>\r\n</body>\r\n</html>', 'freemarker', 0, '2024-08-06 09:13:08', '2024-07-28 13:56:06');

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `menu_type` int NOT NULL COMMENT '类型',
  `parent_id` bigint NOT NULL COMMENT '父菜单ID',
  `sort` int NULL DEFAULT NULL COMMENT '显示顺序',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `perms_type` int NULL DEFAULT NULL COMMENT '权限类型',
  `api_perms` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后端权限字符串',
  `web_perms` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端权限字符串',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `context_menu_id` bigint NULL DEFAULT NULL COMMENT '功能点关联菜单ID',
  `frame_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为外链',
  `frame_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '外链地址',
  `cache_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否缓存',
  `visible_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '显示状态',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 319 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_menu
-- ----------------------------
INSERT INTO `t_menu` VALUES (26, '菜单管理', 2, 50, 1, '/menu/list', '/system/menu/menu-list.vue', NULL, NULL, NULL, 'CopyOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, '2021-08-09 15:04:35', 1, '2023-12-01 19:39:03');
INSERT INTO `t_menu` VALUES (40, '删除', 3, 26, NULL, NULL, NULL, 1, 'system:menu:batchDelete', 'system:menu:batchDelete', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 09:45:56', 1, '2023-10-07 18:15:50');
INSERT INTO `t_menu` VALUES (45, '组织架构', 1, 0, 3, '/organization', NULL, NULL, NULL, NULL, 'UserSwitchOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 16:13:27', 1, '2024-07-02 19:27:44');
INSERT INTO `t_menu` VALUES (46, '后台用户管理', 2, 45, 3, '/organization/backend-user', '/system/backend-user/index.vue', NULL, NULL, NULL, 'AuditOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 16:21:50', 1, '2025-09-16 15:47:34');
INSERT INTO `t_menu` VALUES (50, '系统设置', 1, 0, 6, '/setting', NULL, NULL, NULL, NULL, 'SettingOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-13 16:41:33', 1, '2023-12-01 19:38:03');
INSERT INTO `t_menu` VALUES (76, '角色管理', 2, 45, 4, '/organization/role', '/system/role/index.vue', NULL, NULL, NULL, 'SlidersOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-26 10:31:00', 1, '2024-07-02 20:15:28');
INSERT INTO `t_menu` VALUES (81, '用户操作记录', 2, 213, 6, '/support/operate-log/operate-log-list', '/support/operate-log/operate-log-list.vue', NULL, NULL, NULL, 'VideoCameraOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-20 12:37:24', 44, '2024-08-13 14:34:10');
INSERT INTO `t_menu` VALUES (91, '添加后台用户', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:add', 'system:backendUser:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:11:38', 1, '2025-09-16 15:47:23');
INSERT INTO `t_menu` VALUES (92, '编辑后台用户', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:update', 'system:backendUser:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:10', 1, '2025-09-16 15:47:17');
INSERT INTO `t_menu` VALUES (93, '禁用启用后台用户', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:disabled', 'system:backendUser:disabled', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:37', 1, '2025-09-16 15:47:15');
INSERT INTO `t_menu` VALUES (95, '重置密码', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:password:reset', 'system:backendUser:password:reset', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:13:30', 1, '2023-10-07 18:27:57');
INSERT INTO `t_menu` VALUES (96, '删除后台用户', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:delete', 'system:backendUser:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:14:08', 1, '2025-09-16 16:20:00');
INSERT INTO `t_menu` VALUES (97, '添加角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:add', 'system:role:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:00', 1, '2023-10-07 18:42:31');
INSERT INTO `t_menu` VALUES (98, '删除角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:delete', 'system:role:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:19', 1, '2023-10-07 18:42:35');
INSERT INTO `t_menu` VALUES (99, '编辑角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:update', 'system:role:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:55', 1, '2023-10-07 18:42:44');
INSERT INTO `t_menu` VALUES (100, '更新数据范围', 3, 76, NULL, NULL, NULL, 1, 'system:role:dataScope:update', 'system:role:dataScope:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:37:03', 1, '2023-10-07 18:41:49');
INSERT INTO `t_menu` VALUES (101, '批量移除后台用户', 3, 76, NULL, NULL, NULL, 1, 'system:role:backendUser:batch:delete', 'system:role:backendUser:batch:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:05', 1, '2025-09-16 15:47:04');
INSERT INTO `t_menu` VALUES (102, '移除后台用户', 3, 76, NULL, NULL, NULL, 1, 'system:role:backendUser:delete', 'system:role:backendUser:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:21', 1, '2025-09-16 15:47:09');
INSERT INTO `t_menu` VALUES (103, '添加后台用户', 3, 76, NULL, NULL, NULL, 1, 'system:role:backendUser:add', 'system:role:backendUser:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:38', 1, '2025-09-16 15:47:00');
INSERT INTO `t_menu` VALUES (104, '修改权限', 3, 76, NULL, NULL, NULL, 1, 'system:role:menu:update', 'system:role:menu:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:41:55', 1, '2023-10-07 18:44:11');
INSERT INTO `t_menu` VALUES (105, '添加', 3, 26, NULL, NULL, NULL, 1, 'system:menu:add', 'system:menu:add', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:44:37', 1, '2023-10-07 17:35:35');
INSERT INTO `t_menu` VALUES (106, '编辑', 3, 26, NULL, NULL, NULL, 1, 'system:menu:update', 'system:menu:update', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:44:59', 1, '2023-10-07 17:35:48');
INSERT INTO `t_menu` VALUES (109, '参数配置', 2, 50, 3, '/config/config-list', '/support/config/config-list.vue', NULL, NULL, NULL, 'AntDesignOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 13:34:41', 1, '2022-06-23 16:24:16');
INSERT INTO `t_menu` VALUES (110, '数据字典', 2, 50, 4, '/setting/dict', '/support/dict/index.vue', NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 17:53:00', 1, '2022-05-27 18:09:14');
INSERT INTO `t_menu` VALUES (111, '监控服务', 1, 0, 100, '/monitor', NULL, NULL, NULL, NULL, 'BarChartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-17 11:13:23', 1, '2023-11-28 17:43:56');
INSERT INTO `t_menu` VALUES (114, '运维工具', 1, 0, 200, NULL, NULL, NULL, NULL, NULL, 'NodeCollapseOutlined', NULL, 0, NULL, 0, 1, 0, 1, 1, '2022-06-20 10:09:16', 1, '2023-12-01 19:36:18');
INSERT INTO `t_menu` VALUES (117, 'Reload', 2, 50, 12, '/hook', '/support/reload/reload-list.vue', NULL, NULL, NULL, 'ReloadOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-20 10:16:49', 1, '2023-12-01 19:39:17');
INSERT INTO `t_menu` VALUES (122, '数据库监控', 2, 111, 4, '/support/druid/index', NULL, NULL, NULL, NULL, 'ConsoleSqlOutlined', NULL, 1, 'http://localhost:1024/druid', 1, 1, 0, 0, 1, '2022-06-20 14:49:33', 1, '2023-02-16 19:15:58');
INSERT INTO `t_menu` VALUES (133, '缓存管理', 2, 50, 11, '/support/cache/cache-list', '/support/cache/cache-list.vue', NULL, NULL, NULL, 'BorderInnerOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 18:52:25', 1, '2023-12-01 19:39:13');
INSERT INTO `t_menu` VALUES (143, '登录登出记录', 2, 213, 5, '/support/login-log/login-log-list', '/support/login-log/login-log-list.vue', NULL, NULL, NULL, 'LoginOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-28 15:01:38', 44, '2024-08-13 14:33:49');
INSERT INTO `t_menu` VALUES (147, '帮助文档', 2, 218, 1, '/help-doc/help-doc-manage-list', '/support/help-doc/management/help-doc-manage-list.vue', NULL, NULL, NULL, 'FolderViewOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 19:59:01', 1, '2023-12-01 19:38:23');
INSERT INTO `t_menu` VALUES (148, '意见反馈', 2, 218, 2, '/feedback/feedback-list', '/support/feedback/feedback-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 19:59:52', 1, '2023-12-01 19:38:40');
INSERT INTO `t_menu` VALUES (151, '代码生成', 2, 0, 600, '/support/code-generator', '/support/code-generator/code-generator-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-21 18:25:05', 1, '2022-10-22 11:27:58');
INSERT INTO `t_menu` VALUES (152, '更新日志', 2, 218, 3, '/support/change-log/change-log-list', '/support/change-log/change-log-list.vue', NULL, NULL, NULL, 'HeartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 44, '2022-10-10 10:31:20', 1, '2023-12-01 19:38:51');
INSERT INTO `t_menu` VALUES (153, '清除缓存', 3, 133, NULL, NULL, NULL, 1, 'support:cache:delete', 'support:cache:delete', NULL, 133, 0, NULL, 0, 1, 1, 0, 1, '2022-10-15 22:45:13', 1, '2023-10-07 16:22:29');
INSERT INTO `t_menu` VALUES (154, '获取缓存key', 3, 133, NULL, NULL, NULL, 1, 'support:cache:keys', 'support:cache:keys', NULL, 133, 0, NULL, 0, 1, 1, 0, 1, '2022-10-15 22:45:48', 1, '2023-10-07 16:22:35');
INSERT INTO `t_menu` VALUES (156, '查看结果', 3, 117, NULL, NULL, NULL, 1, 'support:reload:result', 'support:reload:result', NULL, 117, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:17:23', 1, '2023-10-07 14:31:47');
INSERT INTO `t_menu` VALUES (159, '查询', 3, 110, NULL, NULL, NULL, 1, 'support:dict:query', 'support:dict:query', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:23:51', 1, '2025-04-08 19:42:25');
INSERT INTO `t_menu` VALUES (160, '添加', 3, 110, NULL, NULL, NULL, 1, 'support:dict:add', 'support:dict:add', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:05', 1, '2025-04-08 19:43:02');
INSERT INTO `t_menu` VALUES (161, '更新', 3, 110, NULL, NULL, NULL, 1, 'support:dict:update', 'support:dict:update', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:34', 1, '2025-04-08 19:43:34');
INSERT INTO `t_menu` VALUES (162, '删除', 3, 110, NULL, NULL, NULL, 1, 'support:dict:delete', 'support:dict:delete', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:55', 1, '2025-04-08 19:43:52');
INSERT INTO `t_menu` VALUES (163, '新建', 3, 109, NULL, NULL, NULL, 1, 'support:config:add', 'support:config:add', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:26:56', 1, '2023-10-07 18:16:17');
INSERT INTO `t_menu` VALUES (164, '编辑', 3, 109, NULL, NULL, NULL, 1, 'support:config:update', 'support:config:update', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:27:07', 1, '2023-10-07 18:16:24');
INSERT INTO `t_menu` VALUES (168, '查询', 3, 147, 11, NULL, NULL, 1, 'support:helpDoc:query', 'support:helpDoc:query', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:13', 1, '2023-10-07 14:05:49');
INSERT INTO `t_menu` VALUES (169, '新建', 3, 147, 12, NULL, NULL, 1, 'support:helpDoc:add', 'support:helpDoc:add', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:37', 1, '2023-10-07 14:05:56');
INSERT INTO `t_menu` VALUES (170, '新建目录', 3, 147, 1, NULL, NULL, 1, 'support:helpDocCatalog:addCategory', 'support:helpDocCatalog:addCategory', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:57', 1, '2023-10-07 14:06:38');
INSERT INTO `t_menu` VALUES (171, '修改目录', 3, 147, 2, NULL, NULL, 1, 'support:helpDocCatalog:update', 'support:helpDocCatalog:update', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:13:46', 1, '2023-10-07 14:06:49');
INSERT INTO `t_menu` VALUES (190, '查询', 3, 152, NULL, NULL, NULL, 1, '', 'support:changeLog:query', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:28:33', 1, '2023-10-07 14:25:05');
INSERT INTO `t_menu` VALUES (191, '新建', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:add', 'support:changeLog:add', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:28:46', 1, '2023-10-07 14:24:15');
INSERT INTO `t_menu` VALUES (192, '批量删除', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:batchDelete', 'support:changeLog:batchDelete', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:29:10', 1, '2023-10-07 14:24:22');
INSERT INTO `t_menu` VALUES (193, '文件管理', 2, 50, 20, '/support/file/file-list', '/support/file/file-list.vue', NULL, NULL, NULL, 'FolderOpenOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 11:26:11', 1, '2022-10-22 11:29:22');
INSERT INTO `t_menu` VALUES (198, '删除', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:delete', 'support:changeLog:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:42:34', 1, '2023-10-07 14:24:32');
INSERT INTO `t_menu` VALUES (199, '查询', 3, 109, NULL, NULL, NULL, 1, 'support:config:query', 'support:config:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:45:14', 1, '2023-10-07 18:16:27');
INSERT INTO `t_menu` VALUES (200, '查询', 3, 193, NULL, NULL, NULL, 1, 'support:file:query', 'support:file:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:47:23', 1, '2023-10-07 18:24:43');
INSERT INTO `t_menu` VALUES (201, '删除', 3, 147, 14, NULL, NULL, 1, 'support:helpDoc:delete', 'support:helpDoc:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:03:20', 1, '2023-10-07 14:07:02');
INSERT INTO `t_menu` VALUES (202, '更新', 3, 147, 13, NULL, NULL, 1, 'support:helpDoc:update', 'support:helpDoc:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:03:32', 1, '2023-10-07 14:06:56');
INSERT INTO `t_menu` VALUES (203, '查询', 3, 143, NULL, NULL, NULL, 1, 'support:loginLog:query', 'support:loginLog:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 21:05:11', 1, '2023-10-07 14:27:23');
INSERT INTO `t_menu` VALUES (204, '查询', 3, 81, NULL, NULL, NULL, 1, 'support:operateLog:query', 'support:operateLog:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:33:31', 1, '2023-10-07 14:27:56');
INSERT INTO `t_menu` VALUES (205, '详情', 3, 81, NULL, NULL, NULL, 1, 'support:operateLog:detail', 'support:operateLog:detail', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:33:49', 1, '2023-10-07 14:28:04');
INSERT INTO `t_menu` VALUES (206, '心跳监控', 2, 111, 1, '/support/heart-beat/heart-beat-list', '/support/heart-beat/heart-beat-list.vue', 1, NULL, NULL, 'FallOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 10:47:03', 1, '2022-10-22 18:32:52');
INSERT INTO `t_menu` VALUES (207, '更新', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:update', 'support:changeLog:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-22 11:51:32', 1, '2023-10-07 14:24:39');
INSERT INTO `t_menu` VALUES (212, '查询', 3, 117, NULL, NULL, NULL, 1, 'support:reload:query', 'support:reload:query', NULL, NULL, 0, NULL, 1, 1, 1, 0, 1, '2023-10-07 14:31:36', NULL, '2023-10-07 14:31:36');
INSERT INTO `t_menu` VALUES (213, '网络安全', 1, 0, 5, NULL, NULL, 1, NULL, NULL, 'SafetyCertificateOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-17 19:03:08', 1, '2023-12-01 19:38:00');
INSERT INTO `t_menu` VALUES (214, '登录失败锁定', 2, 213, 4, '/support/login-fail', '/support/login-fail/login-fail-list.vue', 1, NULL, NULL, 'LockOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-17 19:04:24', 44, '2024-08-13 14:16:26');
INSERT INTO `t_menu` VALUES (215, '接口加解密', 2, 213, 2, '/support/api-encrypt', '/support/api-encrypt/api-encrypt-index.vue', 1, NULL, NULL, 'CodepenCircleOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-10-24 11:49:28', 44, '2024-08-13 12:00:14');
INSERT INTO `t_menu` VALUES (218, '文档中心', 1, 0, 4, NULL, NULL, 1, NULL, NULL, 'FileSearchOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:37:28', 1, '2023-12-01 19:37:51');
INSERT INTO `t_menu` VALUES (221, '定时任务', 2, 50, 25, '/job/list', '/support/job/job-list.vue', 1, NULL, NULL, 'AppstoreOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, '2024-06-25 17:57:40', 2, '2024-06-25 19:49:21');
INSERT INTO `t_menu` VALUES (229, '查询任务', 3, 221, NULL, NULL, NULL, 1, 'support:job:query', 'support:job:query', NULL, 221, 0, NULL, 1, 1, 0, 0, 2, '2024-06-29 11:14:15', 2, '2024-06-29 11:15:00');
INSERT INTO `t_menu` VALUES (230, '更新任务', 3, 221, NULL, NULL, NULL, 1, 'support:job:update', 'support:job:update', NULL, 221, 0, NULL, 1, 1, 0, 0, 2, '2024-06-29 11:15:40', NULL, '2024-06-29 11:15:40');
INSERT INTO `t_menu` VALUES (231, '执行任务', 3, 221, NULL, NULL, NULL, 1, 'support:job:execute', 'support:job:execute', NULL, 221, 0, NULL, 1, 1, 0, 0, 2, '2024-06-29 11:16:03', NULL, '2024-06-29 11:16:03');
INSERT INTO `t_menu` VALUES (232, '查询记录', 3, 221, NULL, NULL, NULL, 1, 'support:job:log:query', 'support:job:log:query', NULL, 221, 0, NULL, 1, 1, 0, 0, 2, '2024-06-29 11:16:37', NULL, '2024-06-29 11:16:37');
INSERT INTO `t_menu` VALUES (233, 'knife4j文档', 2, 218, 4, '/knife4j', NULL, 1, NULL, NULL, 'FileWordOutlined', NULL, 1, 'http://localhost:1024/doc.html', 1, 1, 0, 0, 1, '2024-07-02 20:23:50', 1, '2024-07-08 13:49:15');
INSERT INTO `t_menu` VALUES (234, 'swagger文档', 2, 218, 5, '/swagger', 'http://localhost:1024/swagger-ui/index.html', 1, NULL, NULL, 'ApiOutlined', NULL, 1, 'http://localhost:1024/swagger-ui/index.html', 1, 1, 0, 0, 1, '2024-07-02 20:35:43', 1, '2024-07-08 13:49:26');
INSERT INTO `t_menu` VALUES (250, '三级等保设置', 2, 213, 1, '/support/level3protect/level3-protect-config-index', '/support/level3protect/level3-protect-config-index.vue', 1, NULL, NULL, 'SafetyOutlined', NULL, 0, NULL, 1, 1, 0, 0, 44, '2024-08-13 11:41:02', 44, '2024-08-13 11:58:12');
INSERT INTO `t_menu` VALUES (251, '敏感数据脱敏', 2, 213, 3, '/support/level3protect/data-masking-list', '/support/level3protect/data-masking-list.vue', 1, NULL, NULL, 'FileProtectOutlined', NULL, 0, NULL, 1, 1, 0, 0, 44, '2024-08-13 11:58:00', 44, '2024-08-13 11:59:49');
INSERT INTO `t_menu` VALUES (252, '启用/禁用', 3, 110, NULL, NULL, NULL, 1, 'support:dict:updateDisabled', 'support:dict:updateDisabled', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2025-04-08 19:44:12', 1, '2025-04-08 19:46:03');
INSERT INTO `t_menu` VALUES (253, '查询字典数据', 3, 110, NULL, NULL, NULL, 1, 'support:dictData:query', 'support:dictData:query', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2025-04-08 19:46:47', NULL, '2025-04-08 19:46:47');
INSERT INTO `t_menu` VALUES (254, '添加字典数据', 3, 110, NULL, NULL, NULL, 1, 'support:dictData:add', 'support:dictData:add', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2025-04-08 19:48:00', NULL, '2025-04-08 19:48:00');
INSERT INTO `t_menu` VALUES (255, '更新字典数据', 3, 110, NULL, NULL, NULL, 1, 'support:dictData:update', 'support:dictData:update', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2025-04-08 19:48:19', NULL, '2025-04-08 19:48:19');
INSERT INTO `t_menu` VALUES (256, '删除字典数据', 3, 110, NULL, NULL, NULL, 1, 'support:dictData:delete', 'support:dictData:delete', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2025-04-08 19:48:38', NULL, '2025-04-08 19:48:38');
INSERT INTO `t_menu` VALUES (257, '启用/禁用字典数据', 3, 110, NULL, NULL, NULL, 1, 'support:dictData:updateDisabled', 'support:dictData:updateDisabled', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2025-04-08 19:48:57', NULL, '2025-04-08 19:48:57');
INSERT INTO `t_menu` VALUES (300, '消息管理', 2, 50, 30, '/message', '/support/message/message-list.vue', 1, NULL, NULL, 'MailOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-04-09 14:30:04', 1, '2025-04-10 20:19:36');
INSERT INTO `t_menu` VALUES (301, '校园活动', 1, 0, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-09-05 14:36:19', NULL, '2025-09-05 14:36:19');
INSERT INTO `t_menu` VALUES (303, '查询', 3, 302, NULL, NULL, NULL, 1, 'activity:query', 'activity:query', NULL, 302, 0, NULL, 0, 1, 0, 1, 1, '2025-09-05 14:42:42', 1, '2025-09-05 14:49:27');
INSERT INTO `t_menu` VALUES (304, '添加', 3, 302, NULL, NULL, NULL, 1, 'activity:add', 'activity:add', NULL, 302, 0, NULL, 0, 1, 0, 1, 1, '2025-09-05 14:42:42', 1, '2025-09-05 14:49:27');
INSERT INTO `t_menu` VALUES (305, '更新', 3, 302, NULL, NULL, NULL, 1, 'activity:update', 'activity:update', NULL, 302, 0, NULL, 0, 1, 0, 1, 1, '2025-09-05 14:42:42', 1, '2025-09-05 14:49:27');
INSERT INTO `t_menu` VALUES (306, '删除', 3, 302, NULL, NULL, NULL, 1, 'activity:delete', 'activity:delete', NULL, 302, 0, NULL, 0, 1, 0, 1, 1, '2025-09-05 14:42:42', 1, '2025-09-05 14:49:27');
INSERT INTO `t_menu` VALUES (307, '活动管理', 2, 301, NULL, '/activity/list', '/business/funcampus/activity/activity-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-09-05 14:44:22', 1, '2025-09-16 16:38:11');
INSERT INTO `t_menu` VALUES (308, '查询', 3, 307, NULL, NULL, NULL, 1, NULL, NULL, NULL, 307, 0, NULL, 0, 1, 0, 0, 1, '2025-09-05 15:28:33', NULL, '2025-09-05 15:28:33');
INSERT INTO `t_menu` VALUES (309, '组织账号运营者', 2, 301, NULL, '/portal-organization-user/list', '/business/funcampus/portalOrganizerUser/portal-organization-user-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-09-19 10:28:13', NULL, '2025-09-19 10:28:13');
INSERT INTO `t_menu` VALUES (310, '查询', 3, 309, NULL, NULL, NULL, 1, 'portalOrganizationUser:query', 'portalOrganizationUser:query', NULL, 309, 0, NULL, 0, 1, 0, 0, 1, '2025-09-19 10:28:13', NULL, '2025-09-19 10:28:13');
INSERT INTO `t_menu` VALUES (311, '添加', 3, 309, NULL, NULL, NULL, 1, 'portalOrganizationUser:add', 'portalOrganizationUser:add', NULL, 309, 0, NULL, 0, 1, 0, 0, 1, '2025-09-19 10:28:13', NULL, '2025-09-19 10:28:13');
INSERT INTO `t_menu` VALUES (312, '更新', 3, 309, NULL, NULL, NULL, 1, 'portalOrganizationUser:update', 'portalOrganizationUser:update', NULL, 309, 0, NULL, 0, 1, 0, 0, 1, '2025-09-19 10:28:13', NULL, '2025-09-19 10:28:13');
INSERT INTO `t_menu` VALUES (313, '删除', 3, 309, NULL, NULL, NULL, 1, 'portalOrganizationUser:delete', 'portalOrganizationUser:delete', NULL, 309, 0, NULL, 0, 1, 0, 0, 1, '2025-09-19 10:28:13', NULL, '2025-09-19 10:28:13');
INSERT INTO `t_menu` VALUES (314, '学校信息表', 2, 301, NULL, '/school-info/list', '/business/funcampus/schoolInfo/school-info-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-09-23 09:05:33', NULL, '2025-09-23 09:05:33');
INSERT INTO `t_menu` VALUES (315, '查询', 3, 314, NULL, NULL, NULL, 1, 'schoolInfo:query', 'schoolInfo:query', NULL, 314, 0, NULL, 0, 1, 0, 0, 1, '2025-09-23 09:05:33', NULL, '2025-09-23 09:05:33');
INSERT INTO `t_menu` VALUES (316, '添加', 3, 314, NULL, NULL, NULL, 1, 'schoolInfo:add', 'schoolInfo:add', NULL, 314, 0, NULL, 0, 1, 0, 0, 1, '2025-09-23 09:05:33', NULL, '2025-09-23 09:05:33');
INSERT INTO `t_menu` VALUES (317, '更新', 3, 314, NULL, NULL, NULL, 1, 'schoolInfo:update', 'schoolInfo:update', NULL, 314, 0, NULL, 0, 1, 0, 0, 1, '2025-09-23 09:05:33', NULL, '2025-09-23 09:05:33');
INSERT INTO `t_menu` VALUES (318, '删除', 3, 314, NULL, NULL, NULL, 1, 'schoolInfo:delete', 'schoolInfo:delete', NULL, 314, 0, NULL, 0, 1, 0, 0, 1, '2025-09-23 09:05:33', NULL, '2025-09-23 09:05:33');

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `message_type` smallint NOT NULL COMMENT '消息类型',
  `receiver_user_type` int NOT NULL COMMENT '接收者用户类型',
  `receiver_user_id` bigint NOT NULL COMMENT '接收者用户id',
  `data_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '相关数据id',
  `title` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `read_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_msg`(`message_type` ASC, `receiver_user_type` ASC, `receiver_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_message
-- ----------------------------
INSERT INTO `t_message` VALUES (1, 1, 1, 1, 'null', '张三的对公付款单 【3000元】', '尊敬的各位技术大佬：\r\n\r\n1024创新实验室技术分享即将隆重举行\r\n\r\n现将有关会议事宜通知如下：\r\n\r\n一、会议内容\r\n\r\n1、研究探讨SmartAdmin的技术体系\r\n\r\n二、会议形式\r\n\r\n大会专题小会分组讨论;\r\n\r\n三、会议时间及地点\r\n\r\n会议报到时间：xxx1年6月14日\r\n\r\n会议报到地点：洛阳市', 0, '2024-09-02 23:00:54', '2024-06-27 01:14:07', '2024-09-03 20:44:19');
INSERT INTO `t_message` VALUES (2, 2, 1, 1, '234', '刘备的请假单【本周四】', '尊敬的各位技术大佬：\r\n\r\n1024创新实验室技术分享即将隆重举行\r\n\r\n现将有关会议事宜通知如下：\r\n\r\n一、会议内容\r\n\r\n1、研究探讨SmartAdmin的技术体系\r\n\r\n二、会议形式\r\n\r\n大会专题小会分组讨论;\r\n\r\n三、会议时间及地点\r\n\r\n会议报到时间：xxx1年6月14日\r\n\r\n会议报到地点：洛阳市', 0, '2024-09-02 23:00:50', '2024-07-04 16:09:49', '2024-09-03 20:44:20');
INSERT INTO `t_message` VALUES (3, 1, 1, 1, '23', '武松的物资采购单【Macbook Pro】', '尊敬的各位技术大佬：\r\n\r\n1024创新实验室技术分享即将隆重举行\r\n\r\n现将有关会议事宜通知如下：\r\n\r\n一、会议内容\r\n\r\n1、研究探讨SmartAdmin的技术体系\r\n\r\n二、会议形式\r\n\r\n大会专题小会分组讨论;\r\n\r\n三、会议时间及地点\r\n\r\n会议报到时间：xxx1年6月14日\r\n\r\n会议报到地点：洛阳市', 0, '2024-09-02 23:00:36', '2024-07-07 22:03:14', '2024-09-03 20:44:21');
INSERT INTO `t_message` VALUES (4, 1, 1, 1, '23', '孙悟空的出差申请【出差洛阳】', '尊敬的各位技术大佬：\r\n\r\n1024创新实验室技术分享即将隆重举行\r\n\r\n现将有关会议事宜通知如下：\r\n\r\n一、会议内容\r\n\r\n1、研究探讨SmartAdmin的技术体系\r\n\r\n二、会议形式\r\n\r\n大会专题小会分组讨论;\r\n\r\n三、会议时间及地点\r\n\r\n会议报到时间：xxx1年6月14日\r\n\r\n会议报到地点：洛阳市', 0, '2024-09-02 23:02:53', '2024-07-07 22:03:14', '2024-09-03 21:43:53');

-- ----------------------------
-- Table structure for t_notice
-- ----------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice`  (
  `notice_id` bigint NOT NULL AUTO_INCREMENT,
  `notice_type_id` bigint NOT NULL COMMENT '类型1公告 2动态',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `all_visible_flag` tinyint(1) NOT NULL COMMENT '是否全部可见',
  `scheduled_publish_flag` tinyint(1) NOT NULL COMMENT '是否定时发布',
  `publish_time` datetime NOT NULL COMMENT '发布时间',
  `content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文本内容',
  `content_html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'html内容',
  `attachment` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件',
  `page_view_count` int NOT NULL DEFAULT 0 COMMENT '页面浏览量，传说中的pv',
  `user_view_count` int NOT NULL DEFAULT 0 COMMENT '用户浏览量，传说中的uv',
  `source` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源',
  `author` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者',
  `document_number` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文号，如：1024创新实验室发〔2022〕字第36号',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0,
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_notice
-- ----------------------------
INSERT INTO `t_notice` VALUES (49, 1, 'Spring Boot 3.0.0 首个 RC 发布', 1, 0, '2024-01-01 20:22:23', 'Spring Boot 3.0.0 首个 RC 发布\nSpring Boot 3.0 首个 RC 已发布，此外还为两个分支发布了更新：2.7.5 & 2.6.13。\n3.0.0-RC1\n发布公告写道，此版本包含 135 项功能增强、文档改进、依赖升级和 Bugfix。\nSpring Boot 3.0 的开发工作始于实验性的 Spring Native，旨在为 GraalVM 原生镜像提供支持。在该版本中，开发者现在可以使用标准 Spring Boot Maven 或 Gradle 插件将 Spring Boot 应用程序转换为原生可执行文件，而无需任何特殊配置。\n此版本还在参考文档中添加新内容来解释 AOT 处理背后的概念以及如何开始生成第一个 GraalVM 原生镜像。\n除此之外，Spring Boot 3.0 还完成了迁移到 JakartaEE 9 的工作，以及将使用的 Java 版本升级到 Java 17。\n其他新特性：\n为 Spring Data JDBC 提供更灵活的自动配置为 Prometheus 示例提供自动配置增强 Log4j2 功能，包括配置文件支持和环境属性查找\n详情查看 Release Note。\nSpring Boot 2.7.5 和 2.6.13 的更新内容主要是修复错误，优化文档和升级依赖，详情查看 Release Note (2.7.5、2.6.13)。\n相关链接\nSpring Boot 的详细介绍：点击查看Spring Boot 的下载地址：点击下载', '<h1 style=\"text-indent: 0px; text-align: start;\"><a href=\"https://www.oschina.net/news/214401/spring-boot-3-0-0-rc1-released\" target=\"_blank\">Spring&nbsp;Boot&nbsp;3.0.0&nbsp;首个&nbsp;RC&nbsp;发布</a></h1><p>Spring&nbsp;Boot&nbsp;3.0 首个&nbsp;RC 已发布，此外还为两个分支发布了更新：2.7.5 & 2.6.13。</p><p>3.0.0-RC1</p><p>发布公告写道，此版本包含 135&nbsp;项功能增强、文档改进、依赖升级和&nbsp;Bugfix。</p><p>Spring&nbsp;Boot&nbsp;3.0&nbsp;的开发工作始于实验性的&nbsp;Spring&nbsp;Native，旨在为&nbsp;GraalVM&nbsp;原生镜像提供支持。在该版本中，开发者现在可以使用标准&nbsp;Spring&nbsp;Boot&nbsp;Maven&nbsp;或&nbsp;Gradle&nbsp;插件将&nbsp;Spring&nbsp;Boot&nbsp;应用程序转换为原生可执行文件，而无需任何特殊配置。</p><p>此版本还在参考文档中添加新内容来解释 AOT&nbsp;处理背后的概念以及如何开始生成第一个&nbsp;GraalVM&nbsp;原生镜像。</p><p>除此之外，Spring&nbsp;Boot&nbsp;3.0&nbsp;还完成了迁移到 JakartaEE&nbsp;9&nbsp;的工作，以及将使用的&nbsp;Java&nbsp;版本升级到&nbsp;Java&nbsp;17。</p><p>其他新特性：</p><p>为&nbsp;Spring&nbsp;Data&nbsp;JDBC&nbsp;提供更灵活的自动配置为&nbsp;Prometheus&nbsp;示例提供自动配置增强&nbsp;Log4j2&nbsp;功能，包括配置文件支持和环境属性查找</p><p>详情查看&nbsp;Release&nbsp;Note。</p><p>Spring&nbsp;Boot&nbsp;2.7.5&nbsp;和&nbsp;2.6.13&nbsp;的更新内容主要是修复错误，优化文档和升级依赖，详情查看&nbsp;Release&nbsp;Note&nbsp;(2.7.5、2.6.13)。</p><p>相关链接</p><p>Spring&nbsp;Boot&nbsp;的详细介绍：点击查看Spring&nbsp;Boot&nbsp;的下载地址：点击下载</p>', '', 0, 0, '开源中国', '卓大', NULL, 0, 1, '2024-03-02 18:53:26', '2022-10-22 14:27:33');
INSERT INTO `t_notice` VALUES (50, 1, 'Oracle 推出 JDK 8 的直接替代品', 1, 0, '2024-01-01 20:22:23', 'Oracle 推出 JDK 8 的直接替代品\n来源: OSCHINA\n编辑: 白开水不加糖\n2022-10-20 08:14:29\n 0\n为了向传统的 Java 8 服务器工作负载提供 Java 17 级别的性能，Oracle 宣布推出 Java SE Subscription Enterprise Performance Pack (Enterprise Performance Pack)。并声称这是 JDK 8 的直接替代品，现已在 MyOracleSupport 上面向所有 Java SE 订阅客户和 Oracle 云基础设施 (OCI) 用户免费提供。\n“Enterprise Performance Pack 为 JDK 8 用户提供了在 JDK 8 和 JDK 17 发布之间的 7 年时间里，为 Java 带来的重大内存管理和性能改进。这些改进包括：现代垃圾回收算法、紧凑字符串、增强的可观察性和数十种其他优化。”\nJava 8 发布于 2014 年，和 Java 17 一样都是长期支持 (LTS) 版本；尽管发布距今已有近九年的历史，但仍被很多开发人员和组织所广泛应用。New Relic 发布的一份 “2022 年 Java 生态系统状况报告” 数据表明，Java 8 仍被 46.45% 的 Java 应用程序在生产中使用。\n根据介绍，Enterprise Performance Pack 在 Intel 和基于 Arm 的系统（如 Ampere Altra）上支持 headless Linux 64 位工作负载。\nOracle 方面称，使用 Enterprise Performance Pack 的客户将可以立即看到以或接近内存或 CPU 容量运行的 JDK 8 工作负载的好处。在 Oracle 自己的产品和云服务进行的测试表明，高负载应用程序的内存和性能都提高了大约 40%。即使没有接近容量运行的 JDK 8 应用程序，也可以会看到高达 5% 的性能提升。\n虽然 Enterprise Performance Pack 中包含的许多改进可以通过默认选项获得，但 Oracle 建议用户还是自己研究文档，以最大限度地提高性能并最大限度地降低内存使用率。例如，通过启用可扩展的低延迟 ZGC 垃圾收集器来提高应用程序响应能力，需要通过 -XX:+UseZGC 选项。', '<h3>Oracle&nbsp;推出&nbsp;JDK&nbsp;8&nbsp;的直接替代品</h3><p>来源:&nbsp;OSCHINA</p><p>编辑: 白开水不加糖</p><p>2022-10-20&nbsp;08:14:29</p><p> 0</p><p>为了向传统的&nbsp;Java&nbsp;8&nbsp;服务器工作负载提供&nbsp;Java&nbsp;17&nbsp;级别的性能，Oracle 宣布推出&nbsp;Java&nbsp;SE&nbsp;Subscription&nbsp;Enterprise&nbsp;Performance&nbsp;Pack&nbsp;(Enterprise&nbsp;Performance&nbsp;Pack)。并声称这是 JDK&nbsp;8&nbsp;的直接替代品，现已在 MyOracleSupport 上面向所有&nbsp;Java&nbsp;SE&nbsp;订阅客户和&nbsp;Oracle&nbsp;云基础设施&nbsp;(OCI)&nbsp;用户免费提供。</p><p>“Enterprise&nbsp;Performance&nbsp;Pack&nbsp;为&nbsp;JDK&nbsp;8&nbsp;用户提供了在&nbsp;JDK&nbsp;8&nbsp;和&nbsp;JDK&nbsp;17&nbsp;发布之间的&nbsp;7&nbsp;年时间里，为&nbsp;Java&nbsp;带来的重大内存管理和性能改进。这些改进包括：现代垃圾回收算法、紧凑字符串、增强的可观察性和数十种其他优化。”</p><p>Java&nbsp;8&nbsp;发布于&nbsp;2014&nbsp;年，和&nbsp;Java&nbsp;17&nbsp;一样都是长期支持&nbsp;(LTS)&nbsp;版本；尽管发布距今已有近九年的历史，但仍被很多开发人员和组织所广泛应用。New&nbsp;Relic&nbsp;发布的一份 “2022&nbsp;年&nbsp;Java&nbsp;生态系统状况报告”&nbsp;数据表明，Java&nbsp;8&nbsp;仍被&nbsp;46.45%&nbsp;的&nbsp;Java&nbsp;应用程序在生产中使用。</p><p>根据介绍，Enterprise&nbsp;Performance&nbsp;Pack&nbsp;在&nbsp;Intel&nbsp;和基于&nbsp;Arm&nbsp;的系统（如&nbsp;Ampere&nbsp;Altra）上支持 headless&nbsp;Linux&nbsp;64&nbsp;位工作负载。</p><p>Oracle 方面称，使用&nbsp;Enterprise&nbsp;Performance&nbsp;Pack&nbsp;的客户将可以立即看到以或接近内存或&nbsp;CPU&nbsp;容量运行的&nbsp;JDK&nbsp;8&nbsp;工作负载的好处。在&nbsp;Oracle&nbsp;自己的产品和云服务进行的测试表明，高负载应用程序的内存和性能都提高了大约&nbsp;40%。即使没有接近容量运行的&nbsp;JDK&nbsp;8&nbsp;应用程序，也可以会看到高达&nbsp;5%&nbsp;的性能提升。</p><p>虽然&nbsp;Enterprise&nbsp;Performance&nbsp;Pack&nbsp;中包含的许多改进可以通过默认选项获得，但 Oracle 建议用户还是自己研究文档，以最大限度地提高性能并最大限度地降低内存使用率。例如，通过启用可扩展的低延迟&nbsp;ZGC&nbsp;垃圾收集器来提高应用程序响应能力，需要通过&nbsp;-XX:+UseZGC&nbsp;选项。</p>', '', 0, 0, 'OSChina', '卓大', NULL, 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:29:56');
INSERT INTO `t_notice` VALUES (51, 1, 'Spring Framework 6.0.0 RC2 发布', 1, 0, '2024-01-01 20:22:23', 'Spring Framework 6.0.0 RC2 发布\nSpring Framework 6.0.0 发布了第二个 RC 版本。\n新特性\n确保可以在构建时评估 classpath 检查 #29352为 JPA 持久化回调引入 Register 反射提示 #29348检查 @RegisterReflectionForBinding 是否至少指定一个类 #29346为 AOT 引擎设置引入 builder API #29341支持检测正在进行的 AOT 处理 #29340重新组织 HTTP Observation 类型 #29334支持在没有 java.beans.Introspector 的前提下，执行基本属性判断 #29320为BindingReflectionHintsRegistrar 添加 Kotlin 数据类组件支持 #29316将 HttpServiceFactory 和 RSocketServiceProxyFactory 切换到 builder 模型，以便优先进行可编程配置 #29296引入基于 GraalVM FieldValueTransformer API 的 PreComputeFieldFeature#29081在 TestContext 框架中引入 SPI 来处理 ApplicationContext 故障 #28826SimpleEvaluationContext 支持禁用 array 分配 #28808DateTimeFormatterRegistrar 支持默认回退到 ISO 解析 #26985\nSpring Framework 6.0 作为重大更新，要求使用 Java 17 或更高版本，并且已迁移到 Jakarta EE 9+（在 jakarta 命名空间中取代了以前基于 javax 的 EE API），以及对其他基础设施的修改。基于这些变化，Spring Framework 6.0 支持最新 Web 容器，如 Tomcat 10 / Jetty 11，以及最新的持久性框架 Hibernate ORM 6.1。这些特性仅可用于 Servlet API 和 JPA 的 jakarta 命名空间变体。\n值得一提的是，开发者可通过此版本在基于 Spring 的应用中体验 “虚拟线程”（JDK 19 中的预览版 “Project Loom”），查看此文章了解更多细节。现在提供了自定义选项来插入基于虚拟线程的 Executor 实现，目标是在 Project Loom 正式可用时提供 “一等公民” 的配置选项。\n除了上述的变化，Spring Framework 6.0 还包含许多其他改进和特性，例如：\n提供基于 @HttpExchange 服务接口的 HTTP 接口客户端对 RFC 7807 问题详细信息的支持Spring HTTP 客户端提供基于 Micrometer 的可观察性……\n详情查看 Release Note。\n按照发布计划，Spring Framework 6.0 将于 11 月正式 GA。', '<h1 style=\"text-indent: 0px; text-align: start;\"><a href=\"https://www.oschina.net/news/214472/spring-framework-6-0-0-rc2-released\" target=\"_blank\">Spring&nbsp;Framework&nbsp;6.0.0&nbsp;RC2&nbsp;发布</a></h1><p style=\"text-indent: 0px; text-align: left;\">Spring&nbsp;Framework&nbsp;6.0.0&nbsp;发布了<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fspring.io%2Fblog%2F2022%2F10%2F20%2Fspring-framework-6-0-0-rc2-available-now\" target=\"_blank\">第二个&nbsp;RC&nbsp;版本</a>。</p><p style=\"text-indent: 0px; text-align: left;\"><strong>新特性</strong></p><ul style=\"text-indent: 0px; text-align: left;\"><li>确保可以在构建时评估&nbsp;classpath&nbsp;检查&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29352\" target=\"_blank\">#29352</a></li><li>为&nbsp;JPA&nbsp;持久化回调引入&nbsp;Register&nbsp;反射提示&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29348\" target=\"_blank\">#29348</a></li><li>检查&nbsp;<span style=\"color: rgb(51, 51, 51); font-size: 13px;\"><code>@RegisterReflectionForBinding</code></span>&nbsp;是否至少指定一个类&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29346\" target=\"_blank\">#29346</a></li><li>为&nbsp;AOT&nbsp;引擎设置引入&nbsp;builder&nbsp;API&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29341\" target=\"_blank\">#29341</a></li><li>支持检测正在进行的&nbsp;AOT&nbsp;处理&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29340\" target=\"_blank\">#29340</a></li><li>重新组织&nbsp;HTTP&nbsp;Observation&nbsp;类型&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29334\" target=\"_blank\">#29334</a></li><li>支持在没有&nbsp;java.beans.Introspector&nbsp;的前提下，执行基本属性判断&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29320\" target=\"_blank\">#29320</a></li><li>为<span style=\"color: rgb(51, 51, 51); font-size: 13px;\"><code>BindingReflectionHintsRegistrar</code></span>&nbsp;添加&nbsp;Kotlin&nbsp;数据类组件支持&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29316\" target=\"_blank\">#29316</a></li><li>将&nbsp;HttpServiceFactory&nbsp;和&nbsp;RSocketServiceProxyFactory&nbsp;切换到&nbsp;builder&nbsp;模型，以便优先进行可编程配置&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29296\" target=\"_blank\">#29296</a></li><li>引入基于&nbsp;GraalVM&nbsp;<span style=\"color: rgb(51, 51, 51); font-size: 13px;\"><code>FieldValueTransformer</code></span>&nbsp;API&nbsp;的&nbsp;<span style=\"color: rgb(51, 51, 51); font-size: 13px;\"><code>PreComputeFieldFeature</code></span><a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F29081\" target=\"_blank\">#29081</a></li><li>在&nbsp;TestContext&nbsp;框架中引入&nbsp;SPI&nbsp;来处理&nbsp;ApplicationContext&nbsp;故障&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F28826\" target=\"_blank\">#28826</a></li><li>SimpleEvaluationContext&nbsp;支持禁用&nbsp;array&nbsp;分配&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F28808\" target=\"_blank\">#28808</a></li><li>DateTimeFormatterRegistrar&nbsp;支持默认回退到&nbsp;ISO&nbsp;解析&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fissues%2F26985\" target=\"_blank\">#26985</a></li></ul><p style=\"text-indent: 0px; text-align: left;\"><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);\">Spring&nbsp;Framework&nbsp;6.0&nbsp;作为重大更新，要求</span><span style=\"color: rgb(51, 51, 51);\"><strong>使用&nbsp;Java&nbsp;17&nbsp;或更高版本</strong></span><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);\">，并且已迁移到&nbsp;Jakarta&nbsp;EE&nbsp;9+（在&nbsp;</span><span style=\"color: rgb(51, 51, 51); font-size: 13px;\"><code>jakarta</code></span><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);\">&nbsp;命名空间中取代了以前基于&nbsp;</span><span style=\"color: rgb(51, 51, 51); font-size: 13px;\"><code>javax</code></span><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);\">&nbsp;的&nbsp;EE&nbsp;API），以及对其他基础设施的修改。基于这些变化，Spring&nbsp;Framework&nbsp;6.0&nbsp;支持最新&nbsp;Web&nbsp;容器，如&nbsp;</span><a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Ftomcat.apache.org%2Fwhichversion.html\" target=\"_blank\">Tomcat&nbsp;10</a><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);\">&nbsp;/&nbsp;</span><a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fwww.eclipse.org%2Fjetty%2Fdownload.php\" target=\"_blank\">Jetty&nbsp;11</a><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);\">，以及最新的持久性框架&nbsp;</span><a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fhibernate.org%2Form%2Freleases%2F6.1%2F\" target=\"_blank\">Hibernate&nbsp;ORM&nbsp;6.1</a><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);\">。这些特性仅可用于&nbsp;Servlet&nbsp;API&nbsp;和&nbsp;JPA&nbsp;的&nbsp;jakarta&nbsp;命名空间变体。</span></p><p style=\"text-indent: 0px; text-align: left;\">值得一提的是，开发者可通过此版本在基于&nbsp;Spring&nbsp;的应用中体验&nbsp;“虚拟线程”（JDK&nbsp;19&nbsp;中的预览版&nbsp;“Project&nbsp;Loom”），<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fspring.io%2Fblog%2F2022%2F10%2F11%2Fembracing-virtual-threads\" target=\"_blank\">查看此文章</a>了解更多细节。现在提供了自定义选项来插入基于虚拟线程的&nbsp;<span style=\"color: rgb(51, 51, 51); font-size: 13px;\"><code>Executor</code></span>&nbsp;实现，目标是在&nbsp;Project&nbsp;Loom&nbsp;正式可用时提供&nbsp;“一等公民”&nbsp;的配置选项。</p><p style=\"text-indent: 0px; text-align: left;\">除了上述的变化，Spring&nbsp;Framework&nbsp;6.0&nbsp;还包含许多其他改进和特性，例如：</p><ul style=\"text-indent: 0px; text-align: left;\"><li>提供基于&nbsp;<span style=\"color: rgb(51, 51, 51); font-size: 13px;\"><code>@HttpExchange</code></span>&nbsp;服务接口的&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fdocs.spring.io%2Fspring-framework%2Fdocs%2F6.0.0-RC1%2Freference%2Fhtml%2Fintegration.html%23rest-http-interface\" target=\"_blank\">HTTP&nbsp;接口客户端</a></li><li>对&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fdocs.spring.io%2Fspring-framework%2Fdocs%2F6.0.0-RC1%2Freference%2Fhtml%2Fweb.html%23mvc-ann-rest-exceptions\" target=\"_blank\">RFC&nbsp;7807&nbsp;问题详细信息</a>的支持</li><li>Spring&nbsp;HTTP&nbsp;客户端提供基于&nbsp;Micrometer&nbsp;的可观察性</li><li>……</li></ul><p style=\"text-indent: 0px; text-align: left;\"><a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Freleases%2Ftag%2Fv6.0.0-RC2\" target=\"_blank\">详情查看&nbsp;Release&nbsp;Note</a>。</p><p style=\"text-indent: 0px; text-align: left;\">按照发布计划，Spring&nbsp;Framework&nbsp;6.0&nbsp;将于&nbsp;11&nbsp;月正式&nbsp;GA。</p>', '', 0, 0, 'CSDN', '罗伊', NULL, 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:30:45');
INSERT INTO `t_notice` VALUES (52, 1, 'Windows Terminal 正式成为 Windows 11 默认终端', 1, 0, '2024-01-01 20:22:23', '今年 7 月 ，微软在 Windows 11 的 Beta 版本测试了将系统默认终端设置为 Windows Terminal 。如今该设置已登录稳定版本，从 Windows 11 22H2 版本开始，Windows Terminal 将正式成为 Windows 11 的默认设置。\n默认终端是在打开命令行应用程序时默认启动的终端模拟器。从 Windows 诞生之日起，其默认终端一直是 Windows 控制台主机 conhost.exe。此次更新则意味着，以后 Windows 11 的所有命令行应用程序都将在 Windows Terminal 中自动打开。\nWindows Terminal 拥有非常多现代化的功能，毕竟它很新（ 2019 年 5 月在 Microsoft Build 上首次发布），吸取了很多现代终端的灵感。它支持多选项卡和窗格、命令面板等现代化的 UI 和操作方式，以及大量的自定义选项，比如目录、配置文件图标、自定义背景图像、配色方案、字体和透明度。\n当然，如果不想用 Windows Terminal，用户也可以在 Windows 设置中的 隐私和安全 > 开发人员页面和 Windows 终端设置 中调整默认终端设置，（此更新使用 “让 Windows 决定” 作为默认选择，即默认采用 Windows Terminal） 。\n此外，如果在更新之前就已设置其他默认终端，此次更新不会覆盖你的偏好。\n关于 Windows 11 默认终端的更多详情可查看微软博客。', '<p style=\"text-indent: 0px; text-align: left;\">今年&nbsp;7&nbsp;月&nbsp;，微软在&nbsp;Windows&nbsp;11&nbsp;的&nbsp;Beta&nbsp;版本<a href=\"https://www.oschina.net/news/204429/wt-default-terminal-in-win11-beta-channel\" target=\"\">测试</a>了将系统默认终端设置为&nbsp;Windows&nbsp;Terminal&nbsp;。如今该设置已登录稳定版本，从&nbsp;Windows&nbsp;11&nbsp;22H2&nbsp;版本开始，Windows&nbsp;Terminal&nbsp;将<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fdevblogs.microsoft.com%2Fcommandline%2Fwindows-terminal-is-now-the-default-in-windows-11%2F\" target=\"_blank\">正式成为</a>&nbsp;Windows&nbsp;11&nbsp;的默认设置。</p><p style=\"text-indent: 0px; text-align: left;\">默认终端是在打开命令行应用程序时默认启动的终端模拟器。从&nbsp;Windows&nbsp;诞生之日起，其默认终端一直是&nbsp;Windows&nbsp;控制台主机&nbsp;conhost.exe。此次更新则意味着，以后&nbsp;Windows&nbsp;11&nbsp;的所有命令行应用程序都将在&nbsp;Windows&nbsp;Terminal&nbsp;中自动打开。</p><p style=\"text-indent: 0px; text-align: left;\">Windows&nbsp;Terminal&nbsp;拥有非常多现代化的功能，毕竟它<span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);\">很新（&nbsp;2019&nbsp;年&nbsp;5&nbsp;月在&nbsp;Microsoft&nbsp;Build&nbsp;上首次发布），吸取了很多现代终端的灵感。它支持多</span>选项卡和窗格、命令面板等现代化的&nbsp;UI&nbsp;和操作方式，以及大量的自定义选项，比如目录、配置文件图标、自定义背景图像、配色方案、字体和透明度。</p><p style=\"text-indent: 0px; text-align: left;\">当然，如果不想用&nbsp;Windows&nbsp;Terminal，用户也可以在&nbsp;Windows&nbsp;设置中的&nbsp;<em>隐私和安全&nbsp;&gt;&nbsp;开发人员页面和&nbsp;Windows&nbsp;终端设置&nbsp;</em>中调整默认终端设置，（此更新使用&nbsp;“让&nbsp;Windows&nbsp;决定”&nbsp;作为默认选择，即默认采用&nbsp;Windows&nbsp;Terminal）&nbsp;。</p><p style=\"text-indent: 0px; text-align: left;\">此外，如果在更新之前就已设置其他默认终端，此次更新<strong>不会覆盖</strong>你的偏好。</p><p style=\"text-indent: 0px; text-align: left;\">关于&nbsp;Windows&nbsp;11&nbsp;默认终端的更多详情可查看<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fdevblogs.microsoft.com%2Fcommandline%2Fwindows-terminal-is-now-the-default-in-windows-11%2F\" target=\"_blank\">微软博客</a>。</p>', '', 0, 0, '开源中国', '善逸', NULL, 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:33:03');
INSERT INTO `t_notice` VALUES (53, 1, 'TypeScript 诞生 10 周年', 1, 0, '2024-01-01 20:22:23', 'TypeScript 已经诞生 10 年了。10 年前 ——2012 年 10 月 1 日，TypeScript 首次公开亮相。当时主导 TypeScript 开发的 Anders Hejlsberg 这样描述 TypeScript：\n它是 JavaScript 的类型化超集，可被编译成常用的 JavaScript。TypeScript 还可以通过启用丰富的工具体验来极大地帮助提升生产力，与此同时开发者保持不变维护现有的代码，并继续使用喜爱的 JavaScript 库。TypeScript is a typed superset of JavaScript that compiles to idiomatic (normal) JavaScript, can dramatically improve your productivity by enabling rich tooling experiences, all while maintaining your existing code and continuing to use the same JavaScript libraries you already love.\n微软在博客中回顾了 TypeScript 刚亮相时受到的评价，大多数人对它都是持怀疑态度，毕竟这对于许多 JavaScript 开发者来说，试图将静态类型引入 JavaScript 是一个笑话 —— 或是邪恶的阴谋。反对者则直言这是十分愚蠢的想法，他们认为当时已存在可以编译为 JavaScript 的强类型语言，例如 C#、Java 和 C++。他们还吐槽主导 TypeScript 开发的 Anders Hejlsberg 对静态类型有 “迷之执着”。\n当时微软意识到 JavaScript 未来将会被应用到无数场景，而且他们公司内部团队在处理复杂的 JavaScript 代码库时面临着巨大的挑战，所以他们觉得有必要创造强大的工具来帮助编写 JavaScript—— 尤其是针对大型 JavaScript 项目。基于此需求，TypeScript 也确定了自己的定位和特性，它是 JavaScript 的超集，将类型检查和静态分析、显式接口和最佳实践结合到单一语言和编译器中。通过在 JavaScript 上构建，TypeScript 能够更接近目标运行时，同时仅添加支持大型应用程序和大型团队所需的语法糖。\n团队还坚持 TypeScript 要能够与现有的 JavaScript 无缝交互，与 JavaScript 共同进化，并且看上去也和 JavaScript 类似。\nTypeScript 诞生之初的部分设计目标：\n不会对已有的程序增加运行时开销与当前和未来的 ECMAScript 提案保持一致保留所有 JavaScript 代码的运行时行为避免添加表达式类型的语法 (expression-level syntax)使用一致、完全可擦除的结构化类型系统……\n这些目标指导着 TypeScript 的发展方向：关注类型系统，成为 JavaScript 的类型检查器，只添加类型检查所需的语法，避免添加新的运行时语法和行为。\n微软提到，TypeScript 拥有如今的繁荣生态离不开一个重要属性：开源。TypeScript 一开始就是免费且开源 —— 语言规范和编译器都是开源项目，并且以真正开放的方式来运作。事实上，微软当时对外展现出的姿态并不是现在的 “拥抱开源”，所以他们内部并没真正认识到 TypeScript 的开源是如何帮助它走向成功。因此有人认为，TypeScript 在很大程度上引导微软开始更多地转向开源。\n现在，TypeScript 仍在积极发展和迭代改进，并被全球数百万开发者使用。在诸多编程语言排名、指数或开发者调查中，TypeScript 一直位居前列，也是最受欢迎和最常用的编程语言。', '<p style=\"text-indent: 0px; text-align: start;\">TypeScript&nbsp;已经诞生&nbsp;10&nbsp;年了。10&nbsp;年前&nbsp;——2012&nbsp;年&nbsp;10&nbsp;月&nbsp;1&nbsp;日，TypeScript&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fweb.archive.org%2Fweb%2F20121003001910%2Fhttps%3A%2F%2Fblogs.msdn.com%2Fb%2Fsomasegar%2Farchive%2F2012%2F10%2F01%2Ftypescript-javascript-development-at-application-scale.aspx\" target=\"_blank\"><strong>首次公开亮相</strong></a>。当时主导&nbsp;TypeScript&nbsp;开发的&nbsp;Anders&nbsp;Hejlsberg&nbsp;这样描述&nbsp;TypeScript：</p><blockquote style=\"text-indent: 0px; text-align: left;\">它是&nbsp;JavaScript&nbsp;的类型化超集，可被编译成常用的&nbsp;JavaScript。TypeScript&nbsp;还可以通过启用丰富的工具体验来极大地帮助提升生产力，与此同时开发者保持不变维护现有的代码，并继续使用喜爱的&nbsp;JavaScript&nbsp;库。TypeScript&nbsp;is&nbsp;a&nbsp;typed&nbsp;superset&nbsp;of&nbsp;JavaScript&nbsp;that&nbsp;compiles&nbsp;to&nbsp;idiomatic&nbsp;(normal)&nbsp;JavaScript,&nbsp;can&nbsp;dramatically&nbsp;improve&nbsp;your&nbsp;productivity&nbsp;by&nbsp;enabling&nbsp;rich&nbsp;tooling&nbsp;experiences,&nbsp;all&nbsp;while&nbsp;maintaining&nbsp;your&nbsp;existing&nbsp;code&nbsp;and&nbsp;continuing&nbsp;to&nbsp;use&nbsp;the&nbsp;same&nbsp;JavaScript&nbsp;libraries&nbsp;you&nbsp;already&nbsp;love.</blockquote><p style=\"text-indent: 0px; text-align: left;\">微软在博客中回顾了&nbsp;TypeScript&nbsp;刚亮相时受到的评价，大多数人对它都是持怀疑态度，毕竟这对于许多&nbsp;JavaScript&nbsp;开发者来说，试图将静态类型引入&nbsp;JavaScript&nbsp;是一个笑话&nbsp;——&nbsp;或是邪恶的阴谋。反对者则直言这是十分愚蠢的想法，他们认为当时已存在可以编译为&nbsp;JavaScript&nbsp;的强类型语言，例如&nbsp;C#、Java&nbsp;和&nbsp;C++。他们还吐槽主导&nbsp;TypeScript&nbsp;开发的&nbsp;Anders&nbsp;Hejlsberg&nbsp;对静态类型有&nbsp;“迷之执着”。</p><p style=\"text-indent: 0px; text-align: start;\">当时微软意识到&nbsp;JavaScript&nbsp;未来将会被应用到无数场景，而且他们公司内部团队在处理复杂的&nbsp;JavaScript&nbsp;代码库时面临着巨大的挑战，所以他们觉得有必要创造强大的工具来帮助编写&nbsp;JavaScript——&nbsp;尤其是针对大型&nbsp;JavaScript&nbsp;项目。基于此需求，TypeScript&nbsp;也确定了自己的定位和特性，它是&nbsp;JavaScript&nbsp;的超集，将类型检查和静态分析、显式接口和最佳实践结合到单一语言和编译器中。通过在&nbsp;JavaScript&nbsp;上构建，TypeScript&nbsp;能够更接近目标运行时，同时仅添加支持大型应用程序和大型团队所需的语法糖。</p><p style=\"text-indent: 0px; text-align: start;\">团队还坚持&nbsp;TypeScript&nbsp;要能够与现有的&nbsp;JavaScript&nbsp;无缝交互，与&nbsp;JavaScript&nbsp;共同进化，并且看上去也和&nbsp;JavaScript&nbsp;类似。</p><p style=\"text-indent: 0px; text-align: start;\">TypeScript&nbsp;诞生之初的部分<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fgithub.com%2Fmicrosoft%2FTypeScript%2Fwiki%2FTypeScript-Design-Goals%2F53ffa9b1802cd8e18dfe4b2cd4e9ef5d4182df10\" target=\"_blank\"><strong>设计目标</strong></a>：</p><ul style=\"text-indent: 0px; text-align: left;\"><li>不会对已有的程序增加运行时开销</li><li>与当前和未来的&nbsp;ECMAScript&nbsp;提案保持一致</li><li>保留所有&nbsp;JavaScript&nbsp;代码的运行时行为</li><li>避免添加表达式类型的语法&nbsp;(expression-level&nbsp;syntax)</li><li>使用一致、完全可擦除的结构化类型系统</li><li>……</li></ul><p style=\"text-indent: 0px; text-align: start;\">这些目标指导着&nbsp;TypeScript&nbsp;的发展方向：关注类型系统，成为&nbsp;JavaScript&nbsp;的类型检查器，只添加类型检查所需的语法，避免添加新的运行时语法和行为。</p><p style=\"text-indent: 0px; text-align: start;\">微软提到，TypeScript&nbsp;拥有如今的繁荣生态离不开一个重要属性：<strong>开源</strong>。TypeScript&nbsp;一开始就是免费且开源&nbsp;——<span style=\"color: rgb(51, 51, 51);\">&nbsp;语言规范和编译器都是开源项目，</span>并且以真正开放的方式来运作。事实上，微软当时对外展现出的姿态并不是现在的&nbsp;“拥抱开源”，所以他们内部并没真正认识到&nbsp;TypeScript&nbsp;的开源是如何帮助它走向成功。因此有人认为，TypeScript&nbsp;在很大程度上引导微软开始更多地转向开源。</p><p style=\"text-indent: 0px; text-align: start;\">现在，TypeScript&nbsp;仍在积极发展和迭代改进，并被全球数百万开发者使用。在诸多编程语言排名、指数或开发者调查中，TypeScript&nbsp;一直位居前列，也是最受欢迎和最常用的编程语言。</p>', '', 0, 0, '开源中国', '开云', NULL, 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:34:56');
INSERT INTO `t_notice` VALUES (54, 1, 'JetBrains Fleet 公测，下一代 IDE', 1, 0, '2024-01-01 20:22:23', 'JetBrains 宣布首次公共预览 Fleet，所有人都可以使用。Fleet 是由 JetBrains 打造的下一代 IDE，于 2021 年首次正式推出。它是一个新的分布式多语言编辑器和 IDE，基于 JetBrains 在后端的 IntelliJ 平台，采用了全新的用户界面和分布式架构从头开始构建。\n下载 Fleet：https://www.jetbrains.com.cn/fleet/download/\n\n公告表示，自从最初宣布 Fleet 以来，有超过 137,000 人报名参加私人预览；官方最初之所以决定从封闭式预览开始，是为了能够以渐进的方式处理反馈。现如今，JetBrains Fleet 仍处于起步阶段，还有大量的工作要做。其向公众开放预览的原因有两个方面：“首先，我们认为让所有注册者再等下去是不对的，但单独邀请这么多人对我们来说也缺乏意义。面向公众开放预览对我们来说更容易。第二，也是最重要的，我们一直是一家以开放态度打造产品的公司。我们不希望 Fleet 在这方面有任何不同。”\nJetBrains 方面提供了一个图表，以显示 Fleet 目前提供支持的语言和技术，以及每个技术的状态。但值得注意的是，Fleet 仍处于早期阶段，有些事情可能无法按预期工作；所以即使有些东西被列为受支持的，也有可能存在问题。\n同时 JetBrains 也强调称，他们并不打算取代其现有的 IDE。\n因此，请不要期望在 Fleet 中看到与我们的 IDE（如 IntelliJ IDEA）完全相同的功能。尽管我们会继续开发 Fleet，我们 IDE 的所有功能也不会出现在其中。Fleet 是我们为开发者提供不同用户体验的一个机会。话虽如此，我们确实希望听到你认为 Fleet 还缺少什么功能的反馈，例如特定的重构选项、工具集成等。我们现有的 IDE 将继续发展。我们对其有很多计划，包括性能改进、新的用户界面、远程开发等等。最后，Fleet 还在底层采用了我们现有工具的智慧，所以这些工具都不会消失。\nJetBrains 透露，在未来几个月他们将致力于稳定 Fleet，并尽可能地解决得到的反馈。同时，将在以下领域开展工作：\n为插件作者提供 API 支持和 SDK–鉴于 Fleet 有一个分布式架构，我们需要努力为插件作者简化工作。 虽然我们保证会为扩展 Fleet 提供一个平台，但也请求大家在这方面多一点耐心。 性能 – 我们希望 Fleet 不仅在内存占用方面，而且在响应时间方面都能表现出色。 有很多地方我们仍然可以提高性能，我们将在这些方面努力。 主题和键盘地图 – 我们知道许多开发者已经习惯了他们现有的编辑器和 IDE，当他们转移到新的 IDE 时，往往会想念他们以前的键盘绑定和主题。 我们将致力于增加对更多主题和键盘映射的支持。 我们当然也会致力于 Vim 的模拟。\n更多详情可查看官方博客。', '<p style=\"text-indent: 0px; text-align: left;\">JetBrains&nbsp;<a href=\"https://my.oschina.net/u/5494143/blog/5584325\" target=\"\">宣布</a>首次公共预览&nbsp;<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fwww.jetbrains.com.cn%2Ffleet%2F\" target=\"_blank\">Fleet</a>，所有人都可以使用。Fleet&nbsp;是由&nbsp;JetBrains&nbsp;打造的下一代&nbsp;IDE，于&nbsp;2021&nbsp;年首次正式<a href=\"https://my.oschina.net/u/5494143/blog/5332934\" target=\"\">推出</a>。它是一个新的分布式多语言编辑器和&nbsp;IDE，基于&nbsp;JetBrains&nbsp;在后端的&nbsp;IntelliJ&nbsp;平台，采用了全新的用户界面和分布式架构从头开始构建。</p><p style=\"text-indent: 0px; text-align: left;\"><strong>下载&nbsp;Fleet：</strong><a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fwww.jetbrains.com.cn%2Ffleet%2Fdownload%2F\" target=\"_blank\">https://www.jetbrains.com.cn/fleet/download/</a></p><p style=\"text-indent: 0px; text-align: left;\"><br></p><p style=\"text-indent: 0px; text-align: left;\">公告表示，自从最初宣布&nbsp;Fleet&nbsp;以来，有超过&nbsp;137,000&nbsp;人报名参加私人预览；官方最初之所以决定从封闭式预览开始，是为了能够以渐进的方式处理反馈。现如今，JetBrains&nbsp;Fleet&nbsp;仍处于起步阶段，还有大量的工作要做。其向公众开放预览的原因有两个方面：“首先，我们认为让所有注册者再等下去是不对的，但单独邀请这么多人对我们来说也缺乏意义。面向公众开放预览对我们来说更容易。第二，也是最重要的，我们一直是一家以开放态度打造产品的公司。我们不希望&nbsp;Fleet&nbsp;在这方面有任何不同。”</p><p style=\"text-indent: 0px; text-align: left;\">JetBrains&nbsp;方面提供了一个<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fjb.gg%2Ffleet-feature-matrix\" target=\"_blank\">图表</a>，以显示&nbsp;Fleet&nbsp;目前提供支持的语言和技术，以及每个技术的状态。但值得注意的是，Fleet&nbsp;仍处于早期阶段，有些事情可能无法按预期工作；所以即使有些东西被列为受支持的，也有可能存在问题。</p><p style=\"text-indent: 0px; text-align: left;\">同时&nbsp;JetBrains&nbsp;也强调称，他们并不打算取代其现有的&nbsp;IDE。</p><blockquote style=\"text-indent: 0px; text-align: left;\">因此，请不要期望在&nbsp;Fleet&nbsp;中看到与我们的&nbsp;IDE（如&nbsp;IntelliJ&nbsp;IDEA）完全相同的功能。尽管我们会继续开发&nbsp;Fleet，我们&nbsp;IDE&nbsp;的所有功能也不会出现在其中。Fleet&nbsp;是我们为开发者提供不同用户体验的一个机会。话虽如此，我们确实希望听到你认为&nbsp;Fleet&nbsp;还缺少什么功能的反馈，例如特定的重构选项、工具集成等。我们现有的&nbsp;IDE&nbsp;将继续发展。我们对其有很多计划，包括性能改进、新的用户界面、远程开发等等。最后，Fleet&nbsp;还在底层采用了我们现有工具的智慧，所以这些工具都不会消失。</blockquote><p style=\"text-indent: 0px; text-align: start;\">JetBrains&nbsp;透露，在未来几个月他们将致力于稳定&nbsp;Fleet，并尽可能地解决得到的反馈。同时，将在以下领域开展工作：</p><ul style=\"text-indent: 0px; text-align: left;\"><li><strong>为插件作者提供&nbsp;API&nbsp;支持和&nbsp;SDK</strong>–鉴于&nbsp;Fleet&nbsp;有一个<a href=\"https://www.oschina.net/action/GoToLink?url=https%3A%2F%2Fblog.jetbrains.com%2Fzh-hans%2Ffleet%2F2022%2F01%2Ffleet-below-deck-part-i-architecture-overview%2F\" target=\"_blank\">分布式架构</a>，我们需要努力为插件作者简化工作。&nbsp;虽然我们保证会为扩展&nbsp;Fleet&nbsp;提供一个平台，但也请求大家在这方面多一点耐心。&nbsp;</li><li><strong>性能</strong>&nbsp;–&nbsp;我们希望&nbsp;Fleet&nbsp;不仅在内存占用方面，而且在响应时间方面都能表现出色。&nbsp;有很多地方我们仍然可以提高性能，我们将在这些方面努力。&nbsp;</li><li><strong>主题和键盘地图</strong>&nbsp;–&nbsp;我们知道许多开发者已经习惯了他们现有的编辑器和&nbsp;IDE，当他们转移到新的&nbsp;IDE&nbsp;时，往往会想念他们以前的键盘绑定和主题。&nbsp;我们将致力于增加对更多主题和键盘映射的支持。&nbsp;我们当然也会致力于&nbsp;Vim&nbsp;的模拟。</li></ul><p style=\"text-indent: 0px; text-align: left;\">更多详情可<a href=\"https://my.oschina.net/u/5494143/blog/5584325\" target=\"\">查看官方博客</a>。</p>', '', 0, 0, 'CSDN', '开云', NULL, 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:36:10');
INSERT INTO `t_notice` VALUES (55, 2, '1024创新实验室 十一放假通知', 1, 0, '2024-01-01 20:22:23', '国庆假期即将来临，根据国务院办公厅关于国庆节的放假安排，废纸信息网安排如下：10月1日至7日放假调休，共7天。\n衷心预祝\n国庆快乐，阖家幸福！', '<p style=\"text-indent: 0px; text-align: justify;\">国庆假期即将来临，根据国务院办公厅关于国庆节的放假安排，废纸信息网安排如下：<strong>10月1日至7日放假调休</strong>，共7天。</p><p style=\"text-indent: 0px; text-align: justify;\"><strong>衷心预祝</strong></p><p style=\"text-indent: 0px; text-align: justify;\"><strong>国庆快乐，阖家幸福！</strong></p>', '', 0, 0, '人力行政部', '卓大', '1024创新实验室发〔2022〕字第36号', 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:37:57');
INSERT INTO `t_notice` VALUES (56, 2, '十月份技术分享会议', 1, 0, '2024-01-01 20:22:23', '尊敬的各位技术大佬：\n1024创新实验室技术分享即将隆重举行\n现将有关会议事宜通知如下：\n一、会议内容\n1、研究探讨SmartAdmin的技术体系\n二、会议形式\n大会专题小会分组讨论;\n三、会议时间及地点\n会议报到时间：xxx1年6月14日\n会议报到地点：洛阳市', '<p style=\"text-indent: 0px; text-align: start;\">尊敬的各位技术大佬：</p><p style=\"text-indent: 0px; text-align: start;\">1024创新实验室技术分享即将隆重举行</p><p style=\"text-indent: 0px; text-align: start;\">现将有关会议事宜通知如下：</p><p style=\"text-indent: 0px; text-align: start;\"><strong>一、会议内容</strong></p><p style=\"text-indent: 0px; text-align: start;\">1、研究探讨SmartAdmin的技术体系</p><p style=\"text-indent: 0px; text-align: start;\"><strong>二、会议形式</strong></p><p style=\"text-indent: 0px; text-align: start;\">大会专题小会分组讨论;</p><p style=\"text-indent: 0px; text-align: start;\"><strong>三、会议时间及地点</strong></p><p style=\"text-indent: 0px; text-align: start;\">会议报到时间：xxx1年6月14日</p><p style=\"text-indent: 0px; text-align: start;\">会议报到地点：洛阳市</p>', '', 0, 0, '技术部', '开云', '1024创新实验室发〔2022〕字第33号', 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:40:45');
INSERT INTO `t_notice` VALUES (57, 2, '关于疫情防控上班通知', 1, 0, '2024-01-01 20:22:23', '近期，国内部分地区疫情频发，多地疫情出现外溢，为有效降低我市疫情输入和传播风险，洛阳市疾病预防控制中心发布疫情防控公众提示：\n一、所有入（返）洛阳人员均需提前3天向目的地社区（村居）、酒店宾馆、接待单位等所属网格进行报备，或通过“洛阳即时通系统”进行自主报备，配合做好健康码和行程码查验、核酸检测、隔离观察和健康监测等相关疫情防控措施。\n二、倡导广大群众减少跨地市出行，避免人群大范围流动引发的疫情传播扩散风险。\n三、对7天内有高风险区旅居史的人员，采取7天集中隔离医学观察；对7天内有中风险区旅居史的人员，采取7天居家隔离医学观察，如不具备居家隔离医学观察条件的，采取集中隔离医学观察。\n四、对疫情发生地出现一定范围社区传播或已实施大范围社区管控措施，基于对疫情输入风险研判结果，对近7天内来自疫情发生地所在县（市、区）的流入人员，参照中风险区旅居史人员的防控要求采取相应措施。\n五、对所有省外入（返）洛阳人员，须持有48小时内核酸检测阴性证明，抵达后进行“5天3检”，每次检测间隔24小时。推广“落地检”，按照“自愿免费即采即走，不限制流动”的原则，抵达我市后，立即进行1次核酸检测。\n六、加强重点机构场所疫情防控，坚持非必要不举办，对确需举办的培训、会展、文艺演出等大型聚集性活动，查验48小时内核酸检测阴性证明；建筑工地等人员密集型单位，查验外省（区、市）返岗人员48小时内核酸检测阴性证明；养老机构、儿童福利机构等查验探访人员48小时内核酸检测阴性证明；对进入宾馆、酒店和旅游景区等人流密集场所时，查验48小时内核酸检测阴性证明。\n七、近期有外出旅行史的人员，请密切关注疫情发生地区公布的病例和无症状感染者流调轨迹信息和中高风险区信息。有涉疫风险的人员要立即向社区（村）、住宿宾馆和单位报告，配合落实隔离医学观察。\n八、发热病人、健康码“黄码”等人员要履行个人防护责任，主动配合健康监测和核酸检测，在未排除感染风险前不出行。\n', '<p style=\"text-indent: 0px; text-align: justify;\">近期，国内部分地区疫情频发，多地疫情出现外溢，为有效降低我市疫情输入和传播风险，洛阳市疾病预防控制中心发布疫情防控公众提示：</p><p style=\"text-indent: 0px; text-align: justify;\">一、所有入（返）洛阳人员均需提前3天向目的地社区（村居）、酒店宾馆、接待单位等所属网格进行报备，或通过“洛阳即时通系统”进行自主报备，配合做好健康码和行程码查验、核酸检测、隔离观察和健康监测等相关疫情防控措施。</p><p style=\"text-indent: 0px; text-align: justify;\">二、倡导广大群众减少跨地市出行，避免人群大范围流动引发的疫情传播扩散风险。</p><p style=\"text-indent: 0px; text-align: justify;\">三、对7天内有高风险区旅居史的人员，采取7天集中隔离医学观察；对7天内有中风险区旅居史的人员，采取7天居家隔离医学观察，如不具备居家隔离医学观察条件的，采取集中隔离医学观察。</p><p style=\"text-indent: 0px; text-align: justify;\">四、对疫情发生地出现一定范围社区传播或已实施大范围社区管控措施，基于对疫情输入风险研判结果，对近7天内来自疫情发生地所在县（市、区）的流入人员，参照中风险区旅居史人员的防控要求采取相应措施。</p><p style=\"text-indent: 0px; text-align: justify;\">五、对所有省外入（返）洛阳人员，须持有48小时内核酸检测阴性证明，抵达后进行“5天3检”，每次检测间隔24小时。推广“落地检”，按照“自愿免费即采即走，不限制流动”的原则，抵达我市后，立即进行1次核酸检测。</p><p style=\"text-indent: 0px; text-align: justify;\">六、加强重点机构场所疫情防控，坚持非必要不举办，对确需举办的培训、会展、文艺演出等大型聚集性活动，查验48小时内核酸检测阴性证明；建筑工地等人员密集型单位，查验外省（区、市）返岗人员48小时内核酸检测阴性证明；养老机构、儿童福利机构等查验探访人员48小时内核酸检测阴性证明；对进入宾馆、酒店和旅游景区等人流密集场所时，查验48小时内核酸检测阴性证明。</p><p style=\"text-indent: 0px; text-align: justify;\">七、近期有外出旅行史的人员，请密切关注疫情发生地区公布的病例和无症状感染者流调轨迹信息和中高风险区信息。有涉疫风险的人员要立即向社区（村）、住宿宾馆和单位报告，配合落实隔离医学观察。</p><p style=\"text-indent: 0px; text-align: justify;\">八、发热病人、健康码“黄码”等人员要履行个人防护责任，主动配合健康监测和核酸检测，在未排除感染风险前不出行。</p><p style=\"text-indent: 0px; text-align: justify;\"><br></p>', '', 0, 0, '行政部', '卓大', '1024创新实验室发〔2022〕字第40号', 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:46:00');
INSERT INTO `t_notice` VALUES (58, 2, '办公室消杀关键位置通知', 1, 0, '2024-01-01 20:22:23', '开展消毒消杀是杀灭病源、切断疫情传播的有效手段，是防控疫情的重要措施。为了切实将新型冠状病毒肺炎疫情防控工作落到实处，守护好辖区居民及工作人员的身体健康和生命安全，青山镇高度重视新型冠状病毒肺炎的消杀工作，将采购的防护服，防护面罩，一次性手套，口罩，84消毒液，酒精消毒液以及喷雾工具等消毒消杀物资，分发到镇级各站所各村（社区），全镇开展消杀工作。', '<p><span style=\"color: rgb(93, 93, 93); background-color: rgb(247, 247, 247);\">开展消毒消杀是杀灭病源、切断疫情传播的有效手段，是防控疫情的重要措施。为了切实将新型冠状病毒肺炎疫情防控工作落到实处，守护好辖区居民及工作人员的身体健康和生命安全，青山镇高度重视新型冠状病毒肺炎的消杀工作，将采购的防护服，防护面罩，一次性手套，口罩，84消毒液，酒精消毒液以及喷雾工具等消毒消杀物资，分发到镇级各站所各村（社区），全镇开展消杀工作。</span></p>', '', 0, 0, '行政部', '卓大', '1024创新实验室发〔2022〕字第26号', 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:47:12');
INSERT INTO `t_notice` VALUES (59, 2, '十月份人事任命通知', 1, 0, '2024-01-01 20:22:23', '1024创新实验室发〔2022〕字第36号\n1024创新实验室发〔2022〕字第36号\n1024创新实验室发〔2022〕字第36号\n1024创新实验室发〔2022〕字第36号\n1024创新实验室发〔2022〕字第36号\n1024创新实验室发〔2022〕字第36号', '<p>1024创新实验室发〔2022〕字第36号</p><p>1024创新实验室发〔2022〕字第36号</p><p>1024创新实验室发〔2022〕字第36号</p><p>1024创新实验室发〔2022〕字第36号</p><p>1024创新实验室发〔2022〕字第36号</p><p>1024创新实验室发〔2022〕字第36号</p>', '', 1, 1, '销售部', '卓大', '1024创新实验室发〔2022〕字第30号', 0, 1, '2025-09-06 14:58:14', '2022-10-22 14:50:11');
INSERT INTO `t_notice` VALUES (60, 2, '1024创新实验室 春节放假通知', 1, 0, '2024-01-01 20:22:23', '春节假期即将来临，根据国务院办公厅关于国庆节的放假安排，废纸信息网安排如下：10月1日至7日放假调休，共7天。\n衷心预祝\n国庆快乐，阖家幸福！', '<p style=\"text-indent: 0px; text-align: justify;\">国庆假期即将来临，根据国务院办公厅关于国庆节的放假安排，废纸信息网安排如下：<strong>10月1日至7日放假调休</strong>，共7天。</p><p style=\"text-indent: 0px; text-align: justify;\"><strong>衷心预祝</strong></p><p style=\"text-indent: 0px; text-align: justify;\"><strong>国庆快乐，阖家幸福！</strong></p>', '', 0, 0, '人力行政部', '卓大', '1024创新实验室发〔2022〕字第36号', 0, 1, '2024-01-08 19:02:12', '2022-10-22 14:37:57');

-- ----------------------------
-- Table structure for t_notice_type
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_type`;
CREATE TABLE `t_notice_type`  (
  `notice_type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知类型',
  `notice_type_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`notice_type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_notice_type
-- ----------------------------
INSERT INTO `t_notice_type` VALUES (1, '新闻', '2022-08-16 20:29:15', '2024-09-03 21:44:42');
INSERT INTO `t_notice_type` VALUES (2, '通知', '2022-08-16 20:29:20', '2022-08-16 20:29:20');

-- ----------------------------
-- Table structure for t_notice_view_record
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_view_record`;
CREATE TABLE `t_notice_view_record`  (
  `notice_id` bigint NOT NULL COMMENT '通知公告id',
  `backend_user_id` bigint NOT NULL COMMENT '员工id',
  `page_view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `first_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次ip',
  `first_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次用户设备等标识',
  `last_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次ip',
  `last_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次用户设备等标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`notice_id`, `backend_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知查看记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_notice_view_record
-- ----------------------------
INSERT INTO `t_notice_view_record` VALUES (59, 1, 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', NULL, NULL, '2025-09-06 14:58:14', '2025-09-06 14:58:14');

-- ----------------------------
-- Table structure for t_notice_visible_range
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_visible_range`;
CREATE TABLE `t_notice_visible_range`  (
  `notice_id` bigint NOT NULL COMMENT '资讯id',
  `data_type` tinyint NOT NULL COMMENT '数据类型1员工 2部门',
  `data_id` bigint NOT NULL COMMENT '员工or部门id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE INDEX `uk_notice_data`(`notice_id` ASC, `data_type` ASC, `data_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知可见范围' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_notice_visible_range
-- ----------------------------
INSERT INTO `t_notice_visible_range` VALUES (63, 1, 63, '2024-08-09 10:40:32');

-- ----------------------------
-- Table structure for t_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operate_log`;
CREATE TABLE `t_operate_log`  (
  `operate_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `operate_user_id` bigint NOT NULL COMMENT '用户id',
  `operate_user_type` int NOT NULL COMMENT '用户类型',
  `operate_user_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名称',
  `module` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作模块',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '操作内容',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求路径',
  `method` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求方法',
  `param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  `response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回值',
  `ip` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ip',
  `ip_region` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ip地区',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求user-agent',
  `success_flag` tinyint NULL DEFAULT NULL COMMENT '请求结果 0失败 1成功',
  `fail_reason` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '失败原因',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`operate_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4602 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_operate_log
-- ----------------------------
INSERT INTO `t_operate_log` VALUES (4499, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 12:57:17', '2025-09-04 12:57:17');
INSERT INTO `t_operate_log` VALUES (4500, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 12:57:17', '2025-09-04 12:57:17');
INSERT INTO `t_operate_log` VALUES (4501, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 13:30:53', '2025-09-04 13:30:53');
INSERT INTO `t_operate_log` VALUES (4502, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 13:30:53', '2025-09-04 13:30:53');
INSERT INTO `t_operate_log` VALUES (4503, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 15:08:46', '2025-09-04 15:08:46');
INSERT INTO `t_operate_log` VALUES (4504, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 15:08:46', '2025-09-04 15:08:46');
INSERT INTO `t_operate_log` VALUES (4505, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 15:10:31', '2025-09-04 15:10:31');
INSERT INTO `t_operate_log` VALUES (4506, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 15:10:31', '2025-09-04 15:10:31');
INSERT INTO `t_operate_log` VALUES (4507, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:35:09', '2025-09-05 14:35:09');
INSERT INTO `t_operate_log` VALUES (4508, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:35:09', '2025-09-05 14:35:09');
INSERT INTO `t_operate_log` VALUES (4509, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:47:09', '2025-09-05 14:47:09');
INSERT INTO `t_operate_log` VALUES (4510, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:47:09', '2025-09-05 14:47:09');
INSERT INTO `t_operate_log` VALUES (4511, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:50:36', '2025-09-05 14:50:36');
INSERT INTO `t_operate_log` VALUES (4512, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:50:36', '2025-09-05 14:50:36');
INSERT INTO `t_operate_log` VALUES (4513, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:00:12', '2025-09-05 15:00:12');
INSERT INTO `t_operate_log` VALUES (4514, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:00:12', '2025-09-05 15:00:12');
INSERT INTO `t_operate_log` VALUES (4515, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:28:54', '2025-09-05 15:28:54');
INSERT INTO `t_operate_log` VALUES (4516, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:29:12', '2025-09-05 15:29:12');
INSERT INTO `t_operate_log` VALUES (4517, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:31:57', '2025-09-05 15:31:57');
INSERT INTO `t_operate_log` VALUES (4518, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:31:57', '2025-09-05 15:31:57');
INSERT INTO `t_operate_log` VALUES (4519, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:33:21', '2025-09-05 15:33:21');
INSERT INTO `t_operate_log` VALUES (4520, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:39:40', '2025-09-05 15:39:40');
INSERT INTO `t_operate_log` VALUES (4521, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:41:55', '2025-09-05 15:41:55');
INSERT INTO `t_operate_log` VALUES (4522, 1, 1, '管理员', 'OA办公-企业', '查询企业详情 @author 开云', '/oa/enterprise/get/2', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.getDetail', '[2]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:42:05', '2025-09-05 15:42:05');
INSERT INTO `t_operate_log` VALUES (4523, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:18:52', '2025-09-05 16:18:52');
INSERT INTO `t_operate_log` VALUES (4524, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:18:56', '2025-09-05 16:18:56');
INSERT INTO `t_operate_log` VALUES (4525, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:18:56', '2025-09-05 16:18:56');
INSERT INTO `t_operate_log` VALUES (4526, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:19:07', '2025-09-05 16:19:07');
INSERT INTO `t_operate_log` VALUES (4527, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:34:26', '2025-09-05 16:34:26');
INSERT INTO `t_operate_log` VALUES (4528, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:39:04', '2025-09-05 18:39:04');
INSERT INTO `t_operate_log` VALUES (4529, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:39:04', '2025-09-05 18:39:04');
INSERT INTO `t_operate_log` VALUES (4530, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:39:07', '2025-09-05 18:39:07');
INSERT INTO `t_operate_log` VALUES (4531, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:44:40', '2025-09-05 18:44:40');
INSERT INTO `t_operate_log` VALUES (4532, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:44:41', '2025-09-05 18:44:41');
INSERT INTO `t_operate_log` VALUES (4533, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:44:41', '2025-09-05 18:44:41');
INSERT INTO `t_operate_log` VALUES (4534, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:01:24', '2025-09-05 19:01:24');
INSERT INTO `t_operate_log` VALUES (4535, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:01:24', '2025-09-05 19:01:24');
INSERT INTO `t_operate_log` VALUES (4536, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:02:17', '2025-09-05 19:02:17');
INSERT INTO `t_operate_log` VALUES (4537, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:02:17', '2025-09-05 19:02:17');
INSERT INTO `t_operate_log` VALUES (4538, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:10:49', '2025-09-05 19:10:49');
INSERT INTO `t_operate_log` VALUES (4539, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:10:49', '2025-09-05 19:10:49');
INSERT INTO `t_operate_log` VALUES (4540, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:24:43', '2025-09-05 19:24:43');
INSERT INTO `t_operate_log` VALUES (4541, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:24:43', '2025-09-05 19:24:43');
INSERT INTO `t_operate_log` VALUES (4542, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:36:09', '2025-09-05 19:36:09');
INSERT INTO `t_operate_log` VALUES (4543, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:36:09', '2025-09-05 19:36:09');
INSERT INTO `t_operate_log` VALUES (4544, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:36:19', '2025-09-05 19:36:19');
INSERT INTO `t_operate_log` VALUES (4545, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:36:19', '2025-09-05 19:36:19');
INSERT INTO `t_operate_log` VALUES (4546, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:09', '2025-09-06 14:58:09');
INSERT INTO `t_operate_log` VALUES (4547, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:10', '2025-09-06 14:58:10');
INSERT INTO `t_operate_log` VALUES (4548, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查看详情 @author 卓大', '/oa/notice/employee/view/59', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.view', '[59]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:14', '2025-09-06 14:58:14');
INSERT INTO `t_operate_log` VALUES (4549, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询 查看记录 @author 卓大', '/oa/notice/employee/queryViewRecord', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryViewRecord', '[{\"keywords\":\"\",\"noticeId\":59,\"pageNum\":1,\"pageSize\":10}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:14', '2025-09-06 14:58:14');
INSERT INTO `t_operate_log` VALUES (4550, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:30', '2025-09-06 14:58:30');
INSERT INTO `t_operate_log` VALUES (4551, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:30', '2025-09-06 14:58:30');
INSERT INTO `t_operate_log` VALUES (4552, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 15:32:59', '2025-09-06 15:32:59');
INSERT INTO `t_operate_log` VALUES (4553, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 15:32:59', '2025-09-06 15:32:59');
INSERT INTO `t_operate_log` VALUES (4554, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-07 12:18:56', '2025-09-07 12:18:56');
INSERT INTO `t_operate_log` VALUES (4555, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-07 12:18:56', '2025-09-07 12:18:56');
INSERT INTO `t_operate_log` VALUES (4556, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-07 15:34:53', '2025-09-07 15:34:53');
INSERT INTO `t_operate_log` VALUES (4557, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-07 15:34:53', '2025-09-07 15:34:53');
INSERT INTO `t_operate_log` VALUES (4558, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-08 15:12:55', '2025-09-08 15:12:55');
INSERT INTO `t_operate_log` VALUES (4559, 1, 1, '管理员', 'OA办公-通知公告', '【员工】通知公告-查询全部 @author 卓大', '/oa/notice/employee/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryEmployeeNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-08 15:12:55', '2025-09-08 15:12:55');
INSERT INTO `t_operate_log` VALUES (4560, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/employee/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', NULL, '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 0, 'org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): dao.notice.system.module.admin.com.akkkka.NoticeDao.queryBackendUserNotice\r\n	at org.apache.ibatis.binding.MapperMethod$SqlCommand.<init>(MapperMethod.java:229)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.<init>(MybatisMapperMethod.java:50)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.lambda$cachedInvoker$0(MybatisMapperProxy.java:103)\r\n	at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1708)\r\n	at org.apache.ibatis.util.MapUtil.computeIfAbsent(MapUtil.java:36)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.cachedInvoker(MybatisMapperProxy.java:101)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:93)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy167.queryBackendUserNotice(Unknown Source)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService.queryList(NoticeBackendUserService.java:59)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService$$SpringCGLIB$$0.queryList(<generated>)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice(NoticeController.java:132)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:64)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor.invoke(AfterReturningAdviceInterceptor.java:57)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController$$SpringCGLIB$$0.queryBackendUserNotice(<generated>)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\r\n	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)\r\n	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\r\n	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\r\n	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\r\n	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)\r\n	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.servlet.resource.ResourceUrlEncodingFilter.doFilter(ResourceUrlEncodingFilter.java:66)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at com.alibaba.druid.support.jakarta.WebStatFilter.doFilter(WebStatFilter.java:73)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\r\n	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\r\n	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\r\n	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\r\n	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\r\n	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)\r\n	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\r\n	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\r\n	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\r\n	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\r\n	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1769)\r\n	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\r\n	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\r\n	at java.base/java.lang.Thread.run(Thread.java:840)\r\n', '2025-09-15 16:30:54', '2025-09-15 16:30:54');
INSERT INTO `t_operate_log` VALUES (4561, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/employee/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', NULL, '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 0, 'org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): dao.notice.system.module.admin.com.akkkka.NoticeDao.queryBackendUserNotice\r\n	at org.apache.ibatis.binding.MapperMethod$SqlCommand.<init>(MapperMethod.java:229)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.<init>(MybatisMapperMethod.java:50)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.lambda$cachedInvoker$0(MybatisMapperProxy.java:103)\r\n	at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1708)\r\n	at org.apache.ibatis.util.MapUtil.computeIfAbsent(MapUtil.java:36)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.cachedInvoker(MybatisMapperProxy.java:101)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:93)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy167.queryBackendUserNotice(Unknown Source)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService.queryList(NoticeBackendUserService.java:59)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService$$SpringCGLIB$$0.queryList(<generated>)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice(NoticeController.java:132)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:64)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor.invoke(AfterReturningAdviceInterceptor.java:57)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController$$SpringCGLIB$$0.queryBackendUserNotice(<generated>)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\r\n	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)\r\n	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\r\n	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\r\n	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\r\n	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)\r\n	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.servlet.resource.ResourceUrlEncodingFilter.doFilter(ResourceUrlEncodingFilter.java:66)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at com.alibaba.druid.support.jakarta.WebStatFilter.doFilter(WebStatFilter.java:73)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\r\n	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\r\n	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\r\n	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\r\n	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\r\n	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)\r\n	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\r\n	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\r\n	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\r\n	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\r\n	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1769)\r\n	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\r\n	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\r\n	at java.base/java.lang.Thread.run(Thread.java:840)\r\n', '2025-09-15 16:30:54', '2025-09-15 16:30:54');
INSERT INTO `t_operate_log` VALUES (4562, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', NULL, '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 0, 'org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): dao.notice.system.module.admin.com.akkkka.NoticeDao.queryBackendUserNotice\r\n	at org.apache.ibatis.binding.MapperMethod$SqlCommand.<init>(MapperMethod.java:229)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.<init>(MybatisMapperMethod.java:50)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.lambda$cachedInvoker$0(MybatisMapperProxy.java:103)\r\n	at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1708)\r\n	at org.apache.ibatis.util.MapUtil.computeIfAbsent(MapUtil.java:36)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.cachedInvoker(MybatisMapperProxy.java:101)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:93)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy167.queryBackendUserNotice(Unknown Source)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService.queryList(NoticeBackendUserService.java:59)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService$$SpringCGLIB$$0.queryList(<generated>)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice(NoticeController.java:132)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:64)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor.invoke(AfterReturningAdviceInterceptor.java:57)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController$$SpringCGLIB$$0.queryBackendUserNotice(<generated>)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\r\n	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)\r\n	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\r\n	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\r\n	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\r\n	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)\r\n	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.servlet.resource.ResourceUrlEncodingFilter.doFilter(ResourceUrlEncodingFilter.java:66)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at com.alibaba.druid.support.jakarta.WebStatFilter.doFilter(WebStatFilter.java:73)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\r\n	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\r\n	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\r\n	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\r\n	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\r\n	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)\r\n	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\r\n	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\r\n	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\r\n	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\r\n	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1769)\r\n	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\r\n	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\r\n	at java.base/java.lang.Thread.run(Thread.java:840)\r\n', '2025-09-15 16:33:18', '2025-09-15 16:33:18');
INSERT INTO `t_operate_log` VALUES (4563, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', NULL, '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 0, 'org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): dao.notice.system.module.admin.com.akkkka.NoticeDao.queryBackendUserNotice\r\n	at org.apache.ibatis.binding.MapperMethod$SqlCommand.<init>(MapperMethod.java:229)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.<init>(MybatisMapperMethod.java:50)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.lambda$cachedInvoker$0(MybatisMapperProxy.java:103)\r\n	at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1708)\r\n	at org.apache.ibatis.util.MapUtil.computeIfAbsent(MapUtil.java:36)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.cachedInvoker(MybatisMapperProxy.java:101)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:93)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy167.queryBackendUserNotice(Unknown Source)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService.queryList(NoticeBackendUserService.java:59)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService$$SpringCGLIB$$0.queryList(<generated>)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice(NoticeController.java:132)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:64)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor.invoke(AfterReturningAdviceInterceptor.java:57)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController$$SpringCGLIB$$0.queryBackendUserNotice(<generated>)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\r\n	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)\r\n	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\r\n	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\r\n	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\r\n	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)\r\n	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.servlet.resource.ResourceUrlEncodingFilter.doFilter(ResourceUrlEncodingFilter.java:66)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at com.alibaba.druid.support.jakarta.WebStatFilter.doFilter(WebStatFilter.java:73)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\r\n	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\r\n	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\r\n	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\r\n	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\r\n	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)\r\n	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\r\n	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\r\n	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\r\n	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\r\n	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1769)\r\n	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\r\n	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\r\n	at java.base/java.lang.Thread.run(Thread.java:840)\r\n', '2025-09-15 16:33:18', '2025-09-15 16:33:18');
INSERT INTO `t_operate_log` VALUES (4564, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', NULL, '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 0, 'org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): dao.notice.system.module.admin.com.akkkka.NoticeDao.queryBackendUserNotice\r\n	at org.apache.ibatis.binding.MapperMethod$SqlCommand.<init>(MapperMethod.java:229)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.<init>(MybatisMapperMethod.java:50)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.lambda$cachedInvoker$0(MybatisMapperProxy.java:103)\r\n	at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1708)\r\n	at org.apache.ibatis.util.MapUtil.computeIfAbsent(MapUtil.java:36)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.cachedInvoker(MybatisMapperProxy.java:101)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:93)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy167.queryBackendUserNotice(Unknown Source)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService.queryList(NoticeBackendUserService.java:59)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService$$SpringCGLIB$$0.queryList(<generated>)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice(NoticeController.java:132)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:64)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor.invoke(AfterReturningAdviceInterceptor.java:57)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController$$SpringCGLIB$$0.queryBackendUserNotice(<generated>)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\r\n	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)\r\n	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\r\n	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\r\n	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\r\n	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)\r\n	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.servlet.resource.ResourceUrlEncodingFilter.doFilter(ResourceUrlEncodingFilter.java:66)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at com.alibaba.druid.support.jakarta.WebStatFilter.doFilter(WebStatFilter.java:73)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\r\n	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\r\n	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\r\n	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\r\n	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\r\n	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)\r\n	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\r\n	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\r\n	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\r\n	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\r\n	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1769)\r\n	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\r\n	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\r\n	at java.base/java.lang.Thread.run(Thread.java:840)\r\n', '2025-09-15 17:00:25', '2025-09-15 17:00:25');
INSERT INTO `t_operate_log` VALUES (4565, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', NULL, '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 0, 'org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): dao.notice.system.module.admin.com.akkkka.NoticeDao.queryBackendUserNotice\r\n	at org.apache.ibatis.binding.MapperMethod$SqlCommand.<init>(MapperMethod.java:229)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.<init>(MybatisMapperMethod.java:50)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.lambda$cachedInvoker$0(MybatisMapperProxy.java:103)\r\n	at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1708)\r\n	at org.apache.ibatis.util.MapUtil.computeIfAbsent(MapUtil.java:36)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.cachedInvoker(MybatisMapperProxy.java:101)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:93)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy167.queryBackendUserNotice(Unknown Source)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService.queryList(NoticeBackendUserService.java:59)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService$$SpringCGLIB$$0.queryList(<generated>)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice(NoticeController.java:132)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:64)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor.invoke(AfterReturningAdviceInterceptor.java:57)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController$$SpringCGLIB$$0.queryBackendUserNotice(<generated>)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\r\n	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)\r\n	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\r\n	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\r\n	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\r\n	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)\r\n	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.servlet.resource.ResourceUrlEncodingFilter.doFilter(ResourceUrlEncodingFilter.java:66)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at com.alibaba.druid.support.jakarta.WebStatFilter.doFilter(WebStatFilter.java:73)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\r\n	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\r\n	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\r\n	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\r\n	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\r\n	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)\r\n	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\r\n	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\r\n	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\r\n	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\r\n	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1769)\r\n	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\r\n	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\r\n	at java.base/java.lang.Thread.run(Thread.java:840)\r\n', '2025-09-15 17:00:25', '2025-09-15 17:00:25');
INSERT INTO `t_operate_log` VALUES (4566, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', NULL, '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 0, 'org.mybatis.spring.MyBatisSystemException: \r\n### Error querying database.  Cause: org.apache.ibatis.binding.BindingException: Parameter \'requestEmployeeId\' not found. Available parameters are [param5, requestBackendUserId, deletedFlag, query, page, param3, param4, param1, backendUserDataType, param2]\r\n### Cause: org.apache.ibatis.binding.BindingException: Parameter \'requestEmployeeId\' not found. Available parameters are [param5, requestBackendUserId, deletedFlag, query, page, param3, param4, param1, backendUserDataType, param2]\r\n	at org.mybatis.spring.MyBatisExceptionTranslator.translateExceptionIfPossible(MyBatisExceptionTranslator.java:99)\r\n	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:347)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy112.selectList(Unknown Source)\r\n	at org.mybatis.spring.SqlSessionTemplate.selectList(SqlSessionTemplate.java:194)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.executeForMany(MybatisMapperMethod.java:164)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.execute(MybatisMapperMethod.java:77)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy$PlainMethodInvoker.invoke(MybatisMapperProxy.java:156)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:93)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy167.queryBackendUserNotice(Unknown Source)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService.queryList(NoticeBackendUserService.java:59)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService$$SpringCGLIB$$0.queryList(<generated>)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice(NoticeController.java:132)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:64)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor.invoke(AfterReturningAdviceInterceptor.java:57)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController$$SpringCGLIB$$0.queryBackendUserNotice(<generated>)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\r\n	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)\r\n	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\r\n	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\r\n	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\r\n	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)\r\n	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.servlet.resource.ResourceUrlEncodingFilter.doFilter(ResourceUrlEncodingFilter.java:66)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at com.alibaba.druid.support.jakarta.WebStatFilter.doFilter(WebStatFilter.java:73)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\r\n	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\r\n	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\r\n	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\r\n	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\r\n	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)\r\n	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\r\n	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\r\n	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\r\n	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\r\n	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1769)\r\n	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\r\n	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\r\n	at java.base/java.lang.Thread.run(Thread.java:840)\r\nCaused by: org.apache.ibatis.binding.BindingException: Parameter \'requestEmployeeId\' not found. Available parameters are [param5, requestBackendUserId, deletedFlag, query, page, param3, param4, param1, backendUserDataType, param2]\r\n	at org.apache.ibatis.binding.MapperMethod$ParamMap.get(MapperMethod.java:210)\r\n	at org.apache.ibatis.reflection.wrapper.MapWrapper.get(MapWrapper.java:46)\r\n	at org.apache.ibatis.reflection.MetaObject.getValue(MetaObject.java:115)\r\n	at org.apache.ibatis.executor.BaseExecutor.createCacheKey(BaseExecutor.java:225)\r\n	at org.apache.ibatis.executor.CachingExecutor.createCacheKey(CachingExecutor.java:149)\r\n	at com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor.intercept(MybatisPlusInterceptor.java:80)\r\n	at org.apache.ibatis.plugin.Plugin.invoke(Plugin.java:59)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy125.query(Unknown Source)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.apache.ibatis.plugin.Invocation.proceed(Invocation.java:61)\r\n	at datascope.system.module.admin.com.akkkka.MyBatisPlugin.intercept(MyBatisPlugin.java:64)\r\n	at org.apache.ibatis.plugin.Plugin.invoke(Plugin.java:59)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy125.query(Unknown Source)\r\n	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:154)\r\n	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:147)\r\n	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:142)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:333)\r\n	... 105 more\r\n', '2025-09-15 17:03:46', '2025-09-15 17:03:46');
INSERT INTO `t_operate_log` VALUES (4567, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', NULL, '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 0, 'org.mybatis.spring.MyBatisSystemException: \r\n### Error querying database.  Cause: org.apache.ibatis.binding.BindingException: Parameter \'requestEmployeeId\' not found. Available parameters are [param5, requestBackendUserId, deletedFlag, query, page, param3, param4, param1, backendUserDataType, param2]\r\n### Cause: org.apache.ibatis.binding.BindingException: Parameter \'requestEmployeeId\' not found. Available parameters are [param5, requestBackendUserId, deletedFlag, query, page, param3, param4, param1, backendUserDataType, param2]\r\n	at org.mybatis.spring.MyBatisExceptionTranslator.translateExceptionIfPossible(MyBatisExceptionTranslator.java:99)\r\n	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:347)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy112.selectList(Unknown Source)\r\n	at org.mybatis.spring.SqlSessionTemplate.selectList(SqlSessionTemplate.java:194)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.executeForMany(MybatisMapperMethod.java:164)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperMethod.execute(MybatisMapperMethod.java:77)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy$PlainMethodInvoker.invoke(MybatisMapperProxy.java:156)\r\n	at com.baomidou.mybatisplus.core.override.MybatisMapperProxy.invoke(MybatisMapperProxy.java:93)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy167.queryBackendUserNotice(Unknown Source)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService.queryList(NoticeBackendUserService.java:59)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at service.notice.system.module.admin.com.akkkka.NoticeBackendUserService$$SpringCGLIB$$0.queryList(<generated>)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice(NoticeController.java:132)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:360)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)\r\n	at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:64)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor.invoke(AfterReturningAdviceInterceptor.java:57)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:173)\r\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)\r\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)\r\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:728)\r\n	at controller.notice.system.module.admin.com.akkkka.NoticeController$$SpringCGLIB$$0.queryBackendUserNotice(<generated>)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258)\r\n	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191)\r\n	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)\r\n	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)\r\n	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)\r\n	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)\r\n	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)\r\n	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:914)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)\r\n	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:91)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.servlet.resource.ResourceUrlEncodingFilter.doFilter(ResourceUrlEncodingFilter.java:66)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at com.alibaba.druid.support.jakarta.WebStatFilter.doFilter(WebStatFilter.java:73)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\r\n	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)\r\n	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)\r\n	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)\r\n	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\r\n	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)\r\n	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:116)\r\n	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\r\n	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:666)\r\n	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)\r\n	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:398)\r\n	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\r\n	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:903)\r\n	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1769)\r\n	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1189)\r\n	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:658)\r\n	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)\r\n	at java.base/java.lang.Thread.run(Thread.java:840)\r\nCaused by: org.apache.ibatis.binding.BindingException: Parameter \'requestEmployeeId\' not found. Available parameters are [param5, requestBackendUserId, deletedFlag, query, page, param3, param4, param1, backendUserDataType, param2]\r\n	at org.apache.ibatis.binding.MapperMethod$ParamMap.get(MapperMethod.java:210)\r\n	at org.apache.ibatis.reflection.wrapper.MapWrapper.get(MapWrapper.java:46)\r\n	at org.apache.ibatis.reflection.MetaObject.getValue(MetaObject.java:115)\r\n	at org.apache.ibatis.executor.BaseExecutor.createCacheKey(BaseExecutor.java:225)\r\n	at org.apache.ibatis.executor.CachingExecutor.createCacheKey(CachingExecutor.java:149)\r\n	at com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor.intercept(MybatisPlusInterceptor.java:80)\r\n	at org.apache.ibatis.plugin.Plugin.invoke(Plugin.java:59)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy125.query(Unknown Source)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.apache.ibatis.plugin.Invocation.proceed(Invocation.java:61)\r\n	at datascope.system.module.admin.com.akkkka.MyBatisPlugin.intercept(MyBatisPlugin.java:64)\r\n	at org.apache.ibatis.plugin.Plugin.invoke(Plugin.java:59)\r\n	at jdk.proxy2/jdk.proxy2.$Proxy125.query(Unknown Source)\r\n	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:154)\r\n	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:147)\r\n	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:142)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:333)\r\n	... 105 more\r\n', '2025-09-15 17:03:46', '2025-09-15 17:03:46');
INSERT INTO `t_operate_log` VALUES (4568, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-15 17:05:21', '2025-09-15 17:05:21');
INSERT INTO `t_operate_log` VALUES (4569, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-15 17:05:21', '2025-09-15 17:05:21');
INSERT INTO `t_operate_log` VALUES (4570, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 13:46:47', '2025-09-16 13:46:47');
INSERT INTO `t_operate_log` VALUES (4571, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 13:46:47', '2025-09-16 13:46:47');
INSERT INTO `t_operate_log` VALUES (4572, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 13:48:24', '2025-09-16 13:48:24');
INSERT INTO `t_operate_log` VALUES (4573, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 13:48:24', '2025-09-16 13:48:24');
INSERT INTO `t_operate_log` VALUES (4574, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 15:58:01', '2025-09-16 15:58:01');
INSERT INTO `t_operate_log` VALUES (4575, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 15:58:01', '2025-09-16 15:58:01');
INSERT INTO `t_operate_log` VALUES (4576, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 16:21:24', '2025-09-16 16:21:24');
INSERT INTO `t_operate_log` VALUES (4577, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 16:21:24', '2025-09-16 16:21:24');
INSERT INTO `t_operate_log` VALUES (4578, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 16:48:45', '2025-09-16 16:48:45');
INSERT INTO `t_operate_log` VALUES (4579, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 16:48:45', '2025-09-16 16:48:45');
INSERT INTO `t_operate_log` VALUES (4580, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:27:52', '2025-09-16 18:27:52');
INSERT INTO `t_operate_log` VALUES (4581, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:27:52', '2025-09-16 18:27:52');
INSERT INTO `t_operate_log` VALUES (4582, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:28:46', '2025-09-16 18:28:46');
INSERT INTO `t_operate_log` VALUES (4583, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:28:46', '2025-09-16 18:28:46');
INSERT INTO `t_operate_log` VALUES (4584, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:30:55', '2025-09-16 18:30:55');
INSERT INTO `t_operate_log` VALUES (4585, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:30:55', '2025-09-16 18:30:55');
INSERT INTO `t_operate_log` VALUES (4586, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:31:23', '2025-09-16 18:31:23');
INSERT INTO `t_operate_log` VALUES (4587, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:31:23', '2025-09-16 18:31:23');
INSERT INTO `t_operate_log` VALUES (4588, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:37:03', '2025-09-16 18:37:03');
INSERT INTO `t_operate_log` VALUES (4589, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:37:03', '2025-09-16 18:37:03');
INSERT INTO `t_operate_log` VALUES (4590, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:53:59', '2025-09-16 18:53:59');
INSERT INTO `t_operate_log` VALUES (4591, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:53:59', '2025-09-16 18:53:59');
INSERT INTO `t_operate_log` VALUES (4592, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:54:11', '2025-09-16 18:54:11');
INSERT INTO `t_operate_log` VALUES (4593, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 18:54:11', '2025-09-16 18:54:11');
INSERT INTO `t_operate_log` VALUES (4594, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 19:11:56', '2025-09-16 19:11:56');
INSERT INTO `t_operate_log` VALUES (4595, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-16 19:11:56', '2025-09-16 19:11:56');
INSERT INTO `t_operate_log` VALUES (4596, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-19 10:11:25', '2025-09-19 10:11:25');
INSERT INTO `t_operate_log` VALUES (4597, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-19 10:11:25', '2025-09-19 10:11:25');
INSERT INTO `t_operate_log` VALUES (4598, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-19 14:03:52', '2025-09-19 14:03:52');
INSERT INTO `t_operate_log` VALUES (4599, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-19 14:03:52', '2025-09-19 14:03:52');
INSERT INTO `t_operate_log` VALUES (4600, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:143.0) Gecko/20100101 Firefox/143.0', 1, NULL, '2025-09-23 08:30:52', '2025-09-23 08:30:52');
INSERT INTO `t_operate_log` VALUES (4601, 1, 1, 'akkkka', '系统-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/notice/backend-user/query', 'controller.notice.system.module.admin.com.akkkka.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:143.0) Gecko/20100101 Firefox/143.0', 1, NULL, '2025-09-23 08:30:52', '2025-09-23 08:30:52');

-- ----------------------------
-- Table structure for t_password_log
-- ----------------------------
DROP TABLE IF EXISTS `t_password_log`;
CREATE TABLE `t_password_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_type` tinyint NOT NULL COMMENT '用户类型',
  `old_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '旧密码',
  `new_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新密码',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_and_type_index`(`user_id` ASC, `user_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '密码修改记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_password_log
-- ----------------------------

-- ----------------------------
-- Table structure for t_reload_item
-- ----------------------------
DROP TABLE IF EXISTS `t_reload_item`;
CREATE TABLE `t_reload_item`  (
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项名称',
  `args` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数 可选',
  `identification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '运行标识',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tag`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'reload项目' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_reload_item
-- ----------------------------
INSERT INTO `t_reload_item` VALUES ('system_config', '4', '234', '2024-08-13 14:14:30', '2019-04-18 11:48:27');

-- ----------------------------
-- Table structure for t_reload_result
-- ----------------------------
DROP TABLE IF EXISTS `t_reload_result`;
CREATE TABLE `t_reload_result`  (
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `identification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '运行标识',
  `args` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `result` tinyint UNSIGNED NOT NULL COMMENT '是否成功 ',
  `exception` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'reload结果' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_reload_result
-- ----------------------------

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `role_code_uni`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES (1, 'admin', '', '', '2025-09-12 20:31:11', '2025-09-12 20:31:11');
INSERT INTO `t_role` VALUES (2, 'organization', NULL, '校园组织运营', '2025-09-12 20:33:02', '2025-09-12 20:33:02');

-- ----------------------------
-- Table structure for t_role_data_scope
-- ----------------------------
DROP TABLE IF EXISTS `t_role_data_scope`;
CREATE TABLE `t_role_data_scope`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_scope_type` int NOT NULL COMMENT '数据范围类型',
  `view_type` int NOT NULL COMMENT '数据可见范围类型',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色的数据范围' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role_data_scope
-- ----------------------------
INSERT INTO `t_role_data_scope` VALUES (69, 1, 10, 1, '2025-09-16 18:28:29', '2025-09-16 18:28:29');

-- ----------------------------
-- Table structure for t_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_role_menu`;
CREATE TABLE `t_role_menu`  (
  `role_menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `menu_id` bigint NOT NULL COMMENT '菜单id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_menu_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1134 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-菜单\n' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role_menu
-- ----------------------------
INSERT INTO `t_role_menu` VALUES (1018, 59, 307, '2025-09-08 15:16:47', '2025-09-08 15:16:47');
INSERT INTO `t_role_menu` VALUES (1019, 59, 308, '2025-09-08 15:16:47', '2025-09-08 15:16:47');
INSERT INTO `t_role_menu` VALUES (1020, 59, 301, '2025-09-08 15:16:47', '2025-09-08 15:16:47');
INSERT INTO `t_role_menu` VALUES (1021, 2, 301, '2025-09-16 13:48:36', '2025-09-16 13:48:36');
INSERT INTO `t_role_menu` VALUES (1022, 2, 307, '2025-09-16 13:48:36', '2025-09-16 13:48:36');
INSERT INTO `t_role_menu` VALUES (1023, 2, 308, '2025-09-16 13:48:36', '2025-09-16 13:48:36');
INSERT INTO `t_role_menu` VALUES (1050, 1, 50, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1051, 1, 26, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1052, 1, 40, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1053, 1, 105, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1054, 1, 106, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1055, 1, 109, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1056, 1, 163, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1057, 1, 164, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1058, 1, 199, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1059, 1, 110, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1060, 1, 159, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1061, 1, 160, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1062, 1, 161, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1063, 1, 162, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1067, 1, 133, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1068, 1, 117, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1069, 1, 156, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1070, 1, 193, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1071, 1, 200, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1073, 1, 45, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1075, 1, 46, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1076, 1, 91, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1077, 1, 92, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1078, 1, 93, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1080, 1, 95, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1081, 1, 96, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1085, 1, 76, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1086, 1, 97, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1087, 1, 98, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1088, 1, 99, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1089, 1, 100, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1090, 1, 101, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1091, 1, 102, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1092, 1, 103, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1093, 1, 104, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1094, 1, 213, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1095, 1, 214, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1096, 1, 143, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1097, 1, 203, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1098, 1, 215, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1099, 1, 218, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1100, 1, 147, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1101, 1, 170, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1102, 1, 171, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1103, 1, 168, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1104, 1, 169, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1105, 1, 202, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1106, 1, 201, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1107, 1, 148, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1108, 1, 152, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1109, 1, 190, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1110, 1, 191, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1111, 1, 192, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1112, 1, 198, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1113, 1, 207, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1114, 1, 111, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1115, 1, 206, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1116, 1, 81, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1117, 1, 204, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1118, 1, 205, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1119, 1, 122, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1120, 1, 301, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1121, 1, 307, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1122, 1, 308, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1123, 1, 233, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1124, 1, 234, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1125, 1, 250, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1126, 1, 251, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1127, 1, 221, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1128, 1, 229, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1129, 1, 230, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1130, 1, 231, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1131, 1, 232, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1132, 1, 300, '2025-09-16 18:28:23', '2025-09-16 18:28:23');
INSERT INTO `t_role_menu` VALUES (1133, 1, 151, '2025-09-16 18:28:23', '2025-09-16 18:28:23');

-- ----------------------------
-- Table structure for t_role_user
-- ----------------------------
DROP TABLE IF EXISTS `t_role_user`;
CREATE TABLE `t_role_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色id',
  `user_id` bigint NOT NULL COMMENT '后台用户id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_employee`(`role_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 342 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色员工功能表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role_user
-- ----------------------------
INSERT INTO `t_role_user` VALUES (325, 1, 1, '2025-09-12 20:53:49', '2022-10-19 20:25:26');
INSERT INTO `t_role_user` VALUES (329, 34, 72, '2022-11-05 10:56:54', '2022-11-05 10:56:54');
INSERT INTO `t_role_user` VALUES (330, 36, 72, '2022-11-05 10:56:54', '2022-11-05 10:56:54');
INSERT INTO `t_role_user` VALUES (333, 1, 44, '2023-10-07 18:53:29', '2023-10-07 18:53:29');
INSERT INTO `t_role_user` VALUES (334, 1, 47, '2023-10-07 18:55:00', '2023-10-07 18:55:00');
INSERT INTO `t_role_user` VALUES (341, 1, 48, '2024-09-02 23:03:28', '2024-09-02 23:03:28');

-- ----------------------------
-- Table structure for t_smart_job
-- ----------------------------
DROP TABLE IF EXISTS `t_smart_job`;
CREATE TABLE `t_smart_job`  (
  `job_id` int NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `job_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_class` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务执行类',
  `trigger_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发类型',
  `trigger_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发配置',
  `enabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否开启',
  `param` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数',
  `last_execute_time` datetime NULL DEFAULT NULL COMMENT '最后一次执行时间',
  `last_execute_log_id` int NULL DEFAULT NULL COMMENT '最后一次执行记录id',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `update_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`job_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务配置 @listen' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_smart_job
-- ----------------------------
INSERT INTO `t_smart_job` VALUES (1, '示例任务1', 'com.akkkka.module.support.job.sample.SmartJobSample1', 'cron', '10 15 0/1 * * *', 1, '执行示例任务1', '2025-09-19 21:32:59', 8746, 1, '执行示例任务1', 0, '管理员', '2024-06-17 20:00:46', '2025-09-19 21:32:59');
INSERT INTO `t_smart_job` VALUES (2, '示例任务2', 'com.akkkka.module.support.job.sample.SmartJobSample2', 'fixed_delay', '120', 1, '执行示例任务2', '2025-09-23 09:05:11', 8757, 2, '执行示例任务2', 0, '管理员', '2024-06-18 20:45:35', '2025-09-23 09:05:10');
INSERT INTO `t_smart_job` VALUES (4, '活动状态定时推进', 'com.akkkka.admin.module.business.funcampus.activityWithSchedule.job.ActivityStatusUpdateJob', 'fixed_delay', '60', 1, '', NULL, NULL, 1, '按时间表推进活动状态：0等待报名->1报名中->2报名结束->3进行中->4结束', 0, '管理员', '2025-09-07 15:37:29', '2025-09-07 15:38:57');
INSERT INTO `t_smart_job` VALUES (5, '活动状态扫描', 'com.akkkka.admin.module.business.funcampus.activityWithSchedule.job.ActivityStatusScanJob', 'fixed_delay', '3600', 1, '', NULL, NULL, 2, '每小时重建当天关键活动名单缓存（大循环）；缓存只做提示，失效由更新任务回源兜底', 0, '管理员', '2025-09-21 10:00:00', '2025-09-21 10:00:00');

-- ----------------------------
-- Table structure for t_smart_job_log
-- ----------------------------
DROP TABLE IF EXISTS `t_smart_job_log`;
CREATE TABLE `t_smart_job_log`  (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `job_id` int NOT NULL COMMENT '任务id',
  `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行参数',
  `success_flag` tinyint(1) NOT NULL COMMENT '是否成功',
  `execute_start_time` datetime NOT NULL COMMENT '执行开始时间',
  `execute_time_millis` int NULL DEFAULT NULL COMMENT '执行时长',
  `execute_end_time` datetime NULL DEFAULT NULL COMMENT '执行结束时间',
  `execute_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ip',
  `process_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '进程id',
  `program_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '程序目录',
  `create_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_job_id`(`job_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8758 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务-执行记录 @listen' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_smart_job_log
-- ----------------------------
INSERT INTO `t_smart_job_log` VALUES (7933, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 12:44:28', 9, '2025-09-04 12:44:28', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 12:44:28');
INSERT INTO `t_smart_job_log` VALUES (7934, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 12:46:29', 6, '2025-09-04 12:46:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 12:46:28');
INSERT INTO `t_smart_job_log` VALUES (7935, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 12:50:29', 6, '2025-09-04 12:50:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 12:50:28');
INSERT INTO `t_smart_job_log` VALUES (7936, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 12:54:29', 5, '2025-09-04 12:54:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 12:54:28');
INSERT INTO `t_smart_job_log` VALUES (7937, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 12:58:29', 5, '2025-09-04 12:58:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 12:58:28');
INSERT INTO `t_smart_job_log` VALUES (7938, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:02:29', 5, '2025-09-04 13:02:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:02:28');
INSERT INTO `t_smart_job_log` VALUES (7939, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:06:29', 6, '2025-09-04 13:06:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:06:28');
INSERT INTO `t_smart_job_log` VALUES (7940, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:10:29', 5, '2025-09-04 13:10:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:10:28');
INSERT INTO `t_smart_job_log` VALUES (7941, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:14:29', 5, '2025-09-04 13:14:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:14:28');
INSERT INTO `t_smart_job_log` VALUES (7942, 1, '示例任务1', '执行示例任务1', 1, '2025-09-04 13:15:10', 0, '2025-09-04 13:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:15:10');
INSERT INTO `t_smart_job_log` VALUES (7943, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:16:29', 7, '2025-09-04 13:16:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:16:29');
INSERT INTO `t_smart_job_log` VALUES (7944, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:18:29', 4, '2025-09-04 13:18:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:18:29');
INSERT INTO `t_smart_job_log` VALUES (7945, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:20:29', 6, '2025-09-04 13:20:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:20:29');
INSERT INTO `t_smart_job_log` VALUES (7946, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:22:29', 6, '2025-09-04 13:22:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:22:29');
INSERT INTO `t_smart_job_log` VALUES (7947, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:24:29', 8, '2025-09-04 13:24:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:24:29');
INSERT INTO `t_smart_job_log` VALUES (7948, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:26:29', 7, '2025-09-04 13:26:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:26:29');
INSERT INTO `t_smart_job_log` VALUES (7949, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:28:29', 7, '2025-09-04 13:28:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:28:29');
INSERT INTO `t_smart_job_log` VALUES (7950, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:30:29', 5, '2025-09-04 13:30:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:30:29');
INSERT INTO `t_smart_job_log` VALUES (7951, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:32:29', 6, '2025-09-04 13:32:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:32:29');
INSERT INTO `t_smart_job_log` VALUES (7952, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:34:29', 4, '2025-09-04 13:34:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:34:29');
INSERT INTO `t_smart_job_log` VALUES (7953, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:36:29', 5, '2025-09-04 13:36:29', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:36:29');
INSERT INTO `t_smart_job_log` VALUES (7954, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:38:30', 4, '2025-09-04 13:38:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:38:29');
INSERT INTO `t_smart_job_log` VALUES (7955, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:42:30', 6, '2025-09-04 13:42:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:42:29');
INSERT INTO `t_smart_job_log` VALUES (7956, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:46:30', 3, '2025-09-04 13:46:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:46:29');
INSERT INTO `t_smart_job_log` VALUES (7957, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:50:30', 2, '2025-09-04 13:50:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:50:29');
INSERT INTO `t_smart_job_log` VALUES (7958, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:54:30', 4, '2025-09-04 13:54:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:54:29');
INSERT INTO `t_smart_job_log` VALUES (7959, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 13:58:30', 3, '2025-09-04 13:58:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 13:58:29');
INSERT INTO `t_smart_job_log` VALUES (7960, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:02:30', 3, '2025-09-04 14:02:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:02:29');
INSERT INTO `t_smart_job_log` VALUES (7961, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:06:30', 5, '2025-09-04 14:06:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:06:29');
INSERT INTO `t_smart_job_log` VALUES (7962, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:10:30', 4, '2025-09-04 14:10:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:10:29');
INSERT INTO `t_smart_job_log` VALUES (7963, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:14:30', 5, '2025-09-04 14:14:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:14:30');
INSERT INTO `t_smart_job_log` VALUES (7964, 1, '示例任务1', '执行示例任务1', 1, '2025-09-04 14:15:10', 0, '2025-09-04 14:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:15:10');
INSERT INTO `t_smart_job_log` VALUES (7965, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:16:30', 3, '2025-09-04 14:16:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:16:30');
INSERT INTO `t_smart_job_log` VALUES (7966, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:18:30', 3, '2025-09-04 14:18:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:18:30');
INSERT INTO `t_smart_job_log` VALUES (7967, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:20:30', 3, '2025-09-04 14:20:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:20:30');
INSERT INTO `t_smart_job_log` VALUES (7968, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:22:30', 3, '2025-09-04 14:22:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:22:30');
INSERT INTO `t_smart_job_log` VALUES (7969, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:24:30', 4, '2025-09-04 14:24:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:24:30');
INSERT INTO `t_smart_job_log` VALUES (7970, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:26:30', 3, '2025-09-04 14:26:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:26:30');
INSERT INTO `t_smart_job_log` VALUES (7971, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:28:30', 6, '2025-09-04 14:28:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:28:30');
INSERT INTO `t_smart_job_log` VALUES (7972, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:30:30', 6, '2025-09-04 14:30:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:30:30');
INSERT INTO `t_smart_job_log` VALUES (7973, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:32:30', 4, '2025-09-04 14:32:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:32:30');
INSERT INTO `t_smart_job_log` VALUES (7974, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:34:30', 3, '2025-09-04 14:34:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:34:30');
INSERT INTO `t_smart_job_log` VALUES (7975, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:36:30', 4, '2025-09-04 14:36:30', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:36:30');
INSERT INTO `t_smart_job_log` VALUES (7976, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:38:30', 6, '2025-09-04 14:38:31', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:38:30');
INSERT INTO `t_smart_job_log` VALUES (7977, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:40:31', 6, '2025-09-04 14:40:31', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:40:30');
INSERT INTO `t_smart_job_log` VALUES (7978, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:44:31', 4, '2025-09-04 14:44:31', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:44:30');
INSERT INTO `t_smart_job_log` VALUES (7979, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 14:48:31', 4, '2025-09-04 14:48:31', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 14:48:30');
INSERT INTO `t_smart_job_log` VALUES (7980, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 15:04:19', 3, '2025-09-04 15:04:19', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 15:04:18');
INSERT INTO `t_smart_job_log` VALUES (7981, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 15:08:19', 6, '2025-09-04 15:08:19', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 15:08:18');
INSERT INTO `t_smart_job_log` VALUES (7982, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 15:12:19', 4, '2025-09-04 15:12:19', '执行成功,本次处理数据1条', '192.168.3.104', '16672', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 15:12:18');
INSERT INTO `t_smart_job_log` VALUES (7983, 2, '示例任务2', '执行示例任务2', 1, '2025-09-04 15:53:50', 6, '2025-09-04 15:53:50', '执行成功,本次处理数据1条', '192.168.3.104', '13060', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-04 15:53:50');
INSERT INTO `t_smart_job_log` VALUES (7984, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:31:26', 6, '2025-09-05 14:31:26', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:31:26');
INSERT INTO `t_smart_job_log` VALUES (7985, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:33:26', 5, '2025-09-05 14:33:26', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:33:26');
INSERT INTO `t_smart_job_log` VALUES (7986, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:35:26', 6, '2025-09-05 14:35:26', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:35:26');
INSERT INTO `t_smart_job_log` VALUES (7987, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:37:27', 6, '2025-09-05 14:37:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:37:26');
INSERT INTO `t_smart_job_log` VALUES (7988, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:41:27', 5, '2025-09-05 14:41:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:41:26');
INSERT INTO `t_smart_job_log` VALUES (7989, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:45:27', 5, '2025-09-05 14:45:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:45:26');
INSERT INTO `t_smart_job_log` VALUES (7990, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:49:27', 5, '2025-09-05 14:49:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:49:26');
INSERT INTO `t_smart_job_log` VALUES (7991, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:53:27', 6, '2025-09-05 14:53:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:53:26');
INSERT INTO `t_smart_job_log` VALUES (7992, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 14:57:27', 3, '2025-09-05 14:57:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 14:57:26');
INSERT INTO `t_smart_job_log` VALUES (7993, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:01:27', 3, '2025-09-05 15:01:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:01:26');
INSERT INTO `t_smart_job_log` VALUES (7994, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:05:27', 2, '2025-09-05 15:05:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:05:26');
INSERT INTO `t_smart_job_log` VALUES (7995, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:09:27', 3, '2025-09-05 15:09:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:09:26');
INSERT INTO `t_smart_job_log` VALUES (7996, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:11:27', 8, '2025-09-05 15:11:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:11:27');
INSERT INTO `t_smart_job_log` VALUES (7997, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:13:27', 4, '2025-09-05 15:13:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:13:27');
INSERT INTO `t_smart_job_log` VALUES (7998, 1, '示例任务1', '执行示例任务1', 1, '2025-09-05 15:15:10', 0, '2025-09-05 15:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:15:10');
INSERT INTO `t_smart_job_log` VALUES (7999, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:15:27', 3, '2025-09-05 15:15:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:15:27');
INSERT INTO `t_smart_job_log` VALUES (8000, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:17:27', 5, '2025-09-05 15:17:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:17:27');
INSERT INTO `t_smart_job_log` VALUES (8001, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:19:27', 6, '2025-09-05 15:19:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:19:27');
INSERT INTO `t_smart_job_log` VALUES (8002, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:21:27', 3, '2025-09-05 15:21:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:21:27');
INSERT INTO `t_smart_job_log` VALUES (8003, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:23:27', 4, '2025-09-05 15:23:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:23:27');
INSERT INTO `t_smart_job_log` VALUES (8004, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:25:27', 2, '2025-09-05 15:25:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:25:27');
INSERT INTO `t_smart_job_log` VALUES (8005, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:27:27', 5, '2025-09-05 15:27:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:27:27');
INSERT INTO `t_smart_job_log` VALUES (8006, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:29:27', 3, '2025-09-05 15:29:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:29:27');
INSERT INTO `t_smart_job_log` VALUES (8007, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:31:27', 3, '2025-09-05 15:31:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:31:27');
INSERT INTO `t_smart_job_log` VALUES (8008, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:33:27', 6, '2025-09-05 15:33:27', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:33:27');
INSERT INTO `t_smart_job_log` VALUES (8009, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:35:28', 3, '2025-09-05 15:35:28', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:35:27');
INSERT INTO `t_smart_job_log` VALUES (8010, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:39:28', 2, '2025-09-05 15:39:28', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:39:27');
INSERT INTO `t_smart_job_log` VALUES (8011, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:43:28', 3, '2025-09-05 15:43:28', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:43:27');
INSERT INTO `t_smart_job_log` VALUES (8012, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:47:28', 2, '2025-09-05 15:47:28', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:47:27');
INSERT INTO `t_smart_job_log` VALUES (8013, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:51:28', 3, '2025-09-05 15:51:28', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:51:27');
INSERT INTO `t_smart_job_log` VALUES (8014, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:55:28', 3, '2025-09-05 15:55:28', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:55:27');
INSERT INTO `t_smart_job_log` VALUES (8015, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 15:59:28', 2, '2025-09-05 15:59:28', '执行成功,本次处理数据1条', '192.168.3.104', '22028', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 15:59:27');
INSERT INTO `t_smart_job_log` VALUES (8016, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:03:51', 7, '2025-09-05 16:03:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:03:50');
INSERT INTO `t_smart_job_log` VALUES (8017, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:07:51', 9, '2025-09-05 16:07:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:07:50');
INSERT INTO `t_smart_job_log` VALUES (8018, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:09:51', 6, '2025-09-05 16:09:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:09:51');
INSERT INTO `t_smart_job_log` VALUES (8019, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:11:51', 6, '2025-09-05 16:11:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:11:51');
INSERT INTO `t_smart_job_log` VALUES (8020, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:13:51', 4, '2025-09-05 16:13:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:13:51');
INSERT INTO `t_smart_job_log` VALUES (8021, 1, '示例任务1', '执行示例任务1', 1, '2025-09-05 16:15:10', 0, '2025-09-05 16:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:15:10');
INSERT INTO `t_smart_job_log` VALUES (8022, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:15:51', 5, '2025-09-05 16:15:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:15:51');
INSERT INTO `t_smart_job_log` VALUES (8023, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:17:51', 5, '2025-09-05 16:17:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:17:51');
INSERT INTO `t_smart_job_log` VALUES (8024, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:19:51', 4, '2025-09-05 16:19:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:19:51');
INSERT INTO `t_smart_job_log` VALUES (8025, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:21:51', 5, '2025-09-05 16:21:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:21:51');
INSERT INTO `t_smart_job_log` VALUES (8026, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:23:51', 3, '2025-09-05 16:23:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:23:51');
INSERT INTO `t_smart_job_log` VALUES (8027, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:25:51', 3, '2025-09-05 16:25:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:25:51');
INSERT INTO `t_smart_job_log` VALUES (8028, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:27:51', 3, '2025-09-05 16:27:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:27:51');
INSERT INTO `t_smart_job_log` VALUES (8029, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:29:51', 3, '2025-09-05 16:29:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:29:51');
INSERT INTO `t_smart_job_log` VALUES (8030, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:31:51', 3, '2025-09-05 16:31:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:31:51');
INSERT INTO `t_smart_job_log` VALUES (8031, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:33:51', 7, '2025-09-05 16:33:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:33:51');
INSERT INTO `t_smart_job_log` VALUES (8032, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:35:51', 4, '2025-09-05 16:35:51', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:35:51');
INSERT INTO `t_smart_job_log` VALUES (8033, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:37:52', 7, '2025-09-05 16:37:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:37:51');
INSERT INTO `t_smart_job_log` VALUES (8034, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:41:52', 5, '2025-09-05 16:41:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:41:51');
INSERT INTO `t_smart_job_log` VALUES (8035, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:45:52', 3, '2025-09-05 16:45:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:45:51');
INSERT INTO `t_smart_job_log` VALUES (8036, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:49:52', 2, '2025-09-05 16:49:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:49:51');
INSERT INTO `t_smart_job_log` VALUES (8037, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:53:52', 3, '2025-09-05 16:53:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:53:51');
INSERT INTO `t_smart_job_log` VALUES (8038, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 16:57:52', 3, '2025-09-05 16:57:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 16:57:51');
INSERT INTO `t_smart_job_log` VALUES (8039, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:01:52', 3, '2025-09-05 17:01:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:01:51');
INSERT INTO `t_smart_job_log` VALUES (8040, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:05:52', 3, '2025-09-05 17:05:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:05:51');
INSERT INTO `t_smart_job_log` VALUES (8041, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:09:52', 2, '2025-09-05 17:09:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:09:51');
INSERT INTO `t_smart_job_log` VALUES (8042, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:13:52', 3, '2025-09-05 17:13:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:13:51');
INSERT INTO `t_smart_job_log` VALUES (8043, 1, '示例任务1', '执行示例任务1', 1, '2025-09-05 17:15:10', 0, '2025-09-05 17:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:15:10');
INSERT INTO `t_smart_job_log` VALUES (8044, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:15:52', 3, '2025-09-05 17:15:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:15:52');
INSERT INTO `t_smart_job_log` VALUES (8045, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:17:52', 4, '2025-09-05 17:17:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:17:52');
INSERT INTO `t_smart_job_log` VALUES (8046, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:19:52', 2, '2025-09-05 17:19:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:19:52');
INSERT INTO `t_smart_job_log` VALUES (8047, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:21:52', 3, '2025-09-05 17:21:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:21:52');
INSERT INTO `t_smart_job_log` VALUES (8048, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:23:52', 3, '2025-09-05 17:23:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:23:52');
INSERT INTO `t_smart_job_log` VALUES (8049, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:25:52', 3, '2025-09-05 17:25:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:25:52');
INSERT INTO `t_smart_job_log` VALUES (8050, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:27:52', 2, '2025-09-05 17:27:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:27:52');
INSERT INTO `t_smart_job_log` VALUES (8051, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:29:52', 2, '2025-09-05 17:29:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:29:52');
INSERT INTO `t_smart_job_log` VALUES (8052, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:31:52', 2, '2025-09-05 17:31:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:31:52');
INSERT INTO `t_smart_job_log` VALUES (8053, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:33:52', 2, '2025-09-05 17:33:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:33:52');
INSERT INTO `t_smart_job_log` VALUES (8054, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:35:52', 2, '2025-09-05 17:35:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:35:52');
INSERT INTO `t_smart_job_log` VALUES (8055, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:37:52', 2, '2025-09-05 17:37:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:37:52');
INSERT INTO `t_smart_job_log` VALUES (8056, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:39:52', 2, '2025-09-05 17:39:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:39:52');
INSERT INTO `t_smart_job_log` VALUES (8057, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:41:52', 2, '2025-09-05 17:41:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:41:52');
INSERT INTO `t_smart_job_log` VALUES (8058, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:43:52', 3, '2025-09-05 17:43:52', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:43:52');
INSERT INTO `t_smart_job_log` VALUES (8059, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:45:52', 3, '2025-09-05 17:45:53', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:45:52');
INSERT INTO `t_smart_job_log` VALUES (8060, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:47:53', 2, '2025-09-05 17:47:53', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:47:52');
INSERT INTO `t_smart_job_log` VALUES (8061, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 17:51:53', 2, '2025-09-05 17:51:53', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 17:51:52');
INSERT INTO `t_smart_job_log` VALUES (8062, 1, '示例任务1', '执行示例任务1', 1, '2025-09-05 18:31:12', 0, '2025-09-05 18:31:12', '执行完毕,随便说点什么吧', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:31:11');
INSERT INTO `t_smart_job_log` VALUES (8063, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 18:31:12', 3, '2025-09-05 18:31:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:31:11');
INSERT INTO `t_smart_job_log` VALUES (8064, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 18:35:12', 3, '2025-09-05 18:35:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:35:11');
INSERT INTO `t_smart_job_log` VALUES (8065, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 18:39:12', 4, '2025-09-05 18:39:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:39:11');
INSERT INTO `t_smart_job_log` VALUES (8066, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 18:43:12', 3, '2025-09-05 18:43:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:43:11');
INSERT INTO `t_smart_job_log` VALUES (8067, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 18:47:12', 3, '2025-09-05 18:47:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:47:11');
INSERT INTO `t_smart_job_log` VALUES (8068, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 18:51:12', 16, '2025-09-05 18:51:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:51:11');
INSERT INTO `t_smart_job_log` VALUES (8069, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 18:55:12', 3, '2025-09-05 18:55:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:55:11');
INSERT INTO `t_smart_job_log` VALUES (8070, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 18:59:12', 2, '2025-09-05 18:59:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 18:59:11');
INSERT INTO `t_smart_job_log` VALUES (8071, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:03:12', 5, '2025-09-05 19:03:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:03:12');
INSERT INTO `t_smart_job_log` VALUES (8072, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:05:12', 3, '2025-09-05 19:05:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:05:12');
INSERT INTO `t_smart_job_log` VALUES (8073, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:07:12', 4, '2025-09-05 19:07:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:07:12');
INSERT INTO `t_smart_job_log` VALUES (8074, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:09:12', 4, '2025-09-05 19:09:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:09:12');
INSERT INTO `t_smart_job_log` VALUES (8075, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:11:12', 4, '2025-09-05 19:11:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:11:12');
INSERT INTO `t_smart_job_log` VALUES (8076, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:13:12', 2, '2025-09-05 19:13:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:13:12');
INSERT INTO `t_smart_job_log` VALUES (8077, 1, '示例任务1', '执行示例任务1', 1, '2025-09-05 19:15:10', 0, '2025-09-05 19:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:15:10');
INSERT INTO `t_smart_job_log` VALUES (8078, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:15:12', 9, '2025-09-05 19:15:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:15:12');
INSERT INTO `t_smart_job_log` VALUES (8079, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:17:12', 3, '2025-09-05 19:17:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:17:12');
INSERT INTO `t_smart_job_log` VALUES (8080, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:19:12', 5, '2025-09-05 19:19:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:19:12');
INSERT INTO `t_smart_job_log` VALUES (8081, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:21:12', 4, '2025-09-05 19:21:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:21:12');
INSERT INTO `t_smart_job_log` VALUES (8082, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:23:12', 4, '2025-09-05 19:23:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:23:12');
INSERT INTO `t_smart_job_log` VALUES (8083, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:25:12', 3, '2025-09-05 19:25:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:25:12');
INSERT INTO `t_smart_job_log` VALUES (8084, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:27:12', 2, '2025-09-05 19:27:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:27:12');
INSERT INTO `t_smart_job_log` VALUES (8085, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:29:12', 3, '2025-09-05 19:29:12', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:29:12');
INSERT INTO `t_smart_job_log` VALUES (8086, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:31:13', 3, '2025-09-05 19:31:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:31:12');
INSERT INTO `t_smart_job_log` VALUES (8087, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:35:13', 5, '2025-09-05 19:35:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:35:12');
INSERT INTO `t_smart_job_log` VALUES (8088, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:39:13', 4, '2025-09-05 19:39:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:39:12');
INSERT INTO `t_smart_job_log` VALUES (8089, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:43:13', 5, '2025-09-05 19:43:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:43:12');
INSERT INTO `t_smart_job_log` VALUES (8090, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:47:13', 4, '2025-09-05 19:47:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:47:12');
INSERT INTO `t_smart_job_log` VALUES (8091, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:51:13', 3, '2025-09-05 19:51:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:51:12');
INSERT INTO `t_smart_job_log` VALUES (8092, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:55:13', 5, '2025-09-05 19:55:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:55:12');
INSERT INTO `t_smart_job_log` VALUES (8093, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 19:59:13', 3, '2025-09-05 19:59:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 19:59:12');
INSERT INTO `t_smart_job_log` VALUES (8094, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:03:13', 3, '2025-09-05 20:03:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:03:13');
INSERT INTO `t_smart_job_log` VALUES (8095, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:05:13', 3, '2025-09-05 20:05:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:05:13');
INSERT INTO `t_smart_job_log` VALUES (8096, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:07:13', 7, '2025-09-05 20:07:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:07:13');
INSERT INTO `t_smart_job_log` VALUES (8097, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:09:13', 3, '2025-09-05 20:09:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:09:13');
INSERT INTO `t_smart_job_log` VALUES (8098, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:11:13', 2, '2025-09-05 20:11:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:11:13');
INSERT INTO `t_smart_job_log` VALUES (8099, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:13:13', 2, '2025-09-05 20:13:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:13:13');
INSERT INTO `t_smart_job_log` VALUES (8100, 1, '示例任务1', '执行示例任务1', 1, '2025-09-05 20:15:10', 0, '2025-09-05 20:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:15:10');
INSERT INTO `t_smart_job_log` VALUES (8101, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:15:13', 3, '2025-09-05 20:15:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:15:13');
INSERT INTO `t_smart_job_log` VALUES (8102, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:17:13', 3, '2025-09-05 20:17:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:17:13');
INSERT INTO `t_smart_job_log` VALUES (8103, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:19:13', 5, '2025-09-05 20:19:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:19:13');
INSERT INTO `t_smart_job_log` VALUES (8104, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 20:21:13', 5, '2025-09-05 20:21:13', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 20:21:13');
INSERT INTO `t_smart_job_log` VALUES (8105, 1, '示例任务1', '执行示例任务1', 1, '2025-09-05 21:16:53', 0, '2025-09-05 21:16:53', '执行完毕,随便说点什么吧', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 21:16:53');
INSERT INTO `t_smart_job_log` VALUES (8106, 2, '示例任务2', '执行示例任务2', 1, '2025-09-05 21:16:53', 4, '2025-09-05 21:16:53', '执行成功,本次处理数据1条', '192.168.3.104', '8712', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-05 21:16:53');
INSERT INTO `t_smart_job_log` VALUES (8107, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 14:56:13', 9, '2025-09-06 14:56:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 14:56:13');
INSERT INTO `t_smart_job_log` VALUES (8108, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 14:58:13', 8, '2025-09-06 14:58:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 14:58:13');
INSERT INTO `t_smart_job_log` VALUES (8109, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:00:13', 8, '2025-09-06 15:00:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:00:13');
INSERT INTO `t_smart_job_log` VALUES (8110, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:02:13', 4, '2025-09-06 15:02:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:02:13');
INSERT INTO `t_smart_job_log` VALUES (8111, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:04:13', 5, '2025-09-06 15:04:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:04:13');
INSERT INTO `t_smart_job_log` VALUES (8112, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:06:13', 4, '2025-09-06 15:06:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:06:13');
INSERT INTO `t_smart_job_log` VALUES (8113, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:08:13', 4, '2025-09-06 15:08:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:08:13');
INSERT INTO `t_smart_job_log` VALUES (8114, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:10:13', 4, '2025-09-06 15:10:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:10:13');
INSERT INTO `t_smart_job_log` VALUES (8115, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:12:13', 3, '2025-09-06 15:12:13', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:12:13');
INSERT INTO `t_smart_job_log` VALUES (8116, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:14:14', 3, '2025-09-06 15:14:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:14:13');
INSERT INTO `t_smart_job_log` VALUES (8117, 1, '示例任务1', '执行示例任务1', 1, '2025-09-06 15:15:10', 0, '2025-09-06 15:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:15:10');
INSERT INTO `t_smart_job_log` VALUES (8118, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:18:14', 3, '2025-09-06 15:18:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:18:13');
INSERT INTO `t_smart_job_log` VALUES (8119, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:22:14', 4, '2025-09-06 15:22:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:22:13');
INSERT INTO `t_smart_job_log` VALUES (8120, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:26:14', 3, '2025-09-06 15:26:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:26:13');
INSERT INTO `t_smart_job_log` VALUES (8121, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:30:14', 4, '2025-09-06 15:30:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:30:13');
INSERT INTO `t_smart_job_log` VALUES (8122, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:34:14', 3, '2025-09-06 15:34:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:34:13');
INSERT INTO `t_smart_job_log` VALUES (8123, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:38:14', 3, '2025-09-06 15:38:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:38:13');
INSERT INTO `t_smart_job_log` VALUES (8124, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:42:14', 3, '2025-09-06 15:42:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:42:13');
INSERT INTO `t_smart_job_log` VALUES (8125, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:46:14', 4, '2025-09-06 15:46:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:46:13');
INSERT INTO `t_smart_job_log` VALUES (8126, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:50:14', 5, '2025-09-06 15:50:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:50:14');
INSERT INTO `t_smart_job_log` VALUES (8127, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:52:14', 5, '2025-09-06 15:52:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:52:14');
INSERT INTO `t_smart_job_log` VALUES (8128, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:54:14', 4, '2025-09-06 15:54:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:54:14');
INSERT INTO `t_smart_job_log` VALUES (8129, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:56:14', 4, '2025-09-06 15:56:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:56:14');
INSERT INTO `t_smart_job_log` VALUES (8130, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 15:58:14', 3, '2025-09-06 15:58:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 15:58:14');
INSERT INTO `t_smart_job_log` VALUES (8131, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:00:14', 3, '2025-09-06 16:00:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:00:14');
INSERT INTO `t_smart_job_log` VALUES (8132, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:02:14', 4, '2025-09-06 16:02:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:02:14');
INSERT INTO `t_smart_job_log` VALUES (8133, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:04:14', 5, '2025-09-06 16:04:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:04:14');
INSERT INTO `t_smart_job_log` VALUES (8134, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:06:14', 4, '2025-09-06 16:06:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:06:14');
INSERT INTO `t_smart_job_log` VALUES (8135, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:08:14', 3, '2025-09-06 16:08:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:08:14');
INSERT INTO `t_smart_job_log` VALUES (8136, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:10:14', 2, '2025-09-06 16:10:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:10:14');
INSERT INTO `t_smart_job_log` VALUES (8137, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:12:14', 3, '2025-09-06 16:12:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:12:14');
INSERT INTO `t_smart_job_log` VALUES (8138, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:14:14', 2, '2025-09-06 16:14:14', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:14:14');
INSERT INTO `t_smart_job_log` VALUES (8139, 1, '示例任务1', '执行示例任务1', 1, '2025-09-06 16:15:10', 0, '2025-09-06 16:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:15:10');
INSERT INTO `t_smart_job_log` VALUES (8140, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:16:15', 2, '2025-09-06 16:16:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:16:14');
INSERT INTO `t_smart_job_log` VALUES (8141, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:20:15', 4, '2025-09-06 16:20:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:20:14');
INSERT INTO `t_smart_job_log` VALUES (8142, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:24:15', 4, '2025-09-06 16:24:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:24:14');
INSERT INTO `t_smart_job_log` VALUES (8143, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:28:15', 3, '2025-09-06 16:28:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:28:14');
INSERT INTO `t_smart_job_log` VALUES (8144, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:32:15', 6, '2025-09-06 16:32:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:32:14');
INSERT INTO `t_smart_job_log` VALUES (8145, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:36:15', 5, '2025-09-06 16:36:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:36:14');
INSERT INTO `t_smart_job_log` VALUES (8146, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:40:15', 3, '2025-09-06 16:40:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:40:14');
INSERT INTO `t_smart_job_log` VALUES (8147, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:44:15', 4, '2025-09-06 16:44:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:44:14');
INSERT INTO `t_smart_job_log` VALUES (8148, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:48:15', 5, '2025-09-06 16:48:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:48:14');
INSERT INTO `t_smart_job_log` VALUES (8149, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:50:15', 3, '2025-09-06 16:50:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:50:15');
INSERT INTO `t_smart_job_log` VALUES (8150, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:52:15', 3, '2025-09-06 16:52:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:52:15');
INSERT INTO `t_smart_job_log` VALUES (8151, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:54:15', 4, '2025-09-06 16:54:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:54:15');
INSERT INTO `t_smart_job_log` VALUES (8152, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:56:15', 3, '2025-09-06 16:56:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:56:15');
INSERT INTO `t_smart_job_log` VALUES (8153, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 16:58:15', 6, '2025-09-06 16:58:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 16:58:15');
INSERT INTO `t_smart_job_log` VALUES (8154, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:00:15', 5, '2025-09-06 17:00:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:00:15');
INSERT INTO `t_smart_job_log` VALUES (8155, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:02:15', 4, '2025-09-06 17:02:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:02:15');
INSERT INTO `t_smart_job_log` VALUES (8156, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:04:15', 8, '2025-09-06 17:04:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:04:15');
INSERT INTO `t_smart_job_log` VALUES (8157, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:06:15', 4, '2025-09-06 17:06:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:06:15');
INSERT INTO `t_smart_job_log` VALUES (8158, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:08:15', 5, '2025-09-06 17:08:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:08:15');
INSERT INTO `t_smart_job_log` VALUES (8159, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:10:15', 2, '2025-09-06 17:10:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:10:15');
INSERT INTO `t_smart_job_log` VALUES (8160, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:12:15', 4, '2025-09-06 17:12:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:12:15');
INSERT INTO `t_smart_job_log` VALUES (8161, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:14:15', 2, '2025-09-06 17:14:15', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:14:15');
INSERT INTO `t_smart_job_log` VALUES (8162, 1, '示例任务1', '执行示例任务1', 1, '2025-09-06 17:15:10', 0, '2025-09-06 17:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:15:10');
INSERT INTO `t_smart_job_log` VALUES (8163, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:16:16', 4, '2025-09-06 17:16:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:16:15');
INSERT INTO `t_smart_job_log` VALUES (8164, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:20:16', 5, '2025-09-06 17:20:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:20:15');
INSERT INTO `t_smart_job_log` VALUES (8165, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:24:16', 2, '2025-09-06 17:24:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:24:15');
INSERT INTO `t_smart_job_log` VALUES (8166, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:28:16', 3, '2025-09-06 17:28:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:28:15');
INSERT INTO `t_smart_job_log` VALUES (8167, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:32:16', 7, '2025-09-06 17:32:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:32:15');
INSERT INTO `t_smart_job_log` VALUES (8168, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:36:16', 2, '2025-09-06 17:36:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:36:15');
INSERT INTO `t_smart_job_log` VALUES (8169, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:40:16', 4, '2025-09-06 17:40:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:40:15');
INSERT INTO `t_smart_job_log` VALUES (8170, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:44:16', 3, '2025-09-06 17:44:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:44:15');
INSERT INTO `t_smart_job_log` VALUES (8171, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:48:16', 3, '2025-09-06 17:48:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:48:15');
INSERT INTO `t_smart_job_log` VALUES (8172, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:52:16', 3, '2025-09-06 17:52:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:52:15');
INSERT INTO `t_smart_job_log` VALUES (8173, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:54:16', 4, '2025-09-06 17:54:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:54:16');
INSERT INTO `t_smart_job_log` VALUES (8174, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:56:16', 3, '2025-09-06 17:56:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:56:16');
INSERT INTO `t_smart_job_log` VALUES (8175, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 17:58:16', 2, '2025-09-06 17:58:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 17:58:16');
INSERT INTO `t_smart_job_log` VALUES (8176, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:00:16', 2, '2025-09-06 18:00:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:00:16');
INSERT INTO `t_smart_job_log` VALUES (8177, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:02:16', 5, '2025-09-06 18:02:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:02:16');
INSERT INTO `t_smart_job_log` VALUES (8178, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:04:16', 4, '2025-09-06 18:04:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:04:16');
INSERT INTO `t_smart_job_log` VALUES (8179, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:06:16', 3, '2025-09-06 18:06:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:06:16');
INSERT INTO `t_smart_job_log` VALUES (8180, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:08:16', 3, '2025-09-06 18:08:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:08:16');
INSERT INTO `t_smart_job_log` VALUES (8181, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:10:16', 5, '2025-09-06 18:10:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:10:16');
INSERT INTO `t_smart_job_log` VALUES (8182, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:12:16', 4, '2025-09-06 18:12:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:12:16');
INSERT INTO `t_smart_job_log` VALUES (8183, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:14:16', 5, '2025-09-06 18:14:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:14:16');
INSERT INTO `t_smart_job_log` VALUES (8184, 1, '示例任务1', '执行示例任务1', 1, '2025-09-06 18:15:10', 0, '2025-09-06 18:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:15:10');
INSERT INTO `t_smart_job_log` VALUES (8185, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:16:16', 4, '2025-09-06 18:16:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:16:16');
INSERT INTO `t_smart_job_log` VALUES (8186, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:18:16', 4, '2025-09-06 18:18:16', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:18:16');
INSERT INTO `t_smart_job_log` VALUES (8187, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 18:20:17', 3, '2025-09-06 18:20:17', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 18:20:16');
INSERT INTO `t_smart_job_log` VALUES (8188, 1, '示例任务1', '执行示例任务1', 1, '2025-09-06 22:07:52', 0, '2025-09-06 22:07:52', '执行完毕,随便说点什么吧', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 22:07:52');
INSERT INTO `t_smart_job_log` VALUES (8189, 2, '示例任务2', '执行示例任务2', 1, '2025-09-06 22:07:52', 4, '2025-09-06 22:07:52', '执行成功,本次处理数据1条', '192.168.3.104', '5424', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-06 22:07:52');
INSERT INTO `t_smart_job_log` VALUES (8190, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:14:10', 6, '2025-09-07 12:14:10', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:14:10');
INSERT INTO `t_smart_job_log` VALUES (8191, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 12:15:10', 0, '2025-09-07 12:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:15:10');
INSERT INTO `t_smart_job_log` VALUES (8192, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:16:11', 6, '2025-09-07 12:16:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:16:10');
INSERT INTO `t_smart_job_log` VALUES (8193, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:20:11', 4, '2025-09-07 12:20:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:20:10');
INSERT INTO `t_smart_job_log` VALUES (8194, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:24:11', 6, '2025-09-07 12:24:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:24:10');
INSERT INTO `t_smart_job_log` VALUES (8195, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:28:11', 8, '2025-09-07 12:28:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:28:10');
INSERT INTO `t_smart_job_log` VALUES (8196, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:32:11', 4, '2025-09-07 12:32:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:32:10');
INSERT INTO `t_smart_job_log` VALUES (8197, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:36:11', 5, '2025-09-07 12:36:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:36:10');
INSERT INTO `t_smart_job_log` VALUES (8198, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:40:11', 5, '2025-09-07 12:40:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:40:10');
INSERT INTO `t_smart_job_log` VALUES (8199, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:42:11', 4, '2025-09-07 12:42:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:42:11');
INSERT INTO `t_smart_job_log` VALUES (8200, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:44:11', 3, '2025-09-07 12:44:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:44:11');
INSERT INTO `t_smart_job_log` VALUES (8201, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:46:11', 20, '2025-09-07 12:46:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:46:11');
INSERT INTO `t_smart_job_log` VALUES (8202, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:48:11', 6, '2025-09-07 12:48:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:48:11');
INSERT INTO `t_smart_job_log` VALUES (8203, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:50:11', 3, '2025-09-07 12:50:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:50:11');
INSERT INTO `t_smart_job_log` VALUES (8204, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:52:11', 5, '2025-09-07 12:52:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:52:11');
INSERT INTO `t_smart_job_log` VALUES (8205, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:54:11', 4, '2025-09-07 12:54:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:54:11');
INSERT INTO `t_smart_job_log` VALUES (8206, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:56:11', 7, '2025-09-07 12:56:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:56:11');
INSERT INTO `t_smart_job_log` VALUES (8207, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 12:58:11', 5, '2025-09-07 12:58:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 12:58:11');
INSERT INTO `t_smart_job_log` VALUES (8208, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:00:11', 4, '2025-09-07 13:00:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:00:11');
INSERT INTO `t_smart_job_log` VALUES (8209, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:02:11', 4, '2025-09-07 13:02:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:02:11');
INSERT INTO `t_smart_job_log` VALUES (8210, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:04:11', 3, '2025-09-07 13:04:11', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:04:11');
INSERT INTO `t_smart_job_log` VALUES (8211, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:06:12', 3, '2025-09-07 13:06:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:06:11');
INSERT INTO `t_smart_job_log` VALUES (8212, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:10:12', 3, '2025-09-07 13:10:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:10:11');
INSERT INTO `t_smart_job_log` VALUES (8213, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:14:12', 3, '2025-09-07 13:14:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:14:11');
INSERT INTO `t_smart_job_log` VALUES (8214, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 13:15:10', 0, '2025-09-07 13:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:15:10');
INSERT INTO `t_smart_job_log` VALUES (8215, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:18:12', 3, '2025-09-07 13:18:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:18:11');
INSERT INTO `t_smart_job_log` VALUES (8216, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:22:12', 4, '2025-09-07 13:22:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:22:11');
INSERT INTO `t_smart_job_log` VALUES (8217, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:26:12', 2, '2025-09-07 13:26:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:26:11');
INSERT INTO `t_smart_job_log` VALUES (8218, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:30:12', 2, '2025-09-07 13:30:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:30:11');
INSERT INTO `t_smart_job_log` VALUES (8219, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:34:12', 2, '2025-09-07 13:34:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:34:11');
INSERT INTO `t_smart_job_log` VALUES (8220, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:38:12', 3, '2025-09-07 13:38:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:38:11');
INSERT INTO `t_smart_job_log` VALUES (8221, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:42:12', 6, '2025-09-07 13:42:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:42:12');
INSERT INTO `t_smart_job_log` VALUES (8222, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:44:12', 5, '2025-09-07 13:44:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:44:12');
INSERT INTO `t_smart_job_log` VALUES (8223, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:46:12', 7, '2025-09-07 13:46:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:46:12');
INSERT INTO `t_smart_job_log` VALUES (8224, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:48:12', 6, '2025-09-07 13:48:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:48:12');
INSERT INTO `t_smart_job_log` VALUES (8225, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:50:12', 5, '2025-09-07 13:50:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:50:12');
INSERT INTO `t_smart_job_log` VALUES (8226, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:52:12', 3, '2025-09-07 13:52:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:52:12');
INSERT INTO `t_smart_job_log` VALUES (8227, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:54:12', 3, '2025-09-07 13:54:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:54:12');
INSERT INTO `t_smart_job_log` VALUES (8228, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:56:12', 5, '2025-09-07 13:56:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:56:12');
INSERT INTO `t_smart_job_log` VALUES (8229, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 13:58:12', 2, '2025-09-07 13:58:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 13:58:12');
INSERT INTO `t_smart_job_log` VALUES (8230, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:00:12', 4, '2025-09-07 14:00:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:00:12');
INSERT INTO `t_smart_job_log` VALUES (8231, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:02:12', 4, '2025-09-07 14:02:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:02:12');
INSERT INTO `t_smart_job_log` VALUES (8232, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:04:12', 3, '2025-09-07 14:04:12', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:04:12');
INSERT INTO `t_smart_job_log` VALUES (8233, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:06:12', 3, '2025-09-07 14:06:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:06:12');
INSERT INTO `t_smart_job_log` VALUES (8234, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:08:13', 3, '2025-09-07 14:08:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:08:12');
INSERT INTO `t_smart_job_log` VALUES (8235, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:12:13', 3, '2025-09-07 14:12:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:12:12');
INSERT INTO `t_smart_job_log` VALUES (8236, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 14:15:10', 0, '2025-09-07 14:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:15:10');
INSERT INTO `t_smart_job_log` VALUES (8237, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:16:13', 2, '2025-09-07 14:16:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:16:12');
INSERT INTO `t_smart_job_log` VALUES (8238, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:20:13', 3, '2025-09-07 14:20:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:20:12');
INSERT INTO `t_smart_job_log` VALUES (8239, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:24:13', 4, '2025-09-07 14:24:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:24:12');
INSERT INTO `t_smart_job_log` VALUES (8240, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:28:13', 2, '2025-09-07 14:28:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:28:12');
INSERT INTO `t_smart_job_log` VALUES (8241, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:32:13', 3, '2025-09-07 14:32:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:32:12');
INSERT INTO `t_smart_job_log` VALUES (8242, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:36:13', 3, '2025-09-07 14:36:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:36:12');
INSERT INTO `t_smart_job_log` VALUES (8243, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:40:13', 3, '2025-09-07 14:40:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:40:12');
INSERT INTO `t_smart_job_log` VALUES (8244, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:44:13', 3, '2025-09-07 14:44:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:44:12');
INSERT INTO `t_smart_job_log` VALUES (8245, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:48:13', 4, '2025-09-07 14:48:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:48:13');
INSERT INTO `t_smart_job_log` VALUES (8246, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:50:13', 2, '2025-09-07 14:50:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:50:13');
INSERT INTO `t_smart_job_log` VALUES (8247, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:52:13', 3, '2025-09-07 14:52:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:52:13');
INSERT INTO `t_smart_job_log` VALUES (8248, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:54:13', 3, '2025-09-07 14:54:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:54:13');
INSERT INTO `t_smart_job_log` VALUES (8249, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:56:13', 2, '2025-09-07 14:56:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:56:13');
INSERT INTO `t_smart_job_log` VALUES (8250, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 14:58:13', 5, '2025-09-07 14:58:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 14:58:13');
INSERT INTO `t_smart_job_log` VALUES (8251, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:00:13', 3, '2025-09-07 15:00:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:00:13');
INSERT INTO `t_smart_job_log` VALUES (8252, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:02:13', 3, '2025-09-07 15:02:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:02:13');
INSERT INTO `t_smart_job_log` VALUES (8253, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:04:13', 2, '2025-09-07 15:04:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:04:13');
INSERT INTO `t_smart_job_log` VALUES (8254, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:06:13', 2, '2025-09-07 15:06:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:06:13');
INSERT INTO `t_smart_job_log` VALUES (8255, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:08:13', 3, '2025-09-07 15:08:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:08:13');
INSERT INTO `t_smart_job_log` VALUES (8256, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:10:13', 5, '2025-09-07 15:10:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:10:13');
INSERT INTO `t_smart_job_log` VALUES (8257, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:12:13', 2, '2025-09-07 15:12:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:12:13');
INSERT INTO `t_smart_job_log` VALUES (8258, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:14:13', 2, '2025-09-07 15:14:13', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:14:13');
INSERT INTO `t_smart_job_log` VALUES (8259, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 15:15:10', 0, '2025-09-07 15:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:15:10');
INSERT INTO `t_smart_job_log` VALUES (8260, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:16:14', 2, '2025-09-07 15:16:14', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:16:13');
INSERT INTO `t_smart_job_log` VALUES (8261, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:20:14', 4, '2025-09-07 15:20:14', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:20:13');
INSERT INTO `t_smart_job_log` VALUES (8262, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:24:14', 2, '2025-09-07 15:24:14', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:24:13');
INSERT INTO `t_smart_job_log` VALUES (8263, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:28:14', 2, '2025-09-07 15:28:14', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:28:13');
INSERT INTO `t_smart_job_log` VALUES (8264, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:32:14', 2, '2025-09-07 15:32:14', '执行成功,本次处理数据1条', '192.168.3.104', '20220', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:32:13');
INSERT INTO `t_smart_job_log` VALUES (8265, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:35:31', 7, '2025-09-07 15:35:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:35:30');
INSERT INTO `t_smart_job_log` VALUES (8266, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:37:31', 4, '2025-09-07 15:37:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:37:31');
INSERT INTO `t_smart_job_log` VALUES (8267, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:39:31', 8, '2025-09-07 15:39:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:39:31');
INSERT INTO `t_smart_job_log` VALUES (8268, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:41:31', 5, '2025-09-07 15:41:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:41:31');
INSERT INTO `t_smart_job_log` VALUES (8269, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:43:31', 4, '2025-09-07 15:43:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:43:31');
INSERT INTO `t_smart_job_log` VALUES (8270, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:45:31', 14, '2025-09-07 15:45:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:45:31');
INSERT INTO `t_smart_job_log` VALUES (8271, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:47:31', 13, '2025-09-07 15:47:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:47:31');
INSERT INTO `t_smart_job_log` VALUES (8272, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:49:31', 4, '2025-09-07 15:49:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:49:31');
INSERT INTO `t_smart_job_log` VALUES (8273, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:51:31', 4, '2025-09-07 15:51:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:51:31');
INSERT INTO `t_smart_job_log` VALUES (8274, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:53:31', 4, '2025-09-07 15:53:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:53:31');
INSERT INTO `t_smart_job_log` VALUES (8275, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:55:31', 3, '2025-09-07 15:55:31', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:55:31');
INSERT INTO `t_smart_job_log` VALUES (8276, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 15:57:32', 3, '2025-09-07 15:57:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 15:57:31');
INSERT INTO `t_smart_job_log` VALUES (8277, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:01:32', 3, '2025-09-07 16:01:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:01:31');
INSERT INTO `t_smart_job_log` VALUES (8278, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:05:32', 4, '2025-09-07 16:05:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:05:31');
INSERT INTO `t_smart_job_log` VALUES (8279, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:09:32', 4, '2025-09-07 16:09:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:09:31');
INSERT INTO `t_smart_job_log` VALUES (8280, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:13:32', 7, '2025-09-07 16:13:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:13:31');
INSERT INTO `t_smart_job_log` VALUES (8281, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 16:15:10', 0, '2025-09-07 16:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:15:10');
INSERT INTO `t_smart_job_log` VALUES (8282, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:17:32', 4, '2025-09-07 16:17:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:17:31');
INSERT INTO `t_smart_job_log` VALUES (8283, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:21:32', 5, '2025-09-07 16:21:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:21:31');
INSERT INTO `t_smart_job_log` VALUES (8284, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:25:32', 5, '2025-09-07 16:25:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:25:31');
INSERT INTO `t_smart_job_log` VALUES (8285, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:29:32', 5, '2025-09-07 16:29:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:29:32');
INSERT INTO `t_smart_job_log` VALUES (8286, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:31:32', 4, '2025-09-07 16:31:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:31:32');
INSERT INTO `t_smart_job_log` VALUES (8287, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:33:32', 4, '2025-09-07 16:33:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:33:32');
INSERT INTO `t_smart_job_log` VALUES (8288, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:35:32', 4, '2025-09-07 16:35:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:35:32');
INSERT INTO `t_smart_job_log` VALUES (8289, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:37:32', 3, '2025-09-07 16:37:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:37:32');
INSERT INTO `t_smart_job_log` VALUES (8290, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:39:32', 4, '2025-09-07 16:39:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:39:32');
INSERT INTO `t_smart_job_log` VALUES (8291, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:41:32', 3, '2025-09-07 16:41:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:41:32');
INSERT INTO `t_smart_job_log` VALUES (8292, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:43:32', 5, '2025-09-07 16:43:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:43:32');
INSERT INTO `t_smart_job_log` VALUES (8293, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:45:32', 6, '2025-09-07 16:45:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:45:32');
INSERT INTO `t_smart_job_log` VALUES (8294, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:47:32', 3, '2025-09-07 16:47:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:47:32');
INSERT INTO `t_smart_job_log` VALUES (8295, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:49:32', 4, '2025-09-07 16:49:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:49:32');
INSERT INTO `t_smart_job_log` VALUES (8296, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:51:32', 10, '2025-09-07 16:51:32', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:51:32');
INSERT INTO `t_smart_job_log` VALUES (8297, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:53:33', 5, '2025-09-07 16:53:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:53:32');
INSERT INTO `t_smart_job_log` VALUES (8298, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 16:57:33', 5, '2025-09-07 16:57:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 16:57:32');
INSERT INTO `t_smart_job_log` VALUES (8299, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 17:01:33', 2, '2025-09-07 17:01:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:01:32');
INSERT INTO `t_smart_job_log` VALUES (8300, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 17:05:33', 3, '2025-09-07 17:05:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:05:32');
INSERT INTO `t_smart_job_log` VALUES (8301, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 17:09:33', 3, '2025-09-07 17:09:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:09:32');
INSERT INTO `t_smart_job_log` VALUES (8302, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 17:13:33', 6, '2025-09-07 17:13:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:13:32');
INSERT INTO `t_smart_job_log` VALUES (8303, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 17:15:10', 0, '2025-09-07 17:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:15:10');
INSERT INTO `t_smart_job_log` VALUES (8304, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 17:17:33', 6, '2025-09-07 17:17:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:17:32');
INSERT INTO `t_smart_job_log` VALUES (8305, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 17:21:33', 5, '2025-09-07 17:21:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:21:32');
INSERT INTO `t_smart_job_log` VALUES (8306, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 17:25:33', 4, '2025-09-07 17:25:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:25:32');
INSERT INTO `t_smart_job_log` VALUES (8307, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 17:27:33', 4, '2025-09-07 17:27:33', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 17:27:33');
INSERT INTO `t_smart_job_log` VALUES (8308, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:21:28', 3, '2025-09-07 19:21:28', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:21:27');
INSERT INTO `t_smart_job_log` VALUES (8309, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 19:21:28', 0, '2025-09-07 19:21:28', '执行完毕,随便说点什么吧', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:21:27');
INSERT INTO `t_smart_job_log` VALUES (8310, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:23:29', 3, '2025-09-07 19:23:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:23:28');
INSERT INTO `t_smart_job_log` VALUES (8311, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:27:29', 2, '2025-09-07 19:27:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:27:28');
INSERT INTO `t_smart_job_log` VALUES (8312, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:31:29', 3, '2025-09-07 19:31:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:31:28');
INSERT INTO `t_smart_job_log` VALUES (8313, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:35:29', 3, '2025-09-07 19:35:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:35:28');
INSERT INTO `t_smart_job_log` VALUES (8314, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:39:29', 3, '2025-09-07 19:39:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:39:29');
INSERT INTO `t_smart_job_log` VALUES (8315, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:41:29', 3, '2025-09-07 19:41:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:41:29');
INSERT INTO `t_smart_job_log` VALUES (8316, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:43:29', 3, '2025-09-07 19:43:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:43:29');
INSERT INTO `t_smart_job_log` VALUES (8317, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:45:29', 3, '2025-09-07 19:45:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:45:29');
INSERT INTO `t_smart_job_log` VALUES (8318, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:47:29', 4, '2025-09-07 19:47:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:47:29');
INSERT INTO `t_smart_job_log` VALUES (8319, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:49:29', 2, '2025-09-07 19:49:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:49:29');
INSERT INTO `t_smart_job_log` VALUES (8320, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:51:29', 5, '2025-09-07 19:51:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:51:29');
INSERT INTO `t_smart_job_log` VALUES (8321, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:53:29', 3, '2025-09-07 19:53:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:53:29');
INSERT INTO `t_smart_job_log` VALUES (8322, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:55:29', 3, '2025-09-07 19:55:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:55:29');
INSERT INTO `t_smart_job_log` VALUES (8323, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:57:29', 2, '2025-09-07 19:57:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:57:29');
INSERT INTO `t_smart_job_log` VALUES (8324, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 19:59:29', 2, '2025-09-07 19:59:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 19:59:29');
INSERT INTO `t_smart_job_log` VALUES (8325, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:01:29', 2, '2025-09-07 20:01:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:01:29');
INSERT INTO `t_smart_job_log` VALUES (8326, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:03:29', 2, '2025-09-07 20:03:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:03:29');
INSERT INTO `t_smart_job_log` VALUES (8327, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:05:29', 2, '2025-09-07 20:05:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:05:29');
INSERT INTO `t_smart_job_log` VALUES (8328, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:07:29', 2, '2025-09-07 20:07:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:07:29');
INSERT INTO `t_smart_job_log` VALUES (8329, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:09:29', 3, '2025-09-07 20:09:29', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:09:29');
INSERT INTO `t_smart_job_log` VALUES (8330, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:11:30', 3, '2025-09-07 20:11:30', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:11:29');
INSERT INTO `t_smart_job_log` VALUES (8331, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 20:15:11', 0, '2025-09-07 20:15:11', '执行完毕,随便说点什么吧', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:15:11');
INSERT INTO `t_smart_job_log` VALUES (8332, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:15:30', 3, '2025-09-07 20:15:30', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:15:29');
INSERT INTO `t_smart_job_log` VALUES (8333, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:19:30', 2, '2025-09-07 20:19:30', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:19:29');
INSERT INTO `t_smart_job_log` VALUES (8334, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:23:30', 2, '2025-09-07 20:23:30', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:23:29');
INSERT INTO `t_smart_job_log` VALUES (8335, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:27:30', 3, '2025-09-07 20:27:30', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:27:29');
INSERT INTO `t_smart_job_log` VALUES (8336, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:31:30', 2, '2025-09-07 20:31:30', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:31:29');
INSERT INTO `t_smart_job_log` VALUES (8337, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 20:35:30', 2, '2025-09-07 20:35:30', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 20:35:29');
INSERT INTO `t_smart_job_log` VALUES (8338, 2, '示例任务2', '执行示例任务2', 1, '2025-09-07 21:24:03', 3, '2025-09-07 21:24:03', '执行成功,本次处理数据1条', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 21:24:02');
INSERT INTO `t_smart_job_log` VALUES (8339, 1, '示例任务1', '执行示例任务1', 1, '2025-09-07 21:24:03', 0, '2025-09-07 21:24:03', '执行完毕,随便说点什么吧', '192.168.3.104', '11372', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-07 21:24:02');
INSERT INTO `t_smart_job_log` VALUES (8340, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:00:43', 5, '2025-09-08 15:00:43', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:00:43');
INSERT INTO `t_smart_job_log` VALUES (8341, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:02:43', 6, '2025-09-08 15:02:43', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:02:43');
INSERT INTO `t_smart_job_log` VALUES (8342, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:04:43', 5, '2025-09-08 15:04:43', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:04:43');
INSERT INTO `t_smart_job_log` VALUES (8343, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:06:43', 13, '2025-09-08 15:06:43', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:06:43');
INSERT INTO `t_smart_job_log` VALUES (8344, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:08:43', 5, '2025-09-08 15:08:43', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:08:43');
INSERT INTO `t_smart_job_log` VALUES (8345, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:10:43', 4, '2025-09-08 15:10:43', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:10:43');
INSERT INTO `t_smart_job_log` VALUES (8346, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:12:43', 6, '2025-09-08 15:12:43', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:12:43');
INSERT INTO `t_smart_job_log` VALUES (8347, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:14:43', 5, '2025-09-08 15:14:43', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:14:43');
INSERT INTO `t_smart_job_log` VALUES (8348, 1, '示例任务1', '执行示例任务1', 1, '2025-09-08 15:15:10', 0, '2025-09-08 15:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:15:10');
INSERT INTO `t_smart_job_log` VALUES (8349, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:16:44', 4, '2025-09-08 15:16:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:16:43');
INSERT INTO `t_smart_job_log` VALUES (8350, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:20:44', 4, '2025-09-08 15:20:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:20:43');
INSERT INTO `t_smart_job_log` VALUES (8351, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:24:44', 5, '2025-09-08 15:24:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:24:43');
INSERT INTO `t_smart_job_log` VALUES (8352, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:28:44', 4, '2025-09-08 15:28:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:28:43');
INSERT INTO `t_smart_job_log` VALUES (8353, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:32:44', 6, '2025-09-08 15:32:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:32:43');
INSERT INTO `t_smart_job_log` VALUES (8354, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:36:44', 4, '2025-09-08 15:36:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:36:43');
INSERT INTO `t_smart_job_log` VALUES (8355, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:40:44', 7, '2025-09-08 15:40:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:40:43');
INSERT INTO `t_smart_job_log` VALUES (8356, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:44:44', 6, '2025-09-08 15:44:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:44:43');
INSERT INTO `t_smart_job_log` VALUES (8357, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:48:44', 5, '2025-09-08 15:48:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:48:44');
INSERT INTO `t_smart_job_log` VALUES (8358, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:50:44', 5, '2025-09-08 15:50:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:50:44');
INSERT INTO `t_smart_job_log` VALUES (8359, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:52:44', 6, '2025-09-08 15:52:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:52:44');
INSERT INTO `t_smart_job_log` VALUES (8360, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:54:44', 5, '2025-09-08 15:54:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:54:44');
INSERT INTO `t_smart_job_log` VALUES (8361, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:56:44', 6, '2025-09-08 15:56:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:56:44');
INSERT INTO `t_smart_job_log` VALUES (8362, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 15:58:44', 7, '2025-09-08 15:58:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 15:58:44');
INSERT INTO `t_smart_job_log` VALUES (8363, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:00:44', 5, '2025-09-08 16:00:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:00:44');
INSERT INTO `t_smart_job_log` VALUES (8364, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:02:44', 4, '2025-09-08 16:02:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:02:44');
INSERT INTO `t_smart_job_log` VALUES (8365, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:04:44', 3, '2025-09-08 16:04:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:04:44');
INSERT INTO `t_smart_job_log` VALUES (8366, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:06:44', 4, '2025-09-08 16:06:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:06:44');
INSERT INTO `t_smart_job_log` VALUES (8367, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:08:44', 5, '2025-09-08 16:08:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:08:44');
INSERT INTO `t_smart_job_log` VALUES (8368, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:10:44', 9, '2025-09-08 16:10:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:10:44');
INSERT INTO `t_smart_job_log` VALUES (8369, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:12:44', 5, '2025-09-08 16:12:44', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:12:44');
INSERT INTO `t_smart_job_log` VALUES (8370, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:14:45', 5, '2025-09-08 16:14:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:14:44');
INSERT INTO `t_smart_job_log` VALUES (8371, 1, '示例任务1', '执行示例任务1', 1, '2025-09-08 16:15:10', 0, '2025-09-08 16:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:15:10');
INSERT INTO `t_smart_job_log` VALUES (8372, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:18:45', 4, '2025-09-08 16:18:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:18:44');
INSERT INTO `t_smart_job_log` VALUES (8373, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:22:45', 5, '2025-09-08 16:22:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:22:44');
INSERT INTO `t_smart_job_log` VALUES (8374, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:26:45', 6, '2025-09-08 16:26:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:26:44');
INSERT INTO `t_smart_job_log` VALUES (8375, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:30:45', 4, '2025-09-08 16:30:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:30:44');
INSERT INTO `t_smart_job_log` VALUES (8376, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:34:45', 6, '2025-09-08 16:34:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:34:44');
INSERT INTO `t_smart_job_log` VALUES (8377, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:38:45', 7, '2025-09-08 16:38:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:38:44');
INSERT INTO `t_smart_job_log` VALUES (8378, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:42:45', 3, '2025-09-08 16:42:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:42:44');
INSERT INTO `t_smart_job_log` VALUES (8379, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:46:45', 3, '2025-09-08 16:46:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:46:45');
INSERT INTO `t_smart_job_log` VALUES (8380, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:48:45', 2, '2025-09-08 16:48:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:48:45');
INSERT INTO `t_smart_job_log` VALUES (8381, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:50:45', 6, '2025-09-08 16:50:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:50:45');
INSERT INTO `t_smart_job_log` VALUES (8382, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:52:45', 8, '2025-09-08 16:52:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:52:45');
INSERT INTO `t_smart_job_log` VALUES (8383, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:54:45', 4, '2025-09-08 16:54:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:54:45');
INSERT INTO `t_smart_job_log` VALUES (8384, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:56:45', 5, '2025-09-08 16:56:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:56:45');
INSERT INTO `t_smart_job_log` VALUES (8385, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 16:58:45', 4, '2025-09-08 16:58:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 16:58:45');
INSERT INTO `t_smart_job_log` VALUES (8386, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:00:45', 3, '2025-09-08 17:00:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:00:45');
INSERT INTO `t_smart_job_log` VALUES (8387, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:02:45', 3, '2025-09-08 17:02:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:02:45');
INSERT INTO `t_smart_job_log` VALUES (8388, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:04:45', 3, '2025-09-08 17:04:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:04:45');
INSERT INTO `t_smart_job_log` VALUES (8389, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:06:45', 2, '2025-09-08 17:06:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:06:45');
INSERT INTO `t_smart_job_log` VALUES (8390, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:08:45', 8, '2025-09-08 17:08:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:08:45');
INSERT INTO `t_smart_job_log` VALUES (8391, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:10:45', 3, '2025-09-08 17:10:45', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:10:45');
INSERT INTO `t_smart_job_log` VALUES (8392, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:12:46', 2, '2025-09-08 17:12:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:12:45');
INSERT INTO `t_smart_job_log` VALUES (8393, 1, '示例任务1', '执行示例任务1', 1, '2025-09-08 17:15:10', 0, '2025-09-08 17:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:15:10');
INSERT INTO `t_smart_job_log` VALUES (8394, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:16:46', 3, '2025-09-08 17:16:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:16:45');
INSERT INTO `t_smart_job_log` VALUES (8395, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:20:46', 3, '2025-09-08 17:20:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:20:45');
INSERT INTO `t_smart_job_log` VALUES (8396, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:24:46', 3, '2025-09-08 17:24:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:24:45');
INSERT INTO `t_smart_job_log` VALUES (8397, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:28:46', 4, '2025-09-08 17:28:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:28:45');
INSERT INTO `t_smart_job_log` VALUES (8398, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:32:46', 2, '2025-09-08 17:32:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:32:45');
INSERT INTO `t_smart_job_log` VALUES (8399, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:36:46', 4, '2025-09-08 17:36:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:36:45');
INSERT INTO `t_smart_job_log` VALUES (8400, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:40:46', 2, '2025-09-08 17:40:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:40:45');
INSERT INTO `t_smart_job_log` VALUES (8401, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:44:46', 2, '2025-09-08 17:44:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:44:45');
INSERT INTO `t_smart_job_log` VALUES (8402, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:48:46', 3, '2025-09-08 17:48:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:48:45');
INSERT INTO `t_smart_job_log` VALUES (8403, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:52:46', 3, '2025-09-08 17:52:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:52:46');
INSERT INTO `t_smart_job_log` VALUES (8404, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:54:46', 4, '2025-09-08 17:54:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:54:46');
INSERT INTO `t_smart_job_log` VALUES (8405, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:56:46', 2, '2025-09-08 17:56:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:56:46');
INSERT INTO `t_smart_job_log` VALUES (8406, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 17:58:46', 2, '2025-09-08 17:58:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 17:58:46');
INSERT INTO `t_smart_job_log` VALUES (8407, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 18:00:46', 3, '2025-09-08 18:00:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 18:00:46');
INSERT INTO `t_smart_job_log` VALUES (8408, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 18:02:46', 3, '2025-09-08 18:02:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 18:02:46');
INSERT INTO `t_smart_job_log` VALUES (8409, 2, '示例任务2', '执行示例任务2', 1, '2025-09-08 18:04:46', 2, '2025-09-08 18:04:46', '执行成功,本次处理数据1条', '192.168.3.104', '13452', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-08 18:04:46');
INSERT INTO `t_smart_job_log` VALUES (8410, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:23:43', 7, '2025-09-12 20:23:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:23:42');
INSERT INTO `t_smart_job_log` VALUES (8411, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:27:43', 7, '2025-09-12 20:27:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:27:43');
INSERT INTO `t_smart_job_log` VALUES (8412, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:29:43', 8, '2025-09-12 20:29:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:29:43');
INSERT INTO `t_smart_job_log` VALUES (8413, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:31:43', 7, '2025-09-12 20:31:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:31:43');
INSERT INTO `t_smart_job_log` VALUES (8414, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:33:43', 5, '2025-09-12 20:33:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:33:43');
INSERT INTO `t_smart_job_log` VALUES (8415, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:35:43', 6, '2025-09-12 20:35:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:35:43');
INSERT INTO `t_smart_job_log` VALUES (8416, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:37:43', 6, '2025-09-12 20:37:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:37:43');
INSERT INTO `t_smart_job_log` VALUES (8417, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:39:43', 5, '2025-09-12 20:39:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:39:43');
INSERT INTO `t_smart_job_log` VALUES (8418, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:41:43', 4, '2025-09-12 20:41:43', '执行成功,本次处理数据1条', '192.168.3.104', '14056', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:41:43');
INSERT INTO `t_smart_job_log` VALUES (8419, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:44:35', 7, '2025-09-12 20:44:35', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:44:35');
INSERT INTO `t_smart_job_log` VALUES (8420, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:46:36', 5, '2025-09-12 20:46:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:46:35');
INSERT INTO `t_smart_job_log` VALUES (8421, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:50:36', 5, '2025-09-12 20:50:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:50:35');
INSERT INTO `t_smart_job_log` VALUES (8422, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:54:36', 5, '2025-09-12 20:54:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:54:35');
INSERT INTO `t_smart_job_log` VALUES (8423, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 20:58:36', 5, '2025-09-12 20:58:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 20:58:35');
INSERT INTO `t_smart_job_log` VALUES (8424, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:02:36', 4, '2025-09-12 21:02:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:02:35');
INSERT INTO `t_smart_job_log` VALUES (8425, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:06:36', 4, '2025-09-12 21:06:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:06:35');
INSERT INTO `t_smart_job_log` VALUES (8426, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:10:36', 4, '2025-09-12 21:10:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:10:35');
INSERT INTO `t_smart_job_log` VALUES (8427, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:14:36', 4, '2025-09-12 21:14:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:14:35');
INSERT INTO `t_smart_job_log` VALUES (8428, 1, '示例任务1', '执行示例任务1', 1, '2025-09-12 21:15:10', 0, '2025-09-12 21:15:10', '执行完毕,随便说点什么吧', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:15:10');
INSERT INTO `t_smart_job_log` VALUES (8429, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:16:36', 3, '2025-09-12 21:16:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:16:36');
INSERT INTO `t_smart_job_log` VALUES (8430, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:18:36', 3, '2025-09-12 21:18:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:18:36');
INSERT INTO `t_smart_job_log` VALUES (8431, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:20:36', 4, '2025-09-12 21:20:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:20:36');
INSERT INTO `t_smart_job_log` VALUES (8432, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:22:36', 4, '2025-09-12 21:22:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:22:36');
INSERT INTO `t_smart_job_log` VALUES (8433, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:24:36', 4, '2025-09-12 21:24:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:24:36');
INSERT INTO `t_smart_job_log` VALUES (8434, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:26:36', 3, '2025-09-12 21:26:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:26:36');
INSERT INTO `t_smart_job_log` VALUES (8435, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:28:36', 3, '2025-09-12 21:28:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:28:36');
INSERT INTO `t_smart_job_log` VALUES (8436, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:30:36', 9, '2025-09-12 21:30:36', '执行成功,本次处理数据1条', '192.168.3.104', '18360', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:30:36');
INSERT INTO `t_smart_job_log` VALUES (8437, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:33:24', 5, '2025-09-12 21:33:24', '执行成功,本次处理数据1条', '192.168.3.104', '19444', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:33:24');
INSERT INTO `t_smart_job_log` VALUES (8438, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:35:24', 6, '2025-09-12 21:35:24', '执行成功,本次处理数据1条', '192.168.3.104', '19444', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:35:24');
INSERT INTO `t_smart_job_log` VALUES (8439, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:37:24', 6, '2025-09-12 21:37:24', '执行成功,本次处理数据1条', '192.168.3.104', '19444', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:37:24');
INSERT INTO `t_smart_job_log` VALUES (8440, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:39:24', 4, '2025-09-12 21:39:24', '执行成功,本次处理数据1条', '192.168.3.104', '19444', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:39:24');
INSERT INTO `t_smart_job_log` VALUES (8441, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:41:24', 4, '2025-09-12 21:41:24', '执行成功,本次处理数据1条', '192.168.3.104', '19444', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:41:24');
INSERT INTO `t_smart_job_log` VALUES (8442, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:44:08', 5, '2025-09-12 21:44:08', '执行成功,本次处理数据1条', '192.168.3.104', '6336', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:44:07');
INSERT INTO `t_smart_job_log` VALUES (8443, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:48:08', 4, '2025-09-12 21:48:08', '执行成功,本次处理数据1条', '192.168.3.104', '6336', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:48:07');
INSERT INTO `t_smart_job_log` VALUES (8444, 2, '示例任务2', '执行示例任务2', 1, '2025-09-12 21:52:08', 4, '2025-09-12 21:52:08', '执行成功,本次处理数据1条', '192.168.3.104', '6336', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-12 21:52:07');
INSERT INTO `t_smart_job_log` VALUES (8445, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 13:41:40', 9, '2025-09-14 13:41:40', '执行成功,本次处理数据1条', '192.168.43.130', '18940', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 13:41:40');
INSERT INTO `t_smart_job_log` VALUES (8446, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 13:43:41', 5, '2025-09-14 13:43:41', '执行成功,本次处理数据1条', '192.168.43.130', '18940', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 13:43:40');
INSERT INTO `t_smart_job_log` VALUES (8447, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 13:47:41', 4, '2025-09-14 13:47:41', '执行成功,本次处理数据1条', '192.168.43.130', '18940', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 13:47:40');
INSERT INTO `t_smart_job_log` VALUES (8448, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 13:51:41', 10, '2025-09-14 13:51:41', '执行成功,本次处理数据1条', '192.168.43.130', '18940', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 13:51:40');
INSERT INTO `t_smart_job_log` VALUES (8449, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 13:56:08', 12, '2025-09-14 13:56:08', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 13:56:07');
INSERT INTO `t_smart_job_log` VALUES (8450, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:00:42', 22, '2025-09-14 14:00:42', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:00:42');
INSERT INTO `t_smart_job_log` VALUES (8451, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:02:46', 5, '2025-09-14 14:02:46', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:02:45');
INSERT INTO `t_smart_job_log` VALUES (8452, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:06:46', 6, '2025-09-14 14:06:46', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:06:45');
INSERT INTO `t_smart_job_log` VALUES (8453, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:10:46', 5, '2025-09-14 14:10:46', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:10:46');
INSERT INTO `t_smart_job_log` VALUES (8454, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:12:46', 11, '2025-09-14 14:12:46', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:12:46');
INSERT INTO `t_smart_job_log` VALUES (8455, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:14:46', 7, '2025-09-14 14:14:46', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:14:46');
INSERT INTO `t_smart_job_log` VALUES (8456, 1, '示例任务1', '执行示例任务1', 1, '2025-09-14 14:15:10', 0, '2025-09-14 14:15:10', '执行完毕,随便说点什么吧', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:15:10');
INSERT INTO `t_smart_job_log` VALUES (8457, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:40:47', 8, '2025-09-14 14:40:47', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:40:46');
INSERT INTO `t_smart_job_log` VALUES (8458, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:42:48', 4, '2025-09-14 14:42:48', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:42:47');
INSERT INTO `t_smart_job_log` VALUES (8459, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 14:46:48', 6, '2025-09-14 14:46:48', '执行成功,本次处理数据1条', '192.168.43.130', '6552', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 14:46:48');
INSERT INTO `t_smart_job_log` VALUES (8460, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:31:20', 5, '2025-09-14 15:31:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:31:19');
INSERT INTO `t_smart_job_log` VALUES (8461, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:35:20', 5, '2025-09-14 15:35:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:35:19');
INSERT INTO `t_smart_job_log` VALUES (8462, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:39:20', 4, '2025-09-14 15:39:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:39:19');
INSERT INTO `t_smart_job_log` VALUES (8463, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:43:20', 5, '2025-09-14 15:43:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:43:19');
INSERT INTO `t_smart_job_log` VALUES (8464, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:47:20', 5, '2025-09-14 15:47:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:47:19');
INSERT INTO `t_smart_job_log` VALUES (8465, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:51:20', 4, '2025-09-14 15:51:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:51:19');
INSERT INTO `t_smart_job_log` VALUES (8466, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:55:20', 4, '2025-09-14 15:55:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:55:20');
INSERT INTO `t_smart_job_log` VALUES (8467, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:57:20', 4, '2025-09-14 15:57:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:57:20');
INSERT INTO `t_smart_job_log` VALUES (8468, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 15:59:20', 3, '2025-09-14 15:59:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 15:59:20');
INSERT INTO `t_smart_job_log` VALUES (8469, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:01:20', 4, '2025-09-14 16:01:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:01:20');
INSERT INTO `t_smart_job_log` VALUES (8470, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:03:20', 3, '2025-09-14 16:03:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:03:20');
INSERT INTO `t_smart_job_log` VALUES (8471, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:05:20', 9, '2025-09-14 16:05:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:05:20');
INSERT INTO `t_smart_job_log` VALUES (8472, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:07:20', 4, '2025-09-14 16:07:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:07:20');
INSERT INTO `t_smart_job_log` VALUES (8473, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:09:20', 4, '2025-09-14 16:09:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:09:20');
INSERT INTO `t_smart_job_log` VALUES (8474, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:11:20', 4, '2025-09-14 16:11:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:11:20');
INSERT INTO `t_smart_job_log` VALUES (8475, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:13:20', 4, '2025-09-14 16:13:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:13:20');
INSERT INTO `t_smart_job_log` VALUES (8476, 1, '示例任务1', '执行示例任务1', 1, '2025-09-14 16:15:10', 0, '2025-09-14 16:15:10', '执行完毕,随便说点什么吧', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:15:10');
INSERT INTO `t_smart_job_log` VALUES (8477, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:15:20', 5, '2025-09-14 16:15:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:15:20');
INSERT INTO `t_smart_job_log` VALUES (8478, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:17:20', 9, '2025-09-14 16:17:20', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:17:20');
INSERT INTO `t_smart_job_log` VALUES (8479, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:19:20', 11, '2025-09-14 16:19:21', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:19:20');
INSERT INTO `t_smart_job_log` VALUES (8480, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:21:21', 3, '2025-09-14 16:21:21', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:21:20');
INSERT INTO `t_smart_job_log` VALUES (8481, 2, '示例任务2', '执行示例任务2', 1, '2025-09-14 16:25:21', 4, '2025-09-14 16:25:21', '执行成功,本次处理数据1条', '10.101.116.254', '19568', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-14 16:25:20');
INSERT INTO `t_smart_job_log` VALUES (8482, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:12:24', 6, '2025-09-15 16:12:24', '执行成功,本次处理数据1条', '', '6248', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:12:24');
INSERT INTO `t_smart_job_log` VALUES (8483, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:30:36', 6, '2025-09-15 16:30:36', '执行成功,本次处理数据1条', '', '4236', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:30:35');
INSERT INTO `t_smart_job_log` VALUES (8484, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:33:17', 9, '2025-09-15 16:33:17', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:33:17');
INSERT INTO `t_smart_job_log` VALUES (8485, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:35:17', 5, '2025-09-15 16:35:17', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:35:17');
INSERT INTO `t_smart_job_log` VALUES (8486, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:37:17', 5, '2025-09-15 16:37:17', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:37:17');
INSERT INTO `t_smart_job_log` VALUES (8487, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:39:17', 5, '2025-09-15 16:39:17', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:39:17');
INSERT INTO `t_smart_job_log` VALUES (8488, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:41:17', 12, '2025-09-15 16:41:17', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:41:17');
INSERT INTO `t_smart_job_log` VALUES (8489, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:43:18', 5, '2025-09-15 16:43:18', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:43:17');
INSERT INTO `t_smart_job_log` VALUES (8490, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:47:18', 4, '2025-09-15 16:47:18', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:47:17');
INSERT INTO `t_smart_job_log` VALUES (8491, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:51:18', 7, '2025-09-15 16:51:18', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:51:17');
INSERT INTO `t_smart_job_log` VALUES (8492, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 16:55:18', 7, '2025-09-15 16:55:18', '执行成功,本次处理数据1条', '', '8396', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 16:55:17');
INSERT INTO `t_smart_job_log` VALUES (8493, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:00:23', 13, '2025-09-15 17:00:23', '执行成功,本次处理数据1条', '', '14632', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:00:22');
INSERT INTO `t_smart_job_log` VALUES (8494, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:03:30', 7, '2025-09-15 17:03:30', '执行成功,本次处理数据1条', '', '18944', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:03:30');
INSERT INTO `t_smart_job_log` VALUES (8495, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:07:21', 5, '2025-09-15 17:07:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:07:20');
INSERT INTO `t_smart_job_log` VALUES (8496, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:11:21', 4, '2025-09-15 17:11:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:11:20');
INSERT INTO `t_smart_job_log` VALUES (8497, 1, '示例任务1', '执行示例任务1', 1, '2025-09-15 17:15:10', 0, '2025-09-15 17:15:10', '执行完毕,随便说点什么吧', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:15:10');
INSERT INTO `t_smart_job_log` VALUES (8498, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:15:21', 5, '2025-09-15 17:15:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:15:20');
INSERT INTO `t_smart_job_log` VALUES (8499, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:19:21', 4, '2025-09-15 17:19:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:19:20');
INSERT INTO `t_smart_job_log` VALUES (8500, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:23:21', 4, '2025-09-15 17:23:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:23:20');
INSERT INTO `t_smart_job_log` VALUES (8501, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:27:21', 4, '2025-09-15 17:27:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:27:20');
INSERT INTO `t_smart_job_log` VALUES (8502, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:29:21', 4, '2025-09-15 17:29:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:29:21');
INSERT INTO `t_smart_job_log` VALUES (8503, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:31:21', 5, '2025-09-15 17:31:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:31:21');
INSERT INTO `t_smart_job_log` VALUES (8504, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:33:21', 4, '2025-09-15 17:33:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:33:21');
INSERT INTO `t_smart_job_log` VALUES (8505, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:35:21', 3, '2025-09-15 17:35:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:35:21');
INSERT INTO `t_smart_job_log` VALUES (8506, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:37:21', 3, '2025-09-15 17:37:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:37:21');
INSERT INTO `t_smart_job_log` VALUES (8507, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:39:21', 3, '2025-09-15 17:39:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:39:21');
INSERT INTO `t_smart_job_log` VALUES (8508, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:41:21', 3, '2025-09-15 17:41:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:41:21');
INSERT INTO `t_smart_job_log` VALUES (8509, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:43:21', 4, '2025-09-15 17:43:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:43:21');
INSERT INTO `t_smart_job_log` VALUES (8510, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:45:21', 3, '2025-09-15 17:45:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:45:21');
INSERT INTO `t_smart_job_log` VALUES (8511, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:47:21', 3, '2025-09-15 17:47:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:47:21');
INSERT INTO `t_smart_job_log` VALUES (8512, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 17:49:21', 3, '2025-09-15 17:49:21', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 17:49:21');
INSERT INTO `t_smart_job_log` VALUES (8513, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 18:50:03', 6, '2025-09-15 18:50:03', '执行成功,本次处理数据1条', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 18:50:02');
INSERT INTO `t_smart_job_log` VALUES (8514, 1, '示例任务1', '执行示例任务1', 1, '2025-09-15 18:50:03', 0, '2025-09-15 18:50:03', '执行完毕,随便说点什么吧', '', '10352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 18:50:02');
INSERT INTO `t_smart_job_log` VALUES (8515, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 18:53:00', 11, '2025-09-15 18:53:00', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 18:53:00');
INSERT INTO `t_smart_job_log` VALUES (8516, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 18:55:01', 5, '2025-09-15 18:55:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 18:55:00');
INSERT INTO `t_smart_job_log` VALUES (8517, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 18:59:01', 5, '2025-09-15 18:59:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 18:59:00');
INSERT INTO `t_smart_job_log` VALUES (8518, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:03:01', 5, '2025-09-15 19:03:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:03:00');
INSERT INTO `t_smart_job_log` VALUES (8519, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:07:01', 4, '2025-09-15 19:07:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:07:00');
INSERT INTO `t_smart_job_log` VALUES (8520, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:11:01', 3, '2025-09-15 19:11:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:11:00');
INSERT INTO `t_smart_job_log` VALUES (8521, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:15:01', 4, '2025-09-15 19:15:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:15:00');
INSERT INTO `t_smart_job_log` VALUES (8522, 1, '示例任务1', '执行示例任务1', 1, '2025-09-15 19:15:10', 0, '2025-09-15 19:15:10', '执行完毕,随便说点什么吧', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:15:10');
INSERT INTO `t_smart_job_log` VALUES (8523, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:19:01', 4, '2025-09-15 19:19:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:19:00');
INSERT INTO `t_smart_job_log` VALUES (8524, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:23:01', 4, '2025-09-15 19:23:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:23:00');
INSERT INTO `t_smart_job_log` VALUES (8525, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:27:01', 3, '2025-09-15 19:27:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:27:00');
INSERT INTO `t_smart_job_log` VALUES (8526, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:29:01', 3, '2025-09-15 19:29:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:29:01');
INSERT INTO `t_smart_job_log` VALUES (8527, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:31:01', 3, '2025-09-15 19:31:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:31:01');
INSERT INTO `t_smart_job_log` VALUES (8528, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:33:01', 3, '2025-09-15 19:33:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:33:01');
INSERT INTO `t_smart_job_log` VALUES (8529, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:35:01', 3, '2025-09-15 19:35:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:35:01');
INSERT INTO `t_smart_job_log` VALUES (8530, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:37:01', 3, '2025-09-15 19:37:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:37:01');
INSERT INTO `t_smart_job_log` VALUES (8531, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:39:01', 3, '2025-09-15 19:39:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:39:01');
INSERT INTO `t_smart_job_log` VALUES (8532, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:41:01', 3, '2025-09-15 19:41:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:41:01');
INSERT INTO `t_smart_job_log` VALUES (8533, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 19:43:01', 4, '2025-09-15 19:43:01', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 19:43:01');
INSERT INTO `t_smart_job_log` VALUES (8534, 2, '示例任务2', '执行示例任务2', 1, '2025-09-15 21:57:08', 6, '2025-09-15 21:57:08', '执行成功,本次处理数据1条', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 21:57:08');
INSERT INTO `t_smart_job_log` VALUES (8535, 1, '示例任务1', '执行示例任务1', 1, '2025-09-15 21:57:08', 0, '2025-09-15 21:57:08', '执行完毕,随便说点什么吧', '', '21532', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-15 21:57:08');
INSERT INTO `t_smart_job_log` VALUES (8536, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:24:20', 6, '2025-09-16 13:24:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:24:20');
INSERT INTO `t_smart_job_log` VALUES (8537, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:26:20', 6, '2025-09-16 13:26:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:26:20');
INSERT INTO `t_smart_job_log` VALUES (8538, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:28:20', 5, '2025-09-16 13:28:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:28:20');
INSERT INTO `t_smart_job_log` VALUES (8539, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:30:20', 5, '2025-09-16 13:30:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:30:20');
INSERT INTO `t_smart_job_log` VALUES (8540, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:32:20', 6, '2025-09-16 13:32:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:32:20');
INSERT INTO `t_smart_job_log` VALUES (8541, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:34:20', 4, '2025-09-16 13:34:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:34:20');
INSERT INTO `t_smart_job_log` VALUES (8542, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:36:20', 4, '2025-09-16 13:36:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:36:20');
INSERT INTO `t_smart_job_log` VALUES (8543, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:38:20', 7, '2025-09-16 13:38:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:38:20');
INSERT INTO `t_smart_job_log` VALUES (8544, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:40:20', 7, '2025-09-16 13:40:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:40:20');
INSERT INTO `t_smart_job_log` VALUES (8545, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:42:20', 4, '2025-09-16 13:42:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:42:20');
INSERT INTO `t_smart_job_log` VALUES (8546, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:44:20', 5, '2025-09-16 13:44:20', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:44:20');
INSERT INTO `t_smart_job_log` VALUES (8547, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:46:21', 4, '2025-09-16 13:46:21', '执行成功,本次处理数据1条', '', '14684', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:46:20');
INSERT INTO `t_smart_job_log` VALUES (8548, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:48:45', 4, '2025-09-16 13:48:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:48:44');
INSERT INTO `t_smart_job_log` VALUES (8549, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:52:45', 5, '2025-09-16 13:52:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:52:44');
INSERT INTO `t_smart_job_log` VALUES (8550, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 13:56:45', 8, '2025-09-16 13:56:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 13:56:44');
INSERT INTO `t_smart_job_log` VALUES (8551, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:00:45', 8, '2025-09-16 14:00:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:00:44');
INSERT INTO `t_smart_job_log` VALUES (8552, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:04:45', 7, '2025-09-16 14:04:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:04:44');
INSERT INTO `t_smart_job_log` VALUES (8553, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:06:45', 5, '2025-09-16 14:06:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:06:45');
INSERT INTO `t_smart_job_log` VALUES (8554, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:08:45', 4, '2025-09-16 14:08:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:08:45');
INSERT INTO `t_smart_job_log` VALUES (8555, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:10:45', 9, '2025-09-16 14:10:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:10:45');
INSERT INTO `t_smart_job_log` VALUES (8556, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:12:45', 8, '2025-09-16 14:12:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:12:45');
INSERT INTO `t_smart_job_log` VALUES (8557, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:14:45', 5, '2025-09-16 14:14:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:14:45');
INSERT INTO `t_smart_job_log` VALUES (8558, 1, '示例任务1', '执行示例任务1', 1, '2025-09-16 14:15:10', 0, '2025-09-16 14:15:10', '执行完毕,随便说点什么吧', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:15:10');
INSERT INTO `t_smart_job_log` VALUES (8559, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:16:45', 6, '2025-09-16 14:16:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:16:45');
INSERT INTO `t_smart_job_log` VALUES (8560, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:18:45', 7, '2025-09-16 14:18:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:18:45');
INSERT INTO `t_smart_job_log` VALUES (8561, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:20:45', 6, '2025-09-16 14:20:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:20:45');
INSERT INTO `t_smart_job_log` VALUES (8562, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:22:45', 8, '2025-09-16 14:22:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:22:45');
INSERT INTO `t_smart_job_log` VALUES (8563, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:24:45', 5, '2025-09-16 14:24:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:24:45');
INSERT INTO `t_smart_job_log` VALUES (8564, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:26:45', 5, '2025-09-16 14:26:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:26:45');
INSERT INTO `t_smart_job_log` VALUES (8565, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:28:45', 4, '2025-09-16 14:28:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:28:45');
INSERT INTO `t_smart_job_log` VALUES (8566, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:30:46', 4, '2025-09-16 14:30:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:30:45');
INSERT INTO `t_smart_job_log` VALUES (8567, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:34:46', 8, '2025-09-16 14:34:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:34:45');
INSERT INTO `t_smart_job_log` VALUES (8568, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:38:46', 4, '2025-09-16 14:38:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:38:45');
INSERT INTO `t_smart_job_log` VALUES (8569, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:42:46', 4, '2025-09-16 14:42:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:42:45');
INSERT INTO `t_smart_job_log` VALUES (8570, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:46:46', 5, '2025-09-16 14:46:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:46:45');
INSERT INTO `t_smart_job_log` VALUES (8571, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:50:46', 4, '2025-09-16 14:50:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:50:45');
INSERT INTO `t_smart_job_log` VALUES (8572, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:54:46', 4, '2025-09-16 14:54:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:54:45');
INSERT INTO `t_smart_job_log` VALUES (8573, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 14:58:46', 3, '2025-09-16 14:58:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 14:58:45');
INSERT INTO `t_smart_job_log` VALUES (8574, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:02:46', 6, '2025-09-16 15:02:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:02:46');
INSERT INTO `t_smart_job_log` VALUES (8575, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:04:46', 4, '2025-09-16 15:04:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:04:46');
INSERT INTO `t_smart_job_log` VALUES (8576, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:06:46', 6, '2025-09-16 15:06:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:06:46');
INSERT INTO `t_smart_job_log` VALUES (8577, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:08:46', 4, '2025-09-16 15:08:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:08:46');
INSERT INTO `t_smart_job_log` VALUES (8578, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:10:46', 5, '2025-09-16 15:10:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:10:46');
INSERT INTO `t_smart_job_log` VALUES (8579, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:12:46', 3, '2025-09-16 15:12:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:12:46');
INSERT INTO `t_smart_job_log` VALUES (8580, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:14:46', 5, '2025-09-16 15:14:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:14:46');
INSERT INTO `t_smart_job_log` VALUES (8581, 1, '示例任务1', '执行示例任务1', 1, '2025-09-16 15:15:10', 0, '2025-09-16 15:15:10', '执行完毕,随便说点什么吧', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:15:10');
INSERT INTO `t_smart_job_log` VALUES (8582, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:16:46', 5, '2025-09-16 15:16:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:16:46');
INSERT INTO `t_smart_job_log` VALUES (8583, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:18:46', 4, '2025-09-16 15:18:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:18:46');
INSERT INTO `t_smart_job_log` VALUES (8584, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:20:46', 3, '2025-09-16 15:20:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:20:46');
INSERT INTO `t_smart_job_log` VALUES (8585, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:22:46', 6, '2025-09-16 15:22:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:22:46');
INSERT INTO `t_smart_job_log` VALUES (8586, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:24:46', 3, '2025-09-16 15:24:46', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:24:46');
INSERT INTO `t_smart_job_log` VALUES (8587, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:26:47', 4, '2025-09-16 15:26:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:26:46');
INSERT INTO `t_smart_job_log` VALUES (8588, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:30:47', 3, '2025-09-16 15:30:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:30:46');
INSERT INTO `t_smart_job_log` VALUES (8589, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:34:47', 6, '2025-09-16 15:34:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:34:46');
INSERT INTO `t_smart_job_log` VALUES (8590, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:38:47', 2, '2025-09-16 15:38:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:38:46');
INSERT INTO `t_smart_job_log` VALUES (8591, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:42:47', 3, '2025-09-16 15:42:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:42:46');
INSERT INTO `t_smart_job_log` VALUES (8592, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:46:47', 3, '2025-09-16 15:46:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:46:46');
INSERT INTO `t_smart_job_log` VALUES (8593, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:50:47', 3, '2025-09-16 15:50:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:50:46');
INSERT INTO `t_smart_job_log` VALUES (8594, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:54:47', 3, '2025-09-16 15:54:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:54:46');
INSERT INTO `t_smart_job_log` VALUES (8595, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 15:58:47', 3, '2025-09-16 15:58:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 15:58:46');
INSERT INTO `t_smart_job_log` VALUES (8596, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:02:47', 2, '2025-09-16 16:02:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:02:46');
INSERT INTO `t_smart_job_log` VALUES (8597, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:04:47', 3, '2025-09-16 16:04:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:04:47');
INSERT INTO `t_smart_job_log` VALUES (8598, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:06:47', 3, '2025-09-16 16:06:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:06:47');
INSERT INTO `t_smart_job_log` VALUES (8599, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:08:47', 2, '2025-09-16 16:08:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:08:47');
INSERT INTO `t_smart_job_log` VALUES (8600, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:10:47', 2, '2025-09-16 16:10:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:10:47');
INSERT INTO `t_smart_job_log` VALUES (8601, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:12:47', 2, '2025-09-16 16:12:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:12:47');
INSERT INTO `t_smart_job_log` VALUES (8602, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:14:47', 3, '2025-09-16 16:14:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:14:47');
INSERT INTO `t_smart_job_log` VALUES (8603, 1, '示例任务1', '执行示例任务1', 1, '2025-09-16 16:15:10', 0, '2025-09-16 16:15:10', '执行完毕,随便说点什么吧', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:15:10');
INSERT INTO `t_smart_job_log` VALUES (8604, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:16:47', 3, '2025-09-16 16:16:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:16:47');
INSERT INTO `t_smart_job_log` VALUES (8605, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:18:47', 3, '2025-09-16 16:18:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:18:47');
INSERT INTO `t_smart_job_log` VALUES (8606, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:20:47', 3, '2025-09-16 16:20:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:20:47');
INSERT INTO `t_smart_job_log` VALUES (8607, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:22:47', 3, '2025-09-16 16:22:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:22:47');
INSERT INTO `t_smart_job_log` VALUES (8608, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:24:47', 6, '2025-09-16 16:24:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:24:47');
INSERT INTO `t_smart_job_log` VALUES (8609, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:26:47', 4, '2025-09-16 16:26:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:26:47');
INSERT INTO `t_smart_job_log` VALUES (8610, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:28:47', 8, '2025-09-16 16:28:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:28:47');
INSERT INTO `t_smart_job_log` VALUES (8611, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:30:47', 3, '2025-09-16 16:30:47', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:30:47');
INSERT INTO `t_smart_job_log` VALUES (8612, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:32:48', 8, '2025-09-16 16:32:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:32:47');
INSERT INTO `t_smart_job_log` VALUES (8613, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:36:48', 2, '2025-09-16 16:36:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:36:47');
INSERT INTO `t_smart_job_log` VALUES (8614, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:40:48', 4, '2025-09-16 16:40:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:40:47');
INSERT INTO `t_smart_job_log` VALUES (8615, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:44:48', 4, '2025-09-16 16:44:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:44:47');
INSERT INTO `t_smart_job_log` VALUES (8616, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:48:48', 5, '2025-09-16 16:48:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:48:47');
INSERT INTO `t_smart_job_log` VALUES (8617, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:52:48', 4, '2025-09-16 16:52:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:52:47');
INSERT INTO `t_smart_job_log` VALUES (8618, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 16:56:48', 2, '2025-09-16 16:56:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 16:56:47');
INSERT INTO `t_smart_job_log` VALUES (8619, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:00:48', 3, '2025-09-16 17:00:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:00:47');
INSERT INTO `t_smart_job_log` VALUES (8620, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:04:48', 2, '2025-09-16 17:04:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:04:47');
INSERT INTO `t_smart_job_log` VALUES (8621, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:08:48', 2, '2025-09-16 17:08:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:08:48');
INSERT INTO `t_smart_job_log` VALUES (8622, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:10:48', 6, '2025-09-16 17:10:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:10:48');
INSERT INTO `t_smart_job_log` VALUES (8623, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:12:48', 5, '2025-09-16 17:12:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:12:48');
INSERT INTO `t_smart_job_log` VALUES (8624, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:14:48', 2, '2025-09-16 17:14:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:14:48');
INSERT INTO `t_smart_job_log` VALUES (8625, 1, '示例任务1', '执行示例任务1', 1, '2025-09-16 17:15:10', 0, '2025-09-16 17:15:10', '执行完毕,随便说点什么吧', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:15:10');
INSERT INTO `t_smart_job_log` VALUES (8626, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:16:48', 2, '2025-09-16 17:16:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:16:48');
INSERT INTO `t_smart_job_log` VALUES (8627, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:18:48', 2, '2025-09-16 17:18:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:18:48');
INSERT INTO `t_smart_job_log` VALUES (8628, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:20:48', 4, '2025-09-16 17:20:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:20:48');
INSERT INTO `t_smart_job_log` VALUES (8629, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:22:48', 3, '2025-09-16 17:22:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:22:48');
INSERT INTO `t_smart_job_log` VALUES (8630, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:24:48', 6, '2025-09-16 17:24:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:24:48');
INSERT INTO `t_smart_job_log` VALUES (8631, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:26:48', 4, '2025-09-16 17:26:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:26:48');
INSERT INTO `t_smart_job_log` VALUES (8632, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:28:48', 3, '2025-09-16 17:28:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:28:48');
INSERT INTO `t_smart_job_log` VALUES (8633, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:30:48', 5, '2025-09-16 17:30:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:30:48');
INSERT INTO `t_smart_job_log` VALUES (8634, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 17:32:48', 3, '2025-09-16 17:32:48', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 17:32:48');
INSERT INTO `t_smart_job_log` VALUES (8635, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:15:42', 3, '2025-09-16 18:15:42', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:15:42');
INSERT INTO `t_smart_job_log` VALUES (8636, 1, '示例任务1', '执行示例任务1', 1, '2025-09-16 18:15:42', 0, '2025-09-16 18:15:42', '执行完毕,随便说点什么吧', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:15:42');
INSERT INTO `t_smart_job_log` VALUES (8637, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:17:42', 3, '2025-09-16 18:17:42', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:17:42');
INSERT INTO `t_smart_job_log` VALUES (8638, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:19:42', 3, '2025-09-16 18:19:42', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:19:42');
INSERT INTO `t_smart_job_log` VALUES (8639, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:21:42', 3, '2025-09-16 18:21:42', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:21:42');
INSERT INTO `t_smart_job_log` VALUES (8640, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:23:42', 3, '2025-09-16 18:23:42', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:23:42');
INSERT INTO `t_smart_job_log` VALUES (8641, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:25:42', 2, '2025-09-16 18:25:42', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:25:42');
INSERT INTO `t_smart_job_log` VALUES (8642, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:27:43', 3, '2025-09-16 18:27:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:27:42');
INSERT INTO `t_smart_job_log` VALUES (8643, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:31:43', 3, '2025-09-16 18:31:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:31:42');
INSERT INTO `t_smart_job_log` VALUES (8644, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:35:43', 2, '2025-09-16 18:35:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:35:42');
INSERT INTO `t_smart_job_log` VALUES (8645, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:39:43', 2, '2025-09-16 18:39:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:39:42');
INSERT INTO `t_smart_job_log` VALUES (8646, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:43:43', 3, '2025-09-16 18:43:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:43:42');
INSERT INTO `t_smart_job_log` VALUES (8647, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:47:43', 3, '2025-09-16 18:47:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:47:42');
INSERT INTO `t_smart_job_log` VALUES (8648, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:51:43', 2, '2025-09-16 18:51:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:51:42');
INSERT INTO `t_smart_job_log` VALUES (8649, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:55:43', 3, '2025-09-16 18:55:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:55:42');
INSERT INTO `t_smart_job_log` VALUES (8650, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 18:59:43', 4, '2025-09-16 18:59:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 18:59:42');
INSERT INTO `t_smart_job_log` VALUES (8651, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:03:43', 5, '2025-09-16 19:03:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:03:42');
INSERT INTO `t_smart_job_log` VALUES (8652, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:05:43', 4, '2025-09-16 19:05:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:05:43');
INSERT INTO `t_smart_job_log` VALUES (8653, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:07:43', 8, '2025-09-16 19:07:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:07:43');
INSERT INTO `t_smart_job_log` VALUES (8654, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:09:43', 3, '2025-09-16 19:09:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:09:43');
INSERT INTO `t_smart_job_log` VALUES (8655, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:11:43', 3, '2025-09-16 19:11:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:11:43');
INSERT INTO `t_smart_job_log` VALUES (8656, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:13:43', 2, '2025-09-16 19:13:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:13:43');
INSERT INTO `t_smart_job_log` VALUES (8657, 1, '示例任务1', '执行示例任务1', 1, '2025-09-16 19:15:10', 0, '2025-09-16 19:15:10', '执行完毕,随便说点什么吧', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:15:10');
INSERT INTO `t_smart_job_log` VALUES (8658, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:15:43', 3, '2025-09-16 19:15:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:15:43');
INSERT INTO `t_smart_job_log` VALUES (8659, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:17:43', 2, '2025-09-16 19:17:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:17:43');
INSERT INTO `t_smart_job_log` VALUES (8660, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:19:43', 3, '2025-09-16 19:19:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:19:43');
INSERT INTO `t_smart_job_log` VALUES (8661, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:21:43', 2, '2025-09-16 19:21:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:21:43');
INSERT INTO `t_smart_job_log` VALUES (8662, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:23:43', 2, '2025-09-16 19:23:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:23:43');
INSERT INTO `t_smart_job_log` VALUES (8663, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:25:43', 2, '2025-09-16 19:25:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:25:43');
INSERT INTO `t_smart_job_log` VALUES (8664, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:27:43', 2, '2025-09-16 19:27:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:27:43');
INSERT INTO `t_smart_job_log` VALUES (8665, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:29:43', 2, '2025-09-16 19:29:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:29:43');
INSERT INTO `t_smart_job_log` VALUES (8666, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:31:43', 3, '2025-09-16 19:31:43', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:31:43');
INSERT INTO `t_smart_job_log` VALUES (8667, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:33:44', 2, '2025-09-16 19:33:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:33:43');
INSERT INTO `t_smart_job_log` VALUES (8668, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:37:44', 3, '2025-09-16 19:37:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:37:43');
INSERT INTO `t_smart_job_log` VALUES (8669, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:41:44', 2, '2025-09-16 19:41:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:41:43');
INSERT INTO `t_smart_job_log` VALUES (8670, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:45:44', 2, '2025-09-16 19:45:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:45:43');
INSERT INTO `t_smart_job_log` VALUES (8671, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:49:44', 2, '2025-09-16 19:49:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:49:43');
INSERT INTO `t_smart_job_log` VALUES (8672, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:53:44', 2, '2025-09-16 19:53:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:53:43');
INSERT INTO `t_smart_job_log` VALUES (8673, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 19:57:44', 2, '2025-09-16 19:57:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 19:57:43');
INSERT INTO `t_smart_job_log` VALUES (8674, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:01:44', 2, '2025-09-16 20:01:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:01:43');
INSERT INTO `t_smart_job_log` VALUES (8675, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:05:44', 2, '2025-09-16 20:05:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:05:43');
INSERT INTO `t_smart_job_log` VALUES (8676, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:09:44', 4, '2025-09-16 20:09:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:09:43');
INSERT INTO `t_smart_job_log` VALUES (8677, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:13:44', 6, '2025-09-16 20:13:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:13:43');
INSERT INTO `t_smart_job_log` VALUES (8678, 1, '示例任务1', '执行示例任务1', 1, '2025-09-16 20:15:10', 0, '2025-09-16 20:15:10', '执行完毕,随便说点什么吧', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:15:10');
INSERT INTO `t_smart_job_log` VALUES (8679, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:15:44', 4, '2025-09-16 20:15:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:15:44');
INSERT INTO `t_smart_job_log` VALUES (8680, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:17:44', 4, '2025-09-16 20:17:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:17:44');
INSERT INTO `t_smart_job_log` VALUES (8681, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:19:44', 3, '2025-09-16 20:19:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:19:44');
INSERT INTO `t_smart_job_log` VALUES (8682, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:21:44', 7, '2025-09-16 20:21:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:21:44');
INSERT INTO `t_smart_job_log` VALUES (8683, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:23:44', 5, '2025-09-16 20:23:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:23:44');
INSERT INTO `t_smart_job_log` VALUES (8684, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:25:44', 6, '2025-09-16 20:25:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:25:44');
INSERT INTO `t_smart_job_log` VALUES (8685, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:27:44', 3, '2025-09-16 20:27:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:27:44');
INSERT INTO `t_smart_job_log` VALUES (8686, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:29:44', 2, '2025-09-16 20:29:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:29:44');
INSERT INTO `t_smart_job_log` VALUES (8687, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:31:44', 4, '2025-09-16 20:31:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:31:44');
INSERT INTO `t_smart_job_log` VALUES (8688, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:33:44', 4, '2025-09-16 20:33:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:33:44');
INSERT INTO `t_smart_job_log` VALUES (8689, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:35:44', 6, '2025-09-16 20:35:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:35:44');
INSERT INTO `t_smart_job_log` VALUES (8690, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:37:44', 4, '2025-09-16 20:37:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:37:44');
INSERT INTO `t_smart_job_log` VALUES (8691, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:39:44', 3, '2025-09-16 20:39:44', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:39:44');
INSERT INTO `t_smart_job_log` VALUES (8692, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:41:45', 3, '2025-09-16 20:41:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:41:44');
INSERT INTO `t_smart_job_log` VALUES (8693, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:45:45', 3, '2025-09-16 20:45:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:45:44');
INSERT INTO `t_smart_job_log` VALUES (8694, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:49:45', 3, '2025-09-16 20:49:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:49:44');
INSERT INTO `t_smart_job_log` VALUES (8695, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:53:45', 2, '2025-09-16 20:53:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:53:44');
INSERT INTO `t_smart_job_log` VALUES (8696, 2, '示例任务2', '执行示例任务2', 1, '2025-09-16 20:57:45', 2, '2025-09-16 20:57:45', '执行成功,本次处理数据1条', '', '16368', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-16 20:57:44');
INSERT INTO `t_smart_job_log` VALUES (8697, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:10:02', 6, '2025-09-19 10:10:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:10:02');
INSERT INTO `t_smart_job_log` VALUES (8698, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:12:02', 8, '2025-09-19 10:12:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:12:02');
INSERT INTO `t_smart_job_log` VALUES (8699, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:14:02', 8, '2025-09-19 10:14:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:14:02');
INSERT INTO `t_smart_job_log` VALUES (8700, 1, '示例任务1', '执行示例任务1', 1, '2025-09-19 10:15:10', 0, '2025-09-19 10:15:10', '执行完毕,随便说点什么吧', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:15:10');
INSERT INTO `t_smart_job_log` VALUES (8701, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:16:02', 6, '2025-09-19 10:16:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:16:02');
INSERT INTO `t_smart_job_log` VALUES (8702, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:18:02', 10, '2025-09-19 10:18:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:18:02');
INSERT INTO `t_smart_job_log` VALUES (8703, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:20:02', 5, '2025-09-19 10:20:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:20:02');
INSERT INTO `t_smart_job_log` VALUES (8704, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:22:02', 3, '2025-09-19 10:22:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:22:02');
INSERT INTO `t_smart_job_log` VALUES (8705, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:24:02', 5, '2025-09-19 10:24:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:24:02');
INSERT INTO `t_smart_job_log` VALUES (8706, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 10:26:02', 4, '2025-09-19 10:26:02', '执行成功,本次处理数据1条', '', '3512', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 10:26:02');
INSERT INTO `t_smart_job_log` VALUES (8707, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:03:15', 7, '2025-09-19 14:03:15', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:03:15');
INSERT INTO `t_smart_job_log` VALUES (8708, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:05:16', 8, '2025-09-19 14:05:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:05:15');
INSERT INTO `t_smart_job_log` VALUES (8709, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:09:16', 5, '2025-09-19 14:09:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:09:15');
INSERT INTO `t_smart_job_log` VALUES (8710, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:13:16', 4, '2025-09-19 14:13:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:13:15');
INSERT INTO `t_smart_job_log` VALUES (8711, 1, '示例任务1', '执行示例任务1', 1, '2025-09-19 14:15:10', 0, '2025-09-19 14:15:10', '执行完毕,随便说点什么吧', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:15:10');
INSERT INTO `t_smart_job_log` VALUES (8712, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:17:16', 4, '2025-09-19 14:17:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:17:15');
INSERT INTO `t_smart_job_log` VALUES (8713, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:21:16', 3, '2025-09-19 14:21:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:21:15');
INSERT INTO `t_smart_job_log` VALUES (8714, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:25:16', 4, '2025-09-19 14:25:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:25:16');
INSERT INTO `t_smart_job_log` VALUES (8715, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:27:16', 5, '2025-09-19 14:27:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:27:16');
INSERT INTO `t_smart_job_log` VALUES (8716, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:29:16', 3, '2025-09-19 14:29:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:29:16');
INSERT INTO `t_smart_job_log` VALUES (8717, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:31:16', 4, '2025-09-19 14:31:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:31:16');
INSERT INTO `t_smart_job_log` VALUES (8718, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:33:16', 3, '2025-09-19 14:33:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:33:16');
INSERT INTO `t_smart_job_log` VALUES (8719, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:35:16', 3, '2025-09-19 14:35:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:35:16');
INSERT INTO `t_smart_job_log` VALUES (8720, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:37:16', 4, '2025-09-19 14:37:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:37:16');
INSERT INTO `t_smart_job_log` VALUES (8721, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:39:16', 4, '2025-09-19 14:39:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:39:16');
INSERT INTO `t_smart_job_log` VALUES (8722, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:41:16', 4, '2025-09-19 14:41:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:41:16');
INSERT INTO `t_smart_job_log` VALUES (8723, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:43:16', 3, '2025-09-19 14:43:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:43:16');
INSERT INTO `t_smart_job_log` VALUES (8724, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:45:16', 4, '2025-09-19 14:45:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:45:16');
INSERT INTO `t_smart_job_log` VALUES (8725, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:47:16', 3, '2025-09-19 14:47:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:47:16');
INSERT INTO `t_smart_job_log` VALUES (8726, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:49:16', 4, '2025-09-19 14:49:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:49:16');
INSERT INTO `t_smart_job_log` VALUES (8727, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:51:16', 3, '2025-09-19 14:51:16', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:51:16');
INSERT INTO `t_smart_job_log` VALUES (8728, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:53:16', 3, '2025-09-19 14:53:17', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:53:16');
INSERT INTO `t_smart_job_log` VALUES (8729, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 14:55:17', 3, '2025-09-19 14:55:17', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 14:55:16');
INSERT INTO `t_smart_job_log` VALUES (8730, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 16:46:25', 3, '2025-09-19 16:46:25', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 16:46:25');
INSERT INTO `t_smart_job_log` VALUES (8731, 1, '示例任务1', '执行示例任务1', 1, '2025-09-19 16:46:25', 0, '2025-09-19 16:46:25', '执行完毕,随便说点什么吧', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 16:46:25');
INSERT INTO `t_smart_job_log` VALUES (8732, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 16:50:25', 3, '2025-09-19 16:50:25', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 16:50:25');
INSERT INTO `t_smart_job_log` VALUES (8733, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 16:52:25', 3, '2025-09-19 16:52:25', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 16:52:25');
INSERT INTO `t_smart_job_log` VALUES (8734, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 16:54:26', 3, '2025-09-19 16:54:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 16:54:25');
INSERT INTO `t_smart_job_log` VALUES (8735, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 16:58:26', 3, '2025-09-19 16:58:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 16:58:25');
INSERT INTO `t_smart_job_log` VALUES (8736, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 17:02:26', 3, '2025-09-19 17:02:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:02:25');
INSERT INTO `t_smart_job_log` VALUES (8737, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 17:06:26', 3, '2025-09-19 17:06:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:06:25');
INSERT INTO `t_smart_job_log` VALUES (8738, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 17:10:26', 4, '2025-09-19 17:10:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:10:25');
INSERT INTO `t_smart_job_log` VALUES (8739, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 17:14:26', 2, '2025-09-19 17:14:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:14:25');
INSERT INTO `t_smart_job_log` VALUES (8740, 1, '示例任务1', '执行示例任务1', 1, '2025-09-19 17:15:10', 0, '2025-09-19 17:15:10', '执行完毕,随便说点什么吧', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:15:10');
INSERT INTO `t_smart_job_log` VALUES (8741, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 17:18:26', 2, '2025-09-19 17:18:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:18:25');
INSERT INTO `t_smart_job_log` VALUES (8742, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 17:22:26', 3, '2025-09-19 17:22:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:22:25');
INSERT INTO `t_smart_job_log` VALUES (8743, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 17:26:26', 3, '2025-09-19 17:26:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:26:25');
INSERT INTO `t_smart_job_log` VALUES (8744, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 17:30:26', 3, '2025-09-19 17:30:26', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 17:30:25');
INSERT INTO `t_smart_job_log` VALUES (8745, 2, '示例任务2', '执行示例任务2', 1, '2025-09-19 21:32:59', 4, '2025-09-19 21:32:59', '执行成功,本次处理数据1条', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 21:32:59');
INSERT INTO `t_smart_job_log` VALUES (8746, 1, '示例任务1', '执行示例任务1', 1, '2025-09-19 21:32:59', 0, '2025-09-19 21:32:59', '执行完毕,随便说点什么吧', '', '19352', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-19 21:32:59');
INSERT INTO `t_smart_job_log` VALUES (8747, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:29:10', 6, '2025-09-23 08:29:10', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:29:10');
INSERT INTO `t_smart_job_log` VALUES (8748, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:31:10', 8, '2025-09-23 08:31:10', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:31:10');
INSERT INTO `t_smart_job_log` VALUES (8749, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:33:11', 8, '2025-09-23 08:33:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:33:10');
INSERT INTO `t_smart_job_log` VALUES (8750, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:37:11', 8, '2025-09-23 08:37:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:37:10');
INSERT INTO `t_smart_job_log` VALUES (8751, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:41:11', 4, '2025-09-23 08:41:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:41:10');
INSERT INTO `t_smart_job_log` VALUES (8752, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:45:11', 5, '2025-09-23 08:45:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:45:10');
INSERT INTO `t_smart_job_log` VALUES (8753, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:49:11', 3, '2025-09-23 08:49:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:49:10');
INSERT INTO `t_smart_job_log` VALUES (8754, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:53:11', 4, '2025-09-23 08:53:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:53:10');
INSERT INTO `t_smart_job_log` VALUES (8755, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 08:57:11', 3, '2025-09-23 08:57:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 08:57:10');
INSERT INTO `t_smart_job_log` VALUES (8756, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 09:01:11', 3, '2025-09-23 09:01:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 09:01:10');
INSERT INTO `t_smart_job_log` VALUES (8757, 2, '示例任务2', '执行示例任务2', 1, '2025-09-23 09:05:11', 12, '2025-09-23 09:05:11', '执行成功,本次处理数据1条', '', '15400', 'D:\\ideaWorkspace\\fun-campus', 'system', '2025-09-23 09:05:10');

-- ----------------------------
-- Table structure for t_table_column
-- ----------------------------
DROP TABLE IF EXISTS `t_table_column`;
CREATE TABLE `t_table_column`  (
  `table_column_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_type` int NOT NULL COMMENT '用户类型',
  `table_id` int NOT NULL COMMENT '表格id',
  `columns` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '具体的表格列，存入的json',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`table_column_id`) USING BTREE,
  UNIQUE INDEX `uni_employee_table`(`user_id` ASC, `table_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '表格的自定义列存储' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_table_column
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityFavoriteMenu.sql —— activity_favorite 表结构
-- ----------------------------------------------------------------------------
-- ----------------------------
-- 活动收藏表
-- ----------------------------
DROP TABLE IF EXISTS `activity_favorite`;
CREATE TABLE `activity_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint UNSIGNED NOT NULL COMMENT '活动id',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_user`(`activity_id` ASC, `user_id` ASC) USING BTREE COMMENT '同一用户对同一活动只有一条收藏记录',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动收藏' ROW_FORMAT = Dynamic;

-- ----------------------------------------------------------------------------
-- 来源文件: update_schema_and_test_data.sql —— 表结构更新 + 测试数据
-- ----------------------------------------------------------------------------
-- =====================================================================
-- Fun Campus 表结构更新 & 测试数据
-- 基于实体类自动生成，执行前请备份数据库
-- Date: 2026-08-16
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 一、新建缺失表（实体类存在但数据库中尚未创建的表）
-- =====================================================================

-- ----------------------------
-- 1. activity_category 活动分类
-- ----------------------------
DROP TABLE IF EXISTS `activity_category`;
CREATE TABLE `activity_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动类型名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 2. activity_can_enroll_college 活动可报名学院
-- ----------------------------
DROP TABLE IF EXISTS `activity_can_enroll_college`;
CREATE TABLE `activity_can_enroll_college` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `can_enroll_college` bigint NOT NULL COMMENT '能报名的学院id',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动可报名学院' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 3. activity_can_enroll_grade 活动可报名年级
-- ----------------------------
DROP TABLE IF EXISTS `activity_can_enroll_grade`;
CREATE TABLE `activity_can_enroll_grade` (
  `id` bigint NOT NULL COMMENT 'id',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `can_enroll_grade` bigint NOT NULL COMMENT '能报名的年级',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动可报名年级' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 4. activity_can_enroll_tribe 活动可报名部落
-- ----------------------------
DROP TABLE IF EXISTS `activity_can_enroll_tribe`;
CREATE TABLE `activity_can_enroll_tribe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `can_enroll_tribe` bigint NOT NULL COMMENT '能报名的部落id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动可报名部落' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 5. activity_review_log 活动审核日志
-- ----------------------------
DROP TABLE IF EXISTS `activity_review_log`;
CREATE TABLE `activity_review_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人id',
  `reviewer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `review_stage` int NULL DEFAULT NULL COMMENT '审核阶段: 0-草稿 1-初审 2-审阅 3-终审 4-报名审核 5-完结审核',
  `action` int NULL DEFAULT NULL COMMENT '审核行为: 0-提交 1-初审通过 2-初审退回 3-审阅通过 4-终审通过 5-终审退回 6-报名审核通过 7-完结审核通过 8-完结审核退回',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '驳回原因',
  `check_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审阅修改建议',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动审核日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 6. activity_attachment 活动附件
-- ----------------------------
DROP TABLE IF EXISTS `activity_attachment`;
CREATE TABLE `activity_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `file_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件key',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动附件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 7. activity_review_attachment 审核附件
-- ----------------------------
DROP TABLE IF EXISTS `activity_review_attachment`;
CREATE TABLE `activity_review_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `review_log_id` bigint NOT NULL COMMENT '审核日志id',
  `file_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件key',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_review_log_id`(`review_log_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审核附件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 8. activity_signin_manager 活动签到管理员
-- ----------------------------
DROP TABLE IF EXISTS `activity_signin_manager`;
CREATE TABLE `activity_signin_manager` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint NOT NULL COMMENT '活动主键',
  `portal_user_id` bigint NOT NULL COMMENT '活动签到员主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '签到员用户名',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动签到管理员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 9. tribe 部落
-- ----------------------------
DROP TABLE IF EXISTS `tribe`;
CREATE TABLE `tribe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部落id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部落名',
  `icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部落图标（文件key）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '部落简介',
  `category_id` bigint NULL DEFAULT NULL COMMENT '部落类型',
  `president_id` bigint NULL DEFAULT NULL COMMENT '主席id',
  `belong_to` int NULL DEFAULT NULL COMMENT '1->组织，2->院系',
  `school_id` bigint NULL DEFAULT NULL COMMENT '所属学校id',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部落' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 10. tribe_user 部落用户
-- ----------------------------
DROP TABLE IF EXISTS `tribe_user`;
CREATE TABLE `tribe_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tribe_id` bigint NOT NULL COMMENT '部落id',
  `portal_user_id` bigint NOT NULL COMMENT '参与部落的前端用户id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tribe_id`(`tribe_id` ASC) USING BTREE,
  INDEX `idx_portal_user_id`(`portal_user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部落用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 11. college_info 学院信息
-- ----------------------------
DROP TABLE IF EXISTS `college_info`;
CREATE TABLE `college_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学院名称',
  `school_id` bigint NOT NULL COMMENT '所属学校id',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_school_id`(`school_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学院信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 12. grade_info 年级信息
-- ----------------------------
DROP TABLE IF EXISTS `grade_info`;
CREATE TABLE `grade_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '年级',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '年级信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 13. organization_info 组织信息
-- ----------------------------
DROP TABLE IF EXISTS `organization_info`;
CREATE TABLE `organization_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '组织id',
  `school_id` bigint NOT NULL COMMENT '属于学校的id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组织名称',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_school_id`(`school_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '各学校组织信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 14. t_data_tracer 数据变更追踪（support-log 模块 DataTracerEntity 直连表，原脚本遗漏）
--     使用 IF NOT EXISTS：全新环境创建；已有环境跳过，避免清空已有追踪数据
-- ----------------------------
CREATE TABLE IF NOT EXISTS `t_data_tracer` (
  `data_tracer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `data_id` bigint NULL DEFAULT NULL COMMENT '数据id',
  `type` int NULL DEFAULT NULL COMMENT '业务类型',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `diff_old` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'diff差异：旧的数据',
  `diff_new` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'diff差异：新的数据',
  `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展字段',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `user_type` int NULL DEFAULT NULL COMMENT '用户类型',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求ip',
  `ip_region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求ip地区',
  `user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求头',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`data_tracer_id`) USING BTREE,
  INDEX `idx_data_id_type`(`data_id` ASC, `type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据变更追踪' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 15. tribe_application 部落加入申请
-- ----------------------------
DROP TABLE IF EXISTS `tribe_application`;
CREATE TABLE `tribe_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tribe_id` bigint NOT NULL COMMENT '部落id',
  `portal_user_id` bigint NOT NULL COMMENT '申请的前端用户id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请人用户名（快照）',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请理由',
  `status` int NOT NULL DEFAULT 0 COMMENT '审核状态: 0-待审核 1-已通过 2-已驳回',
  `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `review_user_id` bigint NULL DEFAULT NULL COMMENT '审核人id（管理端用户）',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tribe_id`(`tribe_id` ASC) USING BTREE,
  INDEX `idx_portal_user_id`(`portal_user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部落加入申请' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 16. activity_evaluation 活动评价
-- ----------------------------
DROP TABLE IF EXISTS `activity_evaluation`;
CREATE TABLE `activity_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `user_id` bigint NOT NULL COMMENT '评价人id（portal_user）',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价人姓名（快照）',
  `score` int NOT NULL COMMENT '评分：1-5',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动评价' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 17. credit_application 学分认定申请
-- ----------------------------
DROP TABLE IF EXISTS `credit_application`;
CREATE TABLE `credit_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `semester` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请内容',
  `image_list` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证明材料图片fileKey列表（逗号分隔，最多9张）',
  `applicant_user_id` bigint NOT NULL COMMENT '申请人id（portal_user）',
  `applicant_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请人用户名（快照）',
  `applicant_school_id` bigint NULL DEFAULT NULL COMMENT '申请人学校id（快照）',
  `review_user_id` bigint NULL DEFAULT NULL COMMENT '审核人id（backend_user，can_review=1）',
  `review_user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人姓名（快照）',
  `review_organization_id` bigint NULL DEFAULT NULL COMMENT '审核人院系/组织id',
  `review_organization_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人院系/组织名称（快照）',
  `status` int NOT NULL DEFAULT 0 COMMENT '审核状态: 0-待审核 1-已通过 2-已驳回',
  `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见/驳回原因',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_applicant_user_id`(`applicant_user_id` ASC) USING BTREE,
  INDEX `idx_review_user_id`(`review_user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学分认定申请' ROW_FORMAT = Dynamic;


-- =====================================================================
-- 二、修改现有表（实体类新增了字段）
-- =====================================================================

-- ----------------------------
-- 1. activity 表：新增归属、分类、封面、管理员等字段
-- ----------------------------
ALTER TABLE `activity`
  ADD COLUMN `activity_belong_to_college_id` bigint NULL DEFAULT NULL COMMENT '活动所属学院' AFTER `activity_organization_id`,
  ADD COLUMN `description` text NULL COMMENT '描述' AFTER `deleted_flag`,
  ADD COLUMN `enroll_need_review` bit(1) NULL DEFAULT b'0' COMMENT '报名需审核' AFTER `description`,
  ADD COLUMN `need_sign_out` bit(1) NULL DEFAULT b'0' COMMENT '需要签退' AFTER `enroll_need_review`,
  ADD COLUMN `attachment` varchar(1000) NULL DEFAULT NULL COMMENT '附件' AFTER `need_sign_out`,
  ADD COLUMN `category_id` bigint NULL DEFAULT NULL COMMENT '分类' AFTER `attachment`,
  ADD COLUMN `cover_img` varchar(500) NULL DEFAULT NULL COMMENT '封面图片' AFTER `category_id`,
  ADD COLUMN `activity_manager_id` bigint NULL DEFAULT NULL COMMENT '活动管理员和发起者' AFTER `cover_img`;

-- 重命名字段 activity_school_id -> activity_belong_to_school_id
ALTER TABLE `activity` CHANGE COLUMN `activity_school_id` `activity_belong_to_school_id` bigint UNSIGNED NOT NULL COMMENT '活动所属学校';
-- 重命名字段 activity_organization_id -> activity_belong_to_organization_id
ALTER TABLE `activity` CHANGE COLUMN `activity_organization_id` `activity_belong_to_organization_id` bigint UNSIGNED NOT NULL COMMENT '活动所属组织';

-- ----------------------------
-- 2. activity_schedule 表：新增签退时间字段
-- ----------------------------
ALTER TABLE `activity_schedule`
  ADD COLUMN `signout_start_time` timestamp NULL DEFAULT NULL COMMENT '签退开始时间' AFTER `signin_end_time`,
  ADD COLUMN `signout_end_time` timestamp NULL DEFAULT NULL COMMENT '签退结束时间' AFTER `signout_start_time`;

-- ----------------------------
-- 3. activity_enrollment 表：新增签退状态，重命名 deleted -> deleted_flag
-- ----------------------------
ALTER TABLE `activity_enrollment`
  ADD COLUMN `sign_out_status` bit(1) NULL DEFAULT b'0' COMMENT '是否已签退' AFTER `sign_in_status`,
  CHANGE COLUMN `deleted` `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除';

-- ----------------------------
-- 4. portal_user 表：新增学校id/学院id/学分/信誉分等字段，去掉 school_name/college_name
-- ----------------------------
ALTER TABLE `portal_user`
  ADD COLUMN `disable_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否禁用' AFTER `deleted_flag`,
  ADD COLUMN `school_id` bigint NULL DEFAULT NULL COMMENT '学校id' AFTER `avatar`,
  ADD COLUMN `college_id` bigint NULL DEFAULT NULL COMMENT '学院id' AFTER `school_id`,
  ADD COLUMN `can_publish_activity` bit(1) NOT NULL DEFAULT b'0' COMMENT '能否发布活动' AFTER `college_id`,
  ADD COLUMN `grade_id` bigint NULL DEFAULT NULL COMMENT '年级id' AFTER `can_publish_activity`,
  ADD COLUMN `grade_score` decimal(10,2) NULL DEFAULT 0.00 COMMENT '学分' AFTER `grade_id`,
  ADD COLUMN `credit_score` int NULL DEFAULT 100 COMMENT '信誉分' AFTER `grade_score`,
  ADD COLUMN `organization_id` bigint NULL DEFAULT NULL COMMENT '组织id' AFTER `credit_score`,
  DROP COLUMN `school_name`,
  DROP COLUMN `college_name`;

-- ----------------------------
-- 5. backend_user 表：新增学校/学院/组织/审核权限字段
-- ----------------------------
ALTER TABLE `backend_user`
  ADD COLUMN `school_id` bigint NULL DEFAULT NULL COMMENT '学校id' AFTER `password`,
  ADD COLUMN `college_id` bigint NULL DEFAULT NULL COMMENT '学院id' AFTER `school_id`,
  ADD COLUMN `organization_id` bigint NULL DEFAULT NULL COMMENT '组织id' AFTER `college_id`,
  ADD COLUMN `can_review` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否具有审核权限' AFTER `organization_id`;


-- =====================================================================
-- 三、插入测试数据
-- =====================================================================

-- ----------------------------
-- 1. school_info 学校信息
-- ----------------------------
INSERT INTO `school_info` (`id`, `name`, `code`, `type`, `address`, `contact_person`, `contact_phone`, `email`, `website`, `description`, `logo_url`, `status`, `sort`, `create_time`, `update_time`, `deleted_flag`)
VALUES (1, '洛阳理工学院', 'LYUT', 6, '河南省洛阳市洛龙区学府街66号', '张校长', '13800138001', 'admin@lyut.edu.cn', 'https://www.lyut.edu.cn', '洛阳理工学院是一所工科为主的本科院校', NULL, 1, 1, NOW(), NOW(), 0);
INSERT INTO `school_info` (`id`, `name`, `code`, `type`, `address`, `contact_person`, `contact_phone`, `email`, `website`, `description`, `logo_url`, `status`, `sort`, `create_time`, `update_time`, `deleted_flag`)
VALUES (2, '河南科技大学', 'HAUST', 6, '河南省洛阳市开元大道263号', '李校长', '13800138002', 'admin@haust.edu.cn', 'https://www.haust.edu.cn', '河南科技大学是河南省重点建设的综合性大学', NULL, 1, 2, NOW(), NOW(), 0);

-- ----------------------------
-- 2. college_info 学院信息
-- ----------------------------
INSERT INTO `college_info` (`id`, `name`, `school_id`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, '计算机与信息技术学院', 1, 0, NOW(), NOW()),
(2, '机械工程学院', 1, 0, NOW(), NOW()),
(3, '信息工程学院', 2, 0, NOW(), NOW()),
(4, '材料科学与工程学院', 2, 0, NOW(), NOW());

-- ----------------------------
-- 3. grade_info 年级信息
-- ----------------------------
INSERT INTO `grade_info` (`id`, `name`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, '2023级', 0, NOW(), NOW()),
(2, '2024级', 0, NOW(), NOW()),
(3, '2025级', 0, NOW(), NOW()),
(4, '2026级', 0, NOW(), NOW());

-- ----------------------------
-- 4. organization_info 组织信息
-- ----------------------------
INSERT INTO `organization_info` (`id`, `school_id`, `name`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, 1, '校团委学生会', 0, NOW(), NOW()),
(2, 1, '青年志愿者协会', 0, NOW(), NOW()),
(3, 1, '科技创新社', 0, NOW(), NOW()),
(4, 2, '校学生会', 0, NOW(), NOW()),
(5, 2, '社团联合会', 0, NOW(), NOW());

-- ----------------------------
-- 5. activity_category 活动分类
-- ----------------------------
INSERT INTO `activity_category` (`id`, `name`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, '学术讲座', 0, NOW(), NOW()),
(2, '志愿服务', 0, NOW(), NOW()),
(3, '文体活动', 0, NOW(), NOW()),
(4, '科技创新', 0, NOW(), NOW()),
(5, '社会实践', 0, NOW(), NOW());

-- ----------------------------
-- 6. tribe 部落
-- ----------------------------
INSERT INTO `tribe` (`id`, `name`, `category_id`, `president_id`, `belong_to`, `school_id`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, 'ACM算法竞赛部落', 4, 1, 2, 1, 0, NOW(), NOW()),
(2, '志愿者服务部落', 2, 2, 1, 1, 0, NOW(), NOW()),
(3, '篮球运动部落', 3, 3, 1, 1, 0, NOW(), NOW()),
(4, '读书分享部落', 1, 4, 2, 2, 0, NOW(), NOW());

-- ----------------------------
-- 7. portal_user 前端用户
-- ----------------------------
INSERT INTO `portal_user` (`id`, `username`, `password`, `create_time`, `update_time`, `deleted_flag`, `disable_flag`, `gender`, `phone`, `avatar`, `school_id`, `college_id`, `can_publish_activity`, `grade_id`, `grade_score`, `credit_score`, `organization_id`) VALUES
(1, '张三', '$argon2id$v=19$m=16384,t=2,p=1$test$testhash1', NOW(), NOW(), 0, 0, 1, '13800000001', NULL, 1, 1, 1, 1, 5.00, 100, NULL),
(2, '李四', '$argon2id$v=19$m=16384,t=2,p=1$test$testhash2', NOW(), NOW(), 0, 0, 0, '13800000002', NULL, 1, 1, 1, 2, 3.00, 100, NULL),
(3, '王五', '$argon2id$v=19$m=16384,t=2,p=1$test$testhash3', NOW(), NOW(), 0, 0, 1, '13800000003', NULL, 1, 2, 0, 1, 0.00, 100, NULL),
(4, '赵六', '$argon2id$v=19$m=16384,t=2,p=1$test$testhash4', NOW(), NOW(), 0, 0, 0, '13800000004', NULL, 2, 3, 1, 1, 8.00, 95, NULL),
(5, '孙七', '$argon2id$v=19$m=16384,t=2,p=1$test$testhash5', NOW(), NOW(), 0, 0, 1, '13800000005', NULL, 2, 3, 0, 2, 0.00, 100, NULL);

-- ----------------------------
-- 8. tribe_user 部落用户关系
-- ----------------------------
INSERT INTO `tribe_user` (`id`, `tribe_id`, `portal_user_id`, `username`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, 1, 1, '张三', 0, NOW(), NOW()),
(2, 1, 2, '李四', 0, NOW(), NOW()),
(3, 2, 1, '张三', 0, NOW(), NOW()),
(4, 2, 3, '王五', 0, NOW(), NOW()),
(5, 3, 2, '李四', 0, NOW(), NOW()),
(6, 4, 4, '赵六', 0, NOW(), NOW()),
(7, 4, 5, '孙七', 0, NOW(), NOW());

-- ----------------------------
-- 9. backend_user 后台用户（更新已有记录 + 新增）
-- ----------------------------
-- 更新 id=1 的管理员，补充新增字段
UPDATE `backend_user` SET `school_id` = 1, `college_id` = NULL, `organization_id` = NULL, `can_review` = 1 WHERE `id` = 1;

-- 新增学院审核人
INSERT INTO `backend_user` (`id`, `update_time`, `create_time`, `deleted_flag`, `role_id`, `username`, `password`, `disabled_flag`, `email`, `school_id`, `college_id`, `organization_id`, `can_review`)
VALUES (2, NOW(), NOW(), 0, 2, 'college_reviewer_1', '$argon2id$v=19$m=16384,t=2,p=1$test$testhash', 0, 'reviewer1@lyut.edu.cn', 1, 1, NULL, 1);

-- 新增组织审核人
INSERT INTO `backend_user` (`id`, `update_time`, `create_time`, `deleted_flag`, `role_id`, `username`, `password`, `disabled_flag`, `email`, `school_id`, `college_id`, `organization_id`, `can_review`)
VALUES (3, NOW(), NOW(), 0, 2, 'org_reviewer_1', '$argon2id$v=19$m=16384,t=2,p=1$test$testhash', 0, 'orgreviewer1@lyut.edu.cn', 1, NULL, 1, 1);

-- ----------------------------
-- 10. activity 活动（更新已有记录 + 新增）
-- ----------------------------
-- 更新已有活动 id=1
UPDATE `activity` SET
  `activity_belong_to_school_id` = 1,
  `activity_belong_to_organization_id` = 1,
  `description` = '这是一次关于算法竞赛的学术讲座活动',
  `enroll_need_review` = 0,
  `need_sign_out` = 1,
  `category_id` = 1,
  `cover_img` = NULL,
  `activity_manager_id` = 1
WHERE `id` = 1;

-- 新增活动
INSERT INTO `activity` (`id`, `title`, `status`, `position`, `score_can_get`, `enroll_num_limit`, `activity_belong_to_school_id`, `activity_belong_to_organization_id`, `deleted_flag`, `create_time`, `update_time`, `description`, `enroll_need_review`, `need_sign_out`, `category_id`, `activity_manager_id`) VALUES
(2, '校园篮球友谊赛', 1, '学校体育馆', 2, 60, 1, 1, 0, NOW(), NOW(), '各学院之间的篮球友谊赛，欢迎同学们积极报名参加！', 0, 1, 3, 2),
(3, 'Python编程入门讲座', 1, '计算机楼301', 1, 100, 1, 3, 0, NOW(), NOW(), '面向初学者的Python编程入门讲座，由计算机学院教授主讲', 1, 0, 1, 1),
(4, '社区志愿服务', 1, '洛龙区社区中心', 3, 30, 1, 2, 0, NOW(), NOW(), '走进社区，开展环保宣传和义务清扫活动', 0, 1, 2, 3),
(5, '科技创新大赛', 0, '图书馆报告厅', 5, 50, 2, 4, 0, NOW(), NOW(), '年度科技创新大赛，展示各团队的创新项目', 1, 1, 4, 4);

-- ----------------------------
-- 11. activity_schedule 活动时间表
-- ----------------------------
INSERT INTO `activity_schedule` (`activity_id`, `enroll_start_time`, `enroll_end_time`, `activity_start_time`, `activity_end_time`, `signin_start_time`, `signin_end_time`, `signout_start_time`, `signout_end_time`, `create_time`, `update_time`, `deleted_flag`) VALUES
(2, '2026-08-20 08:00:00', '2026-08-25 23:59:59', '2026-08-28 14:00:00', '2026-08-28 18:00:00', '2026-08-28 13:30:00', '2026-08-28 14:00:00', '2026-08-28 18:00:00', '2026-08-28 18:30:00', NOW(), NOW(), 0),
(3, '2026-08-22 09:00:00', '2026-08-27 18:00:00', '2026-08-29 10:00:00', '2026-08-29 12:00:00', '2026-08-29 09:30:00', '2026-08-29 10:00:00', NULL, NULL, NOW(), NOW(), 0),
(4, '2026-09-01 08:00:00', '2026-09-05 23:59:59', '2026-09-08 09:00:00', '2026-09-08 17:00:00', '2026-09-08 08:30:00', '2026-09-08 09:00:00', '2026-09-08 17:00:00', '2026-09-08 17:30:00', NOW(), NOW(), 0),
(5, '2026-09-10 08:00:00', '2026-09-15 23:59:59', '2026-09-20 09:00:00', '2026-09-20 18:00:00', '2026-09-20 08:30:00', '2026-09-20 09:00:00', '2026-09-20 17:30:00', '2026-09-20 18:00:00', NOW(), NOW(), 0);

-- ----------------------------
-- 12. activity_can_enroll_college 活动可报名学院
-- ----------------------------
INSERT INTO `activity_can_enroll_college` (`id`, `activity_id`, `can_enroll_college`, `deleted_flag`, `create_time`, `update_time`) VALUES
(NULL, 2, 1, 0, NOW(), NOW()),
(NULL, 2, 2, 0, NOW(), NOW()),
(NULL, 3, 1, 0, NOW(), NOW()),
(NULL, 4, 1, 0, NOW(), NOW()),
(NULL, 4, 2, 0, NOW(), NOW());

-- ----------------------------
-- 13. activity_can_enroll_grade 活动可报名年级
-- ----------------------------
INSERT INTO `activity_can_enroll_grade` (`id`, `activity_id`, `can_enroll_grade`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, 2, 1, 0, NOW(), NOW()),
(2, 2, 2, 0, NOW(), NOW()),
(3, 2, 3, 0, NOW(), NOW()),
(4, 3, 1, 0, NOW(), NOW()),
(5, 3, 2, 0, NOW(), NOW()),
(6, 4, 2, 0, NOW(), NOW()),
(7, 4, 3, 0, NOW(), NOW()),
(8, 5, 1, 0, NOW(), NOW()),
(9, 5, 2, 0, NOW(), NOW()),
(10, 5, 3, 0, NOW(), NOW()),
(11, 5, 4, 0, NOW(), NOW());

-- ----------------------------
-- 14. activity_can_enroll_tribe 活动可报名部落
-- ----------------------------
INSERT INTO `activity_can_enroll_tribe` (`id`, `activity_id`, `can_enroll_tribe`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, 2, 3, 0, NOW(), NOW()),
(2, 4, 2, 0, NOW(), NOW());

-- ----------------------------
-- 15. activity_enrollment 活动报名
-- ----------------------------
INSERT INTO `activity_enrollment` (`activity_id`, `user_id`, `sign_in_status`, `sign_out_status`, `create_time`, `update_time`, `deleted_flag`) VALUES
(2, 1, 0, 0, NOW(), NOW(), 0),
(2, 2, 0, 0, NOW(), NOW(), 0),
(2, 3, 0, 0, NOW(), NOW(), 0),
(3, 1, 0, 0, NOW(), NOW(), 0),
(4, 1, 0, 0, NOW(), NOW(), 0),
(4, 3, 0, 0, NOW(), NOW(), 0);

-- ----------------------------
-- 16. activity_signin_manager 活动签到管理员
-- ----------------------------
INSERT INTO `activity_signin_manager` (`id`, `activity_id`, `portal_user_id`, `username`, `deleted_flag`, `create_time`, `update_time`) VALUES
(1, 2, 3, '王五', 0, NOW(), NOW()),
(2, 4, 1, '张三', 0, NOW(), NOW());

-- ----------------------------
-- 17. activity_review_log 审核日志
-- ----------------------------
INSERT INTO `activity_review_log` (`id`, `activity_id`, `reviewer_id`, `reviewer_name`, `review_stage`, `action`, `reject_reason`, `check_remark`, `create_time`, `update_time`, `deleted_flag`) VALUES
(1, 3, 2, 'college_reviewer_1', 1, 1, NULL, NULL, NOW(), NOW(), 0),
(2, 5, 3, 'org_reviewer_1', 1, 2, '活动描述不够详细，请补充活动流程', NULL, NOW(), NOW(), 0);

-- ----------------------------
-- 18. activity_enroll_num 报名人数（更新已有 + 新增）
-- ----------------------------
INSERT INTO `activity_enroll_num` (`activity_id`, `enroll_num`) VALUES
(2, 3),
(3, 1),
(4, 2);

-- ----------------------------
-- 19. t_smart_job 活动状态定时推进任务（双任务：大循环扫描 + 小循环消费）
-- ----------------------------
-- 旧记录说明：job_id=4(ActivityStatusScanTask) 与 job_id=5(ActivityStatusUpdateJob)
-- 的 job_class 为迁移前包名(net.lab1024.*)，框架无法加载；且 UpdateJob 被配置为
-- 每天 0 点 cron，与高频更新的设计矛盾，实际从未按预期生效。
-- 现使用新包名恢复双任务结构（缓存只做提示，数据库才是事实）：
--   job_id=4 ActivityStatusUpdateJob：fixed_delay 60 秒，消费当天关键活动名单推进状态；
--           名单缺失/过期（非当天生成）时自动回源查库重建，可自愈；
--           处理妥当且当天无未来关键时间点的活动会从名单移除（消费确认）；
--   job_id=5 ActivityStatusScanJob：fixed_delay 3600 秒，扫描数据库重建当天名单（大循环）
DELETE FROM `t_smart_job` WHERE `job_id` IN (4, 5);
INSERT INTO `t_smart_job` (`job_id`, `job_name`, `job_class`, `trigger_type`, `trigger_value`, `enabled_flag`, `param`, `last_execute_time`, `last_execute_log_id`, `sort`, `remark`, `deleted_flag`, `update_name`, `create_time`, `update_time`) VALUES
(4, '活动状态定时推进', 'com.akkkka.admin.module.business.funcampus.activityWithSchedule.job.ActivityStatusUpdateJob', 'fixed_delay', '60', 1, NULL, NULL, NULL, 1, '消费当天关键活动名单推进活动状态：0等待报名->1报名中->2报名结束->3进行中->4结束；名单缺失/过期自动回源重建', 0, '管理员', NOW(), NOW()),
(5, '活动状态扫描', 'com.akkkka.admin.module.business.funcampus.activityWithSchedule.job.ActivityStatusScanJob', 'fixed_delay', '3600', 1, NULL, NULL, NULL, 2, '每小时重建当天关键活动名单缓存（大循环）；写入端新建/改时间表时也会尽力投递，缓存失效由更新任务回源兜底', 0, '管理员', NOW(), NOW());

-- 顺带修正示例任务 job_class（原为迁移时产生的乱序字符串，框架无法加载）
UPDATE `t_smart_job` SET `job_class` = 'com.akkkka.module.support.job.sample.SmartJobSample1' WHERE `job_id` = 1;
UPDATE `t_smart_job` SET `job_class` = 'com.akkkka.module.support.job.sample.SmartJobSample2' WHERE `job_id` = 2;

-- ----------------------------
-- 20. 活动状态字典整理（时间阶段 0-4 + 业务状态 8、9；签到/签退旧值 5、6 退役）
-- ----------------------------
-- 背景：状态值历史上混用了“时间阶段”（0-4）与“签到/签退窗口标记”（5-8），
-- 且 t_dict_data(dict_id=4 ACTIVITY_STATUS) 与代码枚举不一致。
-- 现统一为（与 ActivityStatus 枚举、activity.status 列注释同源）：
--   0-等待报名 → 1-报名中 → 2-报名结束 → 3-进行中 → 4-已结束（时间阶段，由定时任务推进）
--   8-已取消（业务状态：管理端取消活动后的终态，不参与时间阶段流转）
--   9-待审核（业务状态，预留：报名需审核时使用，不参与时间阶段流转）
-- 签到/签退不再使用状态值，由时间窗口校验承担。
-- 排序：sort_order 越大越靠前，故按生命周期逆序赋 6..2，业务状态 8-已取消置 1、9-待审核置 0 排在末尾。
ALTER TABLE `activity` MODIFY COLUMN `status` tinyint NOT NULL COMMENT '活动状态：0-等待报名 1-报名中 2-报名结束 3-进行中 4-已结束 8-已取消（业务状态，管理端取消活动后的终态） 9-待审核（业务状态，预留）；签到/签退由时间窗口校验，不设状态值';
DELETE FROM `t_dict_data` WHERE `dict_id` = 4;
INSERT INTO `t_dict_data` (`dict_data_id`, `dict_id`, `data_value`, `data_label`, `remark`, `sort_order`, `disabled_flag`, `create_time`, `update_time`) VALUES
(9, 4, '0', '等待报名', '', 6, 0, NOW(), NOW()),
(10, 4, '1', '报名中', '', 5, 0, NOW(), NOW()),
(11, 4, '2', '报名结束', '', 4, 0, NOW(), NOW()),
(12, 4, '3', '进行中', '', 3, 0, NOW(), NOW()),
(13, 4, '4', '已结束', '', 2, 0, NOW(), NOW()),
(14, 4, '9', '待审核', '业务状态（预留）：报名需审核时等待审核', 0, 0, NOW(), NOW()),
(15, 4, '8', '已取消', '业务状态：管理端取消活动后的终态，不参与时间推进', 1, 0, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- 更新完成！
-- 新建表: 14 张 (activity_category, activity_can_enroll_college,
--   activity_can_enroll_grade, activity_can_enroll_tribe,
--   activity_review_log, activity_attachment, activity_review_attachment,
--   activity_signin_manager, tribe, tribe_user, college_info,
--   grade_info, organization_info, t_data_tracer)
-- 修改表: 5 张 (activity, activity_schedule, activity_enrollment,
--   portal_user, backend_user)
-- =====================================================================

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityOrderPayment.sql —— Phase 9 活动付费参加（订单/支付/退款/超时关单任务）
-- ----------------------------------------------------------------------------
-- =====================================================================
-- Fun Campus Phase 9：活动付费参加（订单 / 支付 / 退款）
-- 内容：activity 付费配置字段、activity_order、activity_refund、订单超时关单任务
-- 说明：脚本仅需执行一次；执行前请备份数据库
-- Date: 2026-09-24
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. activity 表新增付费配置字段
-- ----------------------------
ALTER TABLE `activity`
  ADD COLUMN `paid_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否付费活动',
  ADD COLUMN `price_fen` int NULL COMMENT '报名费（分，免费活动为 NULL）',
  ADD COLUMN `refund_policy` tinyint NULL COMMENT '退款规则：1-报名截止前可退 2-活动开始前可退 3-不可退款';

-- ----------------------------
-- 2. activity_order 活动报名订单
-- ----------------------------
DROP TABLE IF EXISTS `activity_order`;
CREATE TABLE `activity_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `user_id` bigint NOT NULL COMMENT '报名用户id',
  `amount_fen` int NOT NULL COMMENT '订单金额（分）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待支付 1-已支付 2-已关闭 3-退款中 4-已退款 5-退款失败',
  `pay_channel` tinyint NULL COMMENT '支付渠道：1-微信 2-支付宝 3-Mock',
  `channel_order_no` varchar(64) NULL COMMENT '渠道订单号',
  `pay_time` datetime NULL COMMENT '支付时间',
  `expire_time` datetime NOT NULL COMMENT '支付截止时间',
  `close_time` datetime NULL COMMENT '关闭时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_order_no`(`order_no`) USING BTREE,
  KEY `idx_activity_user`(`activity_id`, `user_id`) USING BTREE,
  KEY `idx_status_expire`(`status`, `expire_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动报名订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 3. activity_refund 活动报名退款记录
-- ----------------------------
DROP TABLE IF EXISTS `activity_refund`;
CREATE TABLE `activity_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `activity_id` bigint NOT NULL COMMENT '活动id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `amount_fen` int NOT NULL COMMENT '退款金额（分）',
  `reason_type` tinyint NOT NULL COMMENT '原因：1-用户申请 2-活动取消 3-报名审核未通过 4-其它',
  `reason` varchar(255) NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-退款中 1-成功 2-失败',
  `channel_refund_no` varchar(64) NULL COMMENT '渠道退款单号',
  `refund_time` datetime NULL COMMENT '退款完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_refund_no`(`refund_no`) USING BTREE,
  KEY `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动报名退款记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 4. t_smart_job 注册订单超时关单任务
--    扫描超时未支付订单：CAS 关单（待支付→已关闭）+ 释放名额；
--    与支付回调抢同一个 CAS（where status=待支付），谁先成功谁生效
-- ----------------------------
DELETE FROM `t_smart_job` WHERE `job_class` = 'com.akkkka.admin.module.business.funcampus.activityOrder.job.ActivityOrderTimeoutJob';
INSERT INTO `t_smart_job`
  (`job_name`, `job_class`, `trigger_type`, `trigger_value`, `enabled_flag`, `param`, `sort`, `remark`, `deleted_flag`, `update_name`, `create_time`, `update_time`)
VALUES
  ('订单超时关单', 'com.akkkka.admin.module.business.funcampus.activityOrder.job.ActivityOrderTimeoutJob', 'fixed_delay', '60', 1, NULL, 3,
   '扫描超时未支付订单：CAS 关单（待支付->已关闭）+ 释放名额；与支付回调抢同一个 CAS，谁先成功谁生效', 0, '管理员', NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityCanEnrollCollegeMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '活动能报名的学院', 2, 301, '/activity-can-enroll-college/list', '/business/funcampus/activity-can-enroll-college/activity-can-enroll-college-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '活动能报名的学院';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollCollege:query', 'activityCanEnrollCollege:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollCollege:add', 'activityCanEnrollCollege:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollCollege:update', 'activityCanEnrollCollege:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollCollege:delete', 'activityCanEnrollCollege:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityCanEnrollGradeMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '活动能报名的年级', 2, 301, '/activity-can-enroll-grade/list', '/business/funcampus/activity-can-enroll-grade/activity-can-enroll-grade-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '活动能报名的年级';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollGrade:query', 'activityCanEnrollGrade:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollGrade:add', 'activityCanEnrollGrade:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollGrade:update', 'activityCanEnrollGrade:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollGrade:delete', 'activityCanEnrollGrade:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityCanEnrollTribeMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '活动能报名的部落', 2, 301, '/activity-can-enroll-tribe/list', '/business/funcampus/activity-can-enroll-tribe/activity-can-enroll-tribe-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '活动能报名的部落';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollTribe:query', 'activityCanEnrollTribe:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollTribe:add', 'activityCanEnrollTribe:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollTribe:update', 'activityCanEnrollTribe:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'activityCanEnrollTribe:delete', 'activityCanEnrollTribe:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityCategoryMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '活动分类', 2, 301, '/activity-category/list', '/business/funcampus/activity-category/activity-category-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '活动分类';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'activityCategory:query', 'activityCategory:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'activityCategory:add', 'activityCategory:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'activityCategory:update', 'activityCategory:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'activityCategory:delete', 'activityCategory:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityEnrollmentMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '活动报名关系', 2, 301, '/activity-enrollment/list', '/business/funcampus/activity-enrollment/activity-enrollment-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '活动报名关系';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'activityEnrollment:query', 'activityEnrollment:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'activityEnrollment:add', 'activityEnrollment:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'activityEnrollment:update', 'activityEnrollment:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'activityEnrollment:delete', 'activityEnrollment:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityReviewLogMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '活动审核日志', 2, 301, '/activity-review-log/list', '/business/funcampus/activity-review-log/activity-review-log-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '活动审核日志';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'activityReviewLog:query', 'activityReviewLog:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'activityReviewLog:add', 'activityReviewLog:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'activityReviewLog:update', 'activityReviewLog:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'activityReviewLog:delete', 'activityReviewLog:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: ActivitySigninManagerMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '活动签到管理员', 2, 301, '/activity-signin-manager/list', '/business/funcampus/activity-signin-manager/activity-signin-manager-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '活动签到管理员';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'activitySigninManager:query', 'activitySigninManager:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'activitySigninManager:add', 'activitySigninManager:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'activitySigninManager:update', 'activitySigninManager:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'activitySigninManager:delete', 'activitySigninManager:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: CollegeInfoMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '学院信息', 2, 301, '/college-info/list', '/business/funcampus/college-info/college-info-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '学院信息';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'collegeInfo:query', 'collegeInfo:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'collegeInfo:add', 'collegeInfo:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'collegeInfo:update', 'collegeInfo:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'collegeInfo:delete', 'collegeInfo:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: CollegeReviewerMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '学院的审核员', 2, 301, '/college-reviewer/list', '/business/college-reviewer/college-reviewer-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '学院的审核员';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'collegeReviewer:query', 'collegeReviewer:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'collegeReviewer:add', 'collegeReviewer:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'collegeReviewer:update', 'collegeReviewer:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'collegeReviewer:delete', 'collegeReviewer:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: GradeInfoMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '年级信息', 2, 301, '/grade-info/list', '/business/funcampus/grade-info/grade-info-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '年级信息';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'gradeInfo:query', 'gradeInfo:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'gradeInfo:add', 'gradeInfo:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'gradeInfo:update', 'gradeInfo:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'gradeInfo:delete', 'gradeInfo:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: MessagePermissionMenu.sql —— 消息管理权限点补充（NOT EXISTS 保护，可重复执行）
-- ----------------------------------------------------------------------------
-- ----------------------------
-- 消息管理：补充按钮权限点并授权
-- 背景：菜单“消息管理”(menu_id=300) 已存在，但 system:message:send/query/delete
--       三个权限点从未注册，导致 @SaCheckPermission 校验必然 403。
-- 说明：接口权限来自 t_menu.api_perms（perms_type=1），并需通过 t_role_menu 授予角色。
-- 本脚本可重复执行（带 NOT EXISTS 保护）。
-- ----------------------------

-- 定位“消息管理”父菜单
SET @message_menu_id = NULL;
SELECT menu_id INTO @message_menu_id FROM t_menu WHERE menu_name = '消息管理' AND menu_type = 2 LIMIT 1;

-- 发送消息 system:message:send
INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
SELECT '发送消息', 3, @message_menu_id, false, false, true, false, 1, 'system:message:send', 'system:message:send', @message_menu_id, 1
FROM DUAL
WHERE @message_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM (SELECT menu_id FROM t_menu WHERE api_perms = 'system:message:send' AND menu_type = 3) t);

-- 查询消息 system:message:query
INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
SELECT '查询消息', 3, @message_menu_id, false, false, true, false, 1, 'system:message:query', 'system:message:query', @message_menu_id, 1
FROM DUAL
WHERE @message_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM (SELECT menu_id FROM t_menu WHERE api_perms = 'system:message:query' AND menu_type = 3) t);

-- 删除消息 system:message:delete
INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
SELECT '删除消息', 3, @message_menu_id, false, false, true, false, 1, 'system:message:delete', 'system:message:delete', @message_menu_id, 1
FROM DUAL
WHERE @message_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM (SELECT menu_id FROM t_menu WHERE api_perms = 'system:message:delete' AND menu_type = 3) t);

-- 授权给角色1（超级管理员），已授权则跳过
INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
SELECT 1, m.menu_id, NOW(), NOW()
FROM t_menu m
WHERE m.menu_type = 3
  AND m.api_perms IN ('system:message:send', 'system:message:query', 'system:message:delete')
  AND NOT EXISTS (
      SELECT 1 FROM (SELECT role_id, menu_id FROM t_role_menu) rm
      WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id
  );

-- ----------------------------------------------------------------------------
-- 来源文件: OrganizationInfoMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '各学校组织信息', 2, 301, '/organization-info/list', '/business/funcampus/organization-info/organization-info-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '各学校组织信息';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'organizationInfo:query', 'organizationInfo:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'organizationInfo:add', 'organizationInfo:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'organizationInfo:update', 'organizationInfo:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'organizationInfo:delete', 'organizationInfo:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: OrganizationReviewerMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '组织的审核员', 2, 301, '/organization-reviewer/list', '/business/organization-reviewer/organization-reviewer-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '组织的审核员';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'organizationReviewer:query', 'organizationReviewer:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'organizationReviewer:add', 'organizationReviewer:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'organizationReviewer:update', 'organizationReviewer:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'organizationReviewer:delete', 'organizationReviewer:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: OrganizerCadreMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '组织干事用户', 2, 301, '/organization-cadre/list', '/business/funcampus/organizer-cadre/organizer-cadre-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '组织干事用户';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'organizationCadre:query', 'organizationCadre:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'organizationCadre:add', 'organizationCadre:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'organizationCadre:update', 'organizationCadre:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'organizationCadre:delete', 'organizationCadre:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: PortalUserMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '前端用户', 2, 301, '/portal-user/list', '/business/funcampus/portal-user/portal-user-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '前端用户';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'portalUser:query', 'portalUser:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'portalUser:add', 'portalUser:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'portalUser:update', 'portalUser:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'portalUser:delete', 'portalUser:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: TribeMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '部落', 2, 301, '/tribe/list', '/business/funcampus/tribe/tribe-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '部落';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'tribe:query', 'tribe:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'tribe:add', 'tribe:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'tribe:update', 'tribe:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'tribe:delete', 'tribe:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: TribeUserMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '参与部落的用户', 2, 301, '/tribe-user/list', '/business/funcampus/tribe-user/tribe-user-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '参与部落的用户';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'tribeUser:query', 'tribeUser:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '添加', 3, @parent_id, false, false, true, false, 1, 'tribeUser:add', 'tribeUser:add', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '更新', 3, @parent_id, false, false, true, false, 1, 'tribeUser:update', 'tribeUser:update', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '删除', 3, @parent_id, false, false, true, false, 1, 'tribeUser:delete', 'tribeUser:delete', @parent_id, 1 );

-- ----------------------------------------------------------------------------
-- 来源文件: (fix) funcampus 菜单排序与可见性修复 —— 与生产库保持一致
-- ----------------------------------------------------------------------------
UPDATE t_menu SET sort = CASE menu_name
  WHEN '活动管理' THEN 10 WHEN '活动分类' THEN 20 WHEN '活动报名关系' THEN 30 WHEN '活动审核日志' THEN 40
  WHEN '活动签到管理员' THEN 50 WHEN '活动能报名的学院' THEN 60 WHEN '活动能报名的年级' THEN 70
  WHEN '活动能报名的部落' THEN 80 WHEN '学院信息' THEN 90 WHEN '年级信息' THEN 100
  WHEN '学校信息表' THEN 110 WHEN '各学校组织信息' THEN 120 WHEN '部落' THEN 130
  WHEN '参与部落的用户' THEN 140 WHEN '组织干事用户' THEN 150 WHEN '前端用户' THEN 160
  WHEN '组织账号运营者' THEN 170 WHEN '学院的审核员' THEN 180 WHEN '组织的审核员' THEN 190
  ELSE sort END
WHERE parent_id = 301 AND deleted_flag = 0;

-- 隐藏无对应前端页面的菜单（学院的审核员、组织的审核员）
UPDATE t_menu SET visible_flag = 0 WHERE menu_name IN ('学院的审核员','组织的审核员');

-- ----------------------------------------------------------------------------
-- 来源文件: AiAssistant.sql —— Phase AI 学生端智能助手（会话/消息/知识库）
-- ----------------------------------------------------------------------------
-- =====================================================================
-- Fun Campus Phase AI：学生端智能助手（RAG 检索 + 工具调用）
-- 内容：ai_conversation / ai_message / ai_knowledge 三表 + 平台规则 FAQ 初始数据
-- 说明：embedding 字段留空，由应用首次加载知识库时计算并回写；切换向量模型后自动失效重算
-- Date: 2026-09-25
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. ai_conversation AI 会话
-- ----------------------------
DROP TABLE IF EXISTS `ai_conversation`;
CREATE TABLE `ai_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '门户用户id',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话标题（取首条消息摘要）',
  `last_message_time` datetime NOT NULL COMMENT '最后一条消息时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_last`(`user_id` ASC, `last_message_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 会话' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 2. ai_message AI 消息
-- ----------------------------
DROP TABLE IF EXISTS `ai_message`;
CREATE TABLE `ai_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `conversation_id` bigint NOT NULL COMMENT '会话id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `role` tinyint NOT NULL COMMENT '角色：1-用户 2-助手',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conversation`(`conversation_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 3. ai_knowledge AI 知识库（平台规则 FAQ，供 RAG 检索）
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge`;
CREATE TABLE `ai_knowledge`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '条目标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '知识内容（FAQ 正文）',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'common' COMMENT '分类：enroll-报名 signin-签到 payment-支付 credit-学分 common-通用',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `embedding` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '向量缓存（JSON 数组字符串，由应用懒计算并回写）',
  `embedding_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '向量模型名（模型切换后自动失效重算）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 知识库（平台规则 FAQ）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 4. 平台规则 FAQ 初始数据
-- ----------------------------
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('活动如何报名', '在活动详情页点击报名按钮即可报名。只有处于「报名中」状态（报名时间窗口内）的活动可以报名；若活动设置了报名人数上限，名额满后将无法报名。报名成功后可在「我的-我的活动」中查看。', 'enroll', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('报名范围限制', '部分活动仅限指定学院、指定年级或指定部落的同学报名。活动详情页会展示报名条件；不符合条件时，报名入口会给出提示说明。', 'enroll', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('报名需要审核吗', '部分活动的报名需要组织方审核。提交报名后状态为「待审核」，审核通过后才算报名成功，审核结果会通过站内消息通知。需要审核的付费活动若审核未通过，报名费会自动退回。', 'enroll', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('付费活动如何支付', '付费活动点击报名后先生成订单并锁定名额，随后跳转收银台完成支付；支付成功报名才正式生效（需审核的活动支付后进入待审核状态）。15 分钟内未完成支付，订单会自动关闭并释放名额。', 'payment', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('订单超时与取消', '待支付订单可在「我的-我的订单」中手动取消；未在 15 分钟内支付的订单会被系统自动关闭，占用的名额随自动释放。已支付订单如需退出，按活动的退款政策申请退款。', 'payment', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('退款政策', '付费活动的退款规则由活动方设置，分三种：报名截止前可退、活动开始前可退、不可退款，具体以活动详情页展示为准。符合规则的退款申请审核通过后原路退回支付账户。', 'payment', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('签到怎么操作', '报名成功并进入签到时间窗口后，在「我的-签到码」出示个人专属二维码（30 秒有效，过期自动刷新），交给活动现场的签到员扫码即可完成签到。二维码一人一码、不绑定具体活动，签到成功后原码作废。', 'signin', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('签退说明', '部分活动要求签退。若活动设置了签退时间窗口，请在活动结束时再次出示「我的-签到码」，由签到员扫码完成签退；未按时签退可能影响该活动的完成认定。', 'signin', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('活动状态说明', '活动状态分为：等待报名（报名未开始）、报名中、报名结束、进行中、已结束。首页和活动详情页会实时展示当前状态与距离下一阶段的倒计时。', 'common', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('实践分如何获得', '活动详情页会标注该活动可获得的实践分（学分）。报名并按要求完成签到（及签退）后，实践分按学校二课规则计入个人学分，具体发放以学校或组织方认定为准。', 'credit', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('评论与点赞', '在活动详情页可以发表评论、回复他人评论，也可以对评论点赞或撤销点赞。评论列表支持按「最新」和「热门」两种方式排序，自己发表的评论可以删除。', 'common', 1);
INSERT INTO `ai_knowledge` (`title`, `content`, `category`, `status`) VALUES
('活动分享', '在活动详情页可以把活动生成分享链接发送给同学，对方打开链接后登录即可直接进入该活动详情页。', 'common', 1);

-- ----------------------------------------------------------------------------
-- 来源文件: CreditApplicationMenu.sql —— 菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------
# 默认是按前端工程文件的 /views/business 文件夹的路径作为前端组件路径，如果你没把生成的 .vue 前端代码放在 /views/business 下，
# 那就根据自己实际情况修改下面 SQL 的 path,component 字段值，避免执行 SQL 后菜单无法访问。
# 如果你一切都是按照默认，那么下面的 SQL 基本不用改

INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '学分认定', 2, 301, '/credit-application/list', '/business/funcampus/credit-application/credit-application-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称查询该菜单的 menu_id 作为按钮权限的 父菜单ID 与 功能点关联菜单ID
SET @parent_id = NULL;
SELECT t_menu.menu_id INTO @parent_id FROM t_menu WHERE t_menu.menu_name = '学分认定';

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @parent_id, false, false, true, false, 1, 'creditApplication:query', 'creditApplication:query', @parent_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '审核', 3, @parent_id, false, false, true, false, 1, 'creditApplication:review', 'creditApplication:review', @parent_id, 1 );

# 学分认定菜单排序（排在 funcampus 菜单组最后）
UPDATE t_menu SET sort = 200 WHERE menu_name = '学分认定' AND parent_id = 301 AND deleted_flag = 0;

# 菜单与按钮权限关联到角色（1-admin 管理端管理员；2-organization 组织账号），否则菜单不显示、审核接口无权限
INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
VALUES ( 1, @parent_id, NOW(), NOW() ), ( 2, @parent_id, NOW(), NOW() );

INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
SELECT 1, t_menu.menu_id, NOW(), NOW() FROM t_menu WHERE t_menu.parent_id = @parent_id AND t_menu.deleted_flag = 0;

INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
SELECT 2, t_menu.menu_id, NOW(), NOW() FROM t_menu WHERE t_menu.parent_id = @parent_id AND t_menu.deleted_flag = 0;

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityOrderMenu.sql —— 订单/退款管理菜单与按钮权限数据（t_menu）
-- ----------------------------------------------------------------------------

# 订单管理菜单（付款链路管理侧，与前端 /views/business/funcampus/activity-order 对应）
INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '订单管理', 2, 301, '/activity-order/list', '/business/funcampus/activity-order/activity-order-list.vue', false, false, true, false, 1, 1 );

# 按菜单名称 + 父菜单精确查询该菜单的 menu_id（避免菜单重名歧义）
SET @order_menu_id = NULL;
SELECT t_menu.menu_id INTO @order_menu_id FROM t_menu WHERE t_menu.menu_name = '订单管理' AND t_menu.parent_id = 301 AND t_menu.deleted_flag = 0;

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @order_menu_id, false, false, true, false, 1, 'activityOrder:query', 'activityOrder:query', @order_menu_id, 1 );

# 订单管理菜单排序（排在学分认定之后）
UPDATE t_menu SET sort = 210 WHERE menu_name = '订单管理' AND parent_id = 301 AND deleted_flag = 0;

# 退款管理菜单
INSERT INTO t_menu ( menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id )
VALUES ( '退款管理', 2, 301, '/activity-refund/list', '/business/funcampus/activity-order/activity-refund-list.vue', false, false, true, false, 1, 1 );

SET @refund_menu_id = NULL;
SELECT t_menu.menu_id INTO @refund_menu_id FROM t_menu WHERE t_menu.menu_name = '退款管理' AND t_menu.parent_id = 301 AND t_menu.deleted_flag = 0;

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '查询', 3, @refund_menu_id, false, false, true, false, 1, 'activityRefund:query', 'activityRefund:query', @refund_menu_id, 1 );

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '重试', 3, @refund_menu_id, false, false, true, false, 1, 'activityRefund:retry', 'activityRefund:retry', @refund_menu_id, 1 );

# 退款管理菜单排序
UPDATE t_menu SET sort = 220 WHERE menu_name = '退款管理' AND parent_id = 301 AND deleted_flag = 0;

# 菜单与按钮权限关联到角色（1-admin 管理端管理员；2-organization 组织账号），否则菜单不显示、接口无权限
INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
VALUES ( 1, @order_menu_id, NOW(), NOW() ), ( 2, @order_menu_id, NOW(), NOW() );

INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
SELECT 1, t_menu.menu_id, NOW(), NOW() FROM t_menu WHERE t_menu.parent_id = @order_menu_id AND t_menu.deleted_flag = 0;

INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
SELECT 2, t_menu.menu_id, NOW(), NOW() FROM t_menu WHERE t_menu.parent_id = @order_menu_id AND t_menu.deleted_flag = 0;

INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
VALUES ( 1, @refund_menu_id, NOW(), NOW() ), ( 2, @refund_menu_id, NOW(), NOW() );

INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
SELECT 1, t_menu.menu_id, NOW(), NOW() FROM t_menu WHERE t_menu.parent_id = @refund_menu_id AND t_menu.deleted_flag = 0;

INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
SELECT 2, t_menu.menu_id, NOW(), NOW() FROM t_menu WHERE t_menu.parent_id = @refund_menu_id AND t_menu.deleted_flag = 0;

-- ----------------------------------------------------------------------------
-- 来源文件: ActivityCancelMenu.sql —— 活动取消按钮权限（t_menu，配合接口 @SaCheckPermission("activity:cancel")）
-- ----------------------------------------------------------------------------

# 活动管理菜单下的「取消活动」按钮（按钮本身不参与侧边栏渲染，仅作为权限点）
SET @activity_manage_menu_id = NULL;
SELECT t_menu.menu_id INTO @activity_manage_menu_id FROM t_menu WHERE t_menu.menu_name = '活动管理' AND t_menu.parent_id = 301 AND t_menu.deleted_flag = 0;

INSERT INTO t_menu ( menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id )
VALUES ( '取消活动', 3, @activity_manage_menu_id, false, false, true, false, 1, 'activity:cancel', 'activity:cancel', @activity_manage_menu_id, 1 );

SET @cancel_btn_menu_id = NULL;
SELECT t_menu.menu_id INTO @cancel_btn_menu_id FROM t_menu WHERE t_menu.menu_name = '取消活动' AND t_menu.parent_id = @activity_manage_menu_id AND t_menu.deleted_flag = 0;

# 关联到角色（1-admin 管理端管理员；2-organization 组织账号），否则接口无权限
INSERT INTO t_role_menu ( role_id, menu_id, create_time, update_time )
VALUES ( 1, @cancel_btn_menu_id, NOW(), NOW() ), ( 2, @cancel_btn_menu_id, NOW(), NOW() );

