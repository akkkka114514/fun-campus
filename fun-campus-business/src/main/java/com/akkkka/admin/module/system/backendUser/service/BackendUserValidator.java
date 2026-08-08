package com.akkkka.admin.module.system.backendUser.service;

import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * author:akkkka114514
 * create at 2026-04-27 14:49
 */
@Component
@AllArgsConstructor
public class BackendUserValidator {
    private final BackendUserManager backendUserManager;

    public BackendUserEntity validateBackendUserId(Long id){
        BackendUserEntity backendUser = backendUserManager.getById(id);
        if(backendUser==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        return backendUser;
    }
}
