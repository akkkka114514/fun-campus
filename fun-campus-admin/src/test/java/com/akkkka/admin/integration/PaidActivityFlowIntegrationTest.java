package com.akkkka.admin.integration;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundPolicy;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityRefundApplyForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderCreateVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityOrderService;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityRefundService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.service.PaymentService;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 付费活动全链路集成测试
 * <p>
 * 覆盖链路：下单锁座 → 支付回调 → 报名生效 → 退款申请 → 退款回调到账（报名逻辑删除 + 名额释放）；
 * 并覆盖关键边界：重复回调幂等、金额篡改拒绝、重复下单拦截、取消后支付回调（PAY_AFTER_CLOSE）、
 * 免费活动下单拦截、退款政策限制、退款失败回退与重新申请。
 * <p>
 * 说明：MockPaymentChannel 的退款回调由调度线程延时投递（异步线程读不到测试未提交数据），
 * 因此测试统一同步构造 NotifyResult 直接调用 PaymentService.handlePayNotify/handleRefundNotify 模拟回调。
 * <p>
 * author:akkkka114514
 * create at 2026-10-01
 */
@DisplayName("付费活动全链路集成测试")
class PaidActivityFlowIntegrationTest extends BaseIntegrationTest {

    private static final int PRICE_FEN = 5000;

    @Resource
    private ActivityOrderService activityOrderService;

    @Resource
    private ActivityRefundService activityRefundService;

    @Resource
    private PaymentService paymentService;

    private PortalUserEntity buyer;

    private ActivityEntity paidActivity;

    @BeforeEach
    void setUp() {
        buyer = createPortalUser("paid-buyer-" + System.nanoTime());
        // 付费活动：报名中、报名截止前可退；时间表覆盖当前时间
        paidActivity = newPaidActivity("付费活动-全链路-" + System.nanoTime(), ActivityStatus.ENROLLING, PRICE_FEN);
        paidActivity.setRefundPolicy(RefundPolicy.BEFORE_ENROLL_END);
        saveActivity(paidActivity);
        LocalDateTime now = LocalDateTime.now();
        saveSchedule(paidActivity.getId(), now.minusHours(1), now.plusHours(2), now.plusHours(3), now.plusHours(4));
        loginAsPortalUser(buyer);
    }

    @Test
    @DisplayName("付费全链路：下单锁座→支付生效→申请退款→退款到账（含回调幂等）")
    void fullPaidFlow_createOrder_pay_enroll_refund() {
        // ---- 1. 下单锁座 ----
        ActivityOrderCreateVO createVO = activityOrderService.createOrder(paidActivity.getId());
        assertNotNull(createVO.getOrderNo());
        assertEquals(PRICE_FEN, createVO.getAmountFen());
        assertNotNull(createVO.getCashierUrl(), "应返回收银台跳转链接");
        assertTrue(createVO.getCashierUrl().contains(createVO.getOrderNo()));

        ActivityOrderEntity order = activityOrderManager.getByOrderNo(createVO.getOrderNo());
        assertNotNull(order);
        assertEquals(OrderStatus.WAIT_PAY, order.getStatus());
        assertEquals(1, enrollNumOf(paidActivity.getId()), "下单应锁座（名额+1）");
        assertNull(enrollmentRow(paidActivity.getId(), buyer.getId()), "未支付不应产生报名记录");

        // ---- 2. 支付回调（同步模拟渠道通知） ----
        PayNotifyResult payNotify = buildSuccessPayNotify(createVO.getOrderNo(), PRICE_FEN);
        paymentService.handlePayNotify(payNotify);

        // ---- 3. 支付生效：订单 PAID + 报名记录写入 + 名额不重复占用 ----
        order = activityOrderManager.getByOrderNo(createVO.getOrderNo());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertNotNull(order.getPayTime());
        assertNotNull(order.getChannelOrderNo());

        ActivityEnrollmentEntity enrollment = enrollmentRow(paidActivity.getId(), buyer.getId());
        assertNotNull(enrollment, "支付成功后应写入报名记录");
        assertFalse(enrollment.getDeletedFlag());
        assertEquals(1, enrollNumOf(paidActivity.getId()), "名额在下单时已锁，支付不应重复占用");

        // ---- 4. 重复支付回调：幂等，状态与数据不变 ----
        paymentService.handlePayNotify(payNotify);
        assertEquals(OrderStatus.PAID, activityOrderManager.getByOrderNo(createVO.getOrderNo()).getStatus());
        assertFalse(enrollmentRow(paidActivity.getId(), buyer.getId()).getDeletedFlag());

        // ---- 5. 申请退款：订单/退款单均进入退款中 ----
        String refundNo = activityRefundService.refundApply(buildRefundApplyForm(createVO.getOrderNo()));
        assertNotNull(refundNo);
        assertEquals(OrderStatus.REFUNDING, activityOrderManager.getByOrderNo(createVO.getOrderNo()).getStatus());
        assertEquals(RefundStatus.REFUNDING, activityRefundManager.getByRefundNo(refundNo).getStatus());

        // ---- 6. 退款回调：到账 ----
        paymentService.handleRefundNotify(buildSuccessRefundNotify(refundNo));

        assertEquals(RefundStatus.SUCCESS, activityRefundManager.getByRefundNo(refundNo).getStatus());
        assertEquals(OrderStatus.REFUNDED, activityOrderManager.getByOrderNo(createVO.getOrderNo()).getStatus());
        assertTrue(enrollmentRow(paidActivity.getId(), buyer.getId()).getDeletedFlag(), "退款到账应逻辑删除报名记录");
        assertEquals(0, enrollNumOf(paidActivity.getId()), "退款到账应释放名额");

        // ---- 7. 重复退款回调：幂等，不重复释放名额 ----
        paymentService.handleRefundNotify(buildSuccessRefundNotify(refundNo));
        assertEquals(OrderStatus.REFUNDED, activityOrderManager.getByOrderNo(createVO.getOrderNo()).getStatus());
        assertEquals(0, enrollNumOf(paidActivity.getId()));
    }

