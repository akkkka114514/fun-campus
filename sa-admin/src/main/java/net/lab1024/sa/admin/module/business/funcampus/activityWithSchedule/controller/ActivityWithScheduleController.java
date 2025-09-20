package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 活动和时间表 组合控制器
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动和时间表管理")
public class ActivityWithScheduleController {

    @Resource
    private ActivityWithScheduleService activityWithScheduleService;

    @Operation(summary = "添加活动和时间表 @author akkkka114514")
    @PostMapping("/activityWithSchedule/add")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> addActivityWithSchedule(@RequestBody @Valid ActivityWithScheduleAddForm addForm) {
        return activityWithScheduleService.addActivityWithSchedule(addForm);
    }

    @Operation(summary = "删除活动和时间表 @author akkkka114514")
    @PostMapping("/activityWithSchedule/delete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> deleteActivityWithSchedule(@RequestBody Long activityId) {
        return activityWithScheduleService.deleteActivityWithSchedule(activityId);
    }

    @Operation(summary = "更新活动和时间表 @author akkkka114514")
    @PostMapping("/activityWithSchedule/update")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> updateActivityWithSchedule(@RequestBody @Valid ActivityWithScheduleUpdateForm updateForm) {
        return activityWithScheduleService.updateActivityWithSchedule(updateForm);
    }

    @Operation(summary = "查询活动和时间表 @author akkkka114514")
    @PostMapping("/activityWithSchedule/query")
    public ResponseDTO<PageResult<ActivityWithScheduleVO>> queryActivityWithSchedule(@RequestBody @Valid ActivityWithScheduleQueryForm queryForm) {
        return activityWithScheduleService.queryActivityWithSchedule(queryForm);
    }

    @Operation(summary = "批量删除活动和时间表 @author akkkka114514")
    @PostMapping("/activityWithSchedule/batchDelete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> batchDelete(@RequestBody List<Integer> ids) {
        return activityWithScheduleService.batchDelete(ids);
    }
}