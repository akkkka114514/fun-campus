package com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity;

import java.time.LocalDateTime;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 活动报名订单 实体类
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_order")
public class ActivityOrderEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 活动id
     */
    @TableField(value = "activity_id")
    private Long activityId;

    /**
     * 报名用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 订单金额（分）
     */
    private Integer amountFen;

    /**
     * 状态：0-待支付 1-已支付 2-已关闭 3-退款中 4-已退款 5-退款失败
     */
    private OrderStatus status;

    /**
     * 支付渠道：1-微信 2-支付宝 3-Mock
     */
    private PayChannelEnum payChannel;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付截止时间
     */
    private LocalDateTime expireTime;

    /**
     * 关闭时间
     */
    private LocalDateTime closeTime;

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
