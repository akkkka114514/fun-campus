package com.akkkka.module.support.pay.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.akkkka.module.support.pay.constant.PayeeAccountStatus;
import com.akkkka.module.support.pay.constant.PayeeOwnerType;
import com.akkkka.module.support.pay.domain.entity.PayeeAccountEntity;
import com.akkkka.module.support.pay.manager.PayeeAccountManager;
import com.akkkka.common.exception.BusinessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 收款账户 Service 单元测试（收款方解析）
 * <p>
 * 覆盖：解析优先级（组织→学院→学校）、归属主体未开户拒绝、账户停用拒绝、
 * 归属信息缺失拒绝；未开户时不兜底到上级主体（钱不能收错主体）
 * <p>
 * 入参为活动三归属字段（organizationId, collegeId, schoolId）而非活动实体：
 * 0 与 null 均表示该级归属不存在（与实体读取后的传参语义一致）
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 */
@ExtendWith(MockitoExtension.class)
public class PayeeAccountServiceTest {

    @Mock
    private PayeeAccountManager payeeAccountManager;

    @InjectMocks
    private PayeeAccountService service;

    private PayeeAccountEntity account(String payeeNo, PayeeAccountStatus status) {
        PayeeAccountEntity entity = new PayeeAccountEntity();
        entity.setPayeeNo(payeeNo);
        entity.setOwnerName("测试主体");
        entity.setStatus(status);
        return entity;
    }

    @Test
    void resolvePayee_whenOrganizationPresent_prefersOrganization() {
        when(payeeAccountManager.getByOwner(PayeeOwnerType.ORGANIZATION, 3L))
                .thenReturn(account("MCH3000001", PayeeAccountStatus.ENABLED));

        // 组织与学院同时存在（历史数据）：组织最具体，优先
        PayeeAccountEntity result = service.resolvePayee(3L, 2L, 1L);

        assertEquals("MCH3000001", result.getPayeeNo());
        verify(payeeAccountManager, never()).getByOwner(eq(PayeeOwnerType.COLLEGE), any());
    }

    @Test
    void resolvePayee_whenOnlyCollege_usesCollege() {
        when(payeeAccountManager.getByOwner(PayeeOwnerType.COLLEGE, 2L))
                .thenReturn(account("MCH2000001", PayeeAccountStatus.ENABLED));

        PayeeAccountEntity result = service.resolvePayee(0L, 2L, 1L);

        assertEquals("MCH2000001", result.getPayeeNo());
        verify(payeeAccountManager, never()).getByOwner(eq(PayeeOwnerType.SCHOOL), any());
    }

    @Test
    void resolvePayee_whenSchoolOnly_fallsBackToSchool() {
        when(payeeAccountManager.getByOwner(PayeeOwnerType.SCHOOL, 1L))
                .thenReturn(account("MCH1000001", PayeeAccountStatus.ENABLED));

        PayeeAccountEntity result = service.resolvePayee(0L, null, 1L);

        assertEquals("MCH1000001", result.getPayeeNo());
    }

    @Test
    void resolvePayee_whenOwnerNotRegistered_throwParamError() {
        // 组织归属存在但未开户：直接拒绝，不兜底到学院/学校（钱不能收错主体）
        when(payeeAccountManager.getByOwner(PayeeOwnerType.ORGANIZATION, 9L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resolvePayee(9L, null, 1L));
        assertTrue(ex.getMessage().contains("未开通收款账户"));
    }

    @Test
    void resolvePayee_whenAccountDisabled_throwParamError() {
        when(payeeAccountManager.getByOwner(PayeeOwnerType.SCHOOL, 1L))
                .thenReturn(account("MCH1000001", PayeeAccountStatus.DISABLED));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resolvePayee(0L, null, 1L));
        assertTrue(ex.getMessage().contains("已停用"));
    }

    @Test
    void resolvePayee_whenBelongInfoMissing_throwParamError() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resolvePayee(0L, null, null));
        assertTrue(ex.getMessage().contains("归属信息缺失"));
    }
}
