package com.akkkka.admin.module.business.funcampus.activityOrder.manager;

import java.time.LocalDateTime;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.dao.ActivityRefundDao;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityRefundEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 活动报名退款记录 Manager
 * <p>
 * 退款单状态流转同样使用 CAS 条件更新（where status=退款中），保证重复退款回调幂等
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Service
public class ActivityRefundManager extends ServiceImpl<ActivityRefundDao, ActivityRefundEntity> {

    /**
     * 按退款单号查询（未删除）
     */
    public ActivityRefundEntity getByRefundNo(String refundNo) {
        return this.getOne(
                new LambdaQueryWrapper<ActivityRefundEntity>()
                        .eq(ActivityRefundEntity::getRefundNo, refundNo)
                        .eq(ActivityRefundEntity::getDeletedFlag, false)
        );
    }

    /**
     * CAS：退款中 → 成功
     * update set status=成功, channel_refund_no=?, refund_time=? where id=? and status=退款中
     *
     * @return 是否由本次调用完成状态流转（false=重复回调，直接幂等返回）
     */
    public boolean markSuccessCas(Long refundId, String channelRefundNo, LocalDateTime refundTime) {
        UpdateWrapper<ActivityRefundEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", refundId);
        updateWrapper.eq("status", RefundStatus.REFUNDING.getCode());
        updateWrapper.set("status", RefundStatus.SUCCESS.getCode());
        updateWrapper.set("channel_refund_no", channelRefundNo);
        updateWrapper.set("refund_time", refundTime);
        return this.update(updateWrapper);
    }

    /**
     * CAS：退款中 → 失败（渠道受理失败/回调失败）
     */
    public boolean markFailedCas(Long refundId) {
        UpdateWrapper<ActivityRefundEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", refundId);
        updateWrapper.eq("status", RefundStatus.REFUNDING.getCode());
        updateWrapper.set("status", RefundStatus.FAILED.getCode());
        return this.update(updateWrapper);
    }
}
