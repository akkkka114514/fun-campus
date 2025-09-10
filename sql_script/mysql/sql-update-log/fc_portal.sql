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

 Date: 08/09/2025 17:16:20
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
  `status` tinyint NOT NULL COMMENT '活动状态，1-》等待报名，2->报名结束，3->活动开始，4->活动结束，5-》签到开始，6-》签到结束',
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动地点',
  `score_can_get` decimal(10, 0) NOT NULL COMMENT '能得到的学分',
  `enroll_num_limit` int NOT NULL COMMENT '报名人数限制',
  `activity_school_id` bigint UNSIGNED NOT NULL COMMENT '活动所属学校',
  `activity_organizer_id` bigint UNSIGNED NOT NULL COMMENT '活动所属组织',
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
  `to_comment_id` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被回复的帖子id，null即为最顶层评论',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` timestamp NOT NULL,
  `root_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '为null即为顶层',
  `deleted` bit(1) NOT NULL,
  `update_time` timestamp NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted_flag` bit(1) NOT NULL COMMENT '是否已删除',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 159 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业关联的后台用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of backend_user
-- ----------------------------

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice_message
-- ----------------------------

-- ----------------------------
-- Table structure for organizer_activity
-- ----------------------------
DROP TABLE IF EXISTS `organizer_activity`;
CREATE TABLE `organizer_activity`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL,
  `organizer_id` bigint NOT NULL,
  `deleted` bit(1) NOT NULL,
  `update_time` timestamp NOT NULL,
  `create_time` timestamp NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of organizer_activity
-- ----------------------------

-- ----------------------------
-- Table structure for organizer_user_follower_num
-- ----------------------------
DROP TABLE IF EXISTS `organizer_user_follower_num`;
CREATE TABLE `organizer_user_follower_num`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '前台组织者id',
  `follower_num` bigint UNSIGNED NOT NULL COMMENT '关注者数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of organizer_user_follower_num
-- ----------------------------

