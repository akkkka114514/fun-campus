package com.akkkka.admin.module.business.funcampus.activityEnrollment.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.akkkka.admin.module.business.funcampus.activityEnrollment.constant.RedisKey;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.dao.ActivityEnrollmentDao;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentKeyForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.MyEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.MyEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.PendingSignVO;
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
import com.akkkka.module.support.message.constant.MessageTemplateEnum;
import com.akkkka.module.support.message.domain.MessageTemplateSendForm;
import com.akkkka.module.support.message.service.MessageService;
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

    private final MessageService messageService;

    private static final int QR_CODE_EXPIRE_SECONDS = 30;


    public void enroll(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        //todo做幂等
        if(requestUser == null || requestUser.getUserType()!= UserTypeEnum.PORTAL_USER){
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "用户类型错误");
        }

        try {
            doEnroll(activityId, requestUser.getUserId());
        } catch (BusinessException e) {
            // 报名失败：发送失败原因站内信（发送失败不影响报名结果与原始异常）
            sendEnrollResultMessage(activityId, requestUser.getUserId(), false, extractFailReason(e));
            throw e;
        }
        // 报名成功（事务已提交）：发送站内信
        sendEnrollResultMessage(activityId, requestUser.getUserId(), true, null);
    }

    /**
     * 报名主流程：校验活动状态与用户资格，事务内落库（失败时抛出 BusinessException）
     */
    private void doEnroll(Long activityId, Long userId){
        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        activity.validateStatus(ActivityStatus.ENROLLING);
        // 付费活动：报名必须走「下单-支付」链路（支付成功后由 saveEnrollmentRecord 写报名记录）
        if (Boolean.TRUE.equals(activity.getPaidFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "该活动为付费活动，请先下单并完成支付");
        }

        //检查用户所属学院年级部落是否在活动指定范围内
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        portalUserValidator.validateUserCanEnrollCollege(activityId,portalUser);
        portalUserValidator.validateUserCanEnrollGrade(activityId,portalUser);
        portalUserValidator.validateUserCanEnrollTribe(activityId,portalUser);
        enrollmentDomainService.validateEnrollmentDuplicate(activityId,userId);

        transactionTemplate.executeWithoutResult(status -> {
            // 尝试增加报名人数，如果达到上限则返回false
            if (!activityEnrollNumDao.increaseEnrollNum(activityId)) {
                log.warn("ActivityEnrollmentService.enroll failed: activity enrollment full, activityId={}", activityId);
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动报名人数已满");
            }

            // 如果增加报名人数成功，则写入报名记录（取消过报名的情况会复活原记录）
            if (activityEnrollmentDao.upsertEnrollment(activityId, userId) == 0) {
                log.error("ActivityEnrollmentService.enroll failed: failed to insert enrollment record, activityId={}", activityId);
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "报名失败");
            }

            log.info("ActivityEnrollmentService.enroll success: new enrollment completed, activityId={}, userId={}", activityId, userId);
        });
    }

    /**
     * 支付成功后写入报名记录（供支付回调链路调用，免费报名不经过此方法）
     * <p>
     * - 幂等：已存在有效报名记录时直接返回（重复回调不会重复插入）；
     * - 名额在下单时已通过 increaseEnrollNum 锁定，此处不再占座；
     * - 报名成功站内信由支付成功通知（ACTIVITY_ORDER_PAID）承担，不在本方法发送。
     */
    public void saveEnrollmentRecord(Long activityId, Long userId){
        boolean exists = activityEnrollmentManager.exists(
                Wrappers.lambdaQuery(ActivityEnrollmentEntity.class)
                        .eq(ActivityEnrollmentEntity::getActivityId, activityId)
                        .eq(ActivityEnrollmentEntity::getUserId, userId)
                        .eq(ActivityEnrollmentEntity::getDeletedFlag, false));
        if (exists) {
            log.info("saveEnrollmentRecord skip: enrollment already exists, activityId={}, userId={}", activityId, userId);
            return;
        }

        if (activityEnrollmentDao.upsertEnrollment(activityId, userId) == 0) {
            log.error("saveEnrollmentRecord failed: upsert error, activityId={}, userId={}", activityId, userId);
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "写报名记录失败");
        }
        log.info("saveEnrollmentRecord success: activityId={}, userId={}", activityId, userId);
    }

    /**
     * 发送报名结果站内信（发送失败仅记录日志，不影响报名主流程）
     *
     * @param success    true 报名成功 | false 报名失败
     * @param failReason 报名失败原因（success=false 时使用）
     */
    private void sendEnrollResultMessage(Long activityId, Long userId, boolean success, String failReason){
        try {
            ActivityEntity activity = activityManager.getById(activityId);
            if (activity == null) {
                log.warn("报名结果站内信发送跳过：活动不存在，activityId:{}", activityId);
                return;
            }
            Map<String, Object> contentParam = new HashMap<>();
            contentParam.put("activityTitle", Objects.toString(activity.getTitle(), ""));
            contentParam.put("reason", failReason == null ? "" : failReason);

            MessageTemplateSendForm sendForm = new MessageTemplateSendForm();
            sendForm.setMessageTemplateEnum(success ? MessageTemplateEnum.ACTIVITY_ENROLL_SUCCESS : MessageTemplateEnum.ACTIVITY_ENROLL_FAIL);
            sendForm.setReceiverUserType(UserTypeEnum.PORTAL_USER);
            sendForm.setReceiverUserId(userId);
            sendForm.setDataId(activityId);
            sendForm.setContentParam(contentParam);
            messageService.sendTemplateMessage(sendForm);
            log.info("报名结果站内信已发送，activityId:{}，userId:{}，success:{}", activityId, userId, success);
        } catch (Exception e) {
            log.warn("报名结果站内信发送失败，activityId:{}，userId:{}，success:{}", activityId, userId, success, e);
        }
    }

    /**
     * 从业务异常中提取用户可读的失败原因
     * <p>
     * BusinessException.getMessage() 格式为「错误码描述:详细原因」，此处取冒号后的详细原因
     */
    private String extractFailReason(BusinessException e){
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            return "报名未成功";
        }
        int idx = message.indexOf(':');
        return idx >= 0 && idx + 1 < message.length() ? message.substring(idx + 1) : message;
    }

    /**
     * 分页查询我的报名（联表带出活动标题/封面/时间表）
     */
    public PageResult<MyEnrollmentVO> queryMyEnrollment(MyEnrollmentQueryForm queryForm) {
        Long userId = getCurrentPortalUserId();
        Page<MyEnrollmentVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<MyEnrollmentVO> list = activityEnrollmentDao.queryMyEnrollment(page, userId, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 查询我的待签到活动列表（已报名 + 未签到 + 当前处于签到窗口内）
     */
    public List<PendingSignVO> queryPendingSignInList() {
        return activityEnrollmentDao.queryPendingSignInList(getCurrentPortalUserId());
    }

    /**
     * 查询我的待签退活动列表（已报名 + 已签到 + 需签退 + 未签退 + 当前处于签退窗口内）
     */
    public List<PendingSignVO> queryPendingSignOutList() {
        return activityEnrollmentDao.queryPendingSignOutList(getCurrentPortalUserId());
    }

    /**
     * 取消报名（仅免费活动；付费活动须走退款链路）
     * <p>
     * 校验：报名记录存在且未签到 → 非付费活动 → 未过报名截止时间（半开区间）；
     * 事务内：CAS 逻辑删报名记录 + 释放名额（防重复取消重复释放）
     */
    public void cancelEnrollment(Long activityId) {
        Long userId = getCurrentPortalUserId();
        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        // 报名记录必须存在（未删除）
        ActivityEnrollmentEntity enrollmentEntity = enrollmentDomainService.validateEnrollmentExist(activityId, userId);
        // 已签到的不允许取消
        if (Boolean.TRUE.equals(enrollmentEntity.getSignInStatus())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "已签到，无法取消报名");
        }
        // 付费活动：报名记录来自支付成功，取消报名须走退款链路（退款回调统一释放名额并删除报名记录）
        if (Boolean.TRUE.equals(activity.getPaidFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "付费活动请通过「申请退款」取消报名");
        }
        // 报名截止后不允许取消（半开区间：now < enroll_end_time）
        ActivityScheduleEntity schedule = getActivitySchedule(activityId);
        if (schedule.getEnrollEndTime() != null && !LocalDateTime.now().isBefore(schedule.getEnrollEndTime())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "报名已截止，无法取消报名");
        }

        transactionTemplate.executeWithoutResult(status -> {
            // 先 CAS 逻辑删报名记录（未删除时 1 行，防重复取消重复释放名额）
            LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                    .eq(ActivityEnrollmentEntity::getUserId, userId)
                    .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                    .set(ActivityEnrollmentEntity::getDeletedFlag, true);
            if (!activityEnrollmentManager.update(updateWrapper)) {
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.SERVICE_BUSY, "取消失败，请稍后重试");
            }
            // 释放名额
            if (!activityEnrollNumDao.decreaseEnrollNum(activityId)) {
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.SERVICE_BUSY, "取消失败，请稍后重试");
            }
            log.info("ActivityEnrollmentService.cancelEnrollment success: activityId={}, userId={}", activityId, userId);
        });
    }

    /**
     * 获取当前登录的门户用户id（我的报名相关接口仅允许门户用户访问）
     */
    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (requestUser == null || requestUser.getUserType() != UserTypeEnum.PORTAL_USER) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "用户类型错误");
        }
        return requestUser.getUserId();
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
        //不修改入参（dbEnrollerIds 可能是不可变列表）：新报名者除去共同元素就是要添加的人
        List<Long> toAddIds = new LinkedList<>(enrollerIds);
        toAddIds.removeAll(dbEnrollerIds);
        //旧报名者除去共同元素就是要删除的人
        List<Long> toDelIds = new LinkedList<>(dbEnrollerIds);
        toDelIds.removeAll(enrollerIds);
        if(!toAddIds.isEmpty()){
            for(Long id:toAddIds){
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
        if(!toDelIds.isEmpty()){
            for (Long id:toDelIds){
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

    /**
     * 分页查询报名记录（管理后台）
     * <p>默认仅查询未删除记录；queryForm.deletedFlag 显式传入时可查询已删除记录。
     */
    public PageResult<ActivityEnrollmentVO> queryEnrollmentPage(ActivityEnrollmentQueryForm queryForm) {
        if (queryForm.getDeletedFlag() == null) {
            queryForm.setDeletedFlag(false);
        }
        Page<ActivityEnrollmentVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<ActivityEnrollmentVO> list = activityEnrollmentDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 逻辑删除单条报名记录（管理后台，同步释放名额）
     */
    public void deleteEnrollment(Long activityId, Long userId) {
        Boolean deleted = transactionTemplate.execute(status -> doDeleteEnrollment(activityId, userId));
        if (!Boolean.TRUE.equals(deleted)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "报名记录不存在或已删除");
        }
    }

    /**
     * 批量逻辑删除报名记录（管理后台，单事务；已删除记录自动跳过）
     */
    public void batchDeleteEnrollment(List<ActivityEnrollmentKeyForm> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "删除列表不能为空");
        }
        transactionTemplate.executeWithoutResult(status -> {
            for (ActivityEnrollmentKeyForm key : keyList) {
                doDeleteEnrollment(key.getActivityId(), key.getUserId());
            }
        });
    }

    /**
     * 执行逻辑删除（CAS：仅未删除记录命中；命中的记录同步释放名额）
     */
    private boolean doDeleteEnrollment(Long activityId, Long userId) {
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                .set(ActivityEnrollmentEntity::getDeletedFlag, true);
        if (!activityEnrollmentManager.update(updateWrapper)) {
            log.warn("deleteEnrollment skip: enrollment not found or already deleted, activityId={}, userId={}", activityId, userId);
            return false;
        }
        // 释放名额（释放失败抛异常回滚逻辑删除，保持报名计数与记录一致）
        if (!activityEnrollNumDao.decreaseEnrollNum(activityId)) {
            log.error("deleteEnrollment failed: decreaseEnrollNum error, activityId={}, userId={}", activityId, userId);
            throw new BusinessException(UserErrorCode.SERVICE_BUSY, "删除失败：释放名额异常，请稍后重试");
        }
        log.info("deleteEnrollment success: activityId={}, userId={}", activityId, userId);
        return true;
    }
}
