package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.dao.ActivityEnrollmentDao;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;

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
        ActivityEntity activityEntity = activityManager.getById(activityId);
        //检查活动是否存在，是否已删除
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        //检查活动是否在报名时间
        if(activityEntity.getStatus()!= ActivityStatus.START_ENROLL){
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
        if(activityEntity==null||
                activityEntity.getDeletedFlag()){
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
}