-- ----------------------------
-- Table structure for portal_organizer_user
-- ----------------------------
DROP TABLE IF EXISTS `portal_organizer_user`;
CREATE TABLE `portal_organizer_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted_flag` bit(1) NOT NULL COMMENT '是否已删除',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` char(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `school_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of portal_organizer_user
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of portal_user_subscriber
-- ----------------------------

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category`  (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类id',
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `category_type` smallint NOT NULL COMMENT '分类类型',
  `parent_id` int NOT NULL COMMENT '父级id',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `disabled_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用',
  `deleted_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 381 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类表，主要用于商品分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_category
-- ----------------------------
INSERT INTO `t_category` VALUES (1, '手机', 1, 0, 0, 0, 0, NULL, '2022-10-10 22:27:24', '2022-07-14 20:55:15');
INSERT INTO `t_category` VALUES (2, '键盘', 1, 0, 0, 0, 0, NULL, '2022-09-14 21:39:00', '2022-07-14 20:55:48');
INSERT INTO `t_category` VALUES (3, '自定义1', 2, 0, 0, 0, 0, NULL, '2022-09-14 22:01:06', '2022-07-14 20:56:03');
INSERT INTO `t_category` VALUES (4, '自定义2', 2, 0, 0, 0, 0, NULL, '2022-09-14 22:01:10', '2022-07-14 20:56:09');
INSERT INTO `t_category` VALUES (351, '鼠标', 1, 0, 0, 0, 0, NULL, '2022-09-14 21:39:06', '2022-09-14 21:39:06');
INSERT INTO `t_category` VALUES (352, '苹果', 1, 1, 0, 0, 0, NULL, '2022-09-14 21:39:25', '2022-09-14 21:39:25');
INSERT INTO `t_category` VALUES (353, '华为', 1, 1, 0, 0, 0, NULL, '2022-09-14 21:39:32', '2022-09-14 21:39:32');
INSERT INTO `t_category` VALUES (354, 'IKBC', 1, 2, 0, 0, 0, NULL, '2022-09-14 21:39:38', '2022-09-14 21:39:38');
INSERT INTO `t_category` VALUES (355, '双飞燕', 1, 2, 0, 0, 0, NULL, '2022-09-14 21:39:47', '2022-09-14 21:39:47');
INSERT INTO `t_category` VALUES (356, '罗技', 1, 351, 0, 0, 0, NULL, '2022-09-14 21:39:57', '2022-09-14 21:39:57');
INSERT INTO `t_category` VALUES (357, '小米', 1, 1, 0, 0, 0, NULL, '2022-10-10 22:27:39', '2022-10-10 22:27:39');
INSERT INTO `t_category` VALUES (360, 'iphone', 1, 352, 0, 0, 0, NULL, '2023-12-04 21:26:55', '2023-12-01 19:54:22');

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
INSERT INTO `t_change_log` VALUES (2, 'v1.1.0', 2, '卓大', '2020-05-09', 'SmartAdmin中后台系统 v1.1.0 版本（20200422）正式更新上线，更新内容如下：\n\n1.【新增】增加后台用户姓名查询\n\n2.【新增】增加文件预览组件\n\n3.【新增】新增四级菜单\n', 'http://smartadmin.1024lab.net/views/1.x/base/About.html', '2022-10-04 21:33:50', '2022-10-04 21:33:50');
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
INSERT INTO `t_code_generator_config` VALUES ('activity', '{\"backendAuthor\":\"akkkka114514\",\"backendDate\":1756964502000,\"copyright\":\"akkkka114514\",\"description\":\"活动管理\",\"frontAuthor\":\"akkkka114514\",\"frontDate\":1756964502000,\"javaPackageName\":\"net.lab1024.sa.admin.module.business.funcampus\",\"moduleName\":\"Activity\"}', '[{\"autoIncreaseFlag\":true,\"columnComment\":\"主键\",\"columnName\":\"id\",\"fieldName\":\"id\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"主键\",\"primaryKeyFlag\":true},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动标题\",\"columnName\":\"title\",\"fieldName\":\"title\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"活动标题\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动状态，1-》等待报名，2->报名结束，3-》等待签到，4-》活动结束\",\"columnName\":\"status\",\"dict\":\"ACTIVITY_STATUS\",\"fieldName\":\"status\",\"javaType\":\"Integer\",\"jsType\":\"Number\",\"label\":\"活动状态\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动地点\",\"columnName\":\"position\",\"fieldName\":\"position\",\"javaType\":\"String\",\"jsType\":\"String\",\"label\":\"活动地点\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"能得到的学分\",\"columnName\":\"score_can_get\",\"fieldName\":\"scoreCanGet\",\"javaType\":\"BigDecimal\",\"jsType\":\"Number\",\"label\":\"能得到的学分\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"报名人数限制\",\"columnName\":\"enroll_num_limit\",\"fieldName\":\"enrollNumLimit\",\"javaType\":\"Integer\",\"jsType\":\"Number\",\"label\":\"报名人数限制\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动所属学校\",\"columnName\":\"activity_school_id\",\"fieldName\":\"activitySchoolId\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"活动所属学校\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动所属组织\",\"columnName\":\"activity_organizer_id\",\"fieldName\":\"activityOrganizerId\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"活动所属组织\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"是否删除\",\"columnName\":\"deleted_flag\",\"fieldName\":\"deletedFlag\",\"javaType\":\"Boolean\",\"jsType\":\"Boolean\",\"label\":\"是否已删除\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"创建时间\",\"columnName\":\"create_time\",\"fieldName\":\"createTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"创建时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"修改时间\",\"columnName\":\"update_time\",\"fieldName\":\"updateTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"修改时间\",\"primaryKeyFlag\":false}]', '{\"countPerLine\":1,\"fieldList\":[{\"columnName\":\"id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"title\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"status\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"position\",\"frontComponent\":\"Input\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"score_can_get\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"enroll_num_limit\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"activity_school_id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"activity_organizer_id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"deleted_flag\",\"frontComponent\":\"BooleanSelect\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"create_time\",\"frontComponent\":\"Date\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"update_time\",\"frontComponent\":\"Date\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false}],\"isSupportInsertAndUpdate\":true,\"pageType\":\"modal\",\"width\":\"30\"}', '{\"deleteEnum\":\"SingleAndBatch\",\"isPhysicallyDeleted\":false,\"isSupportDelete\":true}', '[]', '[{\"columnName\":\"id\",\"ellipsisFlag\":true,\"fieldName\":\"id\",\"label\":\"主键\",\"showFlag\":true},{\"columnName\":\"title\",\"ellipsisFlag\":true,\"fieldName\":\"title\",\"label\":\"活动标题\",\"showFlag\":true},{\"columnName\":\"status\",\"ellipsisFlag\":true,\"fieldName\":\"status\",\"label\":\"活动状态\",\"showFlag\":true},{\"columnName\":\"position\",\"ellipsisFlag\":true,\"fieldName\":\"position\",\"label\":\"活动地点\",\"showFlag\":true},{\"columnName\":\"score_can_get\",\"ellipsisFlag\":true,\"fieldName\":\"scoreCanGet\",\"label\":\"能得到的学分\",\"showFlag\":true},{\"columnName\":\"enroll_num_limit\",\"ellipsisFlag\":true,\"fieldName\":\"enrollNumLimit\",\"label\":\"报名人数限制\",\"showFlag\":true},{\"columnName\":\"activity_school_id\",\"ellipsisFlag\":true,\"fieldName\":\"activitySchoolId\",\"label\":\"活动所属学校\",\"showFlag\":true},{\"columnName\":\"activity_organizer_id\",\"ellipsisFlag\":true,\"fieldName\":\"activityOrganizerId\",\"label\":\"活动所属组织\",\"showFlag\":true},{\"columnName\":\"deleted_flag\",\"ellipsisFlag\":true,\"fieldName\":\"deletedFlag\",\"label\":\"是否已删除\",\"showFlag\":true},{\"columnName\":\"create_time\",\"ellipsisFlag\":true,\"fieldName\":\"createTime\",\"label\":\"创建时间\",\"showFlag\":true},{\"columnName\":\"update_time\",\"ellipsisFlag\":true,\"fieldName\":\"updateTime\",\"label\":\"修改时间\",\"showFlag\":true}]', NULL, '2025-09-04 13:47:41', '2025-09-04 13:47:41');
INSERT INTO `t_code_generator_config` VALUES ('activity_schedule', '{\"backendAuthor\":\"akkkka114514\",\"backendDate\":1757145247000,\"copyright\":\"akkkka114514\",\"description\":\"活动时间表\",\"frontAuthor\":\"akkkka114514\",\"frontDate\":1757145247000,\"javaPackageName\":\"net.lab1024.sa.admin.module.business.funcampus\",\"moduleName\":\"ActivitySchedule\"}', '[{\"autoIncreaseFlag\":true,\"columnComment\":\"主键\",\"columnName\":\"activity_id\",\"fieldName\":\"activityId\",\"javaType\":\"Long\",\"jsType\":\"Number\",\"label\":\"主键\",\"primaryKeyFlag\":true},{\"autoIncreaseFlag\":false,\"columnComment\":\"报名开始时间\",\"columnName\":\"enroll_start_time\",\"fieldName\":\"enrollStartTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"报名开始时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"报名结束时间\",\"columnName\":\"enroll_end_time\",\"fieldName\":\"enrollEndTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"报名结束时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动开始时间\",\"columnName\":\"activity_start_time\",\"fieldName\":\"activityStartTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"活动开始时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"活动结束时间\",\"columnName\":\"activity_end_time\",\"fieldName\":\"activityEndTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"活动结束时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"签到开始时间\",\"columnName\":\"signin_start_time\",\"fieldName\":\"signinStartTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"签到开始时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"签到结束时间\",\"columnName\":\"signin_end_time\",\"fieldName\":\"signinEndTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"签到结束时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"创建时间\",\"columnName\":\"create_time\",\"fieldName\":\"createTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"创建时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"修改时间\",\"columnName\":\"update_time\",\"fieldName\":\"updateTime\",\"javaType\":\"LocalDateTime\",\"jsType\":\"Date\",\"label\":\"修改时间\",\"primaryKeyFlag\":false},{\"autoIncreaseFlag\":false,\"columnComment\":\"是否已删除\",\"columnName\":\"deleted_flag\",\"fieldName\":\"deletedFlag\",\"javaType\":\"Boolean\",\"jsType\":\"Boolean\",\"label\":\"是否已删除\",\"primaryKeyFlag\":false}]', '{\"countPerLine\":1,\"fieldList\":[{\"columnName\":\"activity_id\",\"frontComponent\":\"InputNumber\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"enroll_start_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"enroll_end_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"activity_start_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"activity_end_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"signin_start_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"signin_end_time\",\"frontComponent\":\"Date\",\"insertFlag\":true,\"requiredFlag\":true,\"updateFlag\":false},{\"columnName\":\"create_time\",\"frontComponent\":\"Date\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"update_time\",\"frontComponent\":\"Date\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false},{\"columnName\":\"deleted_flag\",\"frontComponent\":\"BooleanSelect\",\"insertFlag\":false,\"requiredFlag\":false,\"updateFlag\":false}],\"isSupportInsertAndUpdate\":true,\"pageType\":\"modal\",\"width\":\"30\"}', '{\"deleteEnum\":\"SingleAndBatch\",\"isPhysicallyDeleted\":false,\"isSupportDelete\":true}', '[]', '[{\"columnName\":\"activity_id\",\"ellipsisFlag\":true,\"fieldName\":\"activityId\",\"label\":\"主键\",\"showFlag\":true},{\"columnName\":\"enroll_start_time\",\"ellipsisFlag\":true,\"fieldName\":\"enrollStartTime\",\"label\":\"报名开始时间\",\"showFlag\":true},{\"columnName\":\"enroll_end_time\",\"ellipsisFlag\":true,\"fieldName\":\"enrollEndTime\",\"label\":\"报名结束时间\",\"showFlag\":true},{\"columnName\":\"activity_start_time\",\"ellipsisFlag\":true,\"fieldName\":\"activityStartTime\",\"label\":\"活动开始时间\",\"showFlag\":true},{\"columnName\":\"activity_end_time\",\"ellipsisFlag\":true,\"fieldName\":\"activityEndTime\",\"label\":\"活动结束时间\",\"showFlag\":true},{\"columnName\":\"signin_start_time\",\"ellipsisFlag\":true,\"fieldName\":\"signinStartTime\",\"label\":\"签到开始时间\",\"showFlag\":true},{\"columnName\":\"signin_end_time\",\"ellipsisFlag\":true,\"fieldName\":\"signinEndTime\",\"label\":\"签到结束时间\",\"showFlag\":true},{\"columnName\":\"create_time\",\"ellipsisFlag\":true,\"fieldName\":\"createTime\",\"label\":\"创建时间\",\"showFlag\":true},{\"columnName\":\"update_time\",\"ellipsisFlag\":true,\"fieldName\":\"updateTime\",\"label\":\"修改时间\",\"showFlag\":true},{\"columnName\":\"deleted_flag\",\"ellipsisFlag\":true,\"fieldName\":\"deletedFlag\",\"label\":\"是否已删除\",\"showFlag\":true}]', NULL, '2025-09-06 15:55:41', '2025-09-06 15:55:41');

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
-- Table structure for t_data_tracer
-- ----------------------------
DROP TABLE IF EXISTS `t_data_tracer`;
CREATE TABLE `t_data_tracer`  (
  `data_tracer_id` bigint NOT NULL AUTO_INCREMENT,
  `data_id` bigint NOT NULL COMMENT '各种单据的id',
  `type` int NOT NULL COMMENT '单据类型',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '操作内容',
  `diff_old` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '差异：旧的数据',
  `diff_new` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '差异：新的数据',
  `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '额外信息',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_type` int NOT NULL COMMENT '用户类型：1 后管用户 ',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名称',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
  `ip_region` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip地区',
  `user_agent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ua',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`data_tracer_id`) USING BTREE,
  INDEX `order_id_order_type`(`data_id` ASC, `type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '各种单据操作记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_data_tracer
-- ----------------------------
INSERT INTO `t_data_tracer` VALUES (35, 10, 1, '新增', NULL, NULL, NULL, 47, 1, '善逸', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.61', '2023-10-07 19:02:24', '2023-10-07 19:02:24');
INSERT INTO `t_data_tracer` VALUES (36, 11, 1, '新增', NULL, NULL, NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36', '2023-12-01 19:55:53', '2023-12-01 19:55:53');
INSERT INTO `t_data_tracer` VALUES (37, 12, 1, '新增', NULL, NULL, NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36', '2023-12-01 19:57:26', '2023-12-01 19:57:26');
INSERT INTO `t_data_tracer` VALUES (38, 11, 1, '', NULL, NULL, NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36', '2023-12-01 19:58:09', '2023-12-01 19:58:09');
INSERT INTO `t_data_tracer` VALUES (39, 2, 3, '修改企业信息', '统一社会信用代码:\"1024lab\"<br/>详细地址:\"1024大楼\"<br/>区县名称:\"洛龙区\"<br/>禁用状态:false<br/>类型:有限企业<br/>城市名称:\"洛阳市\"<br/>删除状态:false<br/>联系人:\"卓大\"<br/>省份名称:\"河南省\"<br/>企业logo:\"public/common/fb827d63dda74a60ab8b4f70cc7c7d0a_20221022145641_jpg\"<br/>联系人电话:\"18637925892\"<br/>企业名称:\"1024创新实验室\"<br/>邮箱:\"lab1024@163.com\"', '营业执照:\"public/common/59b1ca99b7fe45d78678e6295798a699_20231201200459.jpg\"<br/>统一社会信用代码:\"1024lab1\"<br/>详细地址:\"1024大楼\"<br/>区县名称:\"洛龙区\"<br/>禁用状态:false<br/>类型:外资企业<br/>城市名称:\"洛阳市\"<br/>删除状态:false<br/>联系人:\"卓大1\"<br/>省份名称:\"河南省\"<br/>企业logo:\"\"<br/>联系人电话:\"18637925892\"<br/>企业名称:\"1024创新实验室1\"<br/>邮箱:\"lab1024@163.com\"', NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36', '2023-12-01 20:05:05', '2023-12-01 20:05:05');
INSERT INTO `t_data_tracer` VALUES (40, 2, 3, '修改企业信息', '营业执照:\"public/common/59b1ca99b7fe45d78678e6295798a699_20231201200459.jpg\"<br/>统一社会信用代码:\"1024lab1\"<br/>详细地址:\"1024大楼\"<br/>区县名称:\"洛龙区\"<br/>禁用状态:false<br/>类型:外资企业<br/>城市名称:\"洛阳市\"<br/>删除状态:false<br/>联系人:\"卓大1\"<br/>省份名称:\"河南省\"<br/>企业logo:\"\"<br/>联系人电话:\"18637925892\"<br/>企业名称:\"1024创新实验室1\"<br/>邮箱:\"lab1024@163.com\"', '营业执照:\"public/common/59b1ca99b7fe45d78678e6295798a699_20231201200459.jpg\"<br/>统一社会信用代码:\"1024lab\"<br/>详细地址:\"1024大楼\"<br/>区县名称:\"洛龙区\"<br/>禁用状态:false<br/>类型:外资企业<br/>城市名称:\"洛阳市\"<br/>删除状态:false<br/>联系人:\"卓大\"<br/>省份名称:\"河南省\"<br/>企业logo:\"\"<br/>联系人电话:\"18637925892\"<br/>企业名称:\"1024创新实验室\"<br/>邮箱:\"lab1024@163.com\"', NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36', '2023-12-01 20:05:54', '2023-12-01 20:05:54');
INSERT INTO `t_data_tracer` VALUES (41, 2, 3, '更新银行:<br/>', NULL, NULL, NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36', '2023-12-01 20:09:17', '2023-12-01 20:09:17');
INSERT INTO `t_data_tracer` VALUES (42, 2, 3, '更新发票：<br/>删除状态:由【false】变更为【】', NULL, NULL, NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36', '2023-12-01 20:09:20', '2023-12-01 20:09:20');
INSERT INTO `t_data_tracer` VALUES (49, 1, 3, '修改企业信息', '营业执照:\"public/common/852b7e19bef94af39c1a6156edf47cfb_20221022170332_jpg\"<br/>统一社会信用代码:\"1024lab_block\"<br/>详细地址:\"区块链大楼\"<br/>区县名称:\"洛龙区\"<br/>禁用状态:false<br/>类型:有限企业<br/>城市名称:\"洛阳市\"<br/>删除状态:false<br/>联系人:\"开云\"<br/>省份名称:\"河南省\"<br/>企业logo:\"public/common/f4a76fa720814949a610f05f6f9545bf_20221022170256_jpg\"<br/>联系人电话:\"18637925892\"<br/>企业名称:\"1024创新区块链实验室\"', '营业执照:\"public/common/1d89055e5680426280446aff1e7e627c_20240306112451.jpeg\"<br/>统一社会信用代码:\"1024lab_block\"<br/>详细地址:\"区块链大楼\"<br/>区县名称:\"洛龙区\"<br/>禁用状态:false<br/>类型:有限企业<br/>城市名称:\"洛阳市\"<br/>删除状态:false<br/>联系人:\"开云\"<br/>省份名称:\"河南省\"<br/>企业logo:\"public/common/34f5ac0fc097402294aea75352c128f0_20240306112435.png\"<br/>联系人电话:\"18637925892\"<br/>企业名称:\"1024创新区块链实验室\"', NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36', '2024-03-06 11:24:55', '2024-03-06 11:24:55');
INSERT INTO `t_data_tracer` VALUES (99, 12, 1, '', NULL, NULL, NULL, 1, 1, '管理员', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36', '2024-09-03 21:06:32', '2024-09-03 21:06:32');

-- ----------------------------
-- Table structure for t_department
-- ----------------------------
DROP TABLE IF EXISTS `t_department`;
CREATE TABLE `t_department`  (
  `department_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门主键id',
  `department_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名称',
  `manager_id` bigint NULL DEFAULT NULL COMMENT '部门负责人id',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '部门的父级id',
  `sort` int NOT NULL COMMENT '部门排序',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`department_id`) USING BTREE,
  INDEX `parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_department
-- ----------------------------
INSERT INTO `t_department` VALUES (1, '1024创新实验室', 1, 0, 1, '2022-10-19 20:17:09', '2022-10-19 20:17:09');
INSERT INTO `t_department` VALUES (2, '开发部', 44, 1, 1000, '2022-10-19 20:22:23', '2022-10-19 20:22:23');
INSERT INTO `t_department` VALUES (3, '产品部', 2, 1, 99, '2022-10-21 10:25:30', '2022-10-21 10:25:30');
INSERT INTO `t_department` VALUES (4, '销售部', 64, 1, 9, '2022-10-21 10:25:47', '2022-10-21 10:25:47');
INSERT INTO `t_department` VALUES (5, '测试部', 48, 1, 0, '2022-11-05 10:54:18', '2022-11-05 10:54:18');
INSERT INTO `t_department` VALUES (7, '直播组', 44, 1, 1111, '2024-07-02 19:38:15', '2024-07-02 19:38:15');
INSERT INTO `t_department` VALUES (8, '抖音组', 47, 7, 0, '2024-07-02 19:39:11', '2024-07-02 19:39:11');

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
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_dict_data
-- ----------------------------
INSERT INTO `t_dict_data` VALUES (2, 1, 'LUO_YANG', '洛阳', 'sad', 2, 0, '2025-03-27 15:52:39', '2025-03-27 20:53:21');
INSERT INTO `t_dict_data` VALUES (3, 1, 'ZHENG_ZHOU', '郑州', '', 0, 0, '2025-03-27 18:58:16', '2025-03-27 20:53:32');
INSERT INTO `t_dict_data` VALUES (7, 1, 'BEI_JING', '北京', '', 0, 0, '2025-03-27 20:53:45', '2025-03-27 20:53:45');
INSERT INTO `t_dict_data` VALUES (8, 1, 'SHANG_HAI', '上海', '', 0, 0, '2025-03-27 20:53:45', '2025-03-27 20:53:45');
INSERT INTO `t_dict_data` VALUES (9, 4, '1', '等待报名', '', 0, 0, '2025-09-06 15:06:10', '2025-09-06 15:06:10');
INSERT INTO `t_dict_data` VALUES (10, 4, '2', '报名结束', '', 0, 0, '2025-09-06 15:06:38', '2025-09-06 15:06:38');
INSERT INTO `t_dict_data` VALUES (11, 4, '3', '等待签到', '', 0, 0, '2025-09-06 15:07:07', '2025-09-06 15:07:07');
INSERT INTO `t_dict_data` VALUES (12, 4, '4', '活动结束', '', 0, 0, '2025-09-06 15:07:16', '2025-09-06 15:07:16');

-- ----------------------------
-- Table structure for t_employee
-- ----------------------------
DROP TABLE IF EXISTS `t_employee`;
CREATE TABLE `t_employee`  (
  `employee_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_uid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后台用户uuid',
  `login_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录帐号',
  `login_pwd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录密码',
  `actual_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '后台用户名称',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `gender` tinyint(1) NOT NULL DEFAULT 0 COMMENT '性别',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `department_id` bigint NOT NULL COMMENT '部门id',
  `position_id` bigint NULL DEFAULT NULL COMMENT '职务ID',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `disabled_flag` tinyint UNSIGNED NOT NULL COMMENT '是否被禁用 0否1是',
  `deleted_flag` tinyint UNSIGNED NOT NULL COMMENT '是否删除0否 1是',
  `administrator_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否为超级管理员: 0 不是，1是',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`employee_id`) USING BTREE,
  UNIQUE INDEX `employee_uid_index`(`employee_uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '后台用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_employee
-- ----------------------------
INSERT INTO `t_employee` VALUES (1, 'cf1e361fd46741f5b2a09335cef50db8', 'admin', '$argon2id$v=19$m=16384,t=2,p=1$d9yQEAhck+haKxP2ZtXocg$NEnw3D2Ly8UbYpy2odATLA4ZflZ1FKJjWCuOGrVE4PM', '管理员', 'public/common/f36e59b20faa4720b225edf81d15727a_20250713220349.jpeg', 0, '13500000000', 1, 3, NULL, 0, 0, 1, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (2, '3c253628b4cb4302a7bb83008a82a415', 'huke', '$argon2id$v=19$m=16384,t=2,p=1$3N9HxhPdydtmqXmTmBUxcw$Yh2jMqQ5qmCC1cgezKtFd5vuH8WirHZh6FPnFS0clEY', '胡克', NULL, 0, '13123123121', 1, 4, NULL, 0, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (44, '5e2a57cd8eff4346be03dc2acfed0d7c', 'zhuoda', '$argon2id$v=19$m=16384,t=2,p=1$Mt02VdlsDNrteY/sBOs2uw$0gI5gfb/D4iLGi6RRlEq/4Qo71cseuz5YZrwiCj3VQI', '卓大', NULL, 1, '18637925892', 1, 6, NULL, 0, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (47, 'b031a061076a4732aa0d63989adb1fbc', 'shanyi', '$argon2id$v=19$m=16384,t=2,p=1$lsqZF68KCPkPaF2ShNhtNQ$Zpsv0GLBeau3x0hL0JzpWtnIlNf0hh3+P6Zu5fM6gJw', '善逸', 'public/common/f823b00873684f0a9d31f0d62316cc8e_20240630015141.jpg', 1, '17630506613', 2, 5, NULL, 0, 0, 0, '这个是备注', '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (48, 'e29327485b784211aa9677a9436d2e00', 'qinjiu', '$argon2id$v=19$m=16384,t=2,p=1$ga8Ww+zlLShAzC8o54qftg$3Ete1M8/zzepZqiEV1yNu/U7svMI0EuDWVKZ9X5M1uQ', '琴酒', NULL, 2, '14112343212', 2, 6, NULL, 0, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (63, 'cab6922aeeb949a997c93c043b909b05', 'kaiyun', '$argon2id$v=19$m=16384,t=2,p=1$5TZB3BWsbv0FXrgA60+7ag$pnDVVvjE/J0kOet3xLq19fyv1+a/KGqN6B+xsvDluYc', '开云', NULL, 0, '13112312346', 2, 5, 'ss@qq.com', 0, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (64, '02ce19c1c707448a81159834a60bbd94', 'qingye', '$argon2id$v=19$m=16384,t=2,p=1$X+M3CF1557PGfLavpWXCPQ$2LsEiOgLFP+VbGA/7TPAbLnkyiLollova6iETB9S/ds', '清野', NULL, 1, '13123123111', 2, 4, NULL, 0, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (65, 'e791135b86c34435873f4c9068c0e9ba', 'feiye', '$argon2id$v=19$m=16384,t=2,p=1$cPMw0Xu3dgu4lFX1x+qUvQ$Ol6NktMqi2fGn4Djv+m5ha/DyARWkXA/y784hFVa0rQ', '飞叶', NULL, 1, '13123123112', 4, 3, NULL, 0, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (66, '2954e985557745df844e4c88532cd8a6', 'luoyi', '$argon2id$v=19$m=16384,t=2,p=1$D0lXN4LyhLhtHaKFbS3DRw$0FK9A8F1oT38xqIZvNcu1eWsB5C5vXkwULXhvLxYmK8', '罗伊', NULL, 1, '13123123142', 4, 2, NULL, 1, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (67, '39cb2c7de94141d6824e9a167912c23d', 'chuxiao', '$argon2id$v=19$m=16384,t=2,p=1$/BdtVk/U5utWvple9bfCQw$eK8JjH+cei7gNQwPDDdP5ACQT3qkYvz5Qk4k016jRpU', '初晓', NULL, 1, '13123123123', 1, 2, NULL, 1, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');
INSERT INTO `t_employee` VALUES (68, '2aaf8c8c393c46b080aca86179388d7e', 'xuanpeng', '$argon2id$v=19$m=16384,t=2,p=1$ldHEjEwCWur/RnSy0JmFJQ$nlhVYiFMELToZ9nXI5QxG4maTV/L7pyPU0GRv3+s+tg', '玄朋', NULL, 1, '13123123124', 1, 3, NULL, 0, 0, 0, NULL, '2025-07-15 10:19:23', '2022-10-04 21:33:50');

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
-- Table structure for t_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods`  (
  `goods_id` int NOT NULL AUTO_INCREMENT,
  `goods_status` int NULL DEFAULT NULL COMMENT '商品状态:[1:预约中,2:售卖中,3:售罄]',
  `category_id` int NOT NULL COMMENT '商品类目',
  `goods_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `place` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产地',
  `price` decimal(10, 2) UNSIGNED NOT NULL COMMENT '价格',
  `shelves_flag` tinyint UNSIGNED NOT NULL COMMENT '上架状态',
  `deleted_flag` tinyint UNSIGNED NOT NULL COMMENT '删除状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_goods
-- ----------------------------
INSERT INTO `t_goods` VALUES (1, 1, 353, 'Mote60', 'BEI_JING', 9999.00, 1, 0, NULL, '2022-10-21 19:57:49', '2021-09-01 22:25:30');
INSERT INTO `t_goods` VALUES (7, 1, 352, 'iphone15 pro', 'LUO_YANG', 50000.00, 1, 0, '备注', '2024-06-16 09:34:08', '2022-10-21 19:58:07');
INSERT INTO `t_goods` VALUES (8, 1, 352, 'iphone14', 'ZHENG_ZHOU', 150.00, 0, 0, '', '2022-10-21 19:12:49', '2022-10-21 19:00:11');
INSERT INTO `t_goods` VALUES (10, 1, 357, '小米15', 'LUO_YANG', 7999.00, 1, 0, '', '2023-10-07 19:02:24', '2023-10-07 19:02:24');
INSERT INTO `t_goods` VALUES (11, 1, 354, '青轴键盘', 'ZHENG_ZHOU', 199.00, 1, 0, '支持usb', '2023-12-01 19:58:09', '2023-12-01 19:55:53');
INSERT INTO `t_goods` VALUES (12, 1, 356, '罗技双模鼠标', 'BEI_JING,ZHENG_ZHOU', 99.00, 0, 0, '支持蓝牙', '2024-09-03 21:06:32', '2023-12-01 19:57:25');

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
) ENGINE = InnoDB AUTO_INCREMENT = 195 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公用服务 - 服务心跳' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_heart_beat_record
-- ----------------------------
INSERT INTO `t_heart_beat_record` VALUES (188, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 16672, '2025-09-04 12:44:06', '2025-09-04 15:12:07');
INSERT INTO `t_heart_beat_record` VALUES (189, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 22028, '2025-09-05 14:30:56', '2025-09-05 16:02:17');
INSERT INTO `t_heart_beat_record` VALUES (190, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 8712, '2025-09-05 16:03:29', '2025-09-05 20:20:01');
INSERT INTO `t_heart_beat_record` VALUES (191, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 5424, '2025-09-06 14:55:56', '2025-09-06 18:22:04');
INSERT INTO `t_heart_beat_record` VALUES (192, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 20220, '2025-09-07 12:13:47', '2025-09-07 15:35:01');
INSERT INTO `t_heart_beat_record` VALUES (193, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 11372, '2025-09-07 15:35:15', '2025-09-07 20:33:18');
INSERT INTO `t_heart_beat_record` VALUES (194, 'D:\\ideaWorkspace\\fun-campus', '127.0.0.1;192.168.3.104', 13452, '2025-09-08 15:00:20', '2025-09-08 17:11:34');

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
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录失败次数记录表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1914 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户登录日志' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 309 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_menu
-- ----------------------------
INSERT INTO `t_menu` VALUES (26, '菜单管理', 2, 50, 1, '/menu/list', '/system/menu/menu-list.vue', NULL, NULL, NULL, 'CopyOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, '2021-08-09 15:04:35', 1, '2023-12-01 19:39:03');
INSERT INTO `t_menu` VALUES (40, '删除', 3, 26, NULL, NULL, NULL, 1, 'system:menu:batchDelete', 'system:menu:batchDelete', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 09:45:56', 1, '2023-10-07 18:15:50');
INSERT INTO `t_menu` VALUES (45, '组织架构', 1, 0, 3, '/organization', NULL, NULL, NULL, NULL, 'UserSwitchOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 16:13:27', 1, '2024-07-02 19:27:44');
INSERT INTO `t_menu` VALUES (46, '后台用户管理', 2, 45, 3, '/organization/backendUser', '/system/backendUser/index.vue', NULL, NULL, NULL, 'AuditOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 16:21:50', 1, '2024-07-02 20:15:23');
INSERT INTO `t_menu` VALUES (47, '商品管理', 2, 48, 1, '/erp/goods/list', '/business/erp/goods/goods-list.vue', NULL, NULL, NULL, 'AliwangwangOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2021-08-12 17:58:39', 1, '2023-12-01 19:33:08');
INSERT INTO `t_menu` VALUES (48, '商品管理', 1, 138, 3, '/goods', NULL, NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-12 18:02:59', 1, '2024-07-08 13:58:46');
INSERT INTO `t_menu` VALUES (50, '系统设置', 1, 0, 6, '/setting', NULL, NULL, NULL, NULL, 'SettingOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-13 16:41:33', 1, '2023-12-01 19:38:03');
INSERT INTO `t_menu` VALUES (76, '角色管理', 2, 45, 4, '/organization/role', '/system/role/index.vue', NULL, NULL, NULL, 'SlidersOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2021-08-26 10:31:00', 1, '2024-07-02 20:15:28');
INSERT INTO `t_menu` VALUES (78, '商品分类', 2, 48, 2, '/erp/catalog/goods', '/business/erp/catalog/goods-catalog.vue', NULL, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2022-05-18 23:34:14', 1, '2023-12-01 19:33:13');
INSERT INTO `t_menu` VALUES (79, '自定义分组', 2, 48, 3, '/erp/catalog/custom', '/business/erp/catalog/custom-catalog.vue', NULL, NULL, NULL, 'AppstoreAddOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-18 23:37:53', 1, '2023-12-01 19:33:16');
INSERT INTO `t_menu` VALUES (81, '用户操作记录', 2, 213, 6, '/support/operate-log/operate-log-list', '/support/operate-log/operate-log-list.vue', NULL, NULL, NULL, 'VideoCameraOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-20 12:37:24', 44, '2024-08-13 14:34:10');
INSERT INTO `t_menu` VALUES (85, '组件演示', 2, 84, NULL, '/demonstration/index', '/support/demonstration/index.vue', NULL, NULL, NULL, 'ClearOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-20 23:16:46', NULL, '2022-05-20 23:16:46');
INSERT INTO `t_menu` VALUES (86, '添加部门', 3, 46, 1, NULL, NULL, 1, 'system:department:add', 'system:department:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:33:37', 1, '2023-10-07 18:26:35');
INSERT INTO `t_menu` VALUES (87, '修改部门', 3, 46, 2, NULL, NULL, 1, 'system:department:update', 'system:department:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:34:11', 1, '2023-10-07 18:26:44');
INSERT INTO `t_menu` VALUES (88, '删除部门', 3, 46, 3, NULL, NULL, 1, 'system:department:delete', 'system:department:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-26 23:34:49', 1, '2023-10-07 18:26:49');
INSERT INTO `t_menu` VALUES (91, '添加后台用户', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:add', 'system:backendUser:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:11:38', 1, '2023-10-07 18:27:46');
INSERT INTO `t_menu` VALUES (92, '编辑后台用户', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:update', 'system:backendUser:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:10', 1, '2023-10-07 18:27:49');
INSERT INTO `t_menu` VALUES (93, '禁用启用后台用户', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:disabled', 'system:backendUser:disabled', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:37', 1, '2023-10-07 18:27:53');
INSERT INTO `t_menu` VALUES (94, '调整后台用户部门', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:department:update', 'system:backendUser:department:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:12:59', 1, '2023-10-07 18:27:34');
INSERT INTO `t_menu` VALUES (95, '重置密码', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:password:reset', 'system:backendUser:password:reset', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:13:30', 1, '2023-10-07 18:27:57');
INSERT INTO `t_menu` VALUES (96, '删除后台用户', 3, 46, NULL, NULL, NULL, 1, 'system:backendUser:delete', 'system:backendUser:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:14:08', 1, '2023-10-07 18:28:01');
INSERT INTO `t_menu` VALUES (97, '添加角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:add', 'system:role:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:00', 1, '2023-10-07 18:42:31');
INSERT INTO `t_menu` VALUES (98, '删除角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:delete', 'system:role:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:19', 1, '2023-10-07 18:42:35');
INSERT INTO `t_menu` VALUES (99, '编辑角色', 3, 76, NULL, NULL, NULL, 1, 'system:role:update', 'system:role:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:34:55', 1, '2023-10-07 18:42:44');
INSERT INTO `t_menu` VALUES (100, '更新数据范围', 3, 76, NULL, NULL, NULL, 1, 'system:role:dataScope:update', 'system:role:dataScope:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:37:03', 1, '2023-10-07 18:41:49');
INSERT INTO `t_menu` VALUES (101, '批量移除后台用户', 3, 76, NULL, NULL, NULL, 1, 'system:role:backendUser:batch:delete', 'system:role:backendUser:batch:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:05', 1, '2023-10-07 18:43:32');
INSERT INTO `t_menu` VALUES (102, '移除后台用户', 3, 76, NULL, NULL, NULL, 1, 'system:role:backendUser:delete', 'system:role:backendUser:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:21', 1, '2023-10-07 18:43:37');
INSERT INTO `t_menu` VALUES (103, '添加后台用户', 3, 76, NULL, NULL, NULL, 1, 'system:role:backendUser:add', 'system:role:backendUser:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:39:38', 1, '2023-10-07 18:44:05');
INSERT INTO `t_menu` VALUES (104, '修改权限', 3, 76, NULL, NULL, NULL, 1, 'system:role:menu:update', 'system:role:menu:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:41:55', 1, '2023-10-07 18:44:11');
INSERT INTO `t_menu` VALUES (105, '添加', 3, 26, NULL, NULL, NULL, 1, 'system:menu:add', 'system:menu:add', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:44:37', 1, '2023-10-07 17:35:35');
INSERT INTO `t_menu` VALUES (106, '编辑', 3, 26, NULL, NULL, NULL, 1, 'system:menu:update', 'system:menu:update', NULL, 26, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 00:44:59', 1, '2023-10-07 17:35:48');
INSERT INTO `t_menu` VALUES (109, '参数配置', 2, 50, 3, '/config/config-list', '/support/config/config-list.vue', NULL, NULL, NULL, 'AntDesignOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 13:34:41', 1, '2022-06-23 16:24:16');
INSERT INTO `t_menu` VALUES (110, '数据字典', 2, 50, 4, '/setting/dict', '/support/dict/index.vue', NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-05-27 17:53:00', 1, '2022-05-27 18:09:14');
INSERT INTO `t_menu` VALUES (111, '监控服务', 1, 0, 100, '/monitor', NULL, NULL, NULL, NULL, 'BarChartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-17 11:13:23', 1, '2023-11-28 17:43:56');
INSERT INTO `t_menu` VALUES (113, '查询', 3, 112, NULL, NULL, NULL, NULL, NULL, 'ad', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-17 11:31:36', NULL, '2022-06-17 11:31:36');
INSERT INTO `t_menu` VALUES (114, '运维工具', 1, 0, 200, NULL, NULL, NULL, NULL, NULL, 'NodeCollapseOutlined', NULL, 0, NULL, 0, 1, 0, 1, 1, '2022-06-20 10:09:16', 1, '2023-12-01 19:36:18');
INSERT INTO `t_menu` VALUES (117, 'Reload', 2, 50, 12, '/hook', '/support/reload/reload-list.vue', NULL, NULL, NULL, 'ReloadOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-20 10:16:49', 1, '2023-12-01 19:39:17');
INSERT INTO `t_menu` VALUES (122, '数据库监控', 2, 111, 4, '/support/druid/index', NULL, NULL, NULL, NULL, 'ConsoleSqlOutlined', NULL, 1, 'http://localhost:1024/druid', 1, 1, 0, 0, 1, '2022-06-20 14:49:33', 1, '2023-02-16 19:15:58');
INSERT INTO `t_menu` VALUES (130, '单号管理', 2, 50, 6, '/support/serial-number/serial-number-list', '/support/serial-number/serial-number-list.vue', NULL, NULL, NULL, 'NumberOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 14:45:22', 1, '2022-06-28 16:23:41');
INSERT INTO `t_menu` VALUES (132, '公告管理', 2, 138, 2, '/oa/notice/notice-list', '/business/oa/notice/notice-list.vue', NULL, NULL, NULL, 'SoundOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2022-06-24 18:23:09', 1, '2024-07-08 13:58:51');
INSERT INTO `t_menu` VALUES (133, '缓存管理', 2, 50, 11, '/support/cache/cache-list', '/support/cache/cache-list.vue', NULL, NULL, NULL, 'BorderInnerOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 18:52:25', 1, '2023-12-01 19:39:13');
INSERT INTO `t_menu` VALUES (138, '功能Demo', 1, 0, 1, NULL, NULL, NULL, NULL, NULL, 'BankOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-24 20:09:18', 1, '2024-07-08 13:46:54');
INSERT INTO `t_menu` VALUES (142, '公告详情', 2, 132, NULL, '/oa/notice/notice-detail', '/business/oa/notice/notice-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-06-25 16:38:47', 1, '2022-09-14 19:46:17');
INSERT INTO `t_menu` VALUES (143, '登录登出记录', 2, 213, 5, '/support/login-log/login-log-list', '/support/login-log/login-log-list.vue', NULL, NULL, NULL, 'LoginOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-06-28 15:01:38', 44, '2024-08-13 14:33:49');
INSERT INTO `t_menu` VALUES (144, '企业管理', 2, 138, 1, '/oa/enterprise/enterprise-list', '/business/oa/enterprise/enterprise-list.vue', NULL, NULL, NULL, 'ShopOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 17:00:07', 1, '2024-07-08 13:48:24');
INSERT INTO `t_menu` VALUES (145, '企业详情', 2, 138, NULL, '/oa/enterprise/enterprise-detail', '/business/oa/enterprise/enterprise-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 18:52:52', 1, '2022-11-22 10:39:07');
INSERT INTO `t_menu` VALUES (147, '帮助文档', 2, 218, 1, '/help-doc/help-doc-manage-list', '/support/help-doc/management/help-doc-manage-list.vue', NULL, NULL, NULL, 'FolderViewOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 19:59:01', 1, '2023-12-01 19:38:23');
INSERT INTO `t_menu` VALUES (148, '意见反馈', 2, 218, 2, '/feedback/feedback-list', '/support/feedback/feedback-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-14 19:59:52', 1, '2023-12-01 19:38:40');
INSERT INTO `t_menu` VALUES (149, '我的通知', 2, 132, NULL, '/oa/notice/notice-backendUser-list', '/business/oa/notice/notice-backendUser-list.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 20:29:41', 1, '2022-09-14 20:31:23');
INSERT INTO `t_menu` VALUES (150, '我的通知公告详情', 2, 132, NULL, '/oa/notice/notice-backendUser-detail', '/business/oa/notice/notice-backendUser-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, '2022-09-14 20:30:25', 1, '2022-09-14 20:31:38');
INSERT INTO `t_menu` VALUES (151, '代码生成', 2, 0, 600, '/support/code-generator', '/support/code-generator/code-generator-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-09-21 18:25:05', 1, '2022-10-22 11:27:58');
INSERT INTO `t_menu` VALUES (152, '更新日志', 2, 218, 3, '/support/change-log/change-log-list', '/support/change-log/change-log-list.vue', NULL, NULL, NULL, 'HeartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 44, '2022-10-10 10:31:20', 1, '2023-12-01 19:38:51');
INSERT INTO `t_menu` VALUES (153, '清除缓存', 3, 133, NULL, NULL, NULL, 1, 'support:cache:delete', 'support:cache:delete', NULL, 133, 0, NULL, 0, 1, 1, 0, 1, '2022-10-15 22:45:13', 1, '2023-10-07 16:22:29');
INSERT INTO `t_menu` VALUES (154, '获取缓存key', 3, 133, NULL, NULL, NULL, 1, 'support:cache:keys', 'support:cache:keys', NULL, 133, 0, NULL, 0, 1, 1, 0, 1, '2022-10-15 22:45:48', 1, '2023-10-07 16:22:35');
INSERT INTO `t_menu` VALUES (156, '查看结果', 3, 117, NULL, NULL, NULL, 1, 'support:reload:result', 'support:reload:result', NULL, 117, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:17:23', 1, '2023-10-07 14:31:47');
INSERT INTO `t_menu` VALUES (157, '单号生成', 3, 130, NULL, NULL, NULL, 1, 'support:serialNumber:generate', 'support:serialNumber:generate', NULL, 130, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:21:06', 1, '2023-10-07 18:22:46');
INSERT INTO `t_menu` VALUES (158, '生成记录', 3, 130, NULL, NULL, NULL, 1, 'support:serialNumber:record', 'support:serialNumber:record', NULL, 130, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:21:34', 1, '2023-10-07 18:22:55');
INSERT INTO `t_menu` VALUES (159, '查询', 3, 110, NULL, NULL, NULL, 1, 'support:dict:query', 'support:dict:query', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:23:51', 1, '2025-04-08 19:42:25');
INSERT INTO `t_menu` VALUES (160, '添加', 3, 110, NULL, NULL, NULL, 1, 'support:dict:add', 'support:dict:add', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:05', 1, '2025-04-08 19:43:02');
INSERT INTO `t_menu` VALUES (161, '更新', 3, 110, NULL, NULL, NULL, 1, 'support:dict:update', 'support:dict:update', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:34', 1, '2025-04-08 19:43:34');
INSERT INTO `t_menu` VALUES (162, '删除', 3, 110, NULL, NULL, NULL, 1, 'support:dict:delete', 'support:dict:delete', NULL, 110, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:24:55', 1, '2025-04-08 19:43:52');
INSERT INTO `t_menu` VALUES (163, '新建', 3, 109, NULL, NULL, NULL, 1, 'support:config:add', 'support:config:add', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:26:56', 1, '2023-10-07 18:16:17');
INSERT INTO `t_menu` VALUES (164, '编辑', 3, 109, NULL, NULL, NULL, 1, 'support:config:update', 'support:config:update', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, '2022-10-15 23:27:07', 1, '2023-10-07 18:16:24');
INSERT INTO `t_menu` VALUES (165, '查询', 3, 47, NULL, NULL, NULL, 1, 'goods:query', 'goods:query', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:55:39', 1, '2023-10-07 13:58:28');
INSERT INTO `t_menu` VALUES (166, '新建', 3, 47, NULL, NULL, NULL, 1, 'goods:add', 'goods:add', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:56:00', 1, '2023-10-07 13:58:32');
INSERT INTO `t_menu` VALUES (167, '批量删除', 3, 47, NULL, NULL, NULL, 1, 'goods:batchDelete', 'goods:batchDelete', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 19:56:15', 1, '2023-10-07 13:58:35');
INSERT INTO `t_menu` VALUES (168, '查询', 3, 147, 11, NULL, NULL, 1, 'support:helpDoc:query', 'support:helpDoc:query', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:13', 1, '2023-10-07 14:05:49');
INSERT INTO `t_menu` VALUES (169, '新建', 3, 147, 12, NULL, NULL, 1, 'support:helpDoc:add', 'support:helpDoc:add', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:37', 1, '2023-10-07 14:05:56');
INSERT INTO `t_menu` VALUES (170, '新建目录', 3, 147, 1, NULL, NULL, 1, 'support:helpDocCatalog:addCategory', 'support:helpDocCatalog:addCategory', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:12:57', 1, '2023-10-07 14:06:38');
INSERT INTO `t_menu` VALUES (171, '修改目录', 3, 147, 2, NULL, NULL, 1, 'support:helpDocCatalog:update', 'support:helpDocCatalog:update', NULL, 147, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:13:46', 1, '2023-10-07 14:06:49');
INSERT INTO `t_menu` VALUES (173, '新建', 3, 78, NULL, NULL, NULL, 1, 'category:add', 'category:add', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:02', 1, '2023-10-07 13:54:01');
INSERT INTO `t_menu` VALUES (174, '查询', 3, 78, NULL, NULL, NULL, 1, 'category:tree', 'category:tree', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:22', 1, '2023-10-07 13:54:33');
INSERT INTO `t_menu` VALUES (175, '编辑', 3, 78, NULL, NULL, NULL, 1, 'category:update', 'category:update', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:38', 1, '2023-10-07 13:54:18');
INSERT INTO `t_menu` VALUES (176, '删除', 3, 78, NULL, NULL, NULL, 1, 'category:delete', 'category:delete', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:50', 1, '2023-10-07 13:54:27');
INSERT INTO `t_menu` VALUES (177, '新建', 3, 79, NULL, NULL, NULL, 1, 'category:add', 'custom:category:add', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:02', 1, '2023-10-07 13:57:32');
INSERT INTO `t_menu` VALUES (178, '查询', 3, 79, NULL, NULL, NULL, 1, 'category:tree', 'custom:category:tree', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:22', 1, '2023-10-07 13:57:50');
INSERT INTO `t_menu` VALUES (179, '编辑', 3, 79, NULL, NULL, NULL, 1, 'category:update', 'custom:category:update', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:38', 1, '2023-10-07 13:58:02');
INSERT INTO `t_menu` VALUES (180, '删除', 3, 79, NULL, NULL, NULL, 1, 'category:delete', 'custom:category:delete', NULL, 78, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:17:50', 1, '2023-10-07 13:58:12');
INSERT INTO `t_menu` VALUES (181, '查询', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:query', 'oa:enterprise:query', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:14', 1, '2023-10-07 12:00:09');
INSERT INTO `t_menu` VALUES (182, '新建', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:add', 'oa:enterprise:add', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:25', 1, '2023-10-07 12:00:17');
INSERT INTO `t_menu` VALUES (183, '编辑', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:update', 'oa:enterprise:update', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:36', 1, '2023-10-07 12:00:38');
INSERT INTO `t_menu` VALUES (184, '删除', 3, 144, NULL, NULL, NULL, 1, 'oa:enterprise:delete', 'oa:enterprise:delete', NULL, 144, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:25:53', 1, '2023-10-07 12:00:46');
INSERT INTO `t_menu` VALUES (185, '查询', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:query', 'oa:notice:query', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:26:38', 1, '2023-10-07 11:43:01');
INSERT INTO `t_menu` VALUES (186, '新建', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:add', 'oa:notice:add', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:04', 1, '2023-10-07 11:43:07');
INSERT INTO `t_menu` VALUES (187, '编辑', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:update', 'oa:notice:update', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:15', 1, '2023-10-07 11:43:12');
INSERT INTO `t_menu` VALUES (188, '删除', 3, 132, NULL, NULL, NULL, 1, 'oa:notice:delete', 'oa:notice:delete', NULL, 132, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:27:23', 1, '2023-10-07 11:43:18');
INSERT INTO `t_menu` VALUES (190, '查询', 3, 152, NULL, NULL, NULL, 1, '', 'support:changeLog:query', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:28:33', 1, '2023-10-07 14:25:05');
INSERT INTO `t_menu` VALUES (191, '新建', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:add', 'support:changeLog:add', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:28:46', 1, '2023-10-07 14:24:15');
INSERT INTO `t_menu` VALUES (192, '批量删除', 3, 152, NULL, NULL, NULL, 1, 'support:changeLog:batchDelete', 'support:changeLog:batchDelete', NULL, 152, 0, NULL, 0, 1, 0, 0, 1, '2022-10-16 20:29:10', 1, '2023-10-07 14:24:22');
INSERT INTO `t_menu` VALUES (193, '文件管理', 2, 50, 20, '/support/file/file-list', '/support/file/file-list.vue', NULL, NULL, NULL, 'FolderOpenOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 11:26:11', 1, '2022-10-22 11:29:22');
INSERT INTO `t_menu` VALUES (194, '删除', 3, 47, NULL, NULL, NULL, 1, 'goods:delete', 'goods:delete', NULL, 47, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:00:12', 1, '2023-10-07 13:58:39');
INSERT INTO `t_menu` VALUES (195, '修改', 3, 47, NULL, NULL, NULL, 1, 'goods:update', 'goods:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:05:23', 1, '2023-10-07 13:58:42');
INSERT INTO `t_menu` VALUES (196, '查看详情', 3, 145, NULL, NULL, NULL, 1, 'oa:enterprise:detail', 'oa:enterprise:detail', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2022-10-21 20:16:47', 1, '2023-10-07 11:48:59');
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
INSERT INTO `t_menu` VALUES (216, '导出', 3, 47, NULL, NULL, NULL, 1, 'goods:exportGoods', 'goods:exportGoods', NULL, NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:34:03', NULL, '2023-12-01 19:34:03');
INSERT INTO `t_menu` VALUES (217, '导入', 3, 47, 3, NULL, NULL, 1, 'goods:importGoods', 'goods:importGoods', NULL, NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:34:22', NULL, '2023-12-01 19:34:22');
INSERT INTO `t_menu` VALUES (218, '文档中心', 1, 0, 4, NULL, NULL, 1, NULL, NULL, 'FileSearchOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2023-12-01 19:37:28', 1, '2023-12-01 19:37:51');
INSERT INTO `t_menu` VALUES (219, '部门管理', 2, 45, 1, '/organization/department', '/system/department/department-list.vue', 1, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2024-06-22 16:40:21', 1, '2024-07-02 20:15:17');
INSERT INTO `t_menu` VALUES (221, '定时任务', 2, 50, 25, '/job/list', '/support/job/job-list.vue', 1, NULL, NULL, 'AppstoreOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, '2024-06-25 17:57:40', 2, '2024-06-25 19:49:21');
INSERT INTO `t_menu` VALUES (228, '职务管理', 2, 45, 2, '/organization/position', '/system/position/position-list.vue', 1, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, '2024-06-29 11:11:09', 1, '2024-07-02 20:15:11');
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
INSERT INTO `t_menu` VALUES (258, '查询企业后台用户', 3, 145, NULL, NULL, NULL, 1, 'oa:enterprise:queryBackendUser', 'oa:enterprise:queryBackendUser', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:11:46', 75, '2025-04-08 21:12:24');
INSERT INTO `t_menu` VALUES (259, '查询银行信息', 3, 145, NULL, NULL, NULL, 1, 'oa:bank:query', 'oa:bank:query', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:12:40', NULL, '2025-04-08 21:12:40');
INSERT INTO `t_menu` VALUES (260, '查询发票信息', 3, 145, NULL, NULL, NULL, 1, 'oa:invoice:query', 'oa:invoice:query', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:12:56', NULL, '2025-04-08 21:12:56');
INSERT INTO `t_menu` VALUES (261, '添加企业后台用户', 3, 145, NULL, NULL, NULL, 1, 'oa:enterprise:addBackendUser', 'oa:enterprise:addBackendUser', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:35:34', NULL, '2025-04-08 21:35:34');
INSERT INTO `t_menu` VALUES (262, '删除企业后台用户', 3, 145, NULL, NULL, NULL, 1, 'oa:enterprise:deleteBackendUser', 'oa:enterprise:deleteBackendUser', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:40:17', NULL, '2025-04-08 21:40:17');
INSERT INTO `t_menu` VALUES (263, '添加银行信息', 3, 145, NULL, NULL, NULL, 1, 'oa:bank:add', 'oa:bank:add', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:45:44', NULL, '2025-04-08 21:45:44');
INSERT INTO `t_menu` VALUES (264, '更新银行信息', 3, 145, NULL, NULL, NULL, 1, 'oa:bank:update', 'oa:bank:update', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:46:02', NULL, '2025-04-08 21:46:02');
INSERT INTO `t_menu` VALUES (265, '删除银行信息', 3, 145, NULL, NULL, NULL, 1, 'oa:bank:delete', 'oa:bank:delete', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:46:12', NULL, '2025-04-08 21:46:12');
INSERT INTO `t_menu` VALUES (266, '添加发票信息', 3, 145, NULL, NULL, NULL, 1, 'oa:invoice:add', 'oa:invoice:add', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:46:30', NULL, '2025-04-08 21:46:30');
INSERT INTO `t_menu` VALUES (267, '更新发票信息', 3, 145, NULL, NULL, NULL, 1, 'oa:invoice:update', 'oa:invoice:update', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:46:47', NULL, '2025-04-08 21:46:47');
INSERT INTO `t_menu` VALUES (268, '删除发票信息', 3, 145, NULL, NULL, NULL, 1, 'oa:invoice:delete', 'oa:invoice:delete', NULL, 145, 0, NULL, 0, 1, 0, 0, 75, '2025-04-08 21:46:59', NULL, '2025-04-08 21:46:59');
INSERT INTO `t_menu` VALUES (300, '消息管理', 2, 50, 30, '/message', '/support/message/message-list.vue', 1, NULL, NULL, 'MailOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-04-09 14:30:04', 1, '2025-04-10 20:19:36');
INSERT INTO `t_menu` VALUES (301, 'funcampus', 1, 0, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-09-05 14:36:19', NULL, '2025-09-05 14:36:19');
INSERT INTO `t_menu` VALUES (303, '查询', 3, 302, NULL, NULL, NULL, 1, 'activity:query', 'activity:query', NULL, 302, 0, NULL, 0, 1, 0, 1, 1, '2025-09-05 14:42:42', 1, '2025-09-05 14:49:27');
INSERT INTO `t_menu` VALUES (304, '添加', 3, 302, NULL, NULL, NULL, 1, 'activity:add', 'activity:add', NULL, 302, 0, NULL, 0, 1, 0, 1, 1, '2025-09-05 14:42:42', 1, '2025-09-05 14:49:27');
INSERT INTO `t_menu` VALUES (305, '更新', 3, 302, NULL, NULL, NULL, 1, 'activity:update', 'activity:update', NULL, 302, 0, NULL, 0, 1, 0, 1, 1, '2025-09-05 14:42:42', 1, '2025-09-05 14:49:27');
INSERT INTO `t_menu` VALUES (306, '删除', 3, 302, NULL, NULL, NULL, 1, 'activity:delete', 'activity:delete', NULL, 302, 0, NULL, 0, 1, 0, 1, 1, '2025-09-05 14:42:42', 1, '2025-09-05 14:49:27');
INSERT INTO `t_menu` VALUES (307, '活动管理', 2, 301, NULL, '/activity/list', '/bussiness/funcampus/activity/activity-list.vue', 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, '2025-09-05 14:44:22', 1, '2025-09-05 16:17:42');
INSERT INTO `t_menu` VALUES (308, '查询', 3, 307, NULL, NULL, NULL, 1, NULL, NULL, NULL, 307, 0, NULL, 0, 1, 0, 0, 1, '2025-09-05 15:28:33', NULL, '2025-09-05 15:28:33');

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
  `employee_id` bigint NOT NULL COMMENT '后台用户id',
  `page_view_count` int NULL DEFAULT 0 COMMENT '查看次数',
  `first_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次ip',
  `first_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首次用户设备等标识',
  `last_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次ip',
  `last_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后一次用户设备等标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`notice_id`, `employee_id`) USING BTREE
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
  `data_type` tinyint NOT NULL COMMENT '数据类型1后台用户 2部门',
  `data_id` bigint NOT NULL COMMENT '后台用户or部门id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE INDEX `uk_notice_data`(`notice_id` ASC, `data_type` ASC, `data_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知可见范围' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_notice_visible_range
-- ----------------------------
INSERT INTO `t_notice_visible_range` VALUES (63, 1, 63, '2024-08-09 10:40:32');

-- ----------------------------
-- Table structure for t_oa_bank
-- ----------------------------
DROP TABLE IF EXISTS `t_oa_bank`;
CREATE TABLE `t_oa_bank`  (
  `bank_id` bigint NOT NULL AUTO_INCREMENT COMMENT '银行信息ID',
  `bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开户银行',
  `account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户名称',
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `business_flag` tinyint(1) NOT NULL COMMENT '是否对公',
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`bank_id`) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OA银行信息\n' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_oa_bank
-- ----------------------------
INSERT INTO `t_oa_bank` VALUES (26, '工商银行', '1024创新实验室', '1024', '基本户', 1, 2, 0, 0, 1, '管理员', '2022-10-22 17:58:43', '2022-10-22 17:58:43');
INSERT INTO `t_oa_bank` VALUES (27, '建设银行', '1024创新实验室', '10241', '其他户', 0, 2, 0, 0, 1, '管理员', '2022-10-22 17:59:19', '2022-10-22 17:59:19');

-- ----------------------------
-- Table structure for t_oa_enterprise
-- ----------------------------
DROP TABLE IF EXISTS `t_oa_enterprise`;
CREATE TABLE `t_oa_enterprise`  (
  `enterprise_id` bigint NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `enterprise_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '企业名称',
  `enterprise_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '企业logo',
  `type` int NOT NULL DEFAULT 1 COMMENT '类型（1:有限公司;2:合伙公司）',
  `unified_social_credit_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统一社会信用代码',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人',
  `contact_phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `province` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份',
  `province_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份名称',
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '市',
  `city_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市名称',
  `district` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县',
  `district_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `business_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '营业执照',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`enterprise_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 127 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OA企业模块\r\n' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_oa_enterprise
-- ----------------------------
INSERT INTO `t_oa_enterprise` VALUES (1, '1024创新区块链实验室', 'public/common/34f5ac0fc097402294aea75352c128f0_20240306112435.png', 1, '1024lab_block', '开云', '18637925892', NULL, '410000', '河南省', '410300', '洛阳市', '410311', '洛龙区', '区块链大楼', 'public/common/1d89055e5680426280446aff1e7e627c_20240306112451.jpeg', 0, 0, 1, '管理员', '2021-10-22 17:03:35', '2022-10-22 17:04:18');
INSERT INTO `t_oa_enterprise` VALUES (2, '1024创新实验室', '', 2, '1024lab', '卓大', '18637925892', 'lab1024@163.com', '410000', '河南省', '410300', '洛阳市', '410311', '洛龙区', '1024大楼', 'public/common/59b1ca99b7fe45d78678e6295798a699_20231201200459.jpg', 0, 0, 44, '卓大', '2022-10-22 14:57:36', '2022-10-22 17:03:57');

-- ----------------------------
-- Table structure for t_oa_invoice
-- ----------------------------
DROP TABLE IF EXISTS `t_oa_invoice`;
CREATE TABLE `t_oa_invoice`  (
  `invoice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '发票信息ID',
  `invoice_heads` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开票抬头',
  `taxpayer_identification_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '纳税人识别号',
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '银行账户',
  `bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开户行',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`invoice_id`) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OA发票信息\n' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_oa_invoice
-- ----------------------------
INSERT INTO `t_oa_invoice` VALUES (15, '1024创新实验室', '1024lab', '1024lab', '中国银行', '123', 2, 0, 0, 1, '管理员', '2022-10-22 17:59:35', '2023-09-27 16:26:07');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4560 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_operate_log
-- ----------------------------
INSERT INTO `t_operate_log` VALUES (4499, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 12:57:17', '2025-09-04 12:57:17');
INSERT INTO `t_operate_log` VALUES (4500, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 12:57:17', '2025-09-04 12:57:17');
INSERT INTO `t_operate_log` VALUES (4501, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 13:30:53', '2025-09-04 13:30:53');
INSERT INTO `t_operate_log` VALUES (4502, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 13:30:53', '2025-09-04 13:30:53');
INSERT INTO `t_operate_log` VALUES (4503, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 15:08:46', '2025-09-04 15:08:46');
INSERT INTO `t_operate_log` VALUES (4504, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 15:08:46', '2025-09-04 15:08:46');
INSERT INTO `t_operate_log` VALUES (4505, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 15:10:31', '2025-09-04 15:10:31');
INSERT INTO `t_operate_log` VALUES (4506, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-04 15:10:31', '2025-09-04 15:10:31');
INSERT INTO `t_operate_log` VALUES (4507, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:35:09', '2025-09-05 14:35:09');
INSERT INTO `t_operate_log` VALUES (4508, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:35:09', '2025-09-05 14:35:09');
INSERT INTO `t_operate_log` VALUES (4509, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:47:09', '2025-09-05 14:47:09');
INSERT INTO `t_operate_log` VALUES (4510, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:47:09', '2025-09-05 14:47:09');
INSERT INTO `t_operate_log` VALUES (4511, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:50:36', '2025-09-05 14:50:36');
INSERT INTO `t_operate_log` VALUES (4512, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 14:50:36', '2025-09-05 14:50:36');
INSERT INTO `t_operate_log` VALUES (4513, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:00:12', '2025-09-05 15:00:12');
INSERT INTO `t_operate_log` VALUES (4514, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:00:12', '2025-09-05 15:00:12');
INSERT INTO `t_operate_log` VALUES (4515, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:28:54', '2025-09-05 15:28:54');
INSERT INTO `t_operate_log` VALUES (4516, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:29:12', '2025-09-05 15:29:12');
INSERT INTO `t_operate_log` VALUES (4517, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:31:57', '2025-09-05 15:31:57');
INSERT INTO `t_operate_log` VALUES (4518, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:31:57', '2025-09-05 15:31:57');
INSERT INTO `t_operate_log` VALUES (4519, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:33:21', '2025-09-05 15:33:21');
INSERT INTO `t_operate_log` VALUES (4520, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:39:40', '2025-09-05 15:39:40');
INSERT INTO `t_operate_log` VALUES (4521, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:41:55', '2025-09-05 15:41:55');
INSERT INTO `t_operate_log` VALUES (4522, 1, 1, '管理员', 'OA办公-企业', '查询企业详情 @author 开云', '/oa/enterprise/get/2', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.getDetail', '[2]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 15:42:05', '2025-09-05 15:42:05');
INSERT INTO `t_operate_log` VALUES (4523, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:18:52', '2025-09-05 16:18:52');
INSERT INTO `t_operate_log` VALUES (4524, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:18:56', '2025-09-05 16:18:56');
INSERT INTO `t_operate_log` VALUES (4525, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:18:56', '2025-09-05 16:18:56');
INSERT INTO `t_operate_log` VALUES (4526, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:19:07', '2025-09-05 16:19:07');
INSERT INTO `t_operate_log` VALUES (4527, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 16:34:26', '2025-09-05 16:34:26');
INSERT INTO `t_operate_log` VALUES (4528, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:39:04', '2025-09-05 18:39:04');
INSERT INTO `t_operate_log` VALUES (4529, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:39:04', '2025-09-05 18:39:04');
INSERT INTO `t_operate_log` VALUES (4530, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:39:07', '2025-09-05 18:39:07');
INSERT INTO `t_operate_log` VALUES (4531, 1, 1, '管理员', 'OA办公-企业', '分页查询企业模块 @author 开云', '/oa/enterprise/page/query', 'net.lab1024.sa.admin.module.business.oa.enterprise.controller.EnterpriseController.queryByPage', '[{\"deletedFlag\":false,\"keywords\":\"\",\"pageNum\":1,\"pageSize\":10,\"searchCount\":true}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:44:40', '2025-09-05 18:44:40');
INSERT INTO `t_operate_log` VALUES (4532, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:44:41', '2025-09-05 18:44:41');
INSERT INTO `t_operate_log` VALUES (4533, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 18:44:41', '2025-09-05 18:44:41');
INSERT INTO `t_operate_log` VALUES (4534, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:01:24', '2025-09-05 19:01:24');
INSERT INTO `t_operate_log` VALUES (4535, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:01:24', '2025-09-05 19:01:24');
INSERT INTO `t_operate_log` VALUES (4536, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:02:17', '2025-09-05 19:02:17');
INSERT INTO `t_operate_log` VALUES (4537, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:02:17', '2025-09-05 19:02:17');
INSERT INTO `t_operate_log` VALUES (4538, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:10:49', '2025-09-05 19:10:49');
INSERT INTO `t_operate_log` VALUES (4539, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:10:49', '2025-09-05 19:10:49');
INSERT INTO `t_operate_log` VALUES (4540, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:24:43', '2025-09-05 19:24:43');
INSERT INTO `t_operate_log` VALUES (4541, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:24:43', '2025-09-05 19:24:43');
INSERT INTO `t_operate_log` VALUES (4542, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:36:09', '2025-09-05 19:36:09');
INSERT INTO `t_operate_log` VALUES (4543, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:36:09', '2025-09-05 19:36:09');
INSERT INTO `t_operate_log` VALUES (4544, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:36:19', '2025-09-05 19:36:19');
INSERT INTO `t_operate_log` VALUES (4545, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-05 19:36:19', '2025-09-05 19:36:19');
INSERT INTO `t_operate_log` VALUES (4546, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:09', '2025-09-06 14:58:09');
INSERT INTO `t_operate_log` VALUES (4547, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:10', '2025-09-06 14:58:10');
INSERT INTO `t_operate_log` VALUES (4548, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查看详情 @author 卓大', '/oa/notice/backendUser/view/59', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.view', '[59]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:14', '2025-09-06 14:58:14');
INSERT INTO `t_operate_log` VALUES (4549, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询 查看记录 @author 卓大', '/oa/notice/backendUser/queryViewRecord', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryViewRecord', '[{\"keywords\":\"\",\"noticeId\":59,\"pageNum\":1,\"pageSize\":10}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:14', '2025-09-06 14:58:14');
INSERT INTO `t_operate_log` VALUES (4550, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:30', '2025-09-06 14:58:30');
INSERT INTO `t_operate_log` VALUES (4551, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 14:58:30', '2025-09-06 14:58:30');
INSERT INTO `t_operate_log` VALUES (4552, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 15:32:59', '2025-09-06 15:32:59');
INSERT INTO `t_operate_log` VALUES (4553, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-06 15:32:59', '2025-09-06 15:32:59');
INSERT INTO `t_operate_log` VALUES (4554, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-07 12:18:56', '2025-09-07 12:18:56');
INSERT INTO `t_operate_log` VALUES (4555, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-07 12:18:56', '2025-09-07 12:18:56');
INSERT INTO `t_operate_log` VALUES (4556, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-07 15:34:53', '2025-09-07 15:34:53');
INSERT INTO `t_operate_log` VALUES (4557, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-07 15:34:53', '2025-09-07 15:34:53');
INSERT INTO `t_operate_log` VALUES (4558, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":2,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-08 15:12:55', '2025-09-08 15:12:55');
INSERT INTO `t_operate_log` VALUES (4559, 1, 1, '管理员', 'OA办公-通知公告', '【后台用户】通知公告-查询全部 @author 卓大', '/oa/notice/backendUser/query', 'net.lab1024.sa.admin.module.business.oa.notice.controller.NoticeController.queryBackendUserNotice', '[{\"noticeTypeId\":1,\"pageNum\":1,\"pageSize\":6,\"searchCount\":false}]', '{\"code\":0,\"dataType\":1,\"msg\":\"操作成功\",\"ok\":true}', '127.0.0.1', '0|0|0|内网IP|内网IP', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:142.0) Gecko/20100101 Firefox/142.0', 1, NULL, '2025-09-08 15:12:55', '2025-09-08 15:12:55');

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
-- Table structure for t_position
-- ----------------------------
DROP TABLE IF EXISTS `t_position`;
CREATE TABLE `t_position`  (
  `position_id` bigint NOT NULL AUTO_INCREMENT COMMENT '职务ID',
  `position_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '职务名称',
  `position_level` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职级',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted_flag` tinyint(1) NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`position_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '职务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_position
-- ----------------------------
INSERT INTO `t_position` VALUES (3, '技术P7', 'L1', 3, '', 0, '2024-06-29 15:57:07', '2024-07-15 23:34:35');
INSERT INTO `t_position` VALUES (4, '技术P8', 'L2', 1, NULL, 0, '2024-07-15 23:34:14', '2024-07-15 23:34:23');
INSERT INTO `t_position` VALUES (5, '管理M5', 'L1', 4, NULL, 0, '2024-07-15 23:34:48', '2024-07-15 23:34:48');
INSERT INTO `t_position` VALUES (6, '管理M6', 'L2', 5, NULL, 0, '2024-07-15 23:35:00', '2024-07-15 23:35:00');

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
INSERT INTO `t_role` VALUES (1, '技术总监', NULL, '', '2022-10-19 20:24:09', '2019-06-21 12:09:34');
INSERT INTO `t_role` VALUES (34, '销售总监', 'cto', '', '2023-09-06 19:10:34', '2019-08-30 09:30:50');
INSERT INTO `t_role` VALUES (35, '总经理', NULL, '', '2019-08-30 09:31:05', '2019-08-30 09:31:05');
INSERT INTO `t_role` VALUES (36, '董事长', NULL, '', '2019-08-30 09:31:11', '2019-08-30 09:31:11');
INSERT INTO `t_role` VALUES (37, '财务', NULL, '', '2019-08-30 09:31:16', '2019-08-30 09:31:16');
INSERT INTO `t_role` VALUES (59, '组织账号运营者', 'xxx', NULL, '2025-09-08 15:16:32', '2025-09-08 15:16:32');

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
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色的数据范围' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role_data_scope
-- ----------------------------
INSERT INTO `t_role_data_scope` VALUES (67, 1, 2, 1, '2024-03-18 20:41:00', '2024-03-18 20:41:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1021 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-菜单\n' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role_menu
-- ----------------------------
INSERT INTO `t_role_menu` VALUES (919, 1, 138, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (920, 1, 132, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (921, 1, 142, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (922, 1, 149, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (923, 1, 150, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (924, 1, 185, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (925, 1, 186, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (926, 1, 187, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (927, 1, 188, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (928, 1, 145, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (929, 1, 196, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (930, 1, 144, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (931, 1, 181, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (932, 1, 183, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (933, 1, 184, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (934, 1, 165, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (935, 1, 47, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (936, 1, 48, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (937, 1, 137, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (938, 1, 166, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (939, 1, 194, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (940, 1, 78, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (941, 1, 173, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (942, 1, 174, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (943, 1, 175, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (944, 1, 176, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (945, 1, 50, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (946, 1, 26, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (947, 1, 40, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (948, 1, 105, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (949, 1, 106, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (950, 1, 109, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (951, 1, 163, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (952, 1, 164, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (953, 1, 199, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (954, 1, 110, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (955, 1, 159, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (956, 1, 160, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (957, 1, 161, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (958, 1, 162, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (959, 1, 130, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (960, 1, 157, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (961, 1, 158, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (962, 1, 133, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (963, 1, 117, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (964, 1, 156, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (965, 1, 193, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (966, 1, 200, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (967, 1, 220, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (968, 1, 45, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (969, 1, 219, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (970, 1, 46, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (971, 1, 91, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (972, 1, 92, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (973, 1, 93, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (974, 1, 94, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (975, 1, 95, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (976, 1, 96, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (977, 1, 86, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (978, 1, 87, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (979, 1, 88, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (980, 1, 76, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (981, 1, 97, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (982, 1, 98, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (983, 1, 99, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (984, 1, 100, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (985, 1, 101, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (986, 1, 102, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (987, 1, 103, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (988, 1, 104, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (989, 1, 213, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (990, 1, 214, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (991, 1, 143, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (992, 1, 203, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (993, 1, 215, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (994, 1, 218, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (995, 1, 147, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (996, 1, 170, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (997, 1, 171, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (998, 1, 168, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (999, 1, 169, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1000, 1, 202, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1001, 1, 201, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1002, 1, 148, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1003, 1, 152, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1004, 1, 190, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1005, 1, 191, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1006, 1, 192, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1007, 1, 198, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1008, 1, 207, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1009, 1, 111, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1010, 1, 206, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1011, 1, 81, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1012, 1, 204, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1013, 1, 205, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1014, 1, 122, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1015, 1, 301, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1016, 1, 307, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1017, 1, 308, '2025-09-05 19:21:31', '2025-09-05 19:21:31');
INSERT INTO `t_role_menu` VALUES (1018, 59, 307, '2025-09-08 15:16:47', '2025-09-08 15:16:47');
INSERT INTO `t_role_menu` VALUES (1019, 59, 308, '2025-09-08 15:16:47', '2025-09-08 15:16:47');
INSERT INTO `t_role_menu` VALUES (1020, 59, 301, '2025-09-08 15:16:47', '2025-09-08 15:16:47');

-- ----------------------------
-- Table structure for t_role_user
-- ----------------------------
DROP TABLE IF EXISTS `t_role_user`;
CREATE TABLE `t_role_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色id',
  `employee_id` bigint NOT NULL COMMENT '后台用户id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_employee`(`role_id` ASC, `employee_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 342 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色后台用户功能表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role_user
-- ----------------------------
INSERT INTO `t_role_user` VALUES (325, 36, 63, '2022-10-19 20:25:26', '2022-10-19 20:25:26');
INSERT INTO `t_role_user` VALUES (329, 34, 72, '2022-11-05 10:56:54', '2022-11-05 10:56:54');
INSERT INTO `t_role_user` VALUES (330, 36, 72, '2022-11-05 10:56:54', '2022-11-05 10:56:54');
INSERT INTO `t_role_user` VALUES (333, 1, 44, '2023-10-07 18:53:29', '2023-10-07 18:53:29');
INSERT INTO `t_role_user` VALUES (334, 1, 47, '2023-10-07 18:55:00', '2023-10-07 18:55:00');
INSERT INTO `t_role_user` VALUES (341, 1, 48, '2024-09-02 23:03:28', '2024-09-02 23:03:28');

-- ----------------------------
-- Table structure for t_serial_number
-- ----------------------------
DROP TABLE IF EXISTS `t_serial_number`;
CREATE TABLE `t_serial_number`  (
  `serial_number_id` int NOT NULL,
  `business_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务名称',
  `format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '格式[yyyy]表示年,[mm]标识月,[dd]表示日,[nnn]表示三位数字',
  `rule_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则格式。none没有周期, year 年周期, month月周期, day日周期',
  `init_number` int UNSIGNED NOT NULL COMMENT '初始值',
  `step_random_range` int UNSIGNED NOT NULL COMMENT '步长随机数',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `last_number` bigint NULL DEFAULT NULL COMMENT '上次产生的单号, 默认为空',
  `last_time` datetime NULL DEFAULT NULL COMMENT '上次产生的单号时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`serial_number_id`) USING BTREE,
  UNIQUE INDEX `key_name`(`business_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '单号生成器定义表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_serial_number
-- ----------------------------
INSERT INTO `t_serial_number` VALUES (1, '订单编号', 'DK[yyyy][mm][dd]NO[nnnnn]', 'day', 1000, 10, 'DK20201101NO321', 1, '2023-12-04 09:16:42', '2024-01-08 19:24:46', '2021-02-19 14:37:50');
INSERT INTO `t_serial_number` VALUES (2, '合同编号', 'HT[yyyy][mm][dd][nnnnn]-CX', 'none', 1, 1, '', 8, '2023-12-04 09:54:53', '2023-12-04 09:54:52', '2021-08-12 20:40:37');

-- ----------------------------
-- Table structure for t_serial_number_record
-- ----------------------------
DROP TABLE IF EXISTS `t_serial_number_record`;
CREATE TABLE `t_serial_number_record`  (
  `serial_number_id` int NOT NULL,
  `record_date` date NOT NULL COMMENT '记录日期',
  `last_number` bigint NOT NULL DEFAULT 0 COMMENT '最后更新值',
  `last_time` datetime NOT NULL COMMENT '最后更新时间',
  `count` bigint NOT NULL DEFAULT 0 COMMENT '更新次数',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `uk_generator`(`serial_number_id` ASC, `record_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'serial_number记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_serial_number_record
-- ----------------------------

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
INSERT INTO `t_smart_job` VALUES (1, '示例任务1', 'net.lab1024.sa.base.module.support.job.sample.SmartJobSample1', 'cron', '10 15 0/1 * * *', 1, '执行示例任务1', '2025-09-08 17:15:10', 8393, 1, '执行示例任务1', 0, '管理员', '2024-06-17 20:00:46', '2025-09-08 17:15:10');
INSERT INTO `t_smart_job` VALUES (2, '示例任务2', 'net.lab1024.sa.base.module.support.job.sample.SmartJobSample2', 'fixed_delay', '120', 1, '执行示例任务2', '2025-09-08 17:12:46', 8392, 2, '执行示例任务2', 0, '管理员', '2024-06-18 20:45:35', '2025-09-08 17:12:45');
INSERT INTO `t_smart_job` VALUES (4, 'ActivityStatusScanTask', 'net.lab1024.sa.admin.module.business.funcampus.job.ActivityStatusScanJob', 'cron', '0 0 0 * * *', 1, '', NULL, NULL, 1, '', 0, '管理员', '2025-09-07 15:37:29', '2025-09-07 15:38:57');
INSERT INTO `t_smart_job` VALUES (5, 'ActivityStatusUpdateJob', 'net.lab1024.sa.admin.module.business.funcampus.job.ActivityStatusUpdateJob', 'cron', '0 0 0 * * *', 1, '', NULL, NULL, 1, '', 0, '管理员', '2025-09-07 15:40:19', '2025-09-07 15:40:21');

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
) ENGINE = InnoDB AUTO_INCREMENT = 8394 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务-执行记录 @listen' ROW_FORMAT = DYNAMIC;

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
