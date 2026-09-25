package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;

import lombok.AllArgsConstructor;

/**
 * 活动和时间表 新建表单校验器
 *
 * @Author akkkka114514
 * @Date 2026-08-10
 * @Copyright akkkka114514
 */
@Component
@AllArgsConstructor
public class ActivityWithScheduleAddFormValidator {

    private final ActivityValidator activityValidator;
    private final ActivityScheduleValidator activityScheduleValidator;

    public void validate(ActivityWithScheduleAddForm addForm) {
        if (Objects.isNull(addForm)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "表单不能为空");
        }
        if (Objects.isNull(addForm.getActivityAddForm())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动信息不能为空");
        }
        if (Objects.isNull(addForm.getActivityScheduleAddForm())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动时间表不能为空");
        }
        // 报名范围至少选择一个（学院/年级/社团）
        boolean hasCollege = addForm.getCanEnrollCollegeIdList() != null && !addForm.getCanEnrollCollegeIdList().isEmpty();
        boolean hasGrade = addForm.getCanEnrollGradeIdList() != null && !addForm.getCanEnrollGradeIdList().isEmpty();
        boolean hasTribe = addForm.getCanEnrollTribeIdList() != null && !addForm.getCanEnrollTribeIdList().isEmpty();
        if (!hasCollege && !hasGrade && !hasTribe) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "报名范围至少需要选择学院、年级或社团之一");
        }
    }
}
