package com.akkkka.admin.module.business.funcampus.organizationInfo.service;

import com.akkkka.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * author:akkkka114514
 * create at 2026-04-23 15:46
 */
@Component
@AllArgsConstructor
public class OrganizationInfoValidator {
    private final OrganizationInfoManager organizationInfoManager;
    public void validateOrganizationId(Long id){
        organizationInfoManager.getOptById(id)
                .filter(e->!e.getDeletedFlag())
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR));
    }
    //校验逻辑外键
    public void validateUserOrganization(PortalUserEntity portalUser, Long organizationId){
        if(!portalUser.getOrganizationId().equals(organizationId)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
    }
}
