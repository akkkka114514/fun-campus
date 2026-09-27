package com.akkkka.admin.module.business.funcampus.activityOrder.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogAction;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogOperatorType;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderLogEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderLogVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderLogManager;
import com.akkkka.common.util.SmartRequestUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 活动订单操作留痕 Service
 * <p>
 * 支付、退款等关键操作在状态流转成功后追加一条 activity_order_log，
 * 管理端订单详情按时间线展示；留痕失败仅记录日志，绝不影响资金主流程
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityOrderLogService {

    /**
     * detail 列 varchar(500)，超长截断保护
     */
    private static final int DETAIL_MAX_LENGTH = 500;

    private final ActivityOrderLogManager orderLogManager;

    /**
     * 门户用户操作留痕（下单/取消/申请退款）
     */
    public void recordPortalUser(ActivityOrderEntity order, OrderLogAction action, Long userId, String detail) {
        record(order, action, OrderLogOperatorType.PORTAL_USER, userId, detail);
    }

    /**
     * 管理端用户操作留痕（活动取消关单/重试退款；操作人取当前请求用户，非请求线程下为空）
     */
    public void recordAdminUser(ActivityOrderEntity order, OrderLogAction action, String detail) {
        record(order, action, OrderLogOperatorType.ADMIN_USER, SmartRequestUtil.getRequestUserId(), detail);
    }

    /**
     * 系统操作留痕（关单任务/渠道回调/系统退款）
     */
    public void recordSystem(ActivityOrderEntity order, OrderLogAction action, String detail) {
        record(order, action, OrderLogOperatorType.SYSTEM, null, detail);
    }

    /**
     * 统一落库入口：留痕失败仅告警，不阻断主流程
     */
    public void record(ActivityOrderEntity order, OrderLogAction action, OrderLogOperatorType operatorType, Long operatorId, String detail) {
        try {
            ActivityOrderLogEntity logEntity = new ActivityOrderLogEntity();
            logEntity.setOrderId(order.getId());
            logEntity.setOrderNo(order.getOrderNo());
            logEntity.setAction(action);
            logEntity.setOperatorType(operatorType);
            logEntity.setOperatorId(operatorId);
            logEntity.setDetail(truncate(detail));
            logEntity.setDeletedFlag(false);
            orderLogManager.save(logEntity);
        } catch (Exception e) {
            log.warn("订单操作留痕失败：orderNo:{}，action:{}", order.getOrderNo(), action, e);
        }
    }

    /**
     * 查询订单全部操作记录（按发生时间正序，管理端订单详情时间线使用）
     */
    public List<ActivityOrderLogVO> listByOrderNo(String orderNo) {
        return orderLogManager.listByOrderNo(orderNo).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private ActivityOrderLogVO convertToVO(ActivityOrderLogEntity logEntity) {
        ActivityOrderLogVO vo = new ActivityOrderLogVO();
        vo.setId(logEntity.getId());
        OrderLogAction action = logEntity.getAction();
        vo.setAction(action == null ? null : action.getCode());
        vo.setActionName(action == null ? null : action.getLabel());
        OrderLogOperatorType operatorType = logEntity.getOperatorType();
        vo.setOperatorType(operatorType == null ? null : operatorType.getCode());
        vo.setOperatorName(resolveOperatorName(operatorType, logEntity.getOperatorId()));
        vo.setDetail(logEntity.getDetail());
        vo.setCreateTime(logEntity.getCreateTime());
        return vo;
    }

    /**
     * 操作人文案：不额外查库，按「类型#id」展示（订单详情上方已带下单用户名，可对上）
     */
    private String resolveOperatorName(OrderLogOperatorType operatorType, Long operatorId) {
        if (operatorType == null) {
            return OrderLogOperatorType.SYSTEM.getLabel();
        }
        return switch (operatorType) {
            case PORTAL_USER -> operatorId == null ? "用户" : "用户#" + operatorId;
            case ADMIN_USER -> operatorId == null ? "管理端" : "管理员#" + operatorId;
            case SYSTEM -> "系统";
        };
    }

    private String truncate(String detail) {
        if (detail == null || detail.length() <= DETAIL_MAX_LENGTH) {
            return detail;
        }
        return detail.substring(0, DETAIL_MAX_LENGTH);
    }
}
