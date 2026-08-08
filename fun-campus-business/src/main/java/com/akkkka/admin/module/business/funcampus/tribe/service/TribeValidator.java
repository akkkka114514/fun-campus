package com.akkkka.admin.module.business.funcampus.tribe.service;

import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-04-23 14:22
 */
@Component
@AllArgsConstructor
public class TribeValidator {
    private final TribeManager tribeManager;
    public void validateTribeIds(List<Long> ids){
        //如果部落不为空，为按部落进行参与
        //检查输入的部落是否存在
        if(ids!=null && !ids.isEmpty()){
            if(tribeManager
                    .getBaseMapper()
                    .selectByIds(ids)
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()!=ids.size()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"提交的年级里有部落不存在");
            }
        }
    }
}
