package com.akkkka.admin.module.business.funcampus.activityOrder.service;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogAction;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogOperatorType;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderLogEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderLogVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderLogManager;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.util.SmartRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 活动订单操作留痕 Service 单元测试
 * <p>
 * 覆盖：三类操作人（门户/管理端/系统）字段组装、超长 detail 截断、
 * 留痕失败兜底（不抛出影响资金主流程）、查询转 VO 文案
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 */
@ExtendWith(MockitoExtension.class)
public class ActivityOrderLogServiceTest {

    @Mock
    private ActivityOrderLogManager orderLogManager;

    @InjectMocks
    private ActivityOrderLogService service;

    @AfterEach
    void cleanRequestUser() {
        SmartRequestUtil.remove();
    }

    private ActivityOrderEntity order() {
        ActivityOrderEntity order = new ActivityOrderEntity();
        order.setId(9L);
        order.setOrderNo("AO202609270001");
        order.setActivityId(7L);
        order.setUserId(20L);
        order.setAmountFen(150);
        order.setStatus(OrderStatus.WAIT_PAY);
        return order;
    }

    private ActivityOrderLogEntity capturedLog() {
        ArgumentCaptor<ActivityOrderLogEntity> captor = ArgumentCaptor.forClass(ActivityOrderLogEntity.class);
        verify(orderLogManager).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void recordPortalUser_savesLogWithOrderInfoAndOperator() {
        service.recordPortalUser(order(), OrderLogAction.CREATE, 20L, "创建订单，金额 1.50 元");

        ActivityOrderLogEntity saved = capturedLog();
        assertEquals(9L, saved.getOrderId());
        assertEquals("AO202609270001", saved.getOrderNo());
        assertEquals(OrderLogAction.CREATE, saved.getAction());
        assertEquals(OrderLogOperatorType.PORTAL_USER, saved.getOperatorType());
        assertEquals(20L, saved.getOperatorId());
        assertEquals("创建订单，金额 1.50 元", saved.getDetail());
        assertFalse(saved.getDeletedFlag());
    }

    @Test
    void recordAdminUser_usesCurrentRequestUserId() {
        RequestBackendUser backendUser = new RequestBackendUser();
        backendUser.setId(1L);
        backendUser.setUsername("admin");
        SmartRequestUtil.setRequestUser(backendUser);

        service.recordAdminUser(order(), OrderLogAction.REFUND_RETRY, "管理端重试退款");

        ActivityOrderLogEntity saved = capturedLog();
        assertEquals(OrderLogOperatorType.ADMIN_USER, saved.getOperatorType());
        assertEquals(1L, saved.getOperatorId());
    }

    @Test
    void recordAdminUser_whenNoRequestUser_operatorIdNull() {
        // 非请求线程（如定时任务触发活动取消）下操作人为空，留痕不报错
        service.recordAdminUser(order(), OrderLogAction.CLOSE, "活动已取消，关闭待支付订单");

        ActivityOrderLogEntity saved = capturedLog();
        assertEquals(OrderLogOperatorType.ADMIN_USER, saved.getOperatorType());
        assertNull(saved.getOperatorId());
    }

    @Test
    void recordSystem_savesWithNullOperatorId() {
        service.recordSystem(order(), OrderLogAction.PAY_SUCCESS, "支付成功");

        ActivityOrderLogEntity saved = capturedLog();
        assertEquals(OrderLogOperatorType.SYSTEM, saved.getOperatorType());
        assertNull(saved.getOperatorId());
    }

    @Test
    void record_whenDetailTooLong_truncatedToColumnLength() {
        String detail = "x".repeat(600);

        service.recordSystem(order(), OrderLogAction.PAY_FAILED, detail);

        assertEquals(500, capturedLog().getDetail().length());
    }

    @Test
    void record_whenSaveThrows_swallowedWithoutBreakingMainFlow() {
        when(orderLogManager.save(any())).thenThrow(new RuntimeException("db down"));

        assertDoesNotThrow(() -> service.recordSystem(order(), OrderLogAction.CLOSE, "订单超时未支付，系统自动关闭"));
    }

    @Test
    void listByOrderNo_convertsToVoWithActionAndOperatorNames() {
        ActivityOrderLogEntity userLog = new ActivityOrderLogEntity();
        userLog.setId(1L);
        userLog.setOrderNo("AO202609270001");
        userLog.setAction(OrderLogAction.CREATE);
        userLog.setOperatorType(OrderLogOperatorType.PORTAL_USER);
        userLog.setOperatorId(20L);
        userLog.setDetail("创建订单");
        userLog.setCreateTime(LocalDateTime.now());
        ActivityOrderLogEntity systemLog = new ActivityOrderLogEntity();
        systemLog.setId(2L);
        systemLog.setOrderNo("AO202609270001");
        systemLog.setAction(OrderLogAction.PAY_SUCCESS);
        systemLog.setOperatorType(OrderLogOperatorType.SYSTEM);
        systemLog.setDetail("支付成功");
        when(orderLogManager.listByOrderNo("AO202609270001")).thenReturn(List.of(userLog, systemLog));

        List<ActivityOrderLogVO> list = service.listByOrderNo("AO202609270001");

        assertEquals(2, list.size());
        ActivityOrderLogVO first = list.get(0);
        assertEquals(1, first.getAction());
        assertEquals("创建订单", first.getActionName());
        assertEquals(1, first.getOperatorType());
        assertEquals("用户#20", first.getOperatorName());
        assertEquals("创建订单", first.getDetail());
        ActivityOrderLogVO second = list.get(1);
        assertEquals(2, second.getAction());
        assertEquals("支付成功", second.getActionName());
        assertEquals("系统", second.getOperatorName());
    }
}
