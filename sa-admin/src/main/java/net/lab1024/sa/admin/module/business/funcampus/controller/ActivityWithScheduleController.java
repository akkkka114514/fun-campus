package net.lab1024.sa.admin.module.business.funcampus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityWithScheduleForm;
import net.lab1024.sa.admin.module.business.funcampus.service.ActivityWithScheduleService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "添加活动和时间表(基于编程式事务) @author akkkka114514")
    @PostMapping("/activityWithSchedule/add")
    public ResponseDTO<String> addActivityWithSchedule(@RequestBody @Valid ActivityWithScheduleForm addForm) {
        return activityWithScheduleService.addActivityWithSchedule(addForm);
    }

}