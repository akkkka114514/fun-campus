package com.akkkka.admin.module.business.funcampus.activityEnrollment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动报名关系 Controller
 *
 * @Author akkkka114514
 * @Date 2025-10-02 13:54:42
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动报名关系")
public class ActivityEnrollmentController {

    @Resource
    private ActivityEnrollmentService activityEnrollmentService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activityEnrollment/queryPage")
    @SaCheckPermission("activityEnrollment:query")
    public ResponseDTO<PageResult<ActivityEnrollmentVO>> queryPage(@RequestBody @Valid ActivityEnrollmentQueryForm queryForm) {
        return ResponseDTO.ok(activityEnrollmentService.queryPage(queryForm));
    }

    @Operation(summary = "报名活动 @author akkkka114514")
    @PostMapping("/activityEnrollment/enroll")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> enroll(@RequestBody Long activityId) {
        activityEnrollmentService.enroll(activityId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "生成签到二维码 @author akkkka114514")
    @GetMapping("/activityEnrollment/signIn/QRCode/{userId}")
    public ResponseDTO<String> signInQRCode() {
        return ResponseDTO.ok(activityEnrollmentService.signInQRCode());
    }
}
