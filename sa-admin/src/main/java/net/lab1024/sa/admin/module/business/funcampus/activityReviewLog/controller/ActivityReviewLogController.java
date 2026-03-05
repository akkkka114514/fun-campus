package net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.controller;

import jakarta.annotation.Nullable;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.vo.ActivityReviewLogVO;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogAddFormValidator;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogService;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleUpdateFormValidator;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
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

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activityReviewLog/queryPage")
    @SaCheckPermission("activityReviewLog:query")
    public ResponseDTO<PageResult<ActivityReviewLogVO>> queryPage(@RequestBody @Valid ActivityReviewLogQueryForm queryForm) {
        return ResponseDTO.ok(activityReviewLogService.queryPage(queryForm));
    }

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
            ActivityWithScheduleUpdateFormValidator updateValidator = new ActivityWithScheduleUpdateFormValidator();
            updateValidator.validate(updateForm);
        }
        ActivityReviewLogAddFormValidator addValidator = new ActivityReviewLogAddFormValidator();
        addValidator.validate(addForm);
        activityReviewLogService.initialReview(updateForm,addForm);
        return ResponseDTO.ok();
    }


}
