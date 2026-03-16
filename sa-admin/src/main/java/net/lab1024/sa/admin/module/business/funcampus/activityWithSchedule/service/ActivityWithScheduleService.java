package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.manager.ActivityCanEnrollCollegeManager;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.service.ActivityCanEnrollCollegeService;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.manager.ActivityCanEnrollGradeManager;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.service.ActivityCanEnrollGradeService;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.manager.ActivityCanEnrollTribeManager;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.service.ActivityCanEnrollTribeService;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.entity.ActivityCategoryEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.manager.ActivityCategoryManager;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEnrollNum;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityDetailVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.EnrollerVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.entity.GradeInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.manager.GradeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import net.lab1024.sa.admin.module.business.funcampus.tribe.manager.TribeManager;
import net.lab1024.sa.admin.module.business.funcampus.util.UpdateFormUtil;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.manager.BackendUserManager;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.code.UnexpectedErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private InsertContentFactory factory;

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
    private ActivityCanEnrollTribeManager activityCanEnrollTribeManager;

    @Resource
    private ActivityCanEnrollGradeManager activityCanEnrollGradeManager;

    @Resource
    private ActivityCanEnrollCollegeManager activityCanEnrollCollegeManager;

    @Resource
    private ActivityReviewLogManager activityReviewLogManager;

    @Resource
    private ActivitySigninManagerManager signinManagerManager;

    @Resource
    private ActivityCategoryManager activityCategoryManager;

    @Resource
    private CollegeInfoManager collegeInfoManager;

    @Resource
    private GradeInfoManager gradeInfoManager;

    @Resource
    private TribeManager tribeManager;

    @Resource
    private ActivityCanEnrollCollegeService canEnrollCollegeService;

    @Resource
    private ActivityCanEnrollTribeService canEnrollTribeService;

    @Resource
    private ActivityCanEnrollGradeService canEnrollGradeService;

    @Resource
    private ActivityEnrollmentService enrollmentService;

    @Resource
    private ActivityEnrollmentManager enrollmentManager;


    public void addNotReviewedOne(ActivityWithScheduleAddForm addForm) {
        log.info("添加待审核的活动：{}", addForm.toString());

        ActivityEntity activityEntity=factory.buildActivity(addForm);
        ActivityScheduleEntity scheduleEntity=factory.buildActivitySchedule(addForm);
        List<ActivityCanEnrollCollegeEntity> collegeList;
        List<ActivityCanEnrollGradeEntity> gradeList;
        if(!addForm.getCanEnrollCollegeIdList().isEmpty()&&!addForm.getCanEnrollGradeIdList().isEmpty()){
            collegeList=factory.buildCanEnrollCollege(addForm);
            gradeList=factory.buildCanEnrollGrade(addForm);
        } else {
            gradeList = null;
            collegeList = null;
        }
        List<ActivityCanEnrollTribeEntity> tribeList;
        if(!addForm.getCanEnrollTribeIdList().isEmpty()){
            tribeList=factory.buildCanEnrollTribe(addForm);
        } else {
            tribeList = null;
        }
        ActivityReviewLogEntity activityReviewLog=factory.buildReviewLog(addForm);
        ActivityEnrollNum activityEnrollNum=factory.buildEnrollNum();
        List<ActivitySigninManagerEntity> signinManagerList = factory.buildSigninManagerList(addForm);


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
                assert tribeList != null;
                if(!tribeList.isEmpty()){
                    tribeList.forEach(e->e.setActivityId(id));
                    if(!activityCanEnrollTribeManager.saveBatch(tribeList)){
                        log.warn("创建未审核活动事务失败：插入能报名的部落失败：activityId={}",id);
                        status.setRollbackOnly();
                    }
                }
                signinManagerList.forEach(e->e.setActivityId(id));
                if(!signinManagerManager.saveBatch(signinManagerList)){
                    log.warn("创建未审核活动事务失败：插入签到员失败：activityId={}",id);
                    status.setRollbackOnly();
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


    public void updateActivityWithSchedule(ActivityWithScheduleUpdateForm updateForm) {
        log.info("ActivityWithScheduleService.updateActivityWithSchedule called, activityId={}", updateForm.getId());

        ActivityEntity activityEntity = UpdateContentFactory.buildActivity(updateForm);
        boolean isIgnoreActivity=UpdateFormUtil.isEntityPropertiesAllNull(activityEntity,"id");
        ActivityScheduleEntity activitySchedule = UpdateContentFactory.buildActivitySchedule(updateForm);
        boolean isIgnoreActivitySchedule = UpdateFormUtil.isEntityPropertiesAllNull(activitySchedule,"activityId");
        List<ActivityCanEnrollCollegeEntity> collegeList = UpdateContentFactory.buildCanEnrollCollege(updateForm);
        boolean isIgnoreCollege = collegeList == null;
        List<ActivityCanEnrollGradeEntity> gradeList = UpdateContentFactory.buildCanEnrollGrade(updateForm);
        boolean isIgnoreGrade = gradeList == null;
        List<ActivityCanEnrollTribeEntity> tribeList = UpdateContentFactory.buildCanEnrollTribe(updateForm);
        boolean isIgnoreTribe = tribeList==null;
        ActivityReviewLogEntity reviewLog = UpdateContentFactory.buildReviewLog(updateForm);
        boolean isIgnoreReviewLog = reviewLog==null;
        List<ActivitySigninManagerEntity> signinManagerList = UpdateContentFactory.buildSigninManagerList(updateForm);
        boolean isIgnoreSigninManager = signinManagerList==null;


        //活动与年级学院部落的关系需要删掉旧的再插入新的
        LambdaUpdateWrapper<ActivityCanEnrollCollegeEntity> deleteOldCollege=new LambdaUpdateWrapper<>();
        deleteOldCollege.eq(ActivityCanEnrollCollegeEntity::getActivityId,updateForm.getId())
                    .set(ActivityCanEnrollCollegeEntity::getDeletedFlag,true);

        LambdaUpdateWrapper<ActivityCanEnrollGradeEntity> deleteOldGrade = new LambdaUpdateWrapper<>();
        deleteOldGrade.eq(ActivityCanEnrollGradeEntity::getActivityId,updateForm.getId())
                .set(ActivityCanEnrollGradeEntity::getDeletedFlag,true);

        LambdaUpdateWrapper<ActivityCanEnrollTribeEntity> deleteOldTribe=new LambdaUpdateWrapper<>();
        deleteOldTribe.eq(ActivityCanEnrollTribeEntity::getActivityId,updateForm.getId())
                        .set(ActivityCanEnrollTribeEntity::getDeletedFlag,true);

        LambdaUpdateWrapper<ActivitySigninManagerEntity> deleteSignInManager = new LambdaUpdateWrapper<>();
        deleteSignInManager.eq(ActivitySigninManagerEntity::getActivityId,updateForm.getId())
                        .set(ActivitySigninManagerEntity::getDeletedFlag,true);

        transactionTemplate.executeWithoutResult(status -> {
            try {
                if (!isIgnoreActivity) {
                    if(!activityManager.updateById(activityEntity)){
                        log.warn("更新活动内容的activityEntity失败回滚，activityId:{},update activity entity:{}",
                                activityEntity.getId(),activityEntity);
                        status.setRollbackOnly();
                    }
                }
                if(!isIgnoreActivitySchedule){
                    if(!activityScheduleManager.updateById(activitySchedule)){
                        log.warn("更新活动内容的activityScheduleEntity失败回滚，activityId:{}，update activity schedule entity:{}"
                                ,activityEntity.getId()
                                ,activitySchedule);
                        status.setRollbackOnly();
                    }
                }
                if(!isIgnoreReviewLog){
                    if(!activityReviewLogManager.updateById(reviewLog)){
                        log.warn("更新活动内容的activityReviewLogEntity失败回滚，activityId:{},update review log :{}",
                                activityEntity.getId(),reviewLog);
                        status.setRollbackOnly();
                    }
                }
                if(!isIgnoreGrade){
                    if(!activityCanEnrollGradeManager.update(deleteOldGrade)){
                        log.warn("更新活动内容中的删除旧的activityCanEnrollGrade失败回滚,activityId：{}",updateForm.getId());
                        status.setRollbackOnly();
                    }
                    if(!activityCanEnrollGradeManager.saveBatch(gradeList)){
                        log.warn("更新活动内容中的插入新的activityCanEnrollGrade失败回滚，activityId:{}",updateForm.getId());
                        status.setRollbackOnly();
                    }
                }
                if(!isIgnoreCollege){
                    if(!activityCanEnrollCollegeManager.update(deleteOldCollege)){
                        log.warn("更新活动内容中的删除旧的activityCanEnrollCollege失败回滚,activityId：{}",updateForm.getId());
                        status.setRollbackOnly();
                    }
                    if(!activityCanEnrollCollegeManager.saveBatch(collegeList)){
                        log.warn("更新活动内容中的插入新的activityCanEnrollCollege失败回滚，activityId:{}",updateForm.getId());
                        status.setRollbackOnly();
                    }
                }
                if(!isIgnoreTribe){
                    if(!activityCanEnrollTribeManager.update(deleteOldTribe)){
                        log.warn("更新活动内容中的删除旧的activityCanEnrollTribe失败回滚,activityId：{}",updateForm.getId());
                        status.setRollbackOnly();
                    }
                    if(!activityCanEnrollTribeManager.saveBatch(tribeList)){
                        log.warn("更新活动内容中的插入新的activityCanEnrollTribe失败回滚，activityId:{}",updateForm.getId());
                        status.setRollbackOnly();
                    }
                }
                if(!isIgnoreSigninManager){
                    if(!signinManagerManager.update(deleteSignInManager)){
                        log.warn("更新活动内容中删除旧的activitySigninManager失败回滚，activityId:{}",updateForm.getId());
                        status.setRollbackOnly();
                    }
                    if(!signinManagerManager.saveBatch(signinManagerList)){
                        log.warn("更新活动内容中的插入新的activitySigninManager失败回滚，activityId:{}",updateForm.getId());
                        status.setRollbackOnly();
                    }
                }
            } catch (Exception e) {
                log.warn("更新活动内容事务失败,activityId:{},{},{},{}",updateForm.getId(), e.getCause(), e.getMessage(), e.getStackTrace());
                status.setRollbackOnly();
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "更新活动内容失败，请重试");
            }

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

    public ActivityDetailVO detail(Long activityId){
        ActivityEntity activity = activityManager.getById(activityId);
        if(activity==null||activity.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动不存在");
        }
        ActivityScheduleEntity activitySchedule = activityScheduleManager.getByActivityId(activityId);
        ActivityCategoryEntity activityCategory = activityCategoryManager.getById(activity.getCategoryId());
        ActivityEnrollNum activityEnrollNum = activityEnrollNumDao.selectById(activityId);

        List<Long> collegeIds = canEnrollCollegeService.getCollegeIdsByActivityId(activityId);
        List<Long> tribeIds = canEnrollTribeService.getTribeIdsByActivityId(activityId);
        List<Long> gradeIds = canEnrollGradeService.getGradeIdByActivityId(activityId);

        List<String> collegeNames=null;
        List<String> tribeNames=null;
        List<String> gradeNames=null;
        List<EnrollerVO> enrollers = new ArrayList<>();
        if(gradeIds!=null&&collegeIds!=null){
            collegeNames=collegeInfoManager
                    .listByIds(collegeIds)
                    .stream()
                    .map(CollegeInfoEntity::getName)
                    .toList();
            gradeNames = gradeInfoManager
                    .listByIds(gradeIds)
                    .stream()
                    .map(GradeInfoEntity::getName)
                    .toList();
        }
        if(tribeIds!=null){
            tribeNames = tribeManager
                    .listByIds(tribeIds)
                    .stream()
                    .map(TribeEntity::getName)
                    .toList();
        }

        // 构建查询条件：获取活动报名用户 ID 及签到状态
        LambdaQueryWrapper<ActivityEnrollmentEntity> activityEnrollmentQw = 
                ActivityEnrollmentService.listByActivityIdQw(activityId)
                .select(ActivityEnrollmentEntity::getUserId)
                .select(ActivityEnrollmentEntity::getSignInStatus);
        List<ActivityEnrollmentEntity> enrollmentEntities = enrollmentManager.list(activityEnrollmentQw);
                
        // 将报名用户列表转换为 Map<userId, signInStatus>，便于快速查找签到状态
        if (enrollmentEntities != null && !enrollmentEntities.isEmpty()) {
            Map<Long, Boolean> userSignInStatus = enrollmentEntities.stream().collect(Collectors.toMap(
                    ActivityEnrollmentEntity::getUserId,
                    ActivityEnrollmentEntity::getSignInStatus
            ));
                    
            // 提取所有报名用户的 ID 列表
            List<Long> enrollerIds = enrollmentEntities
                    .stream()
                    .map(ActivityEnrollmentEntity::getUserId)
                    .toList();
                    
            // 批量查询 Portal 用户信息
            List<PortalUserEntity> portalUsers = portalUserManager.listByIds(enrollerIds);

            LambdaQueryWrapper<ActivitySigninManagerEntity> signInManagerQw = new LambdaQueryWrapper<>();
            signInManagerQw.eq(ActivitySigninManagerEntity::getActivityId,activityId)
                            .eq(ActivitySigninManagerEntity::getDeletedFlag,false)
                            .select(ActivitySigninManagerEntity::getPortalUserId);
            List<ActivitySigninManagerEntity> signinManagerList = signinManagerManager.list(signInManagerQw);
            List<Long> signinManagerIdList;
            if(signinManagerList!=null&&!signinManagerList.isEmpty()){
                signinManagerIdList = signinManagerList.stream().map(ActivitySigninManagerEntity::getPortalUserId).toList();
            } else {
                signinManagerIdList = null;
            }
            // 组装 EnrollerVO 对象并设置对应的签到状态
            portalUsers.forEach(e -> {
                EnrollerVO enroller = new EnrollerVO();
                enroller.setId(e.getId());
                enroller.setName(e.getUsername());
                enroller.setAvatarKey(e.getAvatar());
                // 从 Map 中获取该用户的签到状态
                enroller.setSignInStatus(userSignInStatus.get(e.getId()));
                if(activity.getActivityManagerId().equals(e.getId())){
                    enroller.setActivityManagerFlag(true);
                }
                if(signinManagerIdList!=null&&signinManagerIdList.contains(e.getId())){
                    enroller.setSigninManagerFlag(true);
                }
                enrollers.add(enroller);
            });
        }
        //统计签到人数
        LambdaQueryWrapper<ActivityEnrollmentEntity> countSignIn = new LambdaQueryWrapper<>();
        countSignIn.eq(ActivityEnrollmentEntity::getActivityId,activityId)
                    .eq(ActivityEnrollmentEntity::getSignInStatus,true)
                    .eq(ActivityEnrollmentEntity::getDeletedFlag,false);
        long count = enrollmentManager.count(countSignIn);

        ActivityDetailVO activityDetailVO = new ActivityDetailVO();
        activityDetailVO.setId(activityId);
        activityDetailVO.setTitle(activity.getTitle());
        activityDetailVO.setStatus(activity.getStatus());
        activityDetailVO.setPosition(activityDetailVO.getPosition());
        activityDetailVO.setScoreCanGet(activity.getScoreCanGet());
        activityDetailVO.setEnrollNumLimit(activity.getEnrollNumLimit());
        activityDetailVO.setDescription(activity.getDescription());
        activityDetailVO.setEnrollNeedReview(activity.isEnrollNeedReview());
        activityDetailVO.setNeedSignOut(activity.isNeedSignOut());
        activityDetailVO.setAttachment(activity.getAttachment());
        activityDetailVO.setCategory(activityCategory.getName());
        activityDetailVO.setCoverImg(activity.getCoverImg());
        activityDetailVO.setEnrollUsers(enrollers);
        activityDetailVO.setEnrollNum(activityEnrollNum.getEnrollNum());
        activityDetailVO.setSignInNum(count);
        activityDetailVO.setCanEnrollGrade(gradeNames);
        activityDetailVO.setCanEnrollCollege(collegeNames);
        activityDetailVO.setCanEnrollTribe(tribeNames);
        activityDetailVO.setEnrollStartTime(activitySchedule.getEnrollStartTime());
        activityDetailVO.setEnrollEndTime(activitySchedule.getEnrollEndTime());
        activityDetailVO.setActivityStartTime(activitySchedule.getActivityStartTime());
        activityDetailVO.setActivityEndTime(activitySchedule.getActivityEndTime());

        return activityDetailVO;
    }
}