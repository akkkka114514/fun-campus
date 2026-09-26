package com.akkkka.admin.module.business.funcampus.activityWithSchedule.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleQueryForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动管理 控制器（管理后台）
 *
 * 说明：ActivityWithScheduleController 位于 portal 前缀（面向门户用户登录态），
 * 管理后台"活动管理"页面需要 backend 前缀的等价接口，避免与门户登录态冲突。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动管理（管理后台）")
@RequestMapping("backend")
public class ActivityManageController {

    @Resource
    private ActivityWithScheduleService activityWithScheduleService;

    @Operation(summary = "分页查询活动 @author akkkka114514")
    @PostMapping("/activity/query")
    public ResponseDTO<PageResult<ActivityWithScheduleVO>> queryActivityWithSchedule(@RequestBody @Valid ActivityWithScheduleQueryForm queryForm) {
        return activityWithScheduleService.queryActivityWithSchedule(queryForm);
    }

    @Operation(summary = "删除活动 @author akkkka114514")
    @PostMapping("/activity/delete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> deleteActivityWithSchedule(@RequestBody Long activityId) {
        activityWithScheduleService.deleteDraft(activityId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "批量删除活动 @author akkkka114514")
    @PostMapping("/activity/batchDelete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> batchDelete(@RequestBody List<Long> ids) {
        return activityWithScheduleService.batchDelete(ids);
    }
}
