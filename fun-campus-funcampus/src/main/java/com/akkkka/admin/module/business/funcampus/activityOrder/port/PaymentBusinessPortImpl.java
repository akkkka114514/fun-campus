package com.akkkka.admin.module.business.funcampus.activityOrder.port;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogAction;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityOrderLogService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.module.support.pay.constant.PayLogAction;
import com.akkkka.module.support.pay.port.PaymentBusinessPort;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务协作端口实现（支付模块 → 报名/活动/留痕能力的桥）
 * <p>
 * - 报名记录写入复用现有 ActivityEnrollmentService（付款即报名，对签到/审核零侵入）；
 * - 释放名额逻辑（逻辑删除报名记录 + 名额-1）从支付模块迁回业务侧；
 * - 留痕动作语义映射：PayLogAction → OrderLogAction，业务状态机枚举不跨模块
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Slf4j
@Component
@AllArgsConstructor
public class PaymentBusinessPortImpl implements PaymentBusinessPort {

    private final ActivityEnrollmentService enrollmentService;

    private final ActivityEnrollmentManager enrollmentManager;

    private final ActivityEnrollNumDao activityEnrollNumDao;

    private final ActivityManager activityManager;

    private final ActivityOrderLogService orderLogService;

    @Override
    public void saveEnrollmentRecord(Long activityId, Long userId) {
        enrollmentService.saveEnrollmentRecord(activityId, userId);
    }

    /**
     * 逻辑删除报名记录并释放名额
     * <p>
     * 逻辑删除幂等（已删除时 0 行）；名额释放只应在退款终态流程中执行一次，
     * 由退款单 CAS 保证重复回调不会重复执行
     */
    @Override
    public void releaseEnrollment(Long activityId, Long userId) {
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                .set(ActivityEnrollmentEntity::getDeletedFlag, true);
        enrollmentManager.update(updateWrapper);
        if (!activityEnrollNumDao.decreaseEnrollNum(activityId)) {
            log.warn("退款释放名额失败：activityId:{}，userId:{}", activityId, userId);
        }
    }

    @Override
    public String getActivityTitle(Long activityId) {
        ActivityEntity activity = activityManager.getById(activityId);
        return activity == null ? "" : Objects.toString(activity.getTitle(), "");
    }

    @Override
    public void recordSystemLog(Long orderId, String orderNo, PayLogAction action, String detail) {
        orderLogService.recordSystem(orderId, orderNo, mapLogAction(action), detail);
    }

    /**
     * 支付模块语义动作 → 业务侧留痕动作（映射收敛在此，新增动作在此扩展）
     */
    private OrderLogAction mapLogAction(PayLogAction action) {
        return switch (action) {
            case PAY_FAILED -> OrderLogAction.PAY_FAILED;
            case PAY_SUCCESS -> OrderLogAction.PAY_SUCCESS;
            case PAY_AFTER_CLOSE -> OrderLogAction.PAY_AFTER_CLOSE;
            case REFUND_SUCCESS -> OrderLogAction.REFUND_SUCCESS;
            case REFUND_FAILED -> OrderLogAction.REFUND_FAILED;
        };
    }
}
