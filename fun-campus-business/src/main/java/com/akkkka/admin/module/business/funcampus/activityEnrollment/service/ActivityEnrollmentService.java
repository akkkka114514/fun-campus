package com.akkkka.admin.module.business.funcampus.activityEnrollment.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.akkkka.admin.module.business.funcampus.activityEnrollment.dao.ActivityEnrollmentDao;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.dto.EnrollersChangeDTO;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.SignInManagerDomainService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    private final ActivityEnrollmentDomainService enrollmentDomainService;

    private final SignInManagerDomainService signInManagerDomainService;

    private final RedisTemplate<String,String> redisTemplate;

    private static String SIGN_IN_QR_CODE_TOKEN_REDIS_KEY(Long userId){
        return "sign_in:qr_code_token:"+userId;
    }


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


    public void enroll(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();

        assert requestUser!=null;
        if(requestUser.getUserType()!= UserTypeEnum.PORTAL_USER){
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "用户类型错误");
        }

        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        activity.validateStatus(ActivityStatus.START_ENROLL);

        //检查用户所属学院年级部落是否在活动指定范围内
        PortalUserEntity portalUser = portalUserManager.getById(requestUser.getUserId());
        portalUserValidator.validateUserInCollege(activityId,portalUser);
        portalUserValidator.validateUserInGrade(activityId,portalUser);
        portalUserValidator.validateUserInTribe(activityId,portalUser);
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
     */
    public String signInQRCode(){
        Long userId = SmartRequestUtil.getRequestUserId();
        assert userId!=null;
        String redisKey = SIGN_IN_QR_CODE_TOKEN_REDIS_KEY(userId);
        String uuid=UUID.randomUUID().toString().replace("-","");
        if(redisTemplate.opsForValue().get(redisKey)!=null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"不支持手动刷新二维码");
        }
        redisTemplate.opsForValue().set(redisKey,uuid,30,TimeUnit.SECONDS);
        //TODO:填入域名
        String qrContent = String.format(
                "token=%s",
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
            
            log.info("ActivityEnrollmentService.signInQRCode success: QR code generated, userId={}",  userId);
            return base64Image;
        } catch (Exception e) {
            log.error("ActivityEnrollmentService.signInQRCode failed: failed to generate QR code, userId={}", userId);
            throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "生成二维码失败");
        }
    }
    public void signIn(Long activityId, Long needSignInUserId, String uuid){
        String redisKey = SIGN_IN_QR_CODE_TOKEN_REDIS_KEY(needSignInUserId);
        String realUuid = redisTemplate.opsForValue().get(redisKey);
        if (realUuid==null||!Objects.equals(realUuid,uuid)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"二维码已过期");
        }
        //检查活动是否存在，是否已删除
        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        //检查是否处于等待签到状态
        activity.validateStatus(ActivityStatus.START_ENROLL);
        //检查用户是否已报名该活动
        ActivityEnrollmentEntity enrollmentEntity = enrollmentDomainService.validateEnrollmentExist(activityId,needSignInUserId);
        //检查用户是否已签到
        enrollmentEntity.validateSignInStatus();
        //检查操作user是否为该活动的signin manager
        Long signInManagerId = SmartRequestUtil.getRequestUserId();
        signInManagerDomainService.validateUserPermission(signInManagerId,activityId);

        portalUserValidator.validatePortalUserId(needSignInUserId);
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, needSignInUserId)
                .set(ActivityEnrollmentEntity::getSignInStatus, true);
        boolean result = activityEnrollmentManager.update(updateWrapper);
        if (result) {
            log.info("ActivityEnrollmentService.signIn success: user signed in, activityId={}, userId={}", activityId, needSignInUserId);
        } else {
            throw new BusinessException(UserErrorCode.SERVICE_BUSY);
        }
    }



    public static LambdaQueryWrapper<ActivityEnrollmentEntity> listByActivityIdQw(Long activityId){
        LambdaQueryWrapper<ActivityEnrollmentEntity> qw = new LambdaQueryWrapper<>();
        return qw.eq(ActivityEnrollmentEntity::getActivityId,activityId)
            .eq(ActivityEnrollmentEntity::getDeletedFlag,false);
    }

    public List<SimplePortalUserVO> listEnrollUser(Long activityId){
        if(activityManager.getById(activityId)==null){
            log.warn("不存在的activityId:{}",activityId);
            return null;
        }
        List<ActivityEnrollmentEntity> list = activityEnrollmentManager.list(
                listByActivityIdQw(activityId)
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

    public EnrollersChangeDTO convertToEnrollmentChanges(List<Long> enrollerIds, Long activityId){
        //假定activityId正确
        EnrollersChangeDTO  enrollersChangeDTO = new EnrollersChangeDTO();
        List<Long> copy = new LinkedList<>(enrollerIds);
        LambdaQueryWrapper<ActivityEnrollmentEntity> qw = listByActivityIdQw(activityId)
                                                            .select(ActivityEnrollmentEntity::getUserId);
        List<ActivityEnrollmentEntity> dbEnrollerList = activityEnrollmentManager.list(qw);
        assert dbEnrollerList!=null;
        assert !dbEnrollerList.isEmpty();

        List<Long> dbEnrollerIds = new ArrayList<>(dbEnrollerList.stream()
                .map(ActivityEnrollmentEntity::getUserId)
                .toList());
        
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

}