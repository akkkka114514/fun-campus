package com.akkkka.admin.module.business.funcampus.activityEnrollment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentKeyForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动报名关系 控制器（管理后台）
 *
 * 说明：ActivityEnrollmentController 位于 portal 前缀（面向门户用户登录态），
 * 管理后台「活动报名关系」页面需要 backend 前缀的管理接口，避免与门户登录态冲突。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动报名关系（管理后台）")
@RequestMapping("backend")
public class ActivityEnrollmentAdminController {

    @Resource
    private ActivityEnrollmentService activityEnrollmentService;

    @Operation(summary = "分页查询报名记录 @author akkkka114514")
    @PostMapping("/activityEnrollment/queryPage")
    public ResponseDTO<PageResult<ActivityEnrollmentVO>> queryPage(@RequestBody @Valid ActivityEnrollmentQueryForm queryForm) {
        return ResponseDTO.ok(activityEnrollmentService.queryEnrollmentPage(queryForm));
    }

    @Operation(summary = "删除报名记录（逻辑删除并释放名额） @author akkkka114514")
    @GetMapping("/activityEnrollment/delete/{activityId}/{userId}")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> delete(@PathVariable("activityId") Long activityId, @PathVariable("userId") Long userId) {
        activityEnrollmentService.deleteEnrollment(activityId, userId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "批量删除报名记录 @author akkkka114514")
    @PostMapping("/activityEnrollment/batchDelete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> batchDelete(@RequestBody @Valid List<ActivityEnrollmentKeyForm> keyList) {
        activityEnrollmentService.batchDeleteEnrollment(keyList);
        return ResponseDTO.ok();
    }
}
