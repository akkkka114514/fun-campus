package com.akkkka.admin.module.business.funcampus.activityWithSchedule.controller;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewStateMachineContext;
import com.alibaba.cola.statemachine.StateMachine;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import com.akkkka.admin.module.business.funcampus.activityCategory.service.ActivityCategoryService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.IndexActivityPageConst;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleQueryForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.IndexActivityVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.EditActivityDraftSelectionsVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleUpdateFormValidator;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import com.akkkka.admin.module.business.funcampus.gradeInfo.service.GradeInfoService;
import com.akkkka.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoService;
import com.akkkka.admin.module.system.backendUser.service.BackendUserService;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
    private StateMachine<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext>
            stateMachine;

    @Operation(summary = "添加活动和时间表 @author akkkka114514")
    @PostMapping("/activity/draft/submit")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<Void> submitDraft(@RequestBody @Valid ActivityWithScheduleAddForm addForm) {
        if(Objects.isNull(addForm)){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        ActivityWithScheduleAddFormValidator addValidator = new ActivityWithScheduleAddFormValidator();
        ActivityReviewStateMachineContext ctx=new ActivityReviewStateMachineContext();
        ctx.setActivityWithScheduleAddForm(addForm);
        stateMachine.fireEvent(ActivityReviewStage.DRAFT,ActivityReviewEvent.SUBMIT,new ActivityReviewStateMachineContext());
        return ResponseDTO.ok();
    }

    @Operation(summary = "删除活动和时间表 @author akkkka114514")
    @PostMapping("/activity/delete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> deleteActivityWithSchedule(@RequestBody Long activityId) {
        return activityWithScheduleService.deleteActivityWithSchedule(activityId);
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
            mySchoolActivities = activityWithScheduleService.notStartAndPendingEnrollActivityPage(pageNum, pageSize).getData();
            globalActivities =activityWithScheduleService.notStartAndPendingEnrollActivityPageGlobal(1L, pageSize).getData();
        }else {
            mySchoolActivities= activityWithScheduleService.notStartAndPendingEnrollActivityPage(1L, pageSize).getData();
            globalActivities =activityWithScheduleService.notStartAndPendingEnrollActivityPageGlobal(pageNum, pageSize).getData();
        }
        IndexActivityVO indexActivityVO = new IndexActivityVO();
        indexActivityVO.setMySchoolActivities(mySchoolActivities);
        indexActivityVO.setGlobalActivities(globalActivities);
        return ResponseDTO.ok(indexActivityVO);
    }

//    @Operation(description = "活动详情页 @author akkkka114514")
//    @GetMapping("portal/activity/detail")
//    public ResponseDTO<ActivityWithScheduleVO> detail(@RequestParam Long activityId) {
//        return activityWithScheduleService.detail(activityId);
//    }

    @Operation(summary = "初始化发布活动页面 @author akkkka114514")
    @GetMapping("/activity/publish/init")
    public ResponseDTO<EditActivityDraftSelectionsVO> initPublish() {

        return ResponseDTO.ok(initPublishActivityPageVO);
    }

    @Operation(summary = "添加待审核活动 @author akkkka114514")
    @PostMapping("/activity/submit")
    public ResponseDTO<Void> submitActivity(@RequestBody @Valid ActivityWithScheduleAddForm addForm) {
        System.out.println(addForm.toString());
        return null;
    }
}