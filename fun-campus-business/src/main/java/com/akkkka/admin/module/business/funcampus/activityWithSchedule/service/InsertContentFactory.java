package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import jakarta.annotation.Resource;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEnrollNum;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-02-22 13:21
 */
@Component
public class InsertContentFactory {
    @Resource
    private PortalUserManager portalUserManager;
    public ActivityEnrollNum buildEnrollNum(){
        //要插入的activityEnrollNum
        ActivityEnrollNum activityEnrollNum = new ActivityEnrollNum();
        activityEnrollNum.setActivityId(null);
        activityEnrollNum.setEnrollNum(0);
        return activityEnrollNum;
    }

    public List<ActivityCanEnrollTribeEntity> buildCanEnrollTribe(ActivityWithScheduleAddForm addForm){
        List<ActivityCanEnrollTribeEntity> tribeList=new ArrayList<>();
        addForm.getCanEnrollTribeIdList().forEach((id)->{
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

    public List<ActivityCanEnrollCollegeEntity> buildCanEnrollCollege(ActivityWithScheduleAddForm addForm){
        List<ActivityCanEnrollCollegeEntity> collegeList=new ArrayList<>();
        addForm.getCanEnrollCollegeIdList().forEach(id-> {
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

    public List<ActivityCanEnrollGradeEntity> buildCanEnrollGrade(ActivityWithScheduleAddForm addForm){
        List<ActivityCanEnrollGradeEntity> gradeList=new ArrayList<>();
        addForm.getCanEnrollGradeIdList().forEach((id)-> {
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

    public List<ActivitySigninManagerEntity> buildSigninManagerList(ActivityWithScheduleAddForm addForm){
        List<ActivitySigninManagerEntity> result = new ArrayList<>();

        addForm.getActivitySigninManagerIdList().forEach(id -> {
            ActivitySigninManagerEntity signinManager=new ActivitySigninManagerEntity();
            signinManager.setId(null);
            //缺少activityId，等会在事务里补上
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
