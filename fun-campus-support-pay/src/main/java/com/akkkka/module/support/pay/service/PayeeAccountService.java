package com.akkkka.module.support.pay.service;

import org.springframework.stereotype.Service;

import com.akkkka.module.support.pay.constant.PayeeAccountStatus;
import com.akkkka.module.support.pay.constant.PayeeOwnerType;
import com.akkkka.module.support.pay.domain.entity.PayeeAccountEntity;
import com.akkkka.module.support.pay.manager.PayeeAccountManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 收款账户 Service（收款方解析：活动归属 → 收款商户号）
 * <p>
 * 与真实渠道对齐的「多主体收款」语义：活动由哪个主体举办（组织/学院/学校），
 * 报名费就收进哪个主体的收款账户（渠道侧表现为商户号 mch_id）：
 * - 解析优先级：组织 → 学院 → 学校（组织最具体，兼容历史数据中两者同时存在的情况）；
 * - 活动归属主体未开户、或账户已停用时拒绝收费（不能把钱收错主体）；
 * - 下单时快照收款商户号与名称落单，退款按原收款商户原路退回
 * <p>
 * 入参为活动三归属字段而非活动实体：调用方从活动读取后传入，支付模块不依赖活动实体
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class PayeeAccountService {

    private final PayeeAccountManager payeeAccountManager;

    /**
     * 按活动归属解析收款账户
     *
     * @param organizationId 活动归属组织id（最具体，优先）
     * @param collegeId      活动归属学院id
     * @param schoolId       活动归属学校id
     * @return 收款账户（启用状态）
     * @throws BusinessException 归属主体未开户或账户停用时抛出（拒绝下单）
     */
    public PayeeAccountEntity resolvePayee(Long organizationId, Long collegeId, Long schoolId) {
        if (organizationId != null && organizationId > 0) {
            return requireEnabled(PayeeOwnerType.ORGANIZATION, organizationId);
        }
        if (collegeId != null && collegeId > 0) {
            return requireEnabled(PayeeOwnerType.COLLEGE, collegeId);
        }
        if (schoolId != null && schoolId > 0) {
            return requireEnabled(PayeeOwnerType.SCHOOL, schoolId);
        }
        // 归属信息缺失：收款方无从确定，拒绝下单
        throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动归属信息缺失，无法确定收款方");
    }

    /**
     * 校验主体收款账户存在且启用
     */
    private PayeeAccountEntity requireEnabled(PayeeOwnerType ownerType, Long ownerId) {
        PayeeAccountEntity account = payeeAccountManager.getByOwner(ownerType, ownerId);
        if (account == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR,
                    "该活动收款方（" + ownerType.getLabel() + "）未开通收款账户，暂无法收费");
        }
        if (account.getStatus() != PayeeAccountStatus.ENABLED) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR,
                    "该活动收款方（" + ownerType.getLabel() + "）收款账户已停用，暂无法收费");
        }
        return account;
    }
}
