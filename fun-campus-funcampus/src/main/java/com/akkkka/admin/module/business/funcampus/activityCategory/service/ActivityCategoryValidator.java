package com.akkkka.admin.module.business.funcampus.activityCategory.service;

import com.akkkka.admin.module.business.funcampus.activityCategory.manager.ActivityCategoryManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * author:akkkka114514
 * create at 2026-04-24 14:50
 */
@Component
@AllArgsConstructor
public class ActivityCategoryValidator {
    private final ActivityCategoryManager categoryManager;
    public void validateActivityCategory(Long categoryId){
        categoryManager.getOptById(categoryId)
                .filter((cate)->!cate.getDeletedFlag())
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"活动分类不存在"));
    }

}
