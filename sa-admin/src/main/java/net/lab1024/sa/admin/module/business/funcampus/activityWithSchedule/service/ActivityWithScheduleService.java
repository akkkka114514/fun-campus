package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.entity.ActivityCategoryEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.manager.ActivityCategoryManager;
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
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.manager.GradeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.entity.OrganizationInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.entity.SchoolInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.tribe.manager.TribeManager;
import net.lab1024.sa.admin.module.system.backendUser.manager.BackendUserManager;
import net.lab1024.sa.base.common.code.UnexpectedErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageParam;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;
import java.util.Objects;

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

    /**
     * 同时添加活动和活动时间表
     *
     * @param addForm 活动和时间表信息
     * @return ResponseDTO
     */
    public ResponseDTO<String> publishActivityWithSchedule(ActivityWithScheduleAddForm addForm) {
        log.info("ActivityWithScheduleService.addActivityWithSchedule called, title={}", addForm.getTitle());
        //检查用户是否有发布活动的权限
        //activityBelongToCollegeId和activityBelongToOrganizationId不能同时为空或同时不为空
        if((addForm.getActivityBelongToCollegeId() != null && addForm.getActivityBelongToOrganizationId() != null)||
            addForm.getActivityBelongToCollegeId() == null && addForm.getActivityBelongToOrganizationId() == null){
            log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: activityBelongToCollegeId and activityBelongToOrganizationId cannot be both null or both not null");
            return ResponseDTO.userErrorParam("活动所属学院和活动所属组织不能同时为空或同时不为空");
        }
        //检查活动所属学校是否存在
        LambdaQueryWrapper<SchoolInfoEntity> schoolInfoQw = new LambdaQueryWrapper<>();
        schoolInfoQw.eq(SchoolInfoEntity::getId, addForm.getActivityBelongToSchoolId())
                .eq(SchoolInfoEntity::getDeletedFlag, false);
        SchoolInfoEntity schoolInfoEntity = schoolInfoManager.getOne(schoolInfoQw);
        if(schoolInfoEntity == null){
            log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: school not found, schoolId={}", addForm.getActivityBelongToSchoolId());
            return ResponseDTO.userErrorParam("活动所属学校不存在");
        }
        //如果活动所属学院字段不为null,则说明是学院活动
        //检查学院是否存在,以及与表单中的活动所属学院id一致
        if(addForm.getActivityBelongToCollegeId() != null){
            LambdaQueryWrapper<CollegeInfoEntity> collegeInfoQw = new LambdaQueryWrapper<>();
            collegeInfoQw.eq(CollegeInfoEntity::getId, addForm.getActivityBelongToCollegeId())
                    .eq(CollegeInfoEntity::getDeletedFlag, false);
            CollegeInfoEntity collegeInfoEntity = collegeInfoManager.getOne(collegeInfoQw);
            if(collegeInfoEntity == null){
                log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: college not found, collegeId={}", addForm.getActivityBelongToCollegeId());
                return ResponseDTO.userErrorParam("活动所属学院不存在");
            }
            if(!Objects.equals(collegeInfoEntity.getSchoolId(), addForm.getActivityBelongToSchoolId())){
                log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: college not belong to school, collegeId={}, schoolId={}", addForm.getActivityBelongToCollegeId(), addForm.getActivityBelongToSchoolId());
                return ResponseDTO.userErrorParam("活动所属学院不属于活动所属学校");
            }
        }
        //如果活动所属组织字段不为null,则说明是组织活动
        //检查组织是否存在,以及与表单中的活动所属组织id一致
        if(addForm.getActivityBelongToOrganizationId() != null){
            LambdaQueryWrapper<OrganizationInfoEntity> organizationInfoQw = new LambdaQueryWrapper<>();
            organizationInfoQw.eq(OrganizationInfoEntity::getId, addForm.getActivityBelongToOrganizationId())
                    .eq(OrganizationInfoEntity::getDeletedFlag, false);
            OrganizationInfoEntity organizationInfoEntity = organizationInfoManager.getOne(organizationInfoQw);
            if(organizationInfoEntity == null){
                log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: organization not found, organizationId={}", addForm.getActivityBelongToOrganizationId());
                return ResponseDTO.userErrorParam("活动所属组织不存在");
            }
            if(!Objects.equals(organizationInfoEntity.getSchoolId(), addForm.getActivityBelongToSchoolId())){
                log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: organization not belong to school, organizationId={}, schoolId={}", addForm.getActivityBelongToOrganizationId(), addForm.getActivityBelongToSchoolId());
                return ResponseDTO.userErrorParam("活动所属组织不属于活动所属学校");
            }
        }
        //检查输入的活动分类是否存在
        LambdaQueryWrapper<ActivityCategoryEntity> activityCategoryQw = new LambdaQueryWrapper<>();
        activityCategoryQw.eq(ActivityCategoryEntity::getId, addForm.getCategoryId())
                .eq(ActivityCategoryEntity::getDeletedFlag, false);
        ActivityCategoryEntity activityCategoryEntity = activityCategoryManager.getOne(activityCategoryQw);
        if(activityCategoryEntity == null){
            log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: activity category not found, categoryId={}", addForm.getCategoryId());
            return ResponseDTO.userErrorParam("活动分类不存在");
        }

        //处理封面图片，根据您的问题，您想了解在用户填写活动表单的过程中是否可以提前上传封面图片，以及如何处理这种场景。
        //关于上传过程的建议
        //1. 异步上传机制
        //可以实现拖拽/点击上传功能，让用户在填写表单前就上传封面
        //图片先上传到服务器并返回一个临时 URL 或 ID
        //在表单提交时将这个临时 ID 绑定到活动数据上
        //2. 临时存储策略
        //上传后的图片可以先存储为临时状态
        //设置过期时间（例如 24 小时），若用户未完成表单提交，则删除临时文件
        //表单成功提交后，将临时文件转为正式文件并更新数据库记录
        //3. 用户体验优化
        //支持预览功能，用户可即时查看上传效果
        //提供重新上传按钮，允许替换已上传的图片
        //显示上传进度条，提升交互体验
        //后续处理流程
        //1. 表单提交阶段
        //将临时图片 ID 与活动数据一起提交
        //服务器接收到请求后，将临时文件状态改为正式状态
        //清理未使用的临时文件
        //2. 异常处理
        //如果表单提交失败，保留临时图片供用户重试
        //用户离开页面时提示保存草稿，避免数据丢失
        //这种方式可以让用户更灵活地处理图片上传，同时保证数据一致性。您可以根据具体业务需求调整临时文件的处理逻辑
        //TODO 图片上传处理

        //如果院系年级不为空，为按院系年级进行参与
        //检查输入的院系年级是否存在
        if(addForm.getCanEnrollGradeIdList()!=null && !addForm.getCanEnrollGradeIdList().isEmpty()){
            if(gradeInfoManager
                    .getBaseMapper()
                    .selectByIds(addForm.getCanEnrollGradeIdList())
                    .size()!=addForm.getCanEnrollGradeIdList().size()){
                log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: grade not found, gradeIdList={}", addForm.getCanEnrollGradeIdList());
                return ResponseDTO.userErrorParam("活动所属年级不存在");
            }
        }


        //如果部落不为空，为按部落进行参与
        //检查输入的部落是否存在
        if(addForm.getCanEnrollTribeIdList()!=null && !addForm.getCanEnrollTribeIdList().isEmpty()){
            if(tribeManager
                    .getBaseMapper()
                    .selectByIds(addForm.getCanEnrollTribeIdList())
                    .size()!=addForm.getCanEnrollTribeIdList().size()){
                log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: tribe not found, tribeIdList={}", addForm.getCanEnrollTribeIdList());
                return ResponseDTO.userErrorParam("活动所属部落不存在");
            }
        }

        //活动中不能有和添加活动标题一致的
        LambdaQueryWrapper<ActivityEntity> activityEntityQw = new LambdaQueryWrapper<>();
        activityEntityQw.eq(ActivityEntity::getTitle, addForm.getTitle())
                .eq(ActivityEntity::getDeletedFlag, false);
        if(activityManager.getOne(activityEntityQw)!= null){
            log.warn("ActivityWithScheduleService.addActivityWithSchedule failed: activity with same title already exists, title={}", addForm.getTitle());
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

        //要插入的activityEnrollNum
        ActivityEnrollNum activityEnrollNum = new ActivityEnrollNum();
        activityEnrollNum.setActivityId(null);
        activityEnrollNum.setEnrollNum(0);
        //开始事务
        return transactionTemplate.execute(status -> {
            try {
                if(!activityManager.save(activityEntity)){
                    log.error("ActivityWithScheduleService.addActivityWithSchedule failed: failed to save activity, title={}", addForm.getTitle());
                    status.setRollbackOnly();
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "保存失败");
                }
                Long id = activityEntity.getId();
                log.debug("ActivityWithScheduleService.addActivityWithSchedule: activity saved, activityId={}", id);
                
                scheduleEntity.setActivityId(id);
                activityEnrollNum.setActivityId(id);
                //保存activity和activity时间表
                    if (!activityScheduleManager.save(scheduleEntity) ||
                        activityEnrollNumDao.insert(activityEnrollNum)==0) {
                        log.error("ActivityWithScheduleService.addActivityWithSchedule failed: failed to save schedule or enrollNum, activityId={}", id);
                        status.setRollbackOnly();
                        return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "保存失败");
                    }

                log.info("ActivityWithScheduleService.addActivityWithSchedule success: activity created, activityId={}", id);
                return ResponseDTO.ok("保存成功");
            } catch (Exception e) {
                log.error("ActivityWithScheduleService.addActivityWithSchedule failed: exception occurred, title={}", addForm.getTitle(), e);
                status.setRollbackOnly();
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "保存过程中发生异常：" + e.getMessage());
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