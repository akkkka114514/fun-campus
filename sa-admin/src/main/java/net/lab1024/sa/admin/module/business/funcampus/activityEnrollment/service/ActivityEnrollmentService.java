package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.constant.RedisKey;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.constant.ReviewStatus;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.dao.ActivityEnrollmentDao;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.dao.PortalUserDao;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.manager.BackendUserManager;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
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
public class ActivityEnrollmentService {

    @Resource
    private ActivityEnrollmentDao activityEnrollmentDao;
    @Resource
    private ActivityEnrollmentManager activityEnrollmentManager;
    @Resource
    private ActivityManager activityManager;
    @Resource
    private ActivityEnrollNumDao activityEnrollNumDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private PortalUserDao portalUserDao;
    @Resource
    private ActivityDao activityDao;
    @Resource
    private PortalUserManager portalUserManager;
    @Resource
    private BackendUserManager backendUserManager;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 分页查询
     */
    public PageResult<ActivityEnrollmentVO> queryPage(ActivityEnrollmentQueryForm queryForm) {
        log.info("ActivityEnrollmentService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityEnrollmentVO> list = activityEnrollmentDao.queryPage(page, queryForm);
        log.info("ActivityEnrollmentService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }


    public ResponseDTO<String> enroll(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        log.info("ActivityEnrollmentService.enroll called, activityId={}, userId={}", activityId, requestUser != null ? requestUser.getUserId() : null);
        if(requestUser==null){
            log.warn("ActivityEnrollmentService.enroll failed: user not login");
            return ResponseDTO.error(UserErrorCode.LOGIN_STATE_INVALID);
        }
        if(requestUser.getUserType()!= UserTypeEnum.PORTAL_USER){
            log.warn("ActivityEnrollmentService.enroll failed: user type error, userType={}", requestUser.getUserType());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户类型错误");
        }
        ActivityEntity activityEntity = activityManager.getById(activityId);
        //检查活动是否存在，是否已删除
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.enroll failed: activity not found or deleted, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查活动是否在报名时间
        if(activityEntity.getStatus() != ActivityStatus.START_ENROLL){
            log.warn("ActivityEnrollmentService.enroll failed: activity not in enrollment period, activityId={}, status={}", activityId, activityEntity.getStatus());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始报名或报名已结束");
        }
        // 检查用户是否已经报名过该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, requestUser.getUserId());
        ActivityEnrollmentEntity activityEnrollmentEntity = activityEnrollmentDao.selectOne(queryWrapper);
        if (activityEnrollmentEntity != null) {
            if(activityEnrollmentEntity.getDeletedFlag()){
                log.debug("ActivityEnrollmentService.enroll: user previously enrolled but deleted, attempting to re-enroll");

                LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                        .eq(ActivityEnrollmentEntity::getDeletedFlag, false);
                return transactionTemplate.execute(status -> {
                    // 尝试增加报名人数，如果达到上限则返回false
                    if (!activityEnrollNumDao.increaseEnrollNum(activityId)) {
                        log.warn("ActivityEnrollmentService.enroll failed: activity enrollment full, activityId={}", activityId);
                        status.setRollbackOnly();
                        return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动报名人数已满");
                    }
                    // 更新活动报名人数
                    if (!activityEnrollmentManager.update(updateWrapper)){
                        log.error("ActivityEnrollmentService.enroll failed: failed to update enrollment record, activityId={}", activityId);
                        status.setRollbackOnly();
                        return ResponseDTO.ok("报名失败");
                    }
                    log.info("ActivityEnrollmentService.enroll success: re-enrollment completed, activityId={}, userId={}", activityId, requestUser.getUserId());
                    return ResponseDTO.ok("报名成功");
                });
            }
            log.warn("ActivityEnrollmentService.enroll failed: user already enrolled, activityId={}, userId={}", activityId, requestUser.getUserId());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "您已报名该活动");
        }
        
        ActivityEnrollmentEntity enrollmentEntity = new ActivityEnrollmentEntity();
        enrollmentEntity.setActivityId(activityId);
        enrollmentEntity.setUserId(requestUser.getUserId());
        enrollmentEntity.setSignInStatus(false);
        enrollmentEntity.setDeletedFlag(false);

        return transactionTemplate.execute(status -> {
            // 尝试增加报名人数，如果达到上限则返回false
            if (!activityEnrollNumDao.increaseEnrollNum(activityId)) {
                log.warn("ActivityEnrollmentService.enroll failed: activity enrollment full, activityId={}", activityId);
                status.setRollbackOnly();
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动报名人数已满");
            }
            
            // 如果增加报名人数成功，则插入报名记录
            if (activityEnrollmentDao.insert(enrollmentEntity) == 0) {
                log.error("ActivityEnrollmentService.enroll failed: failed to insert enrollment record, activityId={}", activityId);
                status.setRollbackOnly();
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "报名失败");
            }
            
            log.info("ActivityEnrollmentService.enroll success: new enrollment completed, activityId={}, userId={}", activityId, requestUser.getUserId());
            return ResponseDTO.ok("报名成功");
        });
    }
    public ResponseDTO<String> cancelEnroll(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        log.info("ActivityEnrollmentService.cancelEnroll called, activityId={}, userId={}", activityId, requestUser != null ? requestUser.getUserId() : null);
        
        ActivityEntity activityEntity = activityManager.getById(activityId);
        //检查活动是否存在，是否已删除
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.cancelEnroll failed: activity not found or deleted, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查活动是否在报名时间
        if(activityEntity.getStatus()!=ActivityStatus.START_ENROLL){
            log.warn("ActivityEnrollmentService.cancelEnroll failed: activity not in enrollment period, activityId={}, status={}", activityId, activityEntity.getStatus());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始报名或报名已结束");
        }
        // 检查用户是否已经报名过该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, requestUser.getUserId());
        ActivityEnrollmentEntity activityEnrollmentEntity = activityEnrollmentDao.selectOne(queryWrapper);
        if (activityEnrollmentEntity == null) {
            log.warn("ActivityEnrollmentService.cancelEnroll failed: user not enrolled, activityId={}, userId={}", activityId, requestUser.getUserId());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "您未报名该活动");
        }

        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, requestUser.getUserId());
        updateWrapper.set(ActivityEnrollmentEntity::getDeletedFlag, true);

