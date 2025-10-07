package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEnrollNum;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.entity.OrganizerActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.manager.OrganizerActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.entity.PortalOrganizerUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.manager.PortalOrganizerUserManager;
import net.lab1024.sa.base.common.code.UnexpectedErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * 活动和时间表 组合服务
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */

@Service
public class ActivityWithScheduleService {

    @Resource
    private ActivityManager activityManager;

    @Resource
    private ActivityScheduleManager activityScheduleManager;
    
    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ActivityDao activityDao;

    @Resource
    private PortalOrganizerUserManager portalOrganizerUserManager;

    @Resource
    private OrganizerActivityManager organizerActivityManager;

    @Resource
    private ActivityEnrollNumDao activityEnrollNumDao;

    /**
     * 同时添加活动和活动时间表
     *
     * @param addForm 活动和时间表信息
     * @return ResponseDTO
     */
    public ResponseDTO<String> addActivityWithSchedule(ActivityWithScheduleAddForm addForm) {
        //检查提交activity的organizer user是否存在
        LambdaQueryWrapper<PortalOrganizerUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PortalOrganizerUserEntity::getId, addForm.getActivityOrganizerId())
                .eq(PortalOrganizerUserEntity::getDeletedFlag, false);
        PortalOrganizerUserEntity organizerUserEntity = portalOrganizerUserManager.getOne(queryWrapper);
        if(organizerUserEntity == null){
            return ResponseDTO.userErrorParam("组织者用户不存在");
        }
        //未删除和未完全结束的活动中不能有和添加活动标题一致的
        LambdaQueryWrapper<ActivityEntity> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(ActivityEntity::getTitle, addForm.getTitle())
                .eq(ActivityEntity::getDeletedFlag, false)
                .or()
                .ne(ActivityEntity::getStatus, ActivityStatus.END_SIGNIN);
        if(activityManager.getOne(queryWrapper2)!= null){
            return ResponseDTO.userErrorParam("已存在相同标题的活动");
        }
        //要插入的activity
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setId(null);
        activityEntity.setTitle(addForm.getTitle());
        activityEntity.setStatus(ActivityStatus.NOT_START_ENROLL);
        activityEntity.setPosition(addForm.getPosition());
        activityEntity.setScoreCanGet(addForm.getScoreCanGet());
        activityEntity.setEnrollNumLimit(addForm.getEnrollNumLimit());
        activityEntity.setActivitySchoolId(organizerUserEntity.getSchoolId());
        activityEntity.setActivityOrganizerId(addForm.getActivityOrganizerId());
        activityEntity.setDeletedFlag(false);

        //要插入的activity时间表
        ActivityScheduleEntity scheduleEntity = new ActivityScheduleEntity();
        scheduleEntity.setActivityId(null);
        scheduleEntity.setEnrollStartTime(addForm.getEnrollStartTime());
        scheduleEntity.setEnrollEndTime(addForm.getEnrollEndTime());
        scheduleEntity.setActivityStartTime(addForm.getActivityStartTime());
        scheduleEntity.setActivityEndTime(addForm.getActivityEndTime());
        scheduleEntity.setSigninStartTime(addForm.getSigninStartTime());
        scheduleEntity.setSigninEndTime(addForm.getSigninEndTime());
        scheduleEntity.setDeletedFlag(false);

