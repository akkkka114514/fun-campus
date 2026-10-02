package com.akkkka.module.support.pay.port;

import com.akkkka.module.support.pay.constant.PayLogAction;

/**
 * 业务协作端口（支付模块 → 业务模块的能力反向接口）
 * <p>
 * 支付回调成功后需要触碰业务侧三件事：写报名记录、退款时释放名额、通知文案取活动标题，
 * 以及给订单留痕；这些能力都归业务模块所有，由业务侧实现类（PaymentBusinessPortImpl）提供：
 * - releaseEnrollment：逻辑删除报名记录 + 释放名额（内部保证幂等，重复回调不重复执行）；
 * - recordSystemLog：只记单号与留痕内容，不反查订单（支付模块已在手）；
 *   PayLogAction 为跨模块语义枚举，业务侧实现类负责映射回自己的 OrderLogAction
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
public interface PaymentBusinessPort {

    /**
     * 支付成功后写入报名记录（付款即报名，对现有签到/审核代码零侵入）
     */
    void saveEnrollmentRecord(Long activityId, Long userId);

    /**
     * 退款成功后逻辑删除报名记录并释放名额
     */
    void releaseEnrollment(Long activityId, Long userId);

    /**
     * 获取活动标题（站内信文案使用；不存在时返回空串）
     */
    String getActivityTitle(Long activityId);

    /**
     * 订单系统留痕（按订单主键与单号记录，不反查订单）
     */
    void recordSystemLog(Long orderId, String orderNo, PayLogAction action, String detail);
}
