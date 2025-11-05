package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.zxing.common.BitMatrix;
import jakarta.servlet.http.HttpServletResponse;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.apache.commons.math3.linear.MatrixUtils;
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

    @Operation(summary = "查询报名用户 @author akkkka114514")
    @GetMapping("/activityEnrollment/queryEnrollUsers/{activityId}")
    public ResponseDTO<Page<PortalUserVO>> queryEnrollUsersByActivityId(@PathVariable Long activityId) {
        return activityEnrollmentService.queryEnrollUsersByActivityId(activityId);
    }

    @Operation(summary = "查询用户报名活动 @author akkkka114514")
    @GetMapping("/activityEnrollment/queryActivity/")
    public ResponseDTO<Page<ActivityWithScheduleVO>> queryActivityWithScheduleByPortalUserId() {
        return activityEnrollmentService.queryActivityWithScheduleByPortalUserId();
    }
    @Operation(summary = "生成签到二维码 @author akkkka114514")
    @GetMapping("/activityEnrollment/signIn/QRCode/{activityId}/{userId}")
    public ResponseDTO<String> signInQRCode(@PathVariable Long activityId,
                                          @PathVariable Long userId) {
        return activityEnrollmentService.signInQRCode(activityId, userId);
    }
}
