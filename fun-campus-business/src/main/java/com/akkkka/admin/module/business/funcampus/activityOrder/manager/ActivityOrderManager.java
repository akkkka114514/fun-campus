package com.akkkka.admin.module.business.funcampus.activityOrder.manager;

import java.time.LocalDateTime;
import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.dao.ActivityOrderDao;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 活动报名订单 Manager
 * <p>
 * 订单状态流转全部通过「条件更新（CAS）」完成：update ... where id=? and status=旧状态，
 * 返回是否更新成功（影响行数>0）；关单任务与支付回调抢的就是同一个 where status=待支付 条件，
 * 数据库保证只有一个能更新成功，谁先成功谁生效
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Service
public class ActivityOrderManager extends ServiceImpl<ActivityOrderDao, ActivityOrderEntity> {

    /**
     * 按订单号查询（未删除）
     */
    public ActivityOrderEntity getByOrderNo(String orderNo) {
        return this.getOne(
                new LambdaQueryWrapper<ActivityOrderEntity>()
                        .eq(ActivityOrderEntity::getOrderNo, orderNo)
                        .eq(ActivityOrderEntity::getDeletedFlag, false)
        );
    }

    /**
     * 查询用户在某活动的最新一笔订单（用于活动详情展示当前用户订单状态）
     */
    public ActivityOrderEntity getLatestByActivityAndUser(Long activityId, Long userId) {
        return this.getOne(
                new LambdaQueryWrapper<ActivityOrderEntity>()
                        .eq(ActivityOrderEntity::getActivityId, activityId)
                        .eq(ActivityOrderEntity::getUserId, userId)
                        .eq(ActivityOrderEntity::getDeletedFlag, false)
                        .orderByDesc(ActivityOrderEntity::getId)
                        .last("limit 1")
        );
    }

    /**
     * 查询用户在某活动的进行中订单（待支付/已支付），用于下单查重
     */
    public List<ActivityOrderEntity> listActiveOrders(Long activityId, Long userId) {
        return this.list(
                new LambdaQueryWrapper<ActivityOrderEntity>()
                        .eq(ActivityOrderEntity::getActivityId, activityId)
                        .eq(ActivityOrderEntity::getUserId, userId)
                        .in(ActivityOrderEntity::getStatus, OrderStatus.WAIT_PAY, OrderStatus.PAID)
                        .eq(ActivityOrderEntity::getDeletedFlag, false)
        );
    }

    /**
     * 查询一批用户在某活动的已支付订单（用于报名审核剔除后联动退款）
     */
    public List<ActivityOrderEntity> listPaidOrdersByActivityAndUsers(Long activityId, List<Long> userIds) {
        return this.list(
                new LambdaQueryWrapper<ActivityOrderEntity>()
                        .eq(ActivityOrderEntity::getActivityId, activityId)
                        .in(ActivityOrderEntity::getUserId, userIds)
                        .eq(ActivityOrderEntity::getStatus, OrderStatus.PAID)
                        .eq(ActivityOrderEntity::getDeletedFlag, false)
        );
    }

    /**
     * 查询超时未支付订单（扫描关单任务使用）
     */
    public List<ActivityOrderEntity> listTimeoutOrders(LocalDateTime now, int limit) {
        return this.list(
                new LambdaQueryWrapper<ActivityOrderEntity>()
                        .eq(ActivityOrderEntity::getStatus, OrderStatus.WAIT_PAY)
                        .lt(ActivityOrderEntity::getExpireTime, now)
                        .eq(ActivityOrderEntity::getDeletedFlag, false)
                        .orderByAsc(ActivityOrderEntity::getId)
                        .last("limit " + limit)
        );
    }

    /**
     * CAS：待支付 → 已支付（支付回调幂等的核心）
     * update set status=已支付 where id=? and status=待支付
     *
     * @return 是否由本次调用完成状态流转（false=已被回调/关单先行处理）
     */
    public boolean markPaidCas(Long orderId, String channelOrderNo, PayChannelEnum payChannel, LocalDateTime payTime) {
        UpdateWrapper<ActivityOrderEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", orderId);
        updateWrapper.eq("status", OrderStatus.WAIT_PAY.getCode());
        updateWrapper.set("status", OrderStatus.PAID.getCode());
        updateWrapper.set("channel_order_no", channelOrderNo);
        updateWrapper.set("pay_channel", payChannel == null ? null : payChannel.getCode());
        updateWrapper.set("pay_time", payTime);
        return this.update(updateWrapper);
    }

    /**
     * CAS：待支付 → 已关闭（超时关单/用户取消）
     * update set status=已关闭, close_time=? where id=? and status=待支付
     *
     * @return 是否由本次调用完成状态流转（false=已被支付/取消先行处理）
     */
    public boolean closeIfWaitPayCas(Long orderId, LocalDateTime closeTime) {
        UpdateWrapper<ActivityOrderEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", orderId);
        updateWrapper.eq("status", OrderStatus.WAIT_PAY.getCode());
        updateWrapper.set("status", OrderStatus.CLOSED.getCode());
        updateWrapper.set("close_time", closeTime);
        return this.update(updateWrapper);
    }

    /**
     * CAS：已支付 → 退款中（用户申请退款）
     */
    public boolean markRefundingCas(Long orderId) {
        return updateStatusCas(orderId, OrderStatus.PAID, OrderStatus.REFUNDING);
    }

    /**
     * CAS：退款失败 → 退款中（重新发起退款）
     */
    public boolean markRefundingFromFailedCas(Long orderId) {
        return updateStatusCas(orderId, OrderStatus.REFUND_FAILED, OrderStatus.REFUNDING);
    }

    /**
     * CAS：退款中 → 已退款（退款回调成功）
     */
    public boolean markRefundedCas(Long orderId) {
        return updateStatusCas(orderId, OrderStatus.REFUNDING, OrderStatus.REFUNDED);
    }

    /**
     * CAS：退款中 → 退款失败（渠道受理失败/回调失败）
     */
    public boolean markRefundFailedCas(Long orderId) {
        return updateStatusCas(orderId, OrderStatus.REFUNDING, OrderStatus.REFUND_FAILED);
    }

    /**
     * 条件更新订单状态
     */
    private boolean updateStatusCas(Long orderId, OrderStatus expectStatus, OrderStatus targetStatus) {
        UpdateWrapper<ActivityOrderEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", orderId);
        updateWrapper.eq("status", expectStatus.getCode());
        updateWrapper.set("status", targetStatus.getCode());
        return this.update(updateWrapper);
    }
}
