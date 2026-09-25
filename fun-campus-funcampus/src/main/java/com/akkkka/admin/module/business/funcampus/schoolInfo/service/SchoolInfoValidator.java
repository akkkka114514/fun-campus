package com.akkkka.admin.module.business.funcampus.schoolInfo.service;

import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * author:akkkka114514
 * create at 2026-04-24 14:02
 */
@Component
@AllArgsConstructor
public class SchoolInfoValidator {
    private final SchoolInfoManager schoolInfoManager;

    public void validateSchoolId(Long id){
        schoolInfoManager.getOptById(id)
                .filter(e->!e.getDeletedFlag())
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR));
    }
    public void validateUserSchool(PortalUserEntity portalUser, Long schoolId){
        if(!portalUser.getSchoolId().equals(schoolId)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
    }
}
