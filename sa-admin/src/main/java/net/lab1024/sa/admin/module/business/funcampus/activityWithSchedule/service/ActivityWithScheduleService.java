package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.manager.ActivityCanEnrollCollegeManager;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.manager.ActivityCanEnrollGradeManager;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.manager.ActivityCanEnrollTribeManager;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.manager.ActivityCategoryManager;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
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
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.manager.GradeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.tribe.manager.TribeManager;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.manager.BackendUserManager;
import net.lab1024.sa.base.common.code.UnexpectedErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.exception.DangerousUserException;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 活动和时间表 组合服务
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */
@Slf4j
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
    private ActivityEnrollNumDao activityEnrollNumDao;

    @Resource
    private PortalUserManager portalUserManager;

    @Resource
    private SchoolInfoManager schoolInfoManager;

    @Resource
    private CollegeInfoManager collegeInfoManager;

    @Resource
    private OrganizationInfoManager organizationInfoManager;

    @Resource
    private ActivityCategoryManager activityCategoryManager;

    @Resource
    private TribeManager tribeManager;

    @Resource
    private GradeInfoManager gradeInfoManager;

    @Resource
    private BackendUserManager backendUserManager;

    @Resource
    private ActivityCanEnrollTribeManager activityCanEnrollTribeManager;

    @Resource
    private ActivityCanEnrollGradeManager activityCanEnrollGradeManager;

    @Resource
    private ActivityCanEnrollCollegeManager activityCanEnrollCollegeManager;

    @Resource
    private ActivityReviewLogManager activityReviewLogManager;

    public void addNotReviewedOne(ActivityWithScheduleAddForm addForm) {
        log.info("添加待审核的活动：{}", addForm.toString());

        //不能提交空列表
        if(addForm.getCanEnrollTribeIdList().isEmpty()
                ||addForm.getCanEnrollGradeIdList().isEmpty()
                ||addForm.getCanEnrollCollegeIdList().isEmpty()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }


        //检查用户是否有发布活动的权限
        Long userId = SmartRequestUtil.getRequestUserId();
        String ip = SmartRequestUtil.getRequestUser().getIp();
        PortalUserEntity portalUser=portalUserManager.getOptById(userId)
                .filter(portalUserEntity -> !portalUserEntity.getDeletedFlag())
                .filter(portalUserEntity -> !portalUserEntity.getDisableFlag())
                .orElseThrow(()->new BusinessException(UserErrorCode.USER_STATUS_ERROR));

        if (!portalUser.isCanPublishActivity()) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION);
        }

        //报名开始时间《报名结束时间《活动开始时间《活动结束时间《签到开始时间《签到结束时间
        if(!(
                addForm.getEnrollStartTime().isBefore(addForm.getEnrollEndTime())
                        &&addForm.getEnrollEndTime().isBefore(addForm.getActivityStartTime())
                        &&addForm.getActivityStartTime().isBefore(addForm.getActivityEndTime())
                        &&addForm.getActivityEndTime().isBefore(addForm.getSigninStartTime())
                        &&addForm.getSigninStartTime().isBefore(addForm.getSigninEndTime())
        )
        ){
            throw new DangerousUserException(userId,ip,
                    "活动时间不按顺序",
                    DangerousUserException.System.PORTAL);
        }

        if((addForm.getCanEnrollGradeIdList()==null&&addForm.getCanEnrollCollegeIdList()!=null)||
                addForm.getCanEnrollGradeIdList()!=null&&addForm.getCanEnrollCollegeIdList()==null){
            throw new DangerousUserException(userId,ip,
                    "表单规定必须同时为空或不为空",
                    DangerousUserException.System.PORTAL);
        }
        if((addForm.getCanEnrollCollegeIdList()==null&&addForm.getCanEnrollTribeIdList()==null)||
                addForm.getCanEnrollCollegeIdList()!=null&&addForm.getCanEnrollTribeIdList()!=null){
            throw new DangerousUserException(userId,ip,
                    "表单规定不能同时为空或同时不为空",
                    DangerousUserException.System.PORTAL);
        }

        //activityBelongToCollegeId和activityBelongToOrganizationId不能同时为空或同时不为空
        if((addForm.getActivityBelongToCollegeId() != null && addForm.getActivityBelongToOrganizationId() != null)||
            addForm.getActivityBelongToCollegeId() == null && addForm.getActivityBelongToOrganizationId() == null){
            throw new DangerousUserException(
                    userId,ip,
                    "activityBelongToCollegeId和activityBelongToOrganizationId不能同时为空或同时不为空",
                    DangerousUserException.System.PORTAL);
        }
        //检查活动所属学校是否存在
        schoolInfoManager.getOptById(addForm.getActivityBelongToSchoolId())
                .filter((school)->!school.getDeletedFlag())
                .filter(school->school.getId().equals(portalUser.getSchoolId()))
                .orElseThrow(()->new DangerousUserException(
                        userId,ip,
                        "活动所属学校不存在",
                        DangerousUserException.System.PORTAL
                ));

        //如果活动所属学院字段不为null,则说明是学院活动
        //检查学院是否存在,以及与表单中的活动所属学院id一致
        if(addForm.getActivityBelongToCollegeId() != null){
            collegeInfoManager.getOptById(addForm.getActivityBelongToCollegeId())
                    .filter((college)->!college.getDeletedFlag())
                    .filter((college)->college.getSchoolId().equals(portalUser.getSchoolId()))
                    .orElseThrow(()->new DangerousUserException(
                            userId,ip,
                            "添加活动传入的collegeId为错误信息",
                            DangerousUserException.System.PORTAL
                    ));;
        }
        //如果活动所属组织字段不为null,则说明是组织活动
        //检查组织是否存在,以及与表单中的活动所属组织id一致
        if(addForm.getActivityBelongToOrganizationId() != null){
            organizationInfoManager.getOptById(addForm.getActivityBelongToOrganizationId())
                    .filter((org)->!org.getDeletedFlag())
                    .filter((org)->org.getSchoolId().equals(portalUser.getSchoolId()))
                    .orElseThrow(()->new DangerousUserException(
                            userId,ip,
                            "organization不存在或organization的school与其他数据不一致"
                            ,DangerousUserException.System.PORTAL
                    ));
        }

        activityCategoryManager.getOptById(addForm.getCategoryId())
                                .filter((cate)->!cate.getDeletedFlag())
                                .orElseThrow(()->new DangerousUserException(
                                        userId,ip,
                                        "活动分类不存在",
                                        DangerousUserException.System.PORTAL
                                ));

        //如果院系年级不为空，为按院系年级进行参与
        //检查输入的院系年级是否存在
        if(addForm.getCanEnrollGradeIdList()!=null && !addForm.getCanEnrollGradeIdList().isEmpty()){
            if(gradeInfoManager
                    .getBaseMapper()
                    .selectByIds(addForm.getCanEnrollGradeIdList())
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()
                    != addForm.getCanEnrollGradeIdList().size()){
                throw new DangerousUserException(
                        userId,ip,
                        "提交的年级里有年级不存在",
                        DangerousUserException.System.PORTAL
                );
            }
        }

        if(addForm.getCanEnrollCollegeIdList()!=null && !addForm.getCanEnrollCollegeIdList().isEmpty()){
            if(collegeInfoManager.getBaseMapper()
                    .selectByIds(addForm.getCanEnrollCollegeIdList())
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()
                    != addForm.getCanEnrollCollegeIdList().size()
            ){
                throw new DangerousUserException(
                        userId,ip,
                        "提交的学院里有学院不存在",
                        DangerousUserException.System.PORTAL
                );
            }
        }


        //如果部落不为空，为按部落进行参与
        //检查输入的部落是否存在
        if(addForm.getCanEnrollTribeIdList()!=null && !addForm.getCanEnrollTribeIdList().isEmpty()){
            if(tribeManager
                    .getBaseMapper()
                    .selectByIds(addForm.getCanEnrollTribeIdList())
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()!=addForm.getCanEnrollTribeIdList().size()){
                throw new DangerousUserException(
                        userId,ip,
                        "提交的年级里有部落不存在",
                        DangerousUserException.System.PORTAL
                );
            }
        }

        //活动中不能有和添加活动标题一致的
        Optional.ofNullable(
                activityManager.getOne(
                        new LambdaQueryWrapper<ActivityEntity>()
                                .eq(ActivityEntity::getTitle,addForm.getTitle())
                )
        ).filter(e->!e.getDeletedFlag())
                .ifPresent((e)->{
                    throw new BusinessException(UserErrorCode.PARAM_ERROR,"包含此标题的活动已存在");
                }
        );

        if(!Objects.equals(userId, addForm.getActivityManagerId())) {
            //检查activityManager是否存在且有效
            Optional.ofNullable(portalUserManager.getById(addForm.getActivityManagerId()))
                    .filter(e -> !e.getDeletedFlag())
                    .filter(e -> !e.getDisableFlag())
                    .filter(e -> e.getSchoolId().equals(addForm.getActivityBelongToSchoolId()))
                    .orElseThrow(() -> new DangerousUserException(
                            userId, ip,
                            "活动管理员校验不正确",
                            DangerousUserException.System.PORTAL
                    ));
        }
        //检查initialReviewer是否存在且有效
        BackendUserEntity initialReviewer=Optional.ofNullable(backendUserManager.getById(addForm.getInitialReviewer()))
                .filter(e->!e.getDeletedFlag())
                .filter(e->!e.getDisabledFlag())
                .filter(e->e.getSchoolId().equals(portalUser.getSchoolId()))
                .orElseThrow(()->new DangerousUserException(
                        userId,ip,
                        "活动初审人校验不正确",
                        DangerousUserException.System.PORTAL
                ));

        //要插入的activity
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setId(null);
        activityEntity.setTitle(addForm.getTitle());
        activityEntity.setStatus(ActivityStatus.NOT_START_ENROLL);
        activityEntity.setPosition(addForm.getPosition());
        activityEntity.setScoreCanGet(addForm.getScoreCanGet());
        activityEntity.setEnrollNumLimit(addForm.getEnrollNumLimit());
        activityEntity.setActivityBelongToSchoolId(addForm.getActivityBelongToSchoolId());
        activityEntity.setActivityBelongToOrganizationId(addForm.getActivityBelongToOrganizationId());
        activityEntity.setActivityBelongToCollegeId(addForm.getActivityBelongToCollegeId());
        activityEntity.setDeletedFlag(false);
        activityEntity.setCreateTime(LocalDateTime.now());
        activityEntity.setUpdateTime(LocalDateTime.now());
        activityEntity.setDescription(addForm.getDescription());
        activityEntity.setEnrollNeedReview(addForm.isEnrollNeedReview());
        activityEntity.setNeedSignOut(addForm.isNeedSignOut());
        activityEntity.setAttachment(addForm.getAttachment());
        activityEntity.setCategoryId(addForm.getCategoryId());
        activityEntity.setCoverImg(addForm.getCoverImg());
        activityEntity.setActivityManagerId(addForm.getActivityManagerId());

        //要插入的activity时间表
        ActivityScheduleEntity scheduleEntity = new ActivityScheduleEntity();
        scheduleEntity.setEnrollStartTime(addForm.getEnrollStartTime());
        scheduleEntity.setEnrollEndTime(addForm.getEnrollEndTime());
        scheduleEntity.setActivityStartTime(addForm.getActivityStartTime());
        scheduleEntity.setActivityEndTime(addForm.getActivityEndTime());
        scheduleEntity.setSigninStartTime(addForm.getSigninStartTime());
        scheduleEntity.setSigninEndTime(addForm.getSigninEndTime());
        scheduleEntity.setDeletedFlag(false);
        scheduleEntity.setCreateTime(LocalDateTime.now());
        scheduleEntity.setUpdateTime(LocalDateTime.now());

        //要插入的activityEnrollNum
        ActivityEnrollNum activityEnrollNum = new ActivityEnrollNum();
        activityEnrollNum.setActivityId(null);
        activityEnrollNum.setEnrollNum(0);

        List<ActivityCanEnrollCollegeEntity> collegeList=new ArrayList<>();
        List<ActivityCanEnrollGradeEntity> gradeList=new ArrayList<>();
        if(!addForm.getCanEnrollCollegeIdList().isEmpty()&&!addForm.getCanEnrollGradeIdList().isEmpty()){
            addForm.getCanEnrollCollegeIdList().forEach(id-> {
                ActivityCanEnrollCollegeEntity college = new ActivityCanEnrollCollegeEntity();
                college.setId(null);
                college.setCanEnrollCollege(id);
                college.setDeletedFlag(false);
                college.setCreateTime(LocalDateTime.now());
                college.setUpdateTime(LocalDateTime.now());
                collegeList.add(college);
            });


            addForm.getCanEnrollGradeIdList().forEach((id)-> {
                ActivityCanEnrollGradeEntity grade = new ActivityCanEnrollGradeEntity();
                grade.setId(null);
                grade.setCanEnrollGrade(id);
                grade.setDeletedFlag(false);
                grade.setCreateTime(LocalDateTime.now());
                grade.setUpdateTime(LocalDateTime.now());
                gradeList.add(grade);
            });
        }

        List<ActivityCanEnrollTribeEntity> tribeList=new ArrayList<>();
        if(!addForm.getCanEnrollTribeIdList().isEmpty()){
            addForm.getCanEnrollTribeIdList().forEach((id)->{
                ActivityCanEnrollTribeEntity tribe = new ActivityCanEnrollTribeEntity();
                tribe.setId(null);
                tribe.setCanEnrollTribe(id);
                tribe.setCreateTime(LocalDateTime.now());
                tribe.setUpdateTime(LocalDateTime.now());
                tribe.setDeletedFlag(false);

                tribeList.add(tribe);
            });
        }

        ActivityReviewLogEntity activityReviewLog=new ActivityReviewLogEntity();
        activityReviewLog.setId(null);
        activityReviewLog.setReviewerId(addForm.getInitialReviewer());
        activityReviewLog.setReviewerName(initialReviewer.getUsername());
        activityReviewLog.setReviewStage(ActivityReviewStage.INITIAL_REVIEW);
        activityReviewLog.setCreateTime(LocalDateTime.now());

        //开始事务
        transactionTemplate.executeWithoutResult(status -> {
            try {
                if (!activityManager.save(activityEntity)) {
                    log.warn("创建未审核活动事务失败：{}，插入activityEntity失败", addForm.getTitle());
                    status.setRollbackOnly();
                }
                Long id = activityEntity.getId();

                scheduleEntity.setActivityId(id);
                activityEnrollNum.setActivityId(id);
                activityReviewLog.setActivityId(id);
                if(!collegeList.isEmpty()&&!gradeList.isEmpty()){
                    collegeList.forEach((e)->e.setActivityId(id));
                    gradeList.forEach(e->e.setActivityId(id));
                    if(!activityCanEnrollCollegeManager.saveBatch(collegeList)
                    ||!activityCanEnrollGradeManager.saveBatch(gradeList)){
                        log.warn("创建未审核活动事务失败：插入能报名的学院或能报名的年级失败：activityId={}",id);
                        status.setRollbackOnly();
                    }
                }
                if(!tribeList.isEmpty()){
                    tribeList.forEach(e->e.setActivityId(id));
                    if(!activityCanEnrollTribeManager.saveBatch(tribeList)){
                        log.warn("创建未审核活动事务失败：插入能报名的部落失败：activityId={}",id);
                        status.setRollbackOnly();
                    }
                }
                if(!activityReviewLogManager.save(activityReviewLog)){
                    log.warn("创建未审核活动事务失败：插入活动审核记录失败：activityId={}",id);
                }

                //保存activity和activity时间表
                if (!activityScheduleManager.save(scheduleEntity) ||
                        activityEnrollNumDao.insert(activityEnrollNum) == 0) {
                    log.error("ActivityWithScheduleService.addActivityWithSchedule failed: failed to save schedule or enrollNum, activityId={}", id);
                    status.setRollbackOnly();
                }

                log.info("ActivityWithScheduleService.addActivityWithSchedule success: activity created, activityId={}", id);
            } catch (Exception e) {
                log.error("ActivityWithScheduleService.addActivityWithSchedule failed: exception occurred, title={}", addForm.getTitle(), e);
                status.setRollbackOnly();
            }
        });
    }

    public ResponseDTO<String> deleteActivityWithSchedule(Long activityId) {
        log.info("ActivityWithScheduleService.deleteActivityWithSchedule called, activityId={}", activityId);
        
        ActivityEntity deletedActivity = new ActivityEntity();
        deletedActivity.setDeletedFlag(true);
        deletedActivity.setId(activityId);

        ActivityScheduleEntity deletedSchedule = new ActivityScheduleEntity();
        deletedSchedule.setActivityId(activityId);
        deletedSchedule.setDeletedFlag(true);

        ActivityEntity activityEntity = activityManager.getById(activityId);
        if(activityEntity==null){
            log.warn("ActivityWithScheduleService.deleteActivityWithSchedule failed: activity not found, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }

        return transactionTemplate.execute(status -> {
            boolean activityUpdated = activityManager.updateById(deletedActivity);
            boolean scheduleUpdated = activityScheduleManager.updateById(deletedSchedule);
            
            if (!activityUpdated || !scheduleUpdated) {
                log.error("ActivityWithScheduleService.deleteActivityWithSchedule failed: failed to update activity or schedule, activityId={}", activityId);
                status.setRollbackOnly();
                return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "删除失败");
            }
            log.info("ActivityWithScheduleService.deleteActivityWithSchedule success: activity deleted, activityId={}", activityId);
            return ResponseDTO.ok("删除成功");
        });
    }


    public ResponseDTO<String> updateActivityWithSchedule(ActivityWithScheduleUpdateForm updateForm) {
        log.info("ActivityWithScheduleService.updateActivityWithSchedule called, activityId={}", updateForm.getId());
        
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setId(updateForm.getId());
        activityEntity.setTitle(updateForm.getTitle());
        activityEntity.setStatus(null);
        activityEntity.setPosition(updateForm.getPosition());
        activityEntity.setScoreCanGet(updateForm.getScoreCanGet());
        activityEntity.setEnrollNumLimit(updateForm.getEnrollNumLimit());
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
                log.error("ActivityWithScheduleService.updateActivityWithSchedule failed: failed to update activity or schedule, activityId={}", updateForm.getId());
                status.setRollbackOnly();
                return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "更新失败");
            }
            log.info("ActivityWithScheduleService.updateActivityWithSchedule success: activity updated, activityId={}", updateForm.getId());
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
        log.info("ActivityWithScheduleService.queryActivityWithSchedule called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityWithScheduleVO> resultList = activityDao.queryActivityWithSchedule(page, queryForm);
        PageResult<ActivityWithScheduleVO> pageResult = SmartPageUtil.convert2PageResult(page, resultList);
        log.info("ActivityWithScheduleService.queryActivityWithSchedule result: count={}", resultList.size());
        return ResponseDTO.ok(pageResult);
    }

    public ResponseDTO<String> batchDelete(List<Long> ids){
        log.info("ActivityWithScheduleService.batchDelete called, idsCount={}", ids != null ? ids.size() : 0);
        int result = activityDao.batchDelete(ids);
        if (result > 0) {
            log.info("ActivityWithScheduleService.batchDelete success: deleted {} records", result);
            return ResponseDTO.ok();
        } else {
            log.warn("ActivityWithScheduleService.batchDelete failed: no records deleted");
            return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "删除失败");
        }
    }

    public ResponseDTO<String> publish(Long activityId){
        log.info("ActivityWithScheduleService.publish called, activityId={}", activityId);
        // TODO: 实现发布逻辑
        log.warn("ActivityWithScheduleService.publish not implemented");
        return ResponseDTO.ok("功能未实现");
    }
    public ResponseDTO<String> cancelPublish(Long activityId){
        log.info("ActivityWithScheduleService.cancelPublish called, activityId={}", activityId);
        // TODO: 实现取消发布逻辑
        log.warn("ActivityWithScheduleService.cancelPublish not implemented");
        return ResponseDTO.ok("功能未实现");
    }
    public ResponseDTO<String> passReview(Long activityId){
        log.info("ActivityWithScheduleService.passReview called, activityId={}", activityId);
        // TODO: 实现通过审核逻辑
        log.warn("ActivityWithScheduleService.passReview not implemented");
        return ResponseDTO.ok("功能未实现");
    }
    public ResponseDTO<String> batchPassReview(List<Long> activityIds){
        log.info("ActivityWithScheduleService.batchPassReview called, activityIdsCount={}", activityIds != null ? activityIds.size() : 0);
        // TODO: 实现批量通过审核逻辑
        log.warn("ActivityWithScheduleService.batchPassReview not implemented");
        return ResponseDTO.ok("功能未实现");
    }
    public ResponseDTO<String> rejectReview(Long activityId){
        log.info("ActivityWithScheduleService.rejectReview called, activityId={}", activityId);
        // TODO: 实现拒绝审核逻辑
        log.warn("ActivityWithScheduleService.rejectReview not implemented");
        return ResponseDTO.ok("功能未实现");
    }
    public ResponseDTO<String> batchRejectReview(List<Long> activityIds){
        log.info("ActivityWithScheduleService.batchRejectReview called, activityIdsCount={}", activityIds != null ? activityIds.size() : 0);
        // TODO: 实现批量拒绝审核逻辑
        log.warn("ActivityWithScheduleService.batchRejectReview not implemented");
        return ResponseDTO.ok("功能未实现");
    }

    public ResponseDTO<Page<ActivityWithScheduleVO>> notStartAndPendingEnrollActivityPageGlobal(Long pageNum, Long pageSize){
        Page<ActivityWithScheduleVO> page = new Page<>(pageNum, pageSize);
        Page<ActivityWithScheduleVO> result =activityDao.notStartAndPendingEnrollActivityGlobal(page);
        return ResponseDTO.ok(result);
    }

    public ResponseDTO<Page<ActivityWithScheduleVO>> notStartAndPendingEnrollActivityPage(Long pageNum, Long pageSize){
        Page<ActivityWithScheduleVO> page = new Page<>(pageNum, pageSize);
        Long userId = SmartRequestUtil.getRequestUserId();
        PortalUserEntity portalUserEntity =portalUserManager.getById(userId);
        if(portalUserEntity==null||portalUserEntity.getDeletedFlag()) {
            return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "用户不存在");
        }
        Long schoolId = portalUserEntity.getSchoolId();
        Page<ActivityWithScheduleVO> result =activityDao.notStartAndPendingEnrollActivity(page,schoolId);
        return ResponseDTO.ok(result);
    }

}