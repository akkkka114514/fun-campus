package com.akkkka.admin.module.business.funcampus.activityWithSchedule.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.service.ActivityCanEnrollCollegeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.service.ActivityCanEnrollGradeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.service.ActivityCanEnrollTribeService;
import com.akkkka.admin.module.business.funcampus.activityCategory.service.ActivityCategoryService;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogService;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogValidator;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewStateMachineContext;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.IndexActivityPageConst;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleQueryForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityDetailVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityPhaseCountdownVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityReviewProposalVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityScheduleVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.EditActivityDraftSelectionsVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.IndexActivityVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleAddFormValidator;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleUpdateFormValidator;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import com.akkkka.admin.module.business.funcampus.gradeInfo.service.GradeInfoService;
import com.akkkka.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoService;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import com.akkkka.admin.module.system.backendUser.service.BackendUserService;
import com.akkkka.admin.module.system.backendUser.service.BackendUserValidator;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import com.alibaba.cola.statemachine.StateMachine;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

/**
 * 活动和时间表 组合控制器
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动和时间表管理")
@RequestMapping("portal")
public class ActivityWithScheduleController {

    @Resource
    private ActivityWithScheduleService activityWithScheduleService;
    @Resource
    private CollegeInfoService collegeInfoService;
    @Resource
    private OrganizationInfoService organizationInfoService;
    @Resource
    private GradeInfoService gradeInfoService;
    @Resource
    private ActivityCategoryService activityCategoryService;
    @Resource
    private BackendUserService backendUserService;
    @Resource
    private ActivityReviewLogValidator activityReviewLogValidator;
    @Resource
    private ActivityWithScheduleAddFormValidator activityWithScheduleAddFormValidator;
    @Resource
    private StateMachine<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext>
            stateMachine;
    @Resource
    private ActivityReviewLogService reviewLogService;
    @Resource
    private BackendUserValidator backendUserValidator;
    @Resource
    private ActivityCanEnrollCollegeService collegeService;
    @Resource
    private ActivityCanEnrollTribeService tribeService;
    @Resource
    private ActivityCanEnrollGradeService gradeService;
    @Resource
    private ActivityManager activityManager;
    @Resource
    private ActivityScheduleManager activityScheduleManager;
    @Resource
    private PortalUserManager portalUserManager;
    @Resource
    private ActivityDao activityDao;
    
    @Operation(summary = "添加活动和时间表 @author akkkka114514")
    @PostMapping("/activity/draft/submit")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<Void> submitDraft(@RequestBody @Valid ActivityWithScheduleAddForm addForm) {
        if(Objects.isNull(addForm)){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        // TODO: create ActivityWithScheduleAddFormValidator
        stateMachine.fireEvent(ActivityReviewStage.DRAFT,ActivityReviewEvent.SUBMIT,new ActivityReviewStateMachineContext());
        return ResponseDTO.ok();
    }

    @Operation(summary = "删除活动和时间表 @author akkkka114514")
    @PostMapping("/activity/delete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> deleteActivityWithSchedule(@RequestBody Long activityId) {
        activityWithScheduleService.deleteDraft(activityId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "更新活动和时间表 @author akkkka114514")
    @PostMapping("/activity/update")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> updateActivityWithSchedule(@RequestBody @Valid ActivityWithScheduleUpdateForm updateForm) {
        if(Objects.isNull(updateForm)){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        ActivityWithScheduleUpdateFormValidator updateValidator = new ActivityWithScheduleUpdateFormValidator();
        updateValidator.validate(updateForm);
        activityWithScheduleService.updateActivityWithSchedule(updateForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "查询活动和时间表 @author akkkka114514")
    @PostMapping("/activity/query")
    public ResponseDTO<PageResult<ActivityWithScheduleVO>> queryActivityWithSchedule(@RequestBody @Valid ActivityWithScheduleQueryForm queryForm) {
        return activityWithScheduleService.queryActivityWithSchedule(queryForm);
    }

    @Operation(summary = "批量删除活动和时间表 @author akkkka114514")
    @PostMapping("/activity/batchDelete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> batchDelete(@RequestBody List<Long> ids) {
        return activityWithScheduleService.batchDelete(ids);
    }

    @Operation(summary = "首页活动列表 @author akkkka114514")
    @GetMapping("/homeData")
    public ResponseDTO<IndexActivityVO> index(@RequestParam Integer activeActivityPage,
            @RequestParam Long pageNum, @RequestParam Long pageSize) {
        if(activeActivityPage == null){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        if(!activeActivityPage.equals(IndexActivityPageConst.MY_SCHOOL_ACTIVITY) &&
                !activeActivityPage.equals(IndexActivityPageConst.GLOBAL_ACTIVITY) ){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        if(pageNum == null || pageNum < 1|| pageSize == null || pageSize < 1){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        Page<ActivityWithScheduleVO> mySchoolActivities;
        Page<ActivityWithScheduleVO> globalActivities;
        if(activeActivityPage.equals(IndexActivityPageConst.MY_SCHOOL_ACTIVITY)){
            mySchoolActivities = activityWithScheduleService.notStartAndPendingEnrollActivityPage(pageNum, pageSize);
            globalActivities = activityDao.notStartAndPendingEnrollActivityGlobal(new Page<>(1L, pageSize));
        }else {
            mySchoolActivities = activityWithScheduleService.notStartAndPendingEnrollActivityPage(1L, pageSize);
            globalActivities = activityDao.notStartAndPendingEnrollActivityGlobal(new Page<>(pageNum, pageSize));
        }
        IndexActivityVO indexActivityVO = new IndexActivityVO();
        indexActivityVO.setMySchoolActivities(mySchoolActivities);
        indexActivityVO.setGlobalActivities(globalActivities);
        return ResponseDTO.ok(indexActivityVO);
    }


    @Operation(summary = "活动详情页 @author akkkka114514")
    @GetMapping("/activity/detail")
    public ResponseDTO<ActivityDetailVO> detail(@RequestParam Long activityId) {
        return ResponseDTO.ok(activityWithScheduleService.detail(activityId));
    }

    /**
     * 活动阶段倒计时接口
     * 
     * 返回活动当前阶段、下一阶段名称及剩余秒数，前端用于展示倒计时
     * 
     * 前端使用方式：
     * 1. 进入活动详情页时调用此接口获取倒计时数据
     * 2. 用 currentPhase 显示活动当前阶段（如"报名中"、"活动进行中"等）
     * 3. 用 remainingSeconds 做前端倒计时：每秒减1，格式化为"X天X时X分X秒"
     * 4. 倒计时归零时重新调接口刷新下一阶段信息
     * 5. 特殊状态：currentPhase="未开始"表示活动未开始，"已结束"表示活动已结束，remainingSeconds=-1
     * 
     * 阶段顺序：报名开始 → 报名结束 → 活动开始 → 活动结束 → 签到开始 → 签到结束 → 签退开始 → 签退结束
     * 
     * @param activityId 活动ID
     * @return 倒计时信息
     */
    @Operation(summary = "活动阶段倒计时 @author akkkka114514")
    @GetMapping("/activity/countdown")
    public ResponseDTO<ActivityPhaseCountdownVO> phaseCountdown(@RequestParam Long activityId) {
        return ResponseDTO.ok(activityWithScheduleService.phaseCountdown(activityId));
    }

    @Operation(summary = "初始化发布活动页面 @author akkkka114514")
    @GetMapping("/activity/publish/init")
    public ResponseDTO<EditActivityDraftSelectionsVO> initPublish() {
        Long userId = SmartRequestUtil.getRequestUserId();
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        Long schoolId = portalUser.getSchoolId();

        EditActivityDraftSelectionsVO vo = new EditActivityDraftSelectionsVO();
        vo.setSelectableCollegeVOList(collegeInfoService.getIdNameBySchoolId(schoolId));
        vo.setSelectableOrganizationVOList(organizationInfoService.getIdNameBySchoolId(schoolId));
        vo.setSelectableCollegeReviewerList(
                backendUserService.getCollegeReviewerListMap(collegeInfoService.getIdsBySchoolId(schoolId)));
        vo.setSelectableOrganizationReviewerList(
                backendUserService.getOrganizationReviewerListMap(organizationInfoService.getIdsBySchoolId(schoolId)));
        vo.setSelectableCategoryVOList(activityCategoryService.getAll());
        vo.setSelectableGradeVOList(gradeInfoService.getAll());
        return ResponseDTO.ok(vo);
    }

    @Operation(summary = "添加待审核活动 @author akkkka114514")
    @PostMapping("/activity/submit")
    public ResponseDTO<Void> submitActivity(@RequestBody @Valid ActivityWithScheduleAddForm addForm) {
        activityWithScheduleAddFormValidator.validate(addForm);
        activityWithScheduleService.submitDraft(addForm);
        return ResponseDTO.ok();
    }

    public ResponseDTO<ActivityReviewProposalVO> getReviewProposal(@PathParam("activityId") Long activityId) {
        ActivityReviewProposalVO result = new ActivityReviewProposalVO();

        // 获取活动实体
        ActivityEntity activity = activityManager.getById(activityId);
        ActivityVO activityVO = new ActivityVO();
        activityVO.setId(activity.getId());
        activityVO.setTitle(activity.getTitle());
        activityVO.setStatus(activity.getStatus());
        activityVO.setPosition(activity.getPosition());
        activityVO.setScoreCanGet(activity.getScoreCanGet());
        activityVO.setEnrollNumLimit(activity.getEnrollNumLimit());
        activityVO.setActivityBelongToSchoolId(activity.getActivityBelongToSchoolId());
        activityVO.setActivityBelongToSchoolName(collegeInfoService.getNameById(activity.getActivityBelongToSchoolId()));
        activityVO.setActivityBelongToOrganizationId(activity.getActivityBelongToOrganizationId());
        activityVO.setActivityBelongToOrganizationName(organizationInfoService.getNameById(activity.getActivityBelongToOrganizationId()));
        activityVO.setActivityBelongToCollegeId(activity.getActivityBelongToCollegeId());
        activityVO.setActivityBelongToCollegeName(collegeInfoService.getNameById(activity.getActivityBelongToCollegeId()));
        activityVO.setCreateTime(activity.getCreateTime());
        activityVO.setUpdateTime(activity.getUpdateTime());
        activityVO.setDescription(activity.getDescription());
        activityVO.setEnrollNeedReview(activity.getEnrollNeedReview());
        activityVO.setNeedSignOut(activity.getNeedSignOut());
        activityVO.setAttachment(activity.getAttachment());
        activityVO.setCategoryId(activity.getCategoryId());
        activityVO.setCategoryName(activityCategoryService.getNameById(activity.getCategoryId()));
        activityVO.setCoverImg(activity.getCoverImg());
        PortalUserEntity managerEntity = portalUserManager.getById(activity.getActivityManagerId());
        if (managerEntity != null) {
            PortalUserVO managerVO = new PortalUserVO();
            managerVO.setId(managerEntity.getId());
            managerVO.setUsername(managerEntity.getUsername());
            managerVO.setSchoolId(managerEntity.getSchoolId());
            managerVO.setCollegeId(managerEntity.getCollegeId());
            activityVO.setActivityManager(managerVO);
        }
        result.setActivityVO(activityVO);

        // 获取时间表实体
        ActivityScheduleEntity schedule = activityScheduleManager.getById(activityId);
        ActivityScheduleVO scheduleVO = new ActivityScheduleVO();
        scheduleVO.setEnrollStartTime(schedule.getEnrollStartTime());
        scheduleVO.setEnrollEndTime(schedule.getEnrollEndTime());
        scheduleVO.setActivityStartTime(schedule.getActivityStartTime());
        scheduleVO.setActivityEndTime(schedule.getActivityEndTime());
        scheduleVO.setSigninStartTime(schedule.getSigninStartTime());
        scheduleVO.setSigninEndTime(schedule.getSigninEndTime());
        scheduleVO.setSignoutStartTime(schedule.getSignoutStartTime());
        scheduleVO.setSignoutEndTime(schedule.getSignoutEndTime());
        result.setScheduleVO(scheduleVO);

        // 查询当前审核阶段的审核日志
        ActivityReviewLogEntity reviewLog = reviewLogService.getLatestReviewLog(activityId);

        result.setCanEnrollCollege(collegeService.listIdNameByActivityId(activityId));
        result.setCanEnrollTribe(tribeService.getIdNameByActivityId(activityId));
        result.setCanEnrollGrade(gradeService.getIdNameByActivityId(activityId));
        // 组装 EditActivityDraftSelectionsVO
        Long managerUserId = SmartRequestUtil.getRequestUserId();
        PortalUserEntity managerPortalUser = portalUserManager.getById(managerUserId);
        Long managerSchoolId = managerPortalUser.getSchoolId();
        EditActivityDraftSelectionsVO selectionsVO = new EditActivityDraftSelectionsVO();
        selectionsVO.setSelectableCollegeVOList(collegeInfoService.getIdNameBySchoolId(managerSchoolId));
        selectionsVO.setSelectableOrganizationVOList(organizationInfoService.getIdNameBySchoolId(managerSchoolId));
        selectionsVO.setSelectableCollegeReviewerList(
                backendUserService.getCollegeReviewerListMap(collegeInfoService.getIdsBySchoolId(managerSchoolId)));
        selectionsVO.setSelectableOrganizationReviewerList(
                backendUserService.getOrganizationReviewerListMap(organizationInfoService.getIdsBySchoolId(managerSchoolId)));
        selectionsVO.setSelectableCategoryVOList(activityCategoryService.getAll());
        selectionsVO.setSelectableGradeVOList(gradeInfoService.getAll());
        result.setEditActivityDraftSelectionsVO(selectionsVO);

        return ResponseDTO.ok(result);
    }
}