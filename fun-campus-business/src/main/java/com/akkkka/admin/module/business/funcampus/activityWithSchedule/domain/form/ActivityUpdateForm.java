package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-04-18 14:24
 */
@Data
public class ActivityUpdateForm extends ActivityAddForm{
    private Long id;

    public static ActivityEntity convert(ActivityUpdateForm updateForm){
        ActivityEntity e = new ActivityEntity();
        e.setId(updateForm.getId());
        e.setTitle(updateForm.getTitle());
        e.setStatus(null);
        e.setPosition(updateForm.getPosition());
        e.setScoreCanGet(updateForm.getScoreCanGet());
        e.setEnrollNumLimit(updateForm.getEnrollNumLimit());
        e.setActivityBelongToSchoolId(null);
        e.setActivityBelongToOrganizationId(null);
        e.setActivityBelongToCollegeId(null);
        e.setDeletedFlag(false);
        e.setCreateTime(null);
        e.setUpdateTime(LocalDateTime.now());
        e.setDescription(updateForm.getDescription());
        e.setEnrollNeedReview(updateForm.getEnrollNeedReview());
        e.setNeedSignOut(updateForm.getNeedSignOut());
        e.setAttachment(updateForm.getAttachment());
        e.setCategoryId(updateForm.getCategoryId());
        e.setCoverImg(updateForm.getCoverImg());
        e.setActivityManagerId(updateForm.getActivityManagerId());

        return e;

    }
}
