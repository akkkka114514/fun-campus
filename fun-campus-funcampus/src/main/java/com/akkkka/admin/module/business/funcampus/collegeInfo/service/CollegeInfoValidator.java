package com.akkkka.admin.module.business.funcampus.collegeInfo.service;

import com.akkkka.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-04-23 14:23
 */
@Component
@AllArgsConstructor
public class CollegeInfoValidator {
    private final CollegeInfoManager collegeInfoManager;
    public void validateCollegeIds(List<Long> ids){
        if(ids!=null && !ids.isEmpty()){
            if(collegeInfoManager.getBaseMapper()
                    .selectByIds(ids)
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()
                    != ids.size()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"提交的学院里有学院不存在");
            }
        }
    }

    public void validateCollegeId(Long id){
        collegeInfoManager.getOptById(id)
                .filter(e->!e.getDeletedFlag())
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR));
    }
    //校验逻辑外键
    public void validateUserCollege(PortalUserEntity portalUser,Long collegeId){
        if(!portalUser.getCollegeId().equals(collegeId)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
    }
}
