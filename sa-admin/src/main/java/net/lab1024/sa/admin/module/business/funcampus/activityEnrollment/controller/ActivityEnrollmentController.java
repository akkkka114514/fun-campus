package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.controller;

import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
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
        return activityEnrollmentService.enroll(activityId);
    }
    @Operation(summary = "取消报名 @author akkkka114514")
    @PostMapping("/activityEnrollment/cancelEnroll")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> cancelEnroll(@RequestBody Long activityId) {
        return activityEnrollmentService.cancelEnroll(activityId);
    }
}
