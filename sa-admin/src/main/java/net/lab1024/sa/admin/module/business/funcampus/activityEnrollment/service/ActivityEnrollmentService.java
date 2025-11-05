package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.entity.OrganizerActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.manager.OrganizerActivityManager;
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
import org.springframework.stereotype.Service;

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
    private OrganizerActivityManager organizerActivityManager;
    @Resource
    private ActivityDao activityDao;
    @Resource
    private PortalUserManager portalUserManager;
    @Resource
    private BackendUserManager backendUserManager;

    /**
     * 分页查询
     */
    public PageResult<ActivityEnrollmentVO> queryPage(ActivityEnrollmentQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityEnrollmentVO> list = activityEnrollmentDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }


    public ResponseDTO<String> enroll(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.PORTAL_USER){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户类型错误");
        }
        ActivityEntity activityEntity = activityManager.getById(activityId);
        //检查活动是否存在，是否已删除
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查活动是否在报名时间
        if(activityEntity.getStatus() != ActivityStatus.START_ENROLL){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始报名或报名已结束");
        }
        // 检查用户是否已经报名过该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, requestUser.getUserId());
        ActivityEnrollmentEntity activityEnrollmentEntity = activityEnrollmentDao.selectOne(queryWrapper);
        if (activityEnrollmentEntity != null) {
            if(activityEnrollmentEntity.getDeletedFlag()){

                LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                        .eq(ActivityEnrollmentEntity::getDeletedFlag, false);
                return transactionTemplate.execute(status -> {
                    // 尝试增加报名人数，如果达到上限则返回false
                    if (!activityEnrollNumDao.increaseEnrollNum(activityId)) {
                        status.setRollbackOnly();
                        return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动报名人数已满");
                    }
                    // 更新活动报名人数
                    if (!activityEnrollmentManager.update(updateWrapper)){
                        status.setRollbackOnly();
                        return ResponseDTO.ok("报名失败");
                    }
                    return ResponseDTO.ok("报名成功");
                });
            }
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
                status.setRollbackOnly();
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动报名人数已满");
            }
            
            // 如果增加报名人数成功，则插入报名记录
            if (activityEnrollmentDao.insert(enrollmentEntity) == 0) {
                status.setRollbackOnly();
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "报名失败");
            }
            
            return ResponseDTO.ok("报名成功");
        });
    }
    public ResponseDTO<String> cancelEnroll(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        ActivityEntity activityEntity = activityManager.getById(activityId);
        //检查活动是否存在，是否已删除
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查活动是否在报名时间
        if(activityEntity.getStatus()!=ActivityStatus.START_ENROLL){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始报名或报名已结束");
        }
        // 检查用户是否已经报名过该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, requestUser.getUserId());
        ActivityEnrollmentEntity activityEnrollmentEntity = activityEnrollmentDao.selectOne(queryWrapper);
        if (activityEnrollmentEntity == null) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "您未报名该活动");
        }

        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, requestUser.getUserId());
        updateWrapper.set(ActivityEnrollmentEntity::getDeletedFlag, true);

        return transactionTemplate.execute(status -> {
            if(!activityEnrollNumDao.decreaseEnrollNum(activityId)||
                    activityEnrollmentManager.update(updateWrapper)){
                status.setRollbackOnly();
            }
            return ResponseDTO.ok("取消报名成功");
        });
    }

    public ResponseDTO<Page<PortalUserVO>> queryEnrollUsersByActivityId(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        BackendUserEntity backendUserEntity = backendUserManager.getById(requestUser.getUserId());
        if(backendUserEntity==null||backendUserEntity.getDeletedFlag()){
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
        return ResponseDTO.ok(portalUserDao.queryByIds(userIds));
    }
    public ResponseDTO<Page<ActivityWithScheduleVO>> queryActivityWithScheduleByPortalUserId(){
        Long portalUserId = SmartRequestUtil.getRequestUserId();
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
        return ResponseDTO.ok(result);
    }

    public ResponseDTO<String> signInQRCode(Long activityId, Long userId){
        //检查用户是否存在，是否已删除
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null || portalUser.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        //检查活动是否存在，是否已删除
        ActivityEntity activityEntity = activityManager.getById(activityId);
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查是否处于等待签到状态
        if(activityEntity.getStatus()!=ActivityStatus.START_SIGNIN){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
        }
        //检查用户是否已报名该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false);
        ActivityEnrollmentEntity enrollmentEntity = activityEnrollmentManager.getOne(queryWrapper);
        if(enrollmentEntity == null){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
        }
        //检查用户是否已签到
        if(enrollmentEntity.getSignInStatus()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户已签到");
        }
        //检查用户报名审核状态
        if(enrollmentEntity.getEnrollReviewStatus()==ReviewStatus.REJECTED){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户报名审核未通过");
        }
        //检查用户签到审核状态
        if(enrollmentEntity.getSigninReviewStatus() == ReviewStatus.REJECTED){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户报名审核未通过");
        }
        //TODO:填入域名
        String qrContent = String.format("activityId=%d&userId=%d", activityId, userId);
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

            return ResponseDTO.ok(base64Image);
        } catch (Exception e) {
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "生成二维码失败");
        }
    }

    public ResponseDTO<Void> signIn(Long activityId, Long userId){
        //检查用户是否存在，是否已删除
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null || portalUser.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        //检查活动是否存在，是否已删除
        ActivityEntity activityEntity = activityManager.getById(activityId);
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查是否处于等待签到状态
        if(activityEntity.getStatus()!=ActivityStatus.START_SIGNIN){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
        }
        //检查用户是否已报名该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false);
        ActivityEnrollmentEntity enrollmentEntity = activityEnrollmentManager.getOne(queryWrapper);
        if(enrollmentEntity == null){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
        }
        //检查用户是否已签到
        if(enrollmentEntity.getSignInStatus()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户已签到");
        }
        //检查用户报名审核状态
        if(enrollmentEntity.getEnrollReviewStatus()==ReviewStatus.REJECTED){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户报名审核未通过");
        }
        //检查用户签到审核状态
        if(enrollmentEntity.getSigninReviewStatus() == ReviewStatus.REJECTED){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户报名审核未通过");
        }
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .set(ActivityEnrollmentEntity::getSignInStatus, true);
        return activityEnrollmentManager.update(updateWrapper) ? ResponseDTO.ok() : ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "签到失败");
    }
    // 后台操作签到
    public ResponseDTO<String> backendOperateSignIn(Long activityId, Long userId){
        ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }

        if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_SIGNIN){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
        }

        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null||portalUser.getDeletedFlag()) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }

        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }

        Long organizerId = requestUser.getUserId();
        LambdaQueryWrapper<OrganizerActivityEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrganizerActivityEntity::getActivityId, activityId)
                .eq(OrganizerActivityEntity::getOrganizerId, organizerId)
                .eq(OrganizerActivityEntity::getDeletedFlag, false);
        OrganizerActivityEntity organizerActivityEntity = organizerActivityManager.getOne(queryWrapper);
        if(organizerActivityEntity==null){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不是活动发起者");
        }

        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity activityEnrollmentEntity = new ActivityEnrollmentEntity();
        activityEnrollmentEntity.setSignInStatus(true);

        activityEnrollmentManager.update(activityEnrollmentEntity ,updateWrapper);
        return ResponseDTO.ok("后台操作签到成功");
    }
    public ResponseDTO<String> batchBackendOperateSignIn(Map<Long, Set<Long>> activityIdUserIdMap){
        if(activityIdUserIdMap == null || activityIdUserIdMap.isEmpty()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数错误");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        Long organizerId = requestUser.getUserId();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        for(Map.Entry<Long, Set<Long>> entry : activityIdUserIdMap.entrySet()){
            Long activityId = entry.getKey();
            Set<Long> userIdSet = entry.getValue();
            ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
            if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
            }
            if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_SIGNIN){
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
            }
            LambdaQueryWrapper<OrganizerActivityEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrganizerActivityEntity::getActivityId, activityId)
                    .eq(OrganizerActivityEntity::getOrganizerId, organizerId)
                    .eq(OrganizerActivityEntity::getDeletedFlag, false);
            OrganizerActivityEntity organizerActivityEntity = organizerActivityManager.getOne(queryWrapper);
            if(organizerActivityEntity==null){
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不是活动发起者");
            }
            for(Long userId : userIdSet){
                PortalUserEntity portalUser = portalUserManager.getById(userId);
                if(portalUser==null||portalUser.getDeletedFlag()) {
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
                }
                LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper2 = new LambdaQueryWrapper<>();
                queryWrapper2.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                        .eq(ActivityEnrollmentEntity::getUserId, userId);
                ActivityEnrollmentEntity activityEnrollmentEntity = activityEnrollmentManager.getOne(queryWrapper2);
                if(activityEnrollmentEntity==null||activityEnrollmentEntity.getDeletedFlag()){
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
                }
                if(activityEnrollmentEntity.getSigninReviewStatus()==ReviewStatus.REJECTED){
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
        return ResponseDTO.ok("批量后台操作签到成功");
    }
    public ResponseDTO<String> RejectSignIn(Long activityId, Long userId){
        ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_SIGNIN){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始签到或签到已结束");
        }
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null||portalUser.getDeletedFlag()) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity activityEnrollmentEntity = new ActivityEnrollmentEntity();
        activityEnrollmentEntity.setSignInStatus(false);
        activityEnrollmentEntity.setSigninReviewStatus(ReviewStatus.REJECTED);
        activityEnrollmentManager.update(activityEnrollmentEntity ,updateWrapper);
        return ResponseDTO.ok("拒绝签到成功");
    }

    public ResponseDTO<String> passEnrollReview(Long activityId, Long userId){
        ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_SIGNIN
                &&activityWithScheduleVO.getStatus()!=ActivityStatus.END_ENROLL){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始报名或活动已开始");
        }
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity activityEnrollmentEntity = activityEnrollmentManager.getOne(queryWrapper);
        if(activityEnrollmentEntity==null||activityEnrollmentEntity.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户未报名该活动");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        transactionTemplate.executeWithoutResult(status -> {
            LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                    .eq(ActivityEnrollmentEntity::getUserId, userId);
            ActivityEnrollmentEntity updateEntity = new ActivityEnrollmentEntity();
            activityEnrollmentEntity.setEnrollReviewStatus(ReviewStatus.PASSED);
            if(activityEnrollmentManager.update(updateEntity, updateWrapper)){
                status.setRollbackOnly();
            }
        });
        return ResponseDTO.ok("通过审核成功");
    }
    public ResponseDTO<String> batchPassEnrollReview(Map<Long, Long>  activityIdUserIdMap){
        if(activityIdUserIdMap == null || activityIdUserIdMap.isEmpty()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数错误");
        }
        //passEnrollReview中会做校验，所以这里不需要再次做校验
        for(Map.Entry<Long, Long> entry: activityIdUserIdMap.entrySet()){
            passEnrollReview(entry.getKey(), entry.getValue());
        }
        //TODO 出错时告知出错的那一个
        return ResponseDTO.ok("批量通过签到成功");
    }
    public ResponseDTO<String> rejectEnrollReview(Long activityId, Long userId){
        ActivityWithScheduleVO activityWithScheduleVO = activityDao.getActivityWithScheduleById(activityId);
        if(activityWithScheduleVO==null||activityWithScheduleVO.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        if(activityWithScheduleVO.getStatus()!=ActivityStatus.START_ENROLL
                &&activityWithScheduleVO.getStatus()!=ActivityStatus.END_ENROLL){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动未开始报名或活动已开始");
        }
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null||portalUser.getDeletedFlag()) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser.getUserType()!= UserTypeEnum.ADMIN_BACKEND_USER){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "用户权限不足");
        }
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity activityEnrollmentEntity = new ActivityEnrollmentEntity();
        activityEnrollmentEntity.setEnrollReviewStatus(ReviewStatus.REJECTED);
        transactionTemplate.executeWithoutResult(status -> {
            if(!activityEnrollNumDao.decreaseEnrollNum(activityId)||
                activityEnrollmentManager.update(activityEnrollmentEntity, updateWrapper)){
                status.setRollbackOnly();
            }
        });
        return ResponseDTO.ok("拒绝审核成功");
    }
    public ResponseDTO<String> batchRejectEnrollReview(Map<Long, Long>  activityIdUserIdMap){
        if(activityIdUserIdMap == null || activityIdUserIdMap.isEmpty()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数错误");
        }
        for(Map.Entry<Long, Long> entry: activityIdUserIdMap.entrySet()){
            passEnrollReview(entry.getKey(), entry.getValue());
        }
        //TODO 出错时告知出错的那一个
        return ResponseDTO.ok("批拒绝报名成功");
    }
}