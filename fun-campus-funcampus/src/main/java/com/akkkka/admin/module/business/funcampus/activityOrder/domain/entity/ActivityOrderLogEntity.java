package com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity;

import java.time.LocalDateTime;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogAction;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogOperatorType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 活动订单操作记录 实体类
 * <p>
 * 支付、退款等关键操作在状态流转成功后追加一条记录（只增不改，无 updated 场景）
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_order_log")
public class ActivityOrderLogEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单号（冗余，便于按单号检索）
     */
    private String orderNo;

    /**
     * 操作类型：1-创建订单 2-支付成功 3-支付失败回调 4-支付回调晚于关单 5-关闭订单
     * 6-发起退款申请 7-退款成功 8-退款失败 9-管理端重试退款
     */
    private OrderLogAction action;

    /**
     * 操作人类型：1-门户用户 2-管理端用户 3-系统
     */
    private OrderLogOperatorType operatorType;

    /**
     * 操作人id（系统操作为空）
     */
    private Long operatorId;

    /**
     * 操作详情
     */
    private String detail;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否已删除
     */
    private Boolean deletedFlag;
}
