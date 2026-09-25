package com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity;

import java.time.LocalDateTime;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundReasonType;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundStatus;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 活动报名退款记录 实体类
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_refund")
public class ActivityRefundEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 活动id
     */
    @TableField(value = "activity_id")
    private Long activityId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 退款金额（分）
     */
    private Integer amountFen;

    /**
     * 原因：1-用户申请 2-活动取消 3-报名审核未通过 4-其它
     */
    private RefundReasonType reasonType;

    /**
     * 备注
     */
    private String reason;

    /**
     * 状态：0-退款中 1-成功 2-失败
     */
    private RefundStatus status;

    /**
     * 渠道退款单号
     */
    private String channelRefundNo;

    /**
     * 退款完成时间
     */
    private LocalDateTime refundTime;

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
