package com.akkkka.admin.module.business.funcampus.activityOrder.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentValidator;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityOrderQueryForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderCreateVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.util.OrderNoUtil;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.payment.service.PaymentService;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 活动报名订单 Service（付费报名下单链路）
 * <p>
 * - 下单锁座：名额在下单事务内增加（increaseEnrollNum），支付成功前不写报名记录；
 *   关单/取消/退款时释放名额（decreaseEnrollNum）；
 * - 超时关单：15 分钟未支付由 ActivityOrderTimeoutJob 关闭（与支付回调抢同一 CAS）；
 * - 支付成功后由 PaymentService 统一回调写报名记录，对现有签到、审核代码零侵入
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityOrderService {

    /**
     * 待支付订单有效期（分钟）：超时由关单任务关闭并释放名额
     */
    private static final int PAY_EXPIRE_MINUTES = 15;

    private final ActivityValidator activityValidator;

    private final ActivityManager activityManager;

    private final PortalUserManager portalUserManager;

    private final PortalUserValidator portalUserValidator;

    private final ActivityEnrollmentValidator enrollmentValidator;

    private final ActivityEnrollNumDao activityEnrollNumDao;

    private final ActivityOrderManager orderManager;

    private final TransactionTemplate transactionTemplate;

    private final PaymentService paymentService;

    /**
     * 创建订单（付费活动下单锁座）
     * <p>
     * 流程：付费活动与报名资格校验 → 查重进行中订单 → 事务[名额+1 锁座 + 落单] → 事务提交后渠道预下单
     */
    public ActivityOrderCreateVO createOrder(Long activityId) {
        Long userId = getCurrentPortalUserId();
        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        if (!Boolean.TRUE.equals(activity.getPaidFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "该活动为免费活动，无需下单支付");
        }
        activity.validateStatus(ActivityStatus.ENROLLING);

        // 报名范围校验：与免费报名保持一致（学院/年级/部落）
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        portalUserValidator.validateUserCanEnrollCollege(activityId, portalUser);
        portalUserValidator.validateUserCanEnrollGrade(activityId, portalUser);
        portalUserValidator.validateUserCanEnrollTribe(activityId, portalUser);
        enrollmentValidator.validateEnrollmentDuplicate(activityId, userId);

        // 查重：进行中订单（待支付/已支付）不允许重复下单
        List<ActivityOrderEntity> activeOrders = orderManager.listActiveOrders(activityId, userId);
        if (!activeOrders.isEmpty()) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "您有一笔进行中的订单，请先支付或取消");
        }

        // 金额以服务端活动配置为准，前端不传金额，从源头杜绝篡改
        Integer amountFen = activity.getPriceFen();
        if (amountFen == null || amountFen <= 0) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动报名费未配置");
        }

        ActivityOrderEntity order = new ActivityOrderEntity();
        order.setOrderNo(OrderNoUtil.generate(OrderNoUtil.ORDER_PREFIX));
        order.setActivityId(activityId);
        order.setUserId(userId);
        order.setAmountFen(amountFen);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setExpireTime(LocalDateTime.now().plusMinutes(PAY_EXPIRE_MINUTES));
        order.setDeletedFlag(false);

        // 事务：锁座（名额+1）+ 落单，二者一致，任一失败整体回滚
        transactionTemplate.executeWithoutResult(status -> {
            if (!activityEnrollNumDao.increaseEnrollNum(activityId)) {
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动报名人数已满");
            }
            if (!orderManager.save(order)) {
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.SERVICE_BUSY, "下单失败，请稍后重试");
            }
        });

        // 事务提交后再向渠道预下单：渠道调用不属于数据库事务，避免长事务占用连接
        log.info("付费活动下单成功：orderNo:{}，activityId:{}，userId:{}，amountFen:{}",
                order.getOrderNo(), activityId, userId, amountFen);
        return buildCreateVO(order, paymentService.prepay(order).getCashierUrl());
    }

    /**
     * 取消待支付订单（用户主动取消，释放锁定的名额）
     */
    public void cancelOrder(String orderNo) {
        Long userId = getCurrentPortalUserId();
        ActivityOrderEntity order = requireOwnOrder(orderNo, userId);
        if (order.getStatus() == OrderStatus.CLOSED) {
            // 幂等：可能已由关单任务关闭，直接成功返回
            return;
        }
        if (order.getStatus() != OrderStatus.WAIT_PAY) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "当前订单状态不可取消");
        }

        transactionTemplate.executeWithoutResult(status -> {
            // CAS：仅待支付可关闭；与支付回调/关单任务抢同一条件（where status=待支付），谁先成功谁生效
            if (!orderManager.closeIfWaitPayCas(order.getId(), LocalDateTime.now())) {
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单状态已变更，取消失败");
            }
            if (!activityEnrollNumDao.decreaseEnrollNum(order.getActivityId())) {
                log.warn("取消订单释放名额失败：orderNo:{}", order.getOrderNo());
            }
        });
        log.info("订单已取消：orderNo:{}，userId:{}", orderNo, userId);
    }

    /**
     * 重新获取支付参数（待支付订单重新拉起收银台）
     * <p>
     * 已超时但关单任务尚未扫到的订单，在此立即按同一 CAS 关单并释放名额
     */
    public ActivityOrderCreateVO prepay(String orderNo) {
        Long userId = getCurrentPortalUserId();
        ActivityOrderEntity order = requireOwnOrder(orderNo, userId);
        if (order.getStatus() == OrderStatus.CLOSED) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单已关闭，请重新下单");
        }
        if (order.getStatus() != OrderStatus.WAIT_PAY) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "当前订单状态不可支付");
        }
        if (order.getExpireTime() != null && !LocalDateTime.now().isBefore(order.getExpireTime())) {
            closeExpiredOrder(order);
            ActivityOrderEntity latest = orderManager.getById(order.getId());
            if (latest != null && latest.getStatus() == OrderStatus.PAID) {
                // 关单 CAS 迟到：支付回调已抢先完成支付
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单已支付成功，无需重复支付");
            }
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单已超时关闭，请重新下单");
        }
        return buildCreateVO(order, paymentService.prepay(order).getCashierUrl());
    }

    /**
     * 分页查询我的订单
     */
    public PageResult<ActivityOrderVO> queryPage(ActivityOrderQueryForm queryForm) {
        Long userId = getCurrentPortalUserId();
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityOrderVO> list = orderManager.getBaseMapper().queryPage(page, queryForm, userId);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 管理端分页查询订单（全量订单；支持状态/活动/用户/关键词/时间范围筛选）
     */
    public PageResult<ActivityOrderVO> queryPageForAdmin(ActivityOrderQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityOrderVO> list = orderManager.getBaseMapper().queryPageForAdmin(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 管理端订单详情（不限用户；补充下单用户与渠道订单号）
     */
    public ActivityOrderVO detailForAdmin(String orderNo) {
        ActivityOrderEntity order = orderManager.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单不存在");
        }
        ActivityOrderVO vo = convertToVO(order);
        vo.setUserId(order.getUserId());
        vo.setChannelOrderNo(order.getChannelOrderNo());
        PortalUserEntity portalUser = portalUserManager.getById(order.getUserId());
        vo.setUsername(portalUser == null ? null : portalUser.getUsername());
        return vo;
    }

    /**
     * 订单详情（仅本人可见）
     */
    public ActivityOrderVO detail(String orderNo) {
        Long userId = getCurrentPortalUserId();
        return convertToVO(requireOwnOrder(orderNo, userId));
    }

    /**
     * 查询本人订单，不存在或非本人订单直接拒绝
     */
    private ActivityOrderEntity requireOwnOrder(String orderNo, Long userId) {
        ActivityOrderEntity order = orderManager.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "无权操作该订单");
        }
        return order;
    }

    /**
     * 立即关闭已超时订单并释放名额（与关单任务/支付回调同一 CAS 互斥）
     */
    private void closeExpiredOrder(ActivityOrderEntity order) {
        transactionTemplate.executeWithoutResult(status -> {
            if (!orderManager.closeIfWaitPayCas(order.getId(), LocalDateTime.now())) {
                // 已被支付回调或关单任务抢先处理，跳过
                return;
            }
            if (!activityEnrollNumDao.decreaseEnrollNum(order.getActivityId())) {
                log.warn("超时关单释放名额失败：orderNo:{}", order.getOrderNo());
            }
        });
    }

    private ActivityOrderCreateVO buildCreateVO(ActivityOrderEntity order, String cashierUrl) {
        ActivityOrderCreateVO vo = new ActivityOrderCreateVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setAmountFen(order.getAmountFen());
        vo.setExpireTime(order.getExpireTime());
        vo.setCashierUrl(cashierUrl);
        return vo;
    }

    private ActivityOrderVO convertToVO(ActivityOrderEntity order) {
        ActivityOrderVO vo = new ActivityOrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setActivityId(order.getActivityId());
        ActivityEntity activity = activityManager.getById(order.getActivityId());
        vo.setActivityTitle(activity == null ? null : activity.getTitle());
        vo.setAmountFen(order.getAmountFen());
        vo.setStatus(order.getStatus() == null ? null : order.getStatus().getCode());
        vo.setPayChannel(order.getPayChannel() == null ? null : order.getPayChannel().getCode());
        vo.setPayTime(order.getPayTime());
        vo.setExpireTime(order.getExpireTime());
        vo.setCloseTime(order.getCloseTime());
        vo.setCreateTime(order.getCreateTime());
        return vo;
    }

    /**
     * 获取当前登录的门户用户id（订单相关接口仅允许门户用户访问）
     */
    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (requestUser == null || requestUser.getUserType() != UserTypeEnum.PORTAL_USER) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "用户类型错误");
        }
        return requestUser.getUserId();
    }
}
