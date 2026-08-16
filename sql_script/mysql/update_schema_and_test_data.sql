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
  `review_stage` int NULL DEFAULT NULL COMMENT '审核阶段: 1-初审 2-审阅 3-终审 4-完结审核',
  `action` int NULL DEFAULT NULL COMMENT '审核行为: 1-通过 2-驳回 3-建议',
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
  ADD COLUMN `organization_id` bigint NULL DEFAULT NULL COMMENT '组织id' AFTER `credit_score`;

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
VALUES (3, NOW(), NOW(), 0, 3, 'org_reviewer_1', '$argon2id$v=19$m=16384,t=2,p=1$test$testhash', 0, 'orgreviewer1@lyut.edu.cn', 1, NULL, 1, 1);

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

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- 更新完成！
-- 新建表: 13 张 (activity_category, activity_can_enroll_college,
--   activity_can_enroll_grade, activity_can_enroll_tribe,
--   activity_review_log, activity_attachment, activity_review_attachment,
--   activity_signin_manager, tribe, tribe_user, college_info,
--   grade_info, organization_info)
-- 修改表: 5 张 (activity, activity_schedule, activity_enrollment,
--   portal_user, backend_user)
-- =====================================================================
