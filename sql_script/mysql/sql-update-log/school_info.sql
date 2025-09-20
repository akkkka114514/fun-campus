/*
 * 学校信息表
 * 用于存储学校的基本信息
 */

CREATE TABLE `school_info` (
  `id` bigint NOT NULL COMMENT '学校ID',
  `name` varchar(255) NOT NULL COMMENT '学校名称',
  `code` varchar(100) DEFAULT NULL COMMENT '学校编码',
  `type` tinyint DEFAULT NULL COMMENT '学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)',
  `level` tinyint DEFAULT NULL COMMENT '学校层次(1:985,2:211,3:双一流,4:普通)',
  `province_id` bigint DEFAULT NULL COMMENT '省份ID',
  `city_id` bigint DEFAULT NULL COMMENT '城市ID',
  `district_id` bigint DEFAULT NULL COMMENT '区县ID',
  `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `contact_person` varchar(100) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `website` varchar(255) DEFAULT NULL COMMENT '官网',
  `established_year` smallint DEFAULT NULL COMMENT '建校年份',
  `student_count` int DEFAULT '0' COMMENT '学生人数',
  `teacher_count` int DEFAULT '0' COMMENT '教师人数',
  `description` text COMMENT '学校简介',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '学校logo图片URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_flag` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_name` (`name`),
  KEY `idx_province_city` (`province_id`,`city_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校信息表';