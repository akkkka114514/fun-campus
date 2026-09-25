package com.akkkka.module.support.message.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import com.akkkka.common.enumeration.BaseEnum;

/**
 * 消息模板类型
 *
 * @author luoyi
 * @date 2024/06/22 20:20
 */
@Getter
@AllArgsConstructor
public enum MessageTemplateEnum implements BaseEnum {



    ORDER_AUDIT(1000, "订单审批", MessageTypeEnum.ORDER, "您有一个订单等待审批，订单号【${orderNumber}】"),

    /**
     * 活动报名审核通过：审核后通知最终入选的报名学生
     */
    ACTIVITY_ENROLL_PASS(2000, "活动报名审核通过", MessageTypeEnum.MAIL, "您报名的活动【${activityTitle}】已通过审核，请按时参加。"),

    /**
     * 活动报名审核未通过：报名审核被筛选剔除后通知报名学生
     */
    ACTIVITY_ENROLL_REJECT(2001, "活动报名未通过", MessageTypeEnum.MAIL, "很遗憾，您报名的活动【${activityTitle}】未通过审核。"),

    /**
     * 活动报名成功：学生自助报名成功后即时通知
     */
    ACTIVITY_ENROLL_SUCCESS(2002, "活动报名成功", MessageTypeEnum.MAIL, "您已成功报名活动【${activityTitle}】，请按时参加。"),

    /**
     * 活动报名失败：学生自助报名失败后即时通知失败原因
     */
    ACTIVITY_ENROLL_FAIL(2003, "活动报名失败", MessageTypeEnum.MAIL, "很遗憾，您报名活动【${activityTitle}】失败，原因：${reason}。"),

    /**
     * 付费活动支付成功：支付回调处理成功后通知报名学生
     */
    ACTIVITY_ORDER_PAID(2004, "活动报名支付成功", MessageTypeEnum.MAIL, "您已成功支付活动【${activityTitle}】的报名费，报名已生效，请按时参加。"),

    /**
     * 付费订单超时关闭：订单 15 分钟未支付被自动关单后通知下单学生
     */
    ACTIVITY_ORDER_TIMEOUT(2005, "活动订单已关闭", MessageTypeEnum.MAIL, "您报名的活动【${activityTitle}】订单超时未支付，已自动关闭，名额已释放。"),

    /**
     * 退款到账：渠道退款成功回调处理完成后通知报名学生
     */
    ACTIVITY_REFUND_SUCCESS(2006, "退款已到账", MessageTypeEnum.MAIL, "您报名的活动【${activityTitle}】报名费【${amount}】元已退款到账。"),

    ;

    private final Integer value;

    private final String desc;

    private final MessageTypeEnum messageTypeEnum;

    private final String content;
}