    @Test
    @DisplayName("金额篡改：回调金额与订单不一致时拒绝入账")
    void payNotify_amountMismatch_rejected() {
        ActivityOrderCreateVO createVO = activityOrderService.createOrder(paidActivity.getId());

        PayNotifyResult tampered = buildSuccessPayNotify(createVO.getOrderNo(), PRICE_FEN - 1);
        paymentService.handlePayNotify(tampered);

        assertEquals(OrderStatus.WAIT_PAY, activityOrderManager.getByOrderNo(createVO.getOrderNo()).getStatus());
        assertNull(enrollmentRow(paidActivity.getId(), buyer.getId()), "金额不一致不应写入报名记录");
        assertEquals(1, enrollNumOf(paidActivity.getId()), "名额保持锁定，未被释放");
    }

    @Test
    @DisplayName("重复下单：存在进行中订单时拦截，且不重复锁座")
    void createOrder_duplicateActiveOrder_blocked() {
        activityOrderService.createOrder(paidActivity.getId());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityOrderService.createOrder(paidActivity.getId()));
        assertTrue(ex.getMessage().contains("进行中的订单"));
        assertEquals(1, enrollNumOf(paidActivity.getId()), "拦截后不应重复锁座");
    }

    @Test
    @DisplayName("取消订单后支付回调到达：订单保持关闭（PAY_AFTER_CLOSE），不产生报名记录")
    void cancelOrder_thenPayNotify_keepsClosed() {
        ActivityOrderCreateVO createVO = activityOrderService.createOrder(paidActivity.getId());

        activityOrderService.cancelOrder(createVO.getOrderNo());
        assertEquals(OrderStatus.CLOSED, activityOrderManager.getByOrderNo(createVO.getOrderNo()).getStatus());
        assertEquals(0, enrollNumOf(paidActivity.getId()), "取消应释放锁定的名额");

        // 渠道资金晚到的支付回调
        paymentService.handlePayNotify(buildSuccessPayNotify(createVO.getOrderNo(), PRICE_FEN));

        assertEquals(OrderStatus.CLOSED, activityOrderManager.getByOrderNo(createVO.getOrderNo()).getStatus(),
                "支付回调晚于关单：订单保持关闭，等待人工退款补偿");
        assertNull(enrollmentRow(paidActivity.getId(), buyer.getId()), "关闭订单不应产生报名记录");
        assertEquals(0, enrollNumOf(paidActivity.getId()));
    }

    @Test
    @DisplayName("免费活动下单：被拦截")
    void createOrder_freeActivity_blocked() {
        ActivityEntity freeActivity = saveActivity(newActivity("免费活动-下单拦截-" + System.nanoTime(), ActivityStatus.ENROLLING));
        LocalDateTime now = LocalDateTime.now();
        saveSchedule(freeActivity.getId(), now.minusHours(1), now.plusHours(2), now.plusHours(3), now.plusHours(4));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityOrderService.createOrder(freeActivity.getId()));
        assertTrue(ex.getMessage().contains("免费活动"));
        assertEquals(0, enrollNumOf(freeActivity.getId()));
    }

    @Test
    @DisplayName("不可退款政策：退款申请被拒绝，订单保持已支付")
    void refundApply_notRefundablePolicy_blocked() {
        ActivityEntity noRefundActivity = newPaidActivity("付费活动-不可退-" + System.nanoTime(),
                ActivityStatus.ENROLLING, PRICE_FEN);
        noRefundActivity.setRefundPolicy(RefundPolicy.NOT_REFUNDABLE);
        saveActivity(noRefundActivity);
        LocalDateTime now = LocalDateTime.now();
        saveSchedule(noRefundActivity.getId(), now.minusHours(1), now.plusHours(2), now.plusHours(3), now.plusHours(4));

        String orderNo = createAndPayOrder(noRefundActivity.getId());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityRefundService.refundApply(buildRefundApplyForm(orderNo)));
        assertTrue(ex.getMessage().contains("不支持退款"));
        assertEquals(OrderStatus.PAID, activityOrderManager.getByOrderNo(orderNo).getStatus());
        assertEquals(1, enrollNumOf(noRefundActivity.getId()), "拒绝退款不应释放名额");
    }

    @Test
    @DisplayName("报名截止后按政策不可退：退款申请被拒绝")
    void refundApply_afterEnrollEndPolicy_blocked() {
        String orderNo = createAndPayOrder(paidActivity.getId());

        // 模拟报名截止：将时间表报名结束时间改为过去（同事务内立即可见）
        ActivityScheduleEntity schedule = activityScheduleManager.getById(paidActivity.getId());
        schedule.setEnrollEndTime(LocalDateTime.now().minusMinutes(1));
        activityScheduleManager.updateById(schedule);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityRefundService.refundApply(buildRefundApplyForm(orderNo)));
        assertTrue(ex.getMessage().contains("报名已截止"));
        assertEquals(OrderStatus.PAID, activityOrderManager.getByOrderNo(orderNo).getStatus());
    }

    @Test
    @DisplayName("退款失败回退：订单变为退款失败，支持重新申请退款")
    void refundNotify_failed_thenRetry() {
        String orderNo = createAndPayOrder(paidActivity.getId());

        String firstRefundNo = activityRefundService.refundApply(buildRefundApplyForm(orderNo));

        // 渠道退款失败回调：退款单 FAILED + 订单回退 REFUND_FAILED
        RefundNotifyResult failNotify = new RefundNotifyResult();
        failNotify.setChannel(PayChannelEnum.MOCK);
        failNotify.setRefundNo(firstRefundNo);
        failNotify.setSuccess(false);
        failNotify.setFailReason("模拟渠道退款失败");
        paymentService.handleRefundNotify(failNotify);

        assertEquals(RefundStatus.FAILED, activityRefundManager.getByRefundNo(firstRefundNo).getStatus());
        assertEquals(OrderStatus.REFUND_FAILED, activityOrderManager.getByOrderNo(orderNo).getStatus());
        assertFalse(enrollmentRow(paidActivity.getId(), buyer.getId()).getDeletedFlag(), "退款失败不应删除报名记录");
        assertEquals(1, enrollNumOf(paidActivity.getId()), "退款失败不应释放名额");

        // 退款失败支持重新申请：生成新的退款单
        String secondRefundNo = activityRefundService.refundApply(buildRefundApplyForm(orderNo));
        assertNotEquals(firstRefundNo, secondRefundNo);
        assertEquals(OrderStatus.REFUNDING, activityOrderManager.getByOrderNo(orderNo).getStatus());
    }

    // ==================== 私有辅助 ====================

    /**
     * 下单并支付成功，返回订单号
     */
    private String createAndPayOrder(Long activityId) {
        ActivityOrderCreateVO createVO = activityOrderService.createOrder(activityId);
        paymentService.handlePayNotify(buildSuccessPayNotify(createVO.getOrderNo(), createVO.getAmountFen()));
        assertEquals(OrderStatus.PAID, activityOrderManager.getByOrderNo(createVO.getOrderNo()).getStatus());
        return createVO.getOrderNo();
    }

    private PayNotifyResult buildSuccessPayNotify(String orderNo, int amountFen) {
        PayNotifyResult notify = new PayNotifyResult();
        notify.setChannel(PayChannelEnum.MOCK);
        notify.setOrderNo(orderNo);
        notify.setChannelOrderNo("MOCKPAY-" + orderNo);
        notify.setAmountFen(amountFen);
        notify.setSuccess(true);
        return notify;
    }

    private RefundNotifyResult buildSuccessRefundNotify(String refundNo) {
        RefundNotifyResult notify = new RefundNotifyResult();
        notify.setChannel(PayChannelEnum.MOCK);
        notify.setRefundNo(refundNo);
        notify.setChannelRefundNo("MOCKRF-" + refundNo);
        notify.setSuccess(true);
        notify.setRefundTime(LocalDateTime.now());
        return notify;
    }

    private ActivityRefundApplyForm buildRefundApplyForm(String orderNo) {
        ActivityRefundApplyForm form = new ActivityRefundApplyForm();
        form.setOrderNo(orderNo);
        form.setReason("集成测试退款");
        return form;
    }
}
