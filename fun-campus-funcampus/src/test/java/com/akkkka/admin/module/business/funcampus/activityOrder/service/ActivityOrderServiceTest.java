package com.akkkka.admin.module.business.funcampus.activityOrder.service;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentValidator;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderCreateVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayPrepayResult;
import com.akkkka.admin.module.business.funcampus.payment.service.PaymentService;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 活动报名订单 Service 单元测试
 * <p>
 * 覆盖：付费活动下单（校验顺序、锁座事务、金额以服务端为准）、
 * 用户取消订单（归属校验、幂等、CAS 失败与释放名额）、
 * 重新支付（过期立即关单、与支付回调抢同一 CAS 的竞态分支）
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 */
@ExtendWith(MockitoExtension.class)
public class ActivityOrderServiceTest {

    @Mock
    private ActivityValidator activityValidator;
    @Mock
    private ActivityManager activityManager;
    @Mock
    private PortalUserManager portalUserManager;
    @Mock
    private PortalUserValidator portalUserValidator;
    @Mock
    private ActivityEnrollmentValidator enrollmentValidator;
    @Mock
    private ActivityEnrollNumDao activityEnrollNumDao;
    @Mock
    private ActivityOrderManager orderManager;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ActivityOrderService service;

    @AfterEach
    void cleanRequestUser() {
        SmartRequestUtil.remove();
    }

    /** 让事务模板真正执行传入的消费逻辑 */
    private void runTransactionNow() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> consumer = invocation.getArgument(0);
            consumer.accept(mock(TransactionStatus.class));
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    private void setPortalUser(Long userId) {
        RequestPortalUser portalUser = new RequestPortalUser();
        portalUser.setId(userId);
        portalUser.setUsername("student" + userId);
        portalUser.setUserType(UserTypeEnum.PORTAL_USER);
        SmartRequestUtil.setRequestUser(portalUser);
    }

