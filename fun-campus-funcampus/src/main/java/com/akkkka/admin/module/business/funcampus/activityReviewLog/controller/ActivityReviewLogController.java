package com.akkkka.admin.module.business.funcampus.activityReviewLog.controller;

import jakarta.annotation.Nullable;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.vo.ActivityReviewLogVO;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogValidator;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleUpdateFormValidator;
import com.akkkka.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动审核日志 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-10 18:34:56
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动审核日志")
public class ActivityReviewLogController {

    @Resource
    private ActivityReviewLogService activityReviewLogService;
    @Resource
    private ActivityReviewLogValidator activityReviewLogValidator;
    @Resource
    private ActivityWithScheduleUpdateFormValidator activityWithScheduleUpdateFormValidator;


    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/activityReviewLog/add")
    @SaCheckPermission("activityReviewLog:add")
    public ResponseDTO<String> add(@RequestBody @Valid ActivityReviewLogAddForm addForm) {
        return activityReviewLogService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/activityReviewLog/update")
    @SaCheckPermission("activityReviewLog:update")
    public ResponseDTO<String> update(@RequestBody @Valid ActivityReviewLogUpdateForm updateForm) {
        return activityReviewLogService.update(updateForm);
    }
    @Operation(summary = "活动审核初审 @author akkkka114514")
    @PostMapping("/review/initial")
    public ResponseDTO<String> initialReview(@RequestBody @Nullable ActivityWithScheduleUpdateForm updateForm,
                                 @RequestBody ActivityReviewLogAddForm addForm){
        if(updateForm!=null){
            activityWithScheduleUpdateFormValidator.validate(updateForm);
        }
        activityReviewLogValidator.validate(addForm);
        activityReviewLogService.initialReview(updateForm,addForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "根据活动ID获取最新审核日志 @author akkkka114514")
    @GetMapping("/activityReviewLog/latest/{activityId}")
    public ResponseDTO<ActivityReviewLogVO> getLatestReviewLog(@PathVariable Long activityId) {
        ActivityReviewLogEntity entity = activityReviewLogService.getLatestReviewLog(activityId);
        if (entity == null) {
            return ResponseDTO.ok(null);
        }
        ActivityReviewLogVO vo = new ActivityReviewLogVO();
        vo.setId(entity.getId());
        vo.setActivityId(entity.getActivityId());
        vo.setReviewerId(entity.getReviewerId());
        vo.setReviewerName(entity.getReviewerName());
        vo.setReviewStage(entity.getReviewStage() != null ? entity.getReviewStage().getOrder() : null);
        vo.setAction(entity.getAction() != null ? entity.getAction().ordinal() : null);
        vo.setRejectReason(entity.getRejectReason());
        vo.setCheckRemark(entity.getCheckRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setDeletedFlag(entity.getDeletedFlag());
        return ResponseDTO.ok(vo);
    }

}
