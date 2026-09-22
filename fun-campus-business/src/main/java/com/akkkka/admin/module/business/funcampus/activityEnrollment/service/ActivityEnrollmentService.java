package com.akkkka.admin.module.business.funcampus.activityEnrollment.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.akkkka.admin.module.business.funcampus.activityEnrollment.constant.RedisKey;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.dao.ActivityEnrollmentDao;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.dto.EnrollersChangeDTO;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.SignInManagerValidator;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.EnrollerVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.SimplePortalUserVO;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.QRCodeSignInForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.SignInQRCodeVO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


import javax.imageio.ImageIO;

/**
 * 活动报名关系 Service
 *
 * @Author akkkka114514
 * @Date 2025-10-02 13:54:42
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityEnrollmentService {
    
    private final ActivityEnrollmentDao activityEnrollmentDao;
    
    private final ActivityEnrollmentManager activityEnrollmentManager;
    
    private final ActivityManager activityManager;
    
    private final ActivityEnrollNumDao activityEnrollNumDao;
    
    private final TransactionTemplate transactionTemplate;
    
    private final PortalUserManager portalUserManager;
    
    private final PortalUserValidator portalUserValidator;

    private final ActivityValidator activityValidator;

    private final ActivityEnrollmentValidator enrollmentDomainService;

    private final SignInManagerValidator signInManagerValidator;

    private final RedisTemplate<String,String> redisTemplate;

    private final ActivityEnrollmentManager enrollmentManager;

    private final ActivitySigninManagerService signinManagerService;

    private final ActivityScheduleManager activityScheduleManager;

    private static final int QR_CODE_EXPIRE_SECONDS = 30;


    public void enroll(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        //todo做幂等
        assert requestUser!=null;
        if(requestUser.getUserType()!= UserTypeEnum.PORTAL_USER){
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "用户类型错误");
        }

        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        activity.validateStatus(ActivityStatus.ENROLLING);

        //检查用户所属学院年级部落是否在活动指定范围内
        PortalUserEntity portalUser = portalUserManager.getById(requestUser.getUserId());
        portalUserValidator.validateUserCanEnrollCollege(activityId,portalUser);
        portalUserValidator.validateUserCanEnrollGrade(activityId,portalUser);
        portalUserValidator.validateUserCanEnrollTribe(activityId,portalUser);
        enrollmentDomainService.validateEnrollmentDuplicate(activityId,requestUser.getUserId());
        
        ActivityEnrollmentEntity enrollmentEntity = new ActivityEnrollmentEntity();
        enrollmentEntity.setActivityId(activityId);
        enrollmentEntity.setUserId(requestUser.getUserId());
        enrollmentEntity.setSignInStatus(false);
        enrollmentEntity.setDeletedFlag(false);

        transactionTemplate.executeWithoutResult(status -> {
            // 尝试增加报名人数，如果达到上限则返回false
            if (!activityEnrollNumDao.increaseEnrollNum(activityId)) {
                log.warn("ActivityEnrollmentService.enroll failed: activity enrollment full, activityId={}", activityId);
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动报名人数已满");
            }
            
            // 如果增加报名人数成功，则插入报名记录
            if (activityEnrollmentDao.insert(enrollmentEntity) == 0) {
                log.error("ActivityEnrollmentService.enroll failed: failed to insert enrollment record, activityId={}", activityId);
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "报名失败");
            }
            
            log.info("ActivityEnrollmentService.enroll success: new enrollment completed, activityId={}, userId={}", activityId, requestUser.getUserId());
        });
    }
    /*
        产生二维码不指定属于哪个活动，仅提供userId和uuid，activityId需扫描者指定
        二维码内容包含 userId 和 token，扫描后可解析出目标用户
     */
    public SignInQRCodeVO signInQRCode(){
        Long userId = SmartRequestUtil.getRequestUserId();
        assert userId!=null;
        String redisKey = RedisKey.qrCodeTokenKey(userId);
        String uuid=UUID.randomUUID().toString().replace("-","");
        // 允许刷新：重新生成时直接覆盖旧 token，旧值立即失效（无需先删除，避免并发读取空窗口）
        redisTemplate.opsForValue().set(redisKey,uuid,QR_CODE_EXPIRE_SECONDS,TimeUnit.SECONDS);
        // 二维码内容包含 userId 和 token
        String qrContent = String.format(
                "userId=%d&token=%s",
                userId,
                uuid
        );
        // 使用ZXing生成二维码
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    qrContent,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );
            // 转换为BufferedImage
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // 转换为PNG格式的Base64字符串
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // 生成可以直接在HTML中使用的data URL
            String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

            SignInQRCodeVO vo = new SignInQRCodeVO();
            vo.setQrCodeImage(base64Image);
            vo.setToken(uuid);
            vo.setUserId(userId);
            vo.setExpireSeconds(QR_CODE_EXPIRE_SECONDS);

            log.info("ActivityEnrollmentService.signInQRCode success: QR code generated, userId={}",  userId);
            return vo;
        } catch (Exception e) {
            log.error("ActivityEnrollmentService.signInQRCode failed: failed to generate QR code, userId={}", userId);
            throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "生成二维码失败");
        }
    }
    public void signIn(Long activityId, Long needSignInUserId, String uuid){
        String redisKey = RedisKey.qrCodeTokenKey(needSignInUserId);
        String realUuid = redisTemplate.opsForValue().get(redisKey);
        if (realUuid==null||!Objects.equals(realUuid,uuid)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"二维码已过期");
        }
        //检查活动是否存在，是否已删除
        activityValidator.validateActivityId(activityId);
        //检查活动时间表是否存在，当前时间是否处于签到时间窗口内
        validateSignInTimeWindow(activityId);
        //检查用户是否已报名该活动
        ActivityEnrollmentEntity enrollmentEntity = enrollmentDomainService.validateEnrollmentExist(activityId,needSignInUserId);
        //检查用户是否已签到
        enrollmentEntity.validateSignInStatus();
        //检查操作user是否为该活动的signin manager
        Long signInManagerId = SmartRequestUtil.getRequestUserId();
        signInManagerValidator.validateUserPermission(signInManagerId,activityId);

        portalUserValidator.validatePortalUserId(needSignInUserId);
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, needSignInUserId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                .set(ActivityEnrollmentEntity::getSignInStatus, true);
        boolean result = activityEnrollmentManager.update(updateWrapper);
        if (result) {
            // 签到成功后删除token，防止重复使用
            redisTemplate.delete(redisKey);
            log.info("ActivityEnrollmentService.signIn success: user signed in, activityId={}, userId={}", activityId, needSignInUserId);
        } else {
            throw new BusinessException(UserErrorCode.SERVICE_BUSY);
        }
    }

    /**
     * 扫码签退（需活动开启了签退，且当前处于签退时间窗口内）
     */
    public void signOut(Long activityId, Long needSignOutUserId, String uuid){
        String redisKey = RedisKey.qrCodeTokenKey(needSignOutUserId);
        String realUuid = redisTemplate.opsForValue().get(redisKey);
        if (realUuid==null||!Objects.equals(realUuid,uuid)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"二维码已过期");
        }
        //检查活动是否存在，是否已删除
        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        //检查活动是否开启了签退
        if(!Boolean.TRUE.equals(activity.getNeedSignOut())){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"该活动无需签退");
        }
        //检查活动时间表是否存在，当前时间是否处于签退时间窗口内
        validateSignOutTimeWindow(activityId);
        //检查用户是否已报名该活动
        ActivityEnrollmentEntity enrollmentEntity = enrollmentDomainService.validateEnrollmentExist(activityId,needSignOutUserId);
        //检查用户是否已签到（未签到不能签退）
        if(!Boolean.TRUE.equals(enrollmentEntity.getSignInStatus())){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"用户尚未签到，无法签退");
        }
        //检查用户是否已签退
        enrollmentEntity.validateSignOutStatus();
        //检查操作user是否为该活动的signin manager
        Long signOutManagerId = SmartRequestUtil.getRequestUserId();
        signInManagerValidator.validateUserPermission(signOutManagerId,activityId);

        portalUserValidator.validatePortalUserId(needSignOutUserId);
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, needSignOutUserId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                .set(ActivityEnrollmentEntity::getSignOutStatus, true);
        boolean result = activityEnrollmentManager.update(updateWrapper);
        if (result) {
            // 签退成功后删除token，防止重复使用
            redisTemplate.delete(redisKey);
            log.info("ActivityEnrollmentService.signOut success: user signed out, activityId={}, userId={}", activityId, needSignOutUserId);
        } else {
            throw new BusinessException(UserErrorCode.SERVICE_BUSY);
        }
    }

    public List<Long> listPortalUserIds(Long activityId){
        return enrollmentManager.list(
                Wrappers.lambdaQuery(ActivityEnrollmentEntity.class)
                        .eq(ActivityEnrollmentEntity::getActivityId,activityId)
                        .eq(ActivityEnrollmentEntity::getDeletedFlag,false)
                        .select(ActivityEnrollmentEntity::getUserId)
        ).stream().map(ActivityEnrollmentEntity::getUserId).toList();
    }



    public List<SimplePortalUserVO> listEnrollUser(Long activityId){
        if(activityManager.getById(activityId)==null){
            log.warn("不存在的activityId:{}",activityId);
            return null;
        }
        List<ActivityEnrollmentEntity> list = activityEnrollmentManager.list(
                activityEnrollmentManager.qwByActivityId(activityId)
                .select(ActivityEnrollmentEntity::getUserId)
        );
        if(list==null||list.isEmpty()){
            return null;
        }

        List<SimplePortalUserVO> result = new ArrayList<>();
        list.forEach(e->{
            PortalUserEntity portalUser=portalUserManager.getById(e.getUserId());
            SimplePortalUserVO simple = new SimplePortalUserVO();
            simple.setId(portalUser.getId());
            simple.setName(portalUser.getUsername());
            simple.setAvatarKey(portalUser.getAvatar());
            result.add(simple);
        });
        return result;
    }

    /**
     * 获取活动报名用户列表（含签到状态、管理员/签到员标记）
     */
    public List<EnrollerVO> listEnrollUserAsEnrollerVO(Long activityId) {
        List<ActivityEnrollmentEntity> enrollments = activityEnrollmentManager.list(
                activityEnrollmentManager.qwByActivityId(activityId)
        );
        if (enrollments == null || enrollments.isEmpty()) {
            return Collections.emptyList();
        }

        ActivityEntity activity = activityManager.getById(activityId);
        Long managerId = activity != null ? activity.getActivityManagerId() : null;
        List<Long> signinManagerIds = signinManagerService.getSignInManagerIds(activityId);

        List<EnrollerVO> result = new ArrayList<>();
        for (ActivityEnrollmentEntity e : enrollments) {
            PortalUserEntity portalUser = portalUserManager.getById(e.getUserId());
            if (portalUser == null) {
                continue;
            }
            EnrollerVO vo = new EnrollerVO();
            vo.setId(portalUser.getId());
            vo.setName(portalUser.getUsername());
            vo.setAvatarKey(portalUser.getAvatar());
            vo.setSignInStatus(Boolean.TRUE.equals(e.getSignInStatus()));
            vo.setActivityManagerFlag(Objects.equals(portalUser.getId(), managerId));
            vo.setSigninManagerFlag(signinManagerIds.contains(portalUser.getId()));
            result.add(vo);
        }
        return result;
    }

    public EnrollersChangeDTO convertToEnrollmentChanges(Long activityId,List<Long> enrollerIds, List<Long> dbEnrollerIds){
        //假定activityId正确
        EnrollersChangeDTO  enrollersChangeDTO = new EnrollersChangeDTO();
        List<Long> copy = new LinkedList<>(enrollerIds);
        //新报名者除去共同元素就是要添加的人
        enrollerIds.removeAll(dbEnrollerIds);
        //旧报名着除去共同元素就是要删除的人
        dbEnrollerIds.removeAll(copy);
        if(!enrollerIds.isEmpty()){
            for(Long id:enrollerIds){
                ActivityEnrollmentEntity enrollment = new ActivityEnrollmentEntity();
                enrollment.setActivityId(activityId);
                enrollment.setUserId(id);
                enrollment.setSignInStatus(false);
                enrollment.setCreateTime(LocalDateTime.now());
                enrollment.setUpdateTime(LocalDateTime.now());
                enrollment.setDeletedFlag(false);
                enrollment.setSignOutStatus(false);
                enrollersChangeDTO.getAddList().add(enrollment);
            }
        }
        if(!dbEnrollerIds.isEmpty()){
            for (Long id:dbEnrollerIds){
                ActivityEnrollmentEntity enrollment = new ActivityEnrollmentEntity();
                enrollment.setDeletedFlag(true);
                enrollment.setActivityId(activityId);
                enrollment.setUserId(id);
                enrollersChangeDTO.getDelList().add(enrollment);
            }
        }
        return enrollersChangeDTO;

    }
    public void doSaveEnrollerChangesTransaction(Long activityId,List<Long> enrollerIds){
        EnrollersChangeDTO enrollersChangeDTO = new EnrollersChangeDTO();
        if(enrollerIds!=null && !enrollerIds.isEmpty()){
            portalUserValidator.validatePortalUserIds(enrollerIds);
            //如果活动管理员与签到员变动会在convertor体现出来，会有不好的后果。要保证managers没有变动
            Long activityManagerId = activityManager.getById(activityId).getActivityManagerId();
            if(!enrollerIds.contains(activityManagerId)){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"报名审核不能更改活动管理员和签到员");
            }

            List<Long> sigIinManagerIds = signinManagerService.getSignInManagerIds(activityId);
            if(!new HashSet<>(enrollerIds).containsAll(sigIinManagerIds)){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"报名审核不能更改活动管理员和签到员");
            }

            List<Long> dbEnrollerIds = listPortalUserIds(activityId);

            enrollersChangeDTO = convertToEnrollmentChanges(activityId, enrollerIds, dbEnrollerIds);
        }
        EnrollersChangeDTO finalEnrollersChangeDTO = enrollersChangeDTO;
    }

    /**
     * 校验活动时间表存在，且当前时间处于该活动的签到时间窗口内
     */
    private void validateSignInTimeWindow(Long activityId){
        ActivityScheduleEntity schedule = getActivitySchedule(activityId);
        LocalDateTime startTime = schedule.getSigninStartTime();
        LocalDateTime endTime = schedule.getSigninEndTime();
        if(startTime==null||endTime==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动未配置签到时间窗口");
        }
        LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(startTime)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到尚未开始");
        }
        if(!now.isBefore(endTime)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到已结束");
        }
    }

    /**
     * 校验活动时间表存在，且当前时间处于该活动的签退时间窗口内
     */
    private void validateSignOutTimeWindow(Long activityId){
        ActivityScheduleEntity schedule = getActivitySchedule(activityId);
        LocalDateTime startTime = schedule.getSignoutStartTime();
        LocalDateTime endTime = schedule.getSignoutEndTime();
        if(startTime==null||endTime==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动未配置签退时间窗口");
        }
        LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(startTime)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签退尚未开始");
        }
        if(!now.isBefore(endTime)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签退已结束");
        }
    }

    /**
     * 获取活动时间表，不存在则报错
     */
    private ActivityScheduleEntity getActivitySchedule(Long activityId){
        ActivityScheduleEntity schedule = activityScheduleManager.getById(activityId);
        if(schedule==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动时间表不存在");
        }
        return schedule;
    }
}