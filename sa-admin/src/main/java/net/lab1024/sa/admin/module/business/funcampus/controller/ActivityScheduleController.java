package net.lab1024.sa.admin.module.business.funcampus.controller;

import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityScheduleQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.vo.ActivityScheduleVO;
import net.lab1024.sa.admin.module.business.funcampus.service.ActivityScheduleService;
import net.lab1024.sa.base.common.domain.ValidateList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * 活动时间表 Controller
 *
 * @Author akkkka114514
 * @Date 2025-09-06 15:54:07
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动时间表")
public class ActivityScheduleController {

    @Resource
    private ActivityScheduleService activityScheduleService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activitySchedule/queryPage")
    @SaCheckPermission("activitySchedule:query")
    public ResponseDTO<PageResult<ActivityScheduleVO>> queryPage(@RequestBody @Valid ActivityScheduleQueryForm queryForm) {
        return ResponseDTO.ok(activityScheduleService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/activitySchedule/add")
    @SaCheckPermission("activitySchedule:add")
    public ResponseDTO<String> add(@RequestBody @Valid ActivityScheduleAddForm addForm) {
        return activityScheduleService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/activitySchedule/update")
    @SaCheckPermission("activitySchedule:update")
    public ResponseDTO<String> update(@RequestBody @Valid ActivityScheduleUpdateForm updateForm) {
        return activityScheduleService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/activitySchedule/batchDelete")
    @SaCheckPermission("activitySchedule:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return activityScheduleService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/activitySchedule/delete/{activityId}")
    @SaCheckPermission("activitySchedule:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long activityId) {
        return activityScheduleService.delete(activityId);
    }
}
