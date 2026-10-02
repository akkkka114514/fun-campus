package com.akkkka.module.support.pay.port;

import com.akkkka.module.support.pay.constant.PayChannelEnum;

/**
 * 支付订单视图（业务订单在支付域眼中的投影）
 * <p>
 * 依赖倒置：支付模块不直接依赖业务订单实体，业务侧实体实现本接口即可"扮演"支付域需要的角色。
 * - 数据字段为业务实体已有 getter，实现类零改动；
 * - isXxx / getStatusText 为状态语义适配方法：由实现类用自己的状态枚举（如 OrderStatus）判断，
 *   业务状态枚举不跨模块，支付模块只消费语义结果
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
public interface PayOrderView {

    /**
     * 订单主键
     */
    Long getId();

    /**
     * 订单号
     */
    String getOrderNo();

    /**
     * 活动id（支付成功后写报名记录、发站内信使用）
     */
    Long getActivityId();

    /**
     * 报名用户id
     */
    Long getUserId();

    /**
     * 订单金额（分）
     */
    Integer getAmountFen();

    /**
     * 收款商户号（下单快照；退款原路退回校验使用）
     */
    String getPayeeAccountNo();

    /**
     * 收款方名称快照（收银台展示）
     */
    String getPayeeName();

    /**
     * 订单支付渠道（退款/查单路由到原渠道）
     */
    PayChannelEnum getPayChannel();

    /**
     * 是否待支付（收银台可支付性判断）
     */
    boolean isWaitPay();

    /**
     * 是否已支付（支付回调重复到达判定）
     */
    boolean isPaid();

    /**
     * 是否已关闭（支付回调晚于关单判定）
     */
    boolean isClosed();

    /**
     * 是否处于退款流程（退款中/已退款/退款失败）
     */
    boolean isInRefundFlow();

    /**
     * 当前状态文本（日志与收银台提示展示）
     */
    String getStatusText();
}