        return transactionTemplate.execute(status -> {
            if(!activityEnrollNumDao.decreaseEnrollNum(activityId)||
                    activityEnrollmentManager.update(updateWrapper)){
                log.error("ActivityEnrollmentService.cancelEnroll failed: failed to update enrollment or decrease count, activityId={}", activityId);
                status.setRollbackOnly();
            }
            log.info("ActivityEnrollmentService.cancelEnroll success: enrollment cancelled, activityId={}, userId={}", activityId, requestUser.getUserId());
            return ResponseDTO.ok("取消报名成功");
        });
    }

    public ResponseDTO<Page<PortalUserVO>> queryEnrollUsersByActivityId(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        log.info("ActivityEnrollmentService.queryEnrollUsersByActivityId called, activityId={}, userId={}", activityId, requestUser != null ? requestUser.getUserId() : null);
        
        BackendUserEntity backendUserEntity = backendUserManager.getById(requestUser.getUserId());
        if(backendUserEntity==null||backendUserEntity.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.queryEnrollUsersByActivityId failed: backend user not found or deleted, userId={}", requestUser.getUserId());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        //TODO: 添加活动状态条件，删除状态条件
        //TODO: 添加用户签到，报名状态条件
        //TODO: 添加审核状态条件
        List<ActivityEnrollmentEntity> activityEnrollmentList = activityEnrollmentManager.list(new LambdaQueryWrapper<ActivityEnrollmentEntity>()
                .eq(ActivityEnrollmentEntity::getActivityId, activityId));
        List<Long> userIds = activityEnrollmentList
                            .stream()
                            .map(ActivityEnrollmentEntity::getUserId)
                            .toList();
        log.info("ActivityEnrollmentService.queryEnrollUsersByActivityId result: found {} users", userIds.size());
        return ResponseDTO.ok(portalUserDao.queryByIds(userIds));
    }
    public ResponseDTO<Page<ActivityWithScheduleVO>> queryActivityWithScheduleByPortalUserId(){
        Long portalUserId = SmartRequestUtil.getRequestUserId();
        log.info("ActivityEnrollmentService.queryActivityWithScheduleByPortalUserId called, userId={}", portalUserId);
        
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getUserId, portalUserId);
        //TODO: 添加活动状态条件，删除状态条件
        //TODO: 添加用户签到，报名状态条件
        List<ActivityEnrollmentEntity> activityEnrollmentList = activityEnrollmentManager.list(queryWrapper);
        List<Long> activityIds = activityEnrollmentList
                            .stream()
                            .map(ActivityEnrollmentEntity::getActivityId)
                            .toList();
        Page<ActivityWithScheduleVO> result = activityDao.getByIds(activityIds);
        log.info("ActivityEnrollmentService.queryActivityWithScheduleByPortalUserId result: found {} activities", activityIds.size());
        return ResponseDTO.ok(result);
    }

    public ResponseDTO<String> signInQRCode(Long activityId, Long userId){
        log.info("ActivityEnrollmentService.signInQRCode called, activityId={}, userId={}", activityId, userId);
        
        //检查用户是否存在，是否已删除
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null || portalUser.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.signInQRCode failed: user not found or deleted, userId={}", userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        //检查活动是否存在，是否已删除
        ActivityEntity activityEntity = activityManager.getById(activityId);
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.signInQRCode failed: activity not found or deleted, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查是否处于等待签到状态
        if(activityEntity.getStatus()!=ActivityStatus.START_SIGNIN){
            log.warn("ActivityEnrollmentService.signInQRCode failed: activity not in sign-in period, activityId={}, status={}", activityId, activityEntity.getStatus());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
        }
        //检查用户是否已报名该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false);
        ActivityEnrollmentEntity enrollmentEntity = activityEnrollmentManager.getOne(queryWrapper);
        if(enrollmentEntity == null){
            log.warn("ActivityEnrollmentService.signInQRCode failed: user not enrolled, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
        }
        //检查用户是否已签到
        if(enrollmentEntity.getSignInStatus()){
            log.warn("ActivityEnrollmentService.signInQRCode failed: user already signed in, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户已签到");
        }
        //检查用户签到审核状态
        if(enrollmentEntity.getSigninReviewStatus() == ReviewStatus.REJECTED){
            log.warn("ActivityEnrollmentService.signInQRCode failed: sign-in review rejected, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户报名审核未通过");
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String redisKey = RedisKey.SIGN_IN_CODE_KEY(userId, activityId);
        redisTemplate.opsForValue().set(redisKey, uuid, 45, TimeUnit.SECONDS);
        //TODO:填入域名
        String qrContent = String.format(
                "activityId=%d&userId=%d&uuid=%s",
                activityId,
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
            
            log.info("ActivityEnrollmentService.signInQRCode success: QR code generated, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.ok(base64Image);
        } catch (Exception e) {
            log.error("ActivityEnrollmentService.signInQRCode failed: failed to generate QR code, activityId={}, userId={}", activityId, userId, e);
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "生成二维码失败");
        }
    }

    public ResponseDTO<Void> signIn(Long activityId, Long userId){
        log.info("ActivityEnrollmentService.signIn called, activityId={}, userId={}", activityId, userId);
        
        //检查用户是否存在，是否已删除
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null || portalUser.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.signIn failed: user not found or deleted, userId={}", userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        //检查活动是否存在，是否已删除
        ActivityEntity activityEntity = activityManager.getById(activityId);
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            log.warn("ActivityEnrollmentService.signIn failed: activity not found or deleted, activityId={}", activityId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查是否处于等待签到状态
        if(activityEntity.getStatus()!=ActivityStatus.START_SIGNIN){
            log.warn("ActivityEnrollmentService.signIn failed: activity not in sign-in period, activityId={}, status={}", activityId, activityEntity.getStatus());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
        }
        //检查用户是否已报名该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false);
        ActivityEnrollmentEntity enrollmentEntity = activityEnrollmentManager.getOne(queryWrapper);
        if(enrollmentEntity == null){
            log.warn("ActivityEnrollmentService.signIn failed: user not enrolled, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
        }
        //检查用户是否已签到
        if(enrollmentEntity.getSignInStatus()){
            log.warn("ActivityEnrollmentService.signIn failed: user already signed in, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户已签到");
        }
        //检查用户签到审核状态
        if(enrollmentEntity.getSigninReviewStatus() == ReviewStatus.REJECTED){
            log.warn("ActivityEnrollmentService.signIn failed: sign-in review rejected, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户报名审核未通过");
        }
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .set(ActivityEnrollmentEntity::getSignInStatus, true);
        boolean result = activityEnrollmentManager.update(updateWrapper);
        if (result) {
            log.info("ActivityEnrollmentService.signIn success: user signed in, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.ok();
        } else {
            log.error("ActivityEnrollmentService.signIn failed: failed to update sign-in status, activityId={}, userId={}", activityId, userId);
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "签到失败");
        }
    }


}