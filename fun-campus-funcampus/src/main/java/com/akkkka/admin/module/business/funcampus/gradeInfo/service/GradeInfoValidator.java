package com.akkkka.admin.module.business.funcampus.gradeInfo.service;

import com.akkkka.admin.module.business.funcampus.gradeInfo.manager.GradeInfoManager;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-04-23 14:26
 */
@Component
@AllArgsConstructor
public class GradeInfoValidator {
    private final GradeInfoManager gradeInfoManager;

    public void validateGradeIds(List<Long> ids){
        //如果院系年级不为空，为按院系年级进行参与
        //检查输入的院系年级是否存在
        if(ids!=null && !ids.isEmpty()){
            if(gradeInfoManager
                    .getBaseMapper()
                    .selectByIds(ids)
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()
                    != ids.size()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"提交的年级里有年级不存在");
            }
        }
    }
}