    private ActivityEntity activity(ActivityStatus status, boolean paidFlag, Integer priceFen) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(7L);
        activity.setTitle("羽毛球比赛");
        activity.setStatus(status);
        activity.setPaidFlag(paidFlag);
        activity.setPriceFen(priceFen);
        return activity;
    }

    private ActivityOrderEntity order(Long id, String orderNo, OrderStatus status, Long userId) {
        ActivityOrderEntity order = new ActivityOrderEntity();
        order.setId(id);
        order.setOrderNo(orderNo);
        order.setActivityId(7L);
        order.setUserId(userId);
        order.setAmountFen(150);
        order.setStatus(status);
        return order;
    }

    /** 走完「付费校验 → 报名范围 → 查重 → 无进行中订单」的前置链路 */
    private void stubCreateOrderPreconditions(ActivityEntity activity) {
        when(activityValidator.validateActivityId(7L)).thenReturn(activity);
        when(portalUserManager.getById(20L)).thenReturn(new PortalUserEntity());
        when(orderManager.listActiveOrders(7L, 20L)).thenReturn(List.of());
    }

    // ---------------------------------- 下单 createOrder ----------------------------------

    @Test
    void createOrder_whenFreeActivity_throwParamError() {
        setPortalUser(20L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activity(ActivityStatus.ENROLLING, false, null));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createOrder(7L));
        assertTrue(ex.getMessage().contains("免费活动"));
    }

    @Test
    void createOrder_whenStatusNotEnrolling_throwParamError() {
        setPortalUser(20L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activity(ActivityStatus.WAIT_ENROLL, true, 150));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createOrder(7L));
        assertTrue(ex.getMessage().contains("报名"));
    }

    @Test
    void createOrder_whenScopeValidationFails_abortBeforeDedup() {
        setPortalUser(20L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activity(ActivityStatus.ENROLLING, true, 150));
        when(portalUserManager.getById(20L)).thenReturn(new PortalUserEntity());
        doThrow(new BusinessException(UserErrorCode.PARAM_ERROR, "该活动仅限指定学院报名"))
                .when(portalUserValidator).validateUserCanEnrollCollege(eq(7L), any());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createOrder(7L));
        assertTrue(ex.getMessage().contains("仅限指定学院"));
        // 范围校验失败即终止，不应继续查重/下单
        verify(enrollmentValidator, never()).validateEnrollmentDuplicate(any(), any());
        verify(orderManager, never()).save(any());
    }

    @Test
    void createOrder_whenDuplicateEnrollment_throwParamError() {
        setPortalUser(20L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activity(ActivityStatus.ENROLLING, true, 150));
        when(portalUserManager.getById(20L)).thenReturn(new PortalUserEntity());
        doThrow(new BusinessException(UserErrorCode.PARAM_ERROR, "您已报名该活动"))
                .when(enrollmentValidator).validateEnrollmentDuplicate(7L, 20L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createOrder(7L));
        assertTrue(ex.getMessage().contains("已报名"));
        verify(orderManager, never()).listActiveOrders(any(), any());
    }

    @Test
    void createOrder_whenActiveOrderExists_throwParamError() {
        setPortalUser(20L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activity(ActivityStatus.ENROLLING, true, 150));
        when(portalUserManager.getById(20L)).thenReturn(new PortalUserEntity());
        when(orderManager.listActiveOrders(7L, 20L))
                .thenReturn(List.of(order(1L, "AO1", OrderStatus.WAIT_PAY, 20L)));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createOrder(7L));
        assertTrue(ex.getMessage().contains("进行中的订单"));
    }

    @Test
    void createOrder_whenPriceNotConfigured_throwParamError() {
        setPortalUser(20L);
        stubCreateOrderPreconditions(activity(ActivityStatus.ENROLLING, true, 0));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createOrder(7L));
        assertTrue(ex.getMessage().contains("报名费未配置"));
    }

    @Test
    void createOrder_whenEnrollNumFull_rollbackAndThrow() {
        setPortalUser(20L);
        stubCreateOrderPreconditions(activity(ActivityStatus.ENROLLING, true, 150));
        runTransactionNow();
        when(activityEnrollNumDao.increaseEnrollNum(7L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createOrder(7L));
        assertTrue(ex.getMessage().contains("人数已满"));
        verify(orderManager, never()).save(any());
        verify(paymentService, never()).prepay(any());
    }

    @Test
    void createOrder_whenSaveOrderFails_throwServiceBusy() {
        setPortalUser(20L);
        stubCreateOrderPreconditions(activity(ActivityStatus.ENROLLING, true, 150));
        runTransactionNow();
        when(activityEnrollNumDao.increaseEnrollNum(7L)).thenReturn(true);
        when(orderManager.save(any())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.createOrder(7L));
        assertTrue(ex.getMessage().contains("下单失败"));
    }

    @Test
    void createOrder_success_locksSeatAndReturnsCashierUrl() {
        setPortalUser(20L);
        stubCreateOrderPreconditions(activity(ActivityStatus.ENROLLING, true, 150));
        runTransactionNow();
        when(activityEnrollNumDao.increaseEnrollNum(7L)).thenReturn(true);
        when(orderManager.save(any())).thenReturn(true);
        PayPrepayResult prepayResult = new PayPrepayResult();
        prepayResult.setCashierUrl("/portal/payment/mock/cashier?orderNo=AO-TEST");
        when(paymentService.prepay(any())).thenReturn(prepayResult);

        ActivityOrderCreateVO vo = service.createOrder(7L);

        assertEquals(150, vo.getAmountFen());
        assertEquals("/portal/payment/mock/cashier?orderNo=AO-TEST", vo.getCashierUrl());
        ArgumentCaptor<ActivityOrderEntity> orderCaptor = ArgumentCaptor.forClass(ActivityOrderEntity.class);
        verify(orderManager).save(orderCaptor.capture());
        ActivityOrderEntity saved = orderCaptor.getValue();
        // 金额以服务端活动配置为准，状态待支付、名额已锁定（increase 已在上方验证）
        assertEquals(OrderStatus.WAIT_PAY, saved.getStatus());
        assertEquals(150, saved.getAmountFen());
        assertEquals(20L, saved.getUserId());
        assertEquals(7L, saved.getActivityId());
        assertFalse(saved.getDeletedFlag());
        assertNotNull(saved.getOrderNo());
        assertTrue(saved.getOrderNo().startsWith("AO"));
        long expireMinutes = Duration.between(LocalDateTime.now(), saved.getExpireTime()).toMinutes();
        assertTrue(expireMinutes >= 14 && expireMinutes <= 15, "待支付有效期应约 15 分钟");
    }

    // ---------------------------------- 取消订单 cancelOrder ----------------------------------

    @Test
    void cancelOrder_whenOrderNotExist_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.cancelOrder("AO1"));
        assertTrue(ex.getMessage().contains("订单不存在"));
    }

    @Test
    void cancelOrder_whenNotOrderOwner_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(1L, "AO1", OrderStatus.WAIT_PAY, 99L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.cancelOrder("AO1"));
        assertTrue(ex.getMessage().contains("无权操作"));
    }

    @Test
    void cancelOrder_whenAlreadyClosed_idempotentReturn() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(1L, "AO1", OrderStatus.CLOSED, 20L));

        assertDoesNotThrow(() -> service.cancelOrder("AO1"));
        // 已关闭不再走关单事务，也不重复释放名额
        verify(transactionTemplate, never()).executeWithoutResult(any());
        verify(orderManager, never()).closeIfWaitPayCas(any(), any());
    }

    @Test
    void cancelOrder_whenPaid_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(1L, "AO1", OrderStatus.PAID, 20L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.cancelOrder("AO1"));
        assertTrue(ex.getMessage().contains("不可取消"));
    }

    @Test
    void cancelOrder_whenNotPortalUser_throwParamError() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.cancelOrder("AO1"));
        assertTrue(ex.getMessage().contains("用户类型错误"));
    }

    @Test
    void cancelOrder_success_closesOrderAndReleasesSeat() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(1L, "AO1", OrderStatus.WAIT_PAY, 20L));
        runTransactionNow();
        when(orderManager.closeIfWaitPayCas(eq(1L), any())).thenReturn(true);
        when(activityEnrollNumDao.decreaseEnrollNum(7L)).thenReturn(true);

        service.cancelOrder("AO1");

        verify(orderManager).closeIfWaitPayCas(eq(1L), any());
        verify(activityEnrollNumDao).decreaseEnrollNum(7L);
    }

    @Test
    void cancelOrder_whenCasFails_throwAndNoRelease() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(1L, "AO1", OrderStatus.WAIT_PAY, 20L));
        runTransactionNow();
        when(orderManager.closeIfWaitPayCas(eq(1L), any())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.cancelOrder("AO1"));
        assertTrue(ex.getMessage().contains("状态已变更"));
        verify(activityEnrollNumDao, never()).decreaseEnrollNum(any());
    }

    // ---------------------------------- 重新支付 prepay ----------------------------------

    @Test
    void prepay_whenClosed_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(1L, "AO1", OrderStatus.CLOSED, 20L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.prepay("AO1"));
        assertTrue(ex.getMessage().contains("已关闭"));
    }

    @Test
    void prepay_whenPaid_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(1L, "AO1", OrderStatus.PAID, 20L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.prepay("AO1"));
        assertTrue(ex.getMessage().contains("不可支付"));
    }

    @Test
    void prepay_whenNotExpired_returnCashierUrl() {
        setPortalUser(20L);
        ActivityOrderEntity order = order(1L, "AO1", OrderStatus.WAIT_PAY, 20L);
        order.setExpireTime(LocalDateTime.now().plusMinutes(10));
        when(orderManager.getByOrderNo("AO1")).thenReturn(order);
        PayPrepayResult prepayResult = new PayPrepayResult();
        prepayResult.setCashierUrl("/portal/payment/mock/cashier?orderNo=AO1");
        when(paymentService.prepay(order)).thenReturn(prepayResult);

        ActivityOrderCreateVO vo = service.prepay("AO1");

        assertEquals("/portal/payment/mock/cashier?orderNo=AO1", vo.getCashierUrl());
        assertEquals(150, vo.getAmountFen());
    }

    @Test
    void prepay_whenExpired_closeImmediatelyAndThrowTimeout() {
        setPortalUser(20L);
        ActivityOrderEntity order = order(1L, "AO1", OrderStatus.WAIT_PAY, 20L);
        order.setExpireTime(LocalDateTime.now().minusMinutes(1));
        when(orderManager.getByOrderNo("AO1")).thenReturn(order);
        runTransactionNow();
        when(orderManager.closeIfWaitPayCas(eq(1L), any())).thenReturn(true);
        when(activityEnrollNumDao.decreaseEnrollNum(7L)).thenReturn(true);
        when(orderManager.getById(1L)).thenReturn(order(1L, "AO1", OrderStatus.CLOSED, 20L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.prepay("AO1"));
        assertTrue(ex.getMessage().contains("超时关闭"));
        // 过期订单在此立即关单并释放名额，不等关单任务扫到
        verify(orderManager).closeIfWaitPayCas(eq(1L), any());
        verify(activityEnrollNumDao).decreaseEnrollNum(7L);
    }

    @Test
    void prepay_whenExpiredButPaidByCallback_throwPaidMessage() {
        setPortalUser(20L);
        ActivityOrderEntity order = order(1L, "AO1", OrderStatus.WAIT_PAY, 20L);
        order.setExpireTime(LocalDateTime.now().minusMinutes(1));
        when(orderManager.getByOrderNo("AO1")).thenReturn(order);
        runTransactionNow();
        // 关单 CAS 迟到：支付回调已抢先完成支付
        when(orderManager.closeIfWaitPayCas(eq(1L), any())).thenReturn(false);
        when(orderManager.getById(1L)).thenReturn(order(1L, "AO1", OrderStatus.PAID, 20L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.prepay("AO1"));
        assertTrue(ex.getMessage().contains("已支付成功"));
        verify(activityEnrollNumDao, never()).decreaseEnrollNum(any());
        verify(paymentService, never()).prepay(any());
    }
}
