package com.akkkka.admin.module.system.backendUser.service;

import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 关于BackendUser的校验类
 * author:akkkka114514
 * create at 2026-04-27 14:49
 */
@Component
@AllArgsConstructor
public class BackendUserValidator {
    private final BackendUserManager backendUserManager;

    /*
    * 检查这个后端用户是否在数据库中
    */
    public BackendUserEntity validateBackendUserId(Long id){
        BackendUserEntity backendUser = backendUserManager.getById(id);
        if(backendUser==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        return backendUser;
    }
    /*
    * 检查当前用户是否为后端用户,检查通过返回userid
    */
    public Long validateRequestUserIsBackend(){
        RequestUser requestUser=SmartRequestUtil.getRequestUser();
        if(! (requestUser instanceof RequestBackendUser)){
            throw new BusinessException(UserErrorCode.NO_PERMISSION);
        }
        return requestUser.getUserId();
    }
}