        //要插入的organizerActivity
        OrganizerActivityEntity organizerActivityEntity = new OrganizerActivityEntity();
        organizerActivityEntity.setActivityId(null);
        organizerActivityEntity.setOrganizerId(addForm.getActivityOrganizerId());
        organizerActivityEntity.setDeletedFlag(false);
        //要插入的activityEnrollNum
        ActivityEnrollNum activityEnrollNum = new ActivityEnrollNum();
        activityEnrollNum.setActivityId(activityEntity.getId());
        activityEnrollNum.setEnrollNum(0);
        //开始事务
        return transactionTemplate.execute(status -> {
            try {
                //保存activity和activity时间表
                synchronized (this) {
                    if (!activityManager.save(activityEntity) ||
                        !activityScheduleManager.save(scheduleEntity) ||
                        activityEnrollNumDao.insert(activityEnrollNum)==0) {
                        status.setRollbackOnly();
                        return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "保存失败");
                    }
                }
                //保存organizerActivity
                organizerActivityEntity.setActivityId(activityEntity.getId());
                if(!organizerActivityManager.save(organizerActivityEntity)) {
                    status.setRollbackOnly();
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "保存失败");
                }

                return ResponseDTO.ok("保存成功");
            } catch (Exception e) {
                status.setRollbackOnly();
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "保存过程中发生异常：" + e.getMessage());
            }
        });
    }

    public ResponseDTO<String> deleteActivityWithSchedule(Long activityId) {
        ActivityEntity deletedActivity = new ActivityEntity();
        deletedActivity.setDeletedFlag(true);
        deletedActivity.setId(activityId);

        ActivityScheduleEntity deletedSchedule = new ActivityScheduleEntity();
        deletedSchedule.setActivityId(activityId);
        deletedSchedule.setDeletedFlag(true);

        OrganizerActivityEntity deletedOrganizerActivity = new OrganizerActivityEntity();
        ActivityEntity activityEntity = activityManager.getById(activityId);
        if(activityEntity==null){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        deletedOrganizerActivity.setOrganizerId(activityEntity.getActivityOrganizerId());
        deletedOrganizerActivity.setActivityId(activityId);
        deletedOrganizerActivity.setDeletedFlag(true);

        return transactionTemplate.execute(status -> {
            boolean activityUpdated = activityManager.updateById(deletedActivity);
            boolean scheduleUpdated = activityScheduleManager.updateById(deletedSchedule);
            
            if (!activityUpdated || !scheduleUpdated) {
                status.setRollbackOnly();
                return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "删除失败");
            }
            return ResponseDTO.ok("删除成功");
        });
    }


    public ResponseDTO<String> updateActivityWithSchedule(ActivityWithScheduleUpdateForm updateForm) {
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setId(updateForm.getId());
        activityEntity.setTitle(updateForm.getTitle());
        activityEntity.setStatus(null);
        activityEntity.setPosition(updateForm.getPosition());
        activityEntity.setScoreCanGet(updateForm.getScoreCanGet());
        activityEntity.setEnrollNumLimit(updateForm.getEnrollNumLimit());
        activityEntity.setActivityOrganizerId(updateForm.getActivityOrganizerId());
        if(portalOrganizerUserManager.getById(updateForm.getActivityOrganizerId())==null){
            return ResponseDTO.userErrorParam("组织者不存在");
        }
        activityEntity.setActivitySchoolId(updateForm.getActivitySchoolId());
        activityEntity.setDeletedFlag(false);

        ActivityScheduleEntity scheduleEntity = new ActivityScheduleEntity();
        scheduleEntity.setActivityId(updateForm.getId());
        scheduleEntity.setEnrollStartTime(updateForm.getEnrollStartTime());
        scheduleEntity.setEnrollEndTime(updateForm.getEnrollEndTime());
        scheduleEntity.setActivityStartTime(updateForm.getActivityStartTime());
        scheduleEntity.setActivityEndTime(updateForm.getActivityEndTime());
        scheduleEntity.setSigninStartTime(updateForm.getSigninStartTime());
        scheduleEntity.setSigninEndTime(updateForm.getSigninEndTime());
        scheduleEntity.setDeletedFlag(false);
        return transactionTemplate.execute(status -> {
            boolean activityUpdated = activityManager.updateById(activityEntity);
            boolean scheduleUpdated = activityScheduleManager.updateById(scheduleEntity);
            
            if (!activityUpdated || !scheduleUpdated) {
                status.setRollbackOnly();
                return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "更新失败");
            }
            return ResponseDTO.ok("更新成功");
        });
    }
    /**
    * <p>
    * description: 获取活动和时间表
    * </p>
    *
    * @param queryForm
    * @return:
    * @author: akkkka114514
    * @date: 16:10:35 2025-09-18
    */

    public ResponseDTO<PageResult<ActivityWithScheduleVO>> queryActivityWithSchedule(ActivityWithScheduleQueryForm queryForm){
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityWithScheduleVO> resultList = activityDao.queryActivityWithSchedule(page, queryForm);
        PageResult<ActivityWithScheduleVO> pageResult = SmartPageUtil.convert2PageResult(page, resultList);
        return ResponseDTO.ok(pageResult);
    }

    public ResponseDTO<String> batchDelete(List<Integer> ids){
        return activityDao.batchDelete(ids) > 0 ?
                ResponseDTO.ok() : ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "删除失败");
    }
}