package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.vo.SimpleActivityCategoryVO;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.service.ActivityCategoryService;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.constant.IndexActivityPageConst;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.IndexActivityVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.InitPublishActivityPageVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.vo.SimpleCollegeInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.vo.SimpleGradeInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.service.GradeInfoService;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo.SimpleOrganizationInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoService;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.SimpleBackendUserVO;
import net.lab1024.sa.admin.module.system.backendUser.service.BackendUserService;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Operation(summary = "添加活动和时间表 @author akkkka114514")
    @PostMapping("/activity/add")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> addActivityWithSchedule(@RequestBody @Valid ActivityWithScheduleAddForm addForm) {
        return activityWithScheduleService.publishActivityWithSchedule(addForm);
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
        return activityWithScheduleService.updateActivityWithSchedule(updateForm);
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
    public ResponseDTO<InitPublishActivityPageVO> initPublish() {
        List<SimpleCollegeInfoVO> collegeInfoVOList =collegeInfoService.getCollegeInfoByUserId();
        List<SimpleGradeInfoVO> gradeInfoVOList = gradeInfoService.getAll();
        List<SimpleOrganizationInfoVO> organizationInfoVOList = organizationInfoService.getOrganizationInfoByUserId();
        List<SimpleActivityCategoryVO> activityCategoryVOList = activityCategoryService.getAll();
        Map<Long,List<SimpleBackendUserVO>> collegeReviewerList = backendUserService.getCollegeReviewerList();
        Map<Long,List<SimpleBackendUserVO>> organizationReviewerList = backendUserService.getOrganizationReviewerList();

        InitPublishActivityPageVO initPublishActivityPageVO = new InitPublishActivityPageVO();
        initPublishActivityPageVO.setCollegeInfoVOList(collegeInfoVOList);
        initPublishActivityPageVO.setGradeInfoVOList(gradeInfoVOList);
        initPublishActivityPageVO.setOrganizationInfoVOList(organizationInfoVOList);
        initPublishActivityPageVO.setActivityCategoryVOList(activityCategoryVOList);
        initPublishActivityPageVO.setCollegeReviewerList(collegeReviewerList);
        initPublishActivityPageVO.setOrganizationReviewerList(organizationReviewerList);

        return ResponseDTO.ok(initPublishActivityPageVO);
    }
}