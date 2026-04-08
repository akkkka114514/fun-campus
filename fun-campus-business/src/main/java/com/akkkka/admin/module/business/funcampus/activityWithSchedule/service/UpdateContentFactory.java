package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;

/**
 * author:akkkka114514
 * create at 2026-02-28 15:45
 */
public class UpdateContentFactory {
    public static ActivityEntity buildActivity(ActivityWithScheduleUpdateForm updateForm){
        ActivityEntity activityEntity=new ActivityEntity();
        activityEntity.setId(updateForm.getId());
        activityEntity.setTitle(updateForm.getTitle());
        activityEntity.setStatus(null);
        activityEntity.setPosition(updateForm.getPosition());
        activityEntity.setScoreCanGet(updateForm.getScoreCanGet());
        activityEntity.setEnrollNumLimit(updateForm.getEnrollNumLimit());
        activityEntity.setActivityBelongToSchoolId(null);
        activityEntity.setActivityBelongToOrganizationId(updateForm.getActivityBelongToOrganizationId());
        activityEntity.setActivityBelongToCollegeId(updateForm.getActivityBelongToCollegeId());
        activityEntity.setDeletedFlag(false);
        activityEntity.setCreateTime(null);
        activityEntity.setUpdateTime(LocalDateTime.now());
        activityEntity.setDescription(updateForm.getDescription());
        activityEntity.setEnrollNeedReview(updateForm.isEnrollNeedReview());
        activityEntity.setNeedSignOut(updateForm.isNeedSignOut());
        activityEntity.setAttachment(updateForm.getAttachment());
        activityEntity.setCategoryId(updateForm.getCategoryId());
        activityEntity.setCoverImg(updateForm.getCoverImg());
        activityEntity.setActivityManagerId(updateForm.getActivityManagerId());

        return activityEntity;
    }

    public static ActivityScheduleEntity buildActivitySchedule(ActivityWithScheduleUpdateForm updateForm){
        ActivityScheduleEntity activityScheduleEntity = new ActivityScheduleEntity();
        activityScheduleEntity.setActivityId(updateForm.getId());
        activityScheduleEntity.setEnrollStartTime(updateForm.getEnrollStartTime());
        activityScheduleEntity.setEnrollEndTime(updateForm.getEnrollEndTime());
        activityScheduleEntity.setActivityStartTime(updateForm.getActivityStartTime());
        activityScheduleEntity.setActivityEndTime(updateForm.getActivityEndTime());
        activityScheduleEntity.setSigninStartTime(updateForm.getSigninStartTime());
        activityScheduleEntity.setSigninEndTime(updateForm.getSigninEndTime());
        activityScheduleEntity.setSignoutStartTime(updateForm.getSignoutStartTime());
        activityScheduleEntity.setSignoutEndTime(updateForm.getSignoutEndTime());
        activityScheduleEntity.setCreateTime(null);
        activityScheduleEntity.setUpdateTime(LocalDateTime.now());
        activityScheduleEntity.setDeletedFlag(false);
        return activityScheduleEntity;
    }

    public static List<ActivityCanEnrollTribeEntity> buildCanEnrollTribe(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getCanEnrollTribeIdList()==null){
            return null;
        }
        List<ActivityCanEnrollTribeEntity> tribeList=new ArrayList<>();
        updateForm.getCanEnrollTribeIdList().forEach((id)->{
            ActivityCanEnrollTribeEntity tribe = new ActivityCanEnrollTribeEntity();
            tribe.setId(null);
            tribe.setCanEnrollTribe(id);
            tribe.setCreateTime(LocalDateTime.now());
            tribe.setUpdateTime(LocalDateTime.now());
            tribe.setDeletedFlag(false);

            tribeList.add(tribe);
        });
        return tribeList;
    }
    public static List<ActivityCanEnrollCollegeEntity> buildCanEnrollCollege(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getCanEnrollCollegeIdList()==null){
            return null;
        }
        List<ActivityCanEnrollCollegeEntity> collegeList=new ArrayList<>();
        updateForm.getCanEnrollCollegeIdList().forEach(id-> {
            ActivityCanEnrollCollegeEntity college = new ActivityCanEnrollCollegeEntity();
            college.setId(null);
            college.setCanEnrollCollege(id);
            college.setDeletedFlag(false);
            college.setCreateTime(LocalDateTime.now());
            college.setUpdateTime(LocalDateTime.now());
            collegeList.add(college);
        });
        return collegeList;
    }

    public static List<ActivityCanEnrollGradeEntity> buildCanEnrollGrade(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getCanEnrollGradeIdList()==null){
            return null;
        }
        List<ActivityCanEnrollGradeEntity> gradeList=new ArrayList<>();
        updateForm.getCanEnrollGradeIdList().forEach((id)-> {
            ActivityCanEnrollGradeEntity grade = new ActivityCanEnrollGradeEntity();
            grade.setId(null);
            grade.setCanEnrollGrade(id);
            grade.setDeletedFlag(false);
            grade.setCreateTime(LocalDateTime.now());
            grade.setUpdateTime(LocalDateTime.now());
            gradeList.add(grade);
        });
        return gradeList;
    }
    public static ActivityReviewLogEntity buildReviewLog(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getInitialReviewer()==null){
            return null;
        }
        ActivityReviewLogEntity activityReviewLog=new ActivityReviewLogEntity();
        activityReviewLog.setId(null);
        activityReviewLog.setReviewerId(updateForm.getInitialReviewer());
        activityReviewLog.setReviewerName(updateForm.getInitialReviewerName());
        activityReviewLog.setReviewStage(ActivityReviewStage.INITIAL_REVIEW);
        activityReviewLog.setCreateTime(LocalDateTime.now());
        return activityReviewLog;
    }
    public static List<ActivitySigninManagerEntity> buildSigninManagerList(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getActivitySigninManagerIdList()==null){
            return null;
        }
        List<ActivitySigninManagerEntity> result = new ArrayList<>();
        PortalUserManager portalUserManager=new PortalUserManager();

        updateForm.getActivitySigninManagerIdList().forEach(id -> {
            ActivitySigninManagerEntity signinManager=new ActivitySigninManagerEntity();
            signinManager.setId(null);
            signinManager.setActivityId(updateForm.getId());
            signinManager.setPortalUserId(id);
            signinManager.setDeletedFlag(false);
            signinManager.setCreateTime(LocalDateTime.now());
            signinManager.setUpdateTime(LocalDateTime.now());
            signinManager.setUsername(portalUserManager.getById(id).getUsername());

            result.add(signinManager);
        });
        return result;
    }
}
