package net.lab1024.sa.admin.module.business.funcampus.organizerCadre.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.constant.RedisKey;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.constant.ReviewStatus;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.entity.OrganizerActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.manager.OrganizerActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.dao.OrganizerCadreDao;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.entity.OrganizerCadreEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form.OrganizerCadreAddForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form.OrganizerCadreQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form.OrganizerCadreUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.vo.OrganizerCadreVO;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 组织干事用户 Service
 *
 * @Author akkkka114514
 * @Date 2025-11-08 14:09:30
 * @Copyright akkkka114514
 */

@Service
@Slf4j
public class OrganizerCadreService {

    @Resource
    private OrganizerCadreDao organizerCadreDao;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private PortalUserManager portalUserManager;
    @Resource
    private ActivityEnrollmentService activityEnrollmentService;
    @Resource
    private ActivityDao activityDao;
    @Resource
    private ActivityEnrollmentManager activityEnrollmentManager;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private OrganizerActivityManager organizerActivityManager;
    @Resource
    private ActivityEnrollNumDao activityEnrollNumDao;
    /**
     * 分页查询
     */
    public PageResult<OrganizerCadreVO> queryPage(OrganizerCadreQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OrganizerCadreVO> list = organizerCadreDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(OrganizerCadreAddForm addForm) {
        OrganizerCadreEntity organizerCadreEntity = SmartBeanUtil.copy(addForm, OrganizerCadreEntity.class);
        organizerCadreDao.insert(organizerCadreEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(OrganizerCadreUpdateForm updateForm) {
        OrganizerCadreEntity organizerCadreEntity = SmartBeanUtil.copy(updateForm, OrganizerCadreEntity.class);
        organizerCadreDao.updateById(organizerCadreEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        organizerCadreDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        organizerCadreDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }

    /**
     * 扫描签到码以后的确认行为
     */
    public ResponseDTO<Void> confirmSignInCode(Long userId,Long activityId, String uuid) {
        PortalUserEntity portalUser =portalUserManager.getById(userId);
        if(portalUser==null||portalUser.getDeletedFlag()) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        ActivityWithScheduleVO activityWithSchedule = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithSchedule== null||activityWithSchedule.getDeletedFlag()) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(!requestUser.getUserType().equals(UserTypeEnum.ORGANIZER_CADRE)) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        Long cadreId = requestUser.getUserId();
        if(!organizerCadreDao.canManageActivity(cadreId, activityId)){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }

        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                .ne(ActivityEnrollmentEntity::getSignInStatus, ReviewStatus.REJECTED);
        ActivityEnrollmentEntity activityEnrollment = activityEnrollmentManager.getOne(queryWrapper);
        if(activityEnrollment==null) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
        }
        String realUuid = redisTemplate.opsForValue().get(RedisKey.SIGN_IN_CODE_KEY(userId, activityId));
        if(realUuid==null) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "签到码已过期");
        }
        if(!realUuid.equals(uuid)) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "签到码错误");
        }
        activityEnrollmentService.signIn(activityId, userId);
        return ResponseDTO.ok();
    }

    // 后台操作签到
    public ResponseDTO<String> backendOperateSignIn(Long activityId, Long userId){
        log.info("ActivityEnrollmentService.backendOperateSignIn called, activityId={}, userId={}", activityId, userId);

        ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.backendOperateSignIn failed: activity not found or deleted, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }

        if(activityWithScheduleVO.getStatus()!= ActivityStatus.START_SIGNIN){
            log.warn("ActivityEnrollmentService.backendOperateSignIn failed: activity not in sign-in period, activityId={}, status={}", activityId, activityWithScheduleVO.getStatus());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
        }

        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null||portalUser.getDeletedFlag()) {
            log.warn("ActivityEnrollmentService.backendOperateSignIn failed: user not found or deleted, userId={}", userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }

        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            log.warn("ActivityEnrollmentService.backendOperateSignIn failed: user type error, userType={}", requestUser.getUserType());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }

        Long organizerId = requestUser.getUserId();
        LambdaQueryWrapper<OrganizerActivityEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrganizerActivityEntity::getActivityId, activityId)
                .eq(OrganizerActivityEntity::getOrganizerId, organizerId)
                .eq(OrganizerActivityEntity::getDeletedFlag, false);
        OrganizerActivityEntity organizerActivityEntity = organizerActivityManager.getOne(queryWrapper);
        if(organizerActivityEntity==null){
            log.warn("ActivityEnrollmentService.backendOperateSignIn failed: user is not activity organizer, activityId={}, userId={}", activityId, organizerId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不是活动发起者");
        }

        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity activityEnrollmentEntity = new ActivityEnrollmentEntity();
        activityEnrollmentEntity.setSignInStatus(true);

        boolean result = activityEnrollmentManager.update(activityEnrollmentEntity ,updateWrapper);
        if (result) {
            log.info("ActivityEnrollmentService.backendOperateSignIn success: backend sign-in completed, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.ok("后台操作签到成功");
        } else {
            log.error("ActivityEnrollmentService.backendOperateSignIn failed: failed to update sign-in status, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "签到失败");
        }
    }
    public ResponseDTO<String> batchBackendOperateSignIn(Map<Long, Set<Long>> activityIdUserIdMap){
        log.info("ActivityEnrollmentService.batchBackendOperateSignIn called, activity count={}", activityIdUserIdMap != null ? activityIdUserIdMap.size() : 0);

        if(activityIdUserIdMap == null || activityIdUserIdMap.isEmpty()){
            log.warn("ActivityEnrollmentService.batchBackendOperateSignIn failed: empty parameters");
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数错误");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        Long organizerId = requestUser.getUserId();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            log.warn("ActivityEnrollmentService.batchBackendOperateSignIn failed: user type error, userType={}", requestUser.getUserType());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        for(Map.Entry<Long, Set<Long>> entry : activityIdUserIdMap.entrySet()){
            Long activityId = entry.getKey();
            Set<Long> userIdSet = entry.getValue();
            ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
            if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
                log.warn("ActivityEnrollmentService.batchBackendOperateSignIn failed: activity not found or deleted, activityId={}", activityId);
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
            }
            if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_SIGNIN){
                log.warn("ActivityEnrollmentService.batchBackendOperateSignIn failed: activity not in sign-in period, activityId={}, status={}", activityId, activityWithScheduleVO.getStatus());
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
            }
            LambdaQueryWrapper<OrganizerActivityEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrganizerActivityEntity::getActivityId, activityId)
                    .eq(OrganizerActivityEntity::getOrganizerId, organizerId)
                    .eq(OrganizerActivityEntity::getDeletedFlag, false);
            OrganizerActivityEntity organizerActivityEntity = organizerActivityManager.getOne(queryWrapper);
            if(organizerActivityEntity==null){
                log.warn("ActivityEnrollmentService.batchBackendOperateSignIn failed: user is not activity organizer, activityId={}, userId={}", activityId, organizerId);
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不是活动发起者");
            }
            for(Long userId : userIdSet){
                PortalUserEntity portalUser = portalUserManager.getById(userId);
                if(portalUser==null||portalUser.getDeletedFlag()) {
                    log.warn("ActivityEnrollmentService.batchBackendOperateSignIn failed: user not found or deleted, userId={}", userId);
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
                }
                LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper2 = new LambdaQueryWrapper<>();
                queryWrapper2.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                        .eq(ActivityEnrollmentEntity::getUserId, userId);
                ActivityEnrollmentEntity activityEnrollmentEntity = activityEnrollmentManager.getOne(queryWrapper2);
                if(activityEnrollmentEntity==null||activityEnrollmentEntity.getDeletedFlag()){
                    log.warn("ActivityEnrollmentService.batchBackendOperateSignIn failed: user not enrolled, activityId={}, userId={}", activityId, userId);
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
                }
                if(activityEnrollmentEntity.getSigninReviewStatus()== ReviewStatus.REJECTED){
                    log.warn("ActivityEnrollmentService.batchBackendOperateSignIn failed: sign-in review rejected, activityId={}, userId={}", activityId, userId);
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户报名该活动已被拒绝签到");
                }
            }
        }
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            for(Map.Entry<Long, Set<Long>> entry : activityIdUserIdMap.entrySet()){
                for (Long userId : entry.getValue()){
                    LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, entry.getKey())
                            .eq(ActivityEnrollmentEntity::getUserId, userId);
                    ActivityEnrollmentEntity activityEnrollmentEntity = new ActivityEnrollmentEntity();
                    activityEnrollmentEntity.setSignInStatus(true);
                    activityEnrollmentEntity.setSigninReviewStatus(ReviewStatus.PASSED);
                    activityEnrollmentManager.update(activityEnrollmentEntity, updateWrapper);
                }
            }
        });
        log.info("ActivityEnrollmentService.batchBackendOperateSignIn success: batch backend sign-in completed");
        return ResponseDTO.ok("批量后台操作签到成功");
    }


    public ResponseDTO<String> RejectSignIn(Long activityId, Long userId){
        log.info("ActivityEnrollmentService.RejectSignIn called, activityId={}, userId={}", activityId, userId);

        ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.RejectSignIn failed: activity not found or deleted, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_SIGNIN){
            log.warn("ActivityEnrollmentService.RejectSignIn failed: activity not in sign-in period, activityId={}, status={}", activityId, activityWithScheduleVO.getStatus());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
        }
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null||portalUser.getDeletedFlag()) {
            log.warn("ActivityEnrollmentService.RejectSignIn failed: user not found or deleted, userId={}", userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            log.warn("ActivityEnrollmentService.RejectSignIn failed: user type error, userType={}", requestUser.getUserType());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity activityEnrollmentEntity = new ActivityEnrollmentEntity();
        activityEnrollmentEntity.setSignInStatus(false);
        activityEnrollmentEntity.setSigninReviewStatus(ReviewStatus.REJECTED);
        boolean result = activityEnrollmentManager.update(activityEnrollmentEntity ,updateWrapper);
        if (result) {
            log.info("ActivityEnrollmentService.RejectSignIn success: sign-in rejected, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.ok("拒绝签到成功");
        } else {
            log.error("ActivityEnrollmentService.RejectSignIn failed: failed to update sign-in status, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "拒绝签到失败");
        }
    }

    public ResponseDTO<String> passEnrollReview(Long activityId, Long userId){
        log.info("ActivityEnrollmentService.passEnrollReview called, activityId={}, userId={}", activityId, userId);

        ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.passEnrollReview failed: activity not found or deleted, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_SIGNIN
                &&activityWithScheduleVO.getStatus()!=ActivityStatus.END_ENROLL){
            log.warn("ActivityEnrollmentService.passEnrollReview failed: activity not in enrollment period, activityId={}, status={}", activityId, activityWithScheduleVO.getStatus());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始报名或活动已开始");
        }
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity activityEnrollmentEntity = activityEnrollmentManager.getOne(queryWrapper);
        if(activityEnrollmentEntity==null||activityEnrollmentEntity.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.passEnrollReview failed: user not enrolled, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            log.warn("ActivityEnrollmentService.passEnrollReview failed: user type error, userType={}", requestUser.getUserType());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        transactionTemplate.executeWithoutResult(status -> {
            LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                    .eq(ActivityEnrollmentEntity::getUserId, userId);
            ActivityEnrollmentEntity updateEntity = new ActivityEnrollmentEntity();
            if(activityEnrollmentManager.update(updateEntity, updateWrapper)){
                log.error("ActivityEnrollmentService.passEnrollReview failed: failed to update review status, activityId={}, userId={}", activityId, userId);
                status.setRollbackOnly();
            }
        });
        log.info("ActivityEnrollmentService.passEnrollReview success: enrollment review passed, activityId={}, userId={}", activityId, userId);
        return ResponseDTO.ok("通过审核成功");
    }

    public ResponseDTO<String> batchPassEnrollReview(Map<Long, Long>  activityIdUserIdMap){
        log.info("ActivityEnrollmentService.batchPassEnrollReview called, entry count={}", activityIdUserIdMap != null ? activityIdUserIdMap.size() : 0);

        if(activityIdUserIdMap == null || activityIdUserIdMap.isEmpty()){
            log.warn("ActivityEnrollmentService.batchPassEnrollReview failed: empty parameters");
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数错误");
        }
        //passEnrollReview中会做校验，所以这里不需要再次做校验
        for(Map.Entry<Long, Long> entry: activityIdUserIdMap.entrySet()){
            passEnrollReview(entry.getKey(), entry.getValue());
        }
        log.info("ActivityEnrollmentService.batchPassEnrollReview success: batch enrollment review passed");
        return ResponseDTO.ok("批量通过签到成功");
    }
    public ResponseDTO<String> rejectEnrollReview(Long activityId, Long userId){
        log.info("ActivityEnrollmentService.rejectEnrollReview called, activityId={}, userId={}", activityId, userId);

        ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.rejectEnrollReview failed: activity not found or deleted, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_ENROLL
                &&activityWithScheduleVO.getStatus()!=ActivityStatus.END_ENROLL){
            log.warn("ActivityEnrollmentService.rejectEnrollReview failed: activity not in enrollment period, activityId={}, status={}", activityId, activityWithScheduleVO.getStatus());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始报名或活动已开始");
        }
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null||portalUser.getDeletedFlag()) {
            log.warn("ActivityEnrollmentService.rejectEnrollReview failed: user not found or deleted, userId={}", userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            log.warn("ActivityEnrollmentService.rejectEnrollReview failed: user type error, userType={}", requestUser.getUserType());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity activityEnrollmentEntity = new ActivityEnrollmentEntity();
        transactionTemplate.executeWithoutResult(status -> {
            if(!activityEnrollNumDao.decreaseEnrollNum(activityId)||
                    activityEnrollmentManager.update(activityEnrollmentEntity, updateWrapper)){
                log.error("ActivityEnrollmentService.rejectEnrollReview failed: failed to update review status or decrease enrollment count, activityId={}, userId={}", activityId, userId);
                status.setRollbackOnly();
            }
        });
        log.info("ActivityEnrollmentService.rejectEnrollReview success: enrollment review rejected, activityId={}, userId={}", activityId, userId);
        return ResponseDTO.ok("拒绝审核成功");
    }
    public ResponseDTO<String> batchRejectEnrollReview(Map<Long, Long>  activityIdUserIdMap){
        log.info("ActivityEnrollmentService.batchRejectEnrollReview called, entry count={}", activityIdUserIdMap != null ? activityIdUserIdMap.size() : 0);

        if(activityIdUserIdMap == null || activityIdUserIdMap.isEmpty()){
            log.warn("ActivityEnrollmentService.batchRejectEnrollReview failed: empty parameters");
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数错误");
        }
        for(Map.Entry<Long, Long> entry: activityIdUserIdMap.entrySet()){
            passEnrollReview(entry.getKey(), entry.getValue());
        }
        log.info("ActivityEnrollmentService.batchRejectEnrollReview success: batch enrollment review rejected");
        return ResponseDTO.ok("批拒绝报名成功");
    }
}
