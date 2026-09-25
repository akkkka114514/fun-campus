package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.converter;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundPolicy;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityAddForm;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-04-19 14:28
 */
public class ActivityAddFormConverter {
    public static ActivityEntity convert(ActivityAddForm addForm){
        ActivityEntity activity = new ActivityEntity();
        activity.setId(null);
        activity.setTitle(addForm.getTitle());
        activity.setPosition(addForm.getPosition());
        activity.setScoreCanGet(addForm.getScoreCanGet());
        activity.setEnrollNumLimit(addForm.getEnrollNumLimit());
        activity.setActivityBelongToSchoolId(addForm.getActivityBelongToSchoolId());
        activity.setActivityBelongToOrganizationId(addForm.getActivityBelongToOrganizationId());
        activity.setActivityBelongToCollegeId(addForm.getActivityBelongToCollegeId());
        activity.setDeletedFlag(false);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        activity.setDescription(addForm.getDescription());
        activity.setEnrollNeedReview(addForm.getEnrollNeedReview());
        activity.setNeedSignOut(addForm.getNeedSignOut());
        activity.setAttachment(addForm.getAttachment());
        activity.setCategoryId(addForm.getCategoryId());
        activity.setCoverImg(addForm.getCoverImg());
        activity.setActivityManagerId(addForm.getActivityManagerId());
        // 付费信息：付费活动未配置退款政策时，默认「报名截止前可退」
        activity.setPaidFlag(addForm.getPaidFlag());
        activity.setPriceFen(addForm.getPriceFen());
        RefundPolicy refundPolicy = RefundPolicy.fromCode(addForm.getRefundPolicy());
        if (refundPolicy == null && Boolean.TRUE.equals(addForm.getPaidFlag())) {
            refundPolicy = RefundPolicy.BEFORE_ENROLL_END;
        }
        activity.setRefundPolicy(refundPolicy);

        return activity;
    }
}
