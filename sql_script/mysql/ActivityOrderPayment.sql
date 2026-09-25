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
