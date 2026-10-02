package com.akkkka.admin.module.business.funcampus.payment.manager;

import com.akkkka.admin.module.business.funcampus.payment.constant.PayeeOwnerType;
import com.akkkka.admin.module.business.funcampus.payment.dao.PayeeAccountDao;
import com.akkkka.admin.module.business.funcampus.payment.domain.entity.PayeeAccountEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 收款账户 Manager
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Service
public class PayeeAccountManager extends ServiceImpl<PayeeAccountDao, PayeeAccountEntity> {

    /**
     * 按主体查询收款账户（未删除）
     */
    public PayeeAccountEntity getByOwner(PayeeOwnerType ownerType, Long ownerId) {
        return this.getOne(
                new LambdaQueryWrapper<PayeeAccountEntity>()
                        .eq(PayeeAccountEntity::getOwnerType, ownerType)
                        .eq(PayeeAccountEntity::getOwnerId, ownerId)
                        .eq(PayeeAccountEntity::getDeletedFlag, false)
        );
    }
}
