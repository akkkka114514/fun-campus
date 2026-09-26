package com.akkkka.admin.module.business.funcampus.activityEnrollment.controller;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.MyEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.QRCodeSignInForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.MyEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.SignInQRCodeVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import com.akkkka.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("portal")
public class ActivityEnrollmentController {

    @Resource
    private ActivityEnrollmentService activityEnrollmentService;


    @Operation(summary = "报名活动 @author akkkka114514")
    @PostMapping("/activityEnrollment/enroll")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    public ResponseDTO<String> enroll(@RequestBody Long activityId) {
        activityEnrollmentService.enroll(activityId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "分页查询我的报名 @author akkkka114514")
    @PostMapping("/activityEnrollment/queryMy")
    public ResponseDTO<PageResult<MyEnrollmentVO>> queryMy(@RequestBody @Valid MyEnrollmentQueryForm queryForm) {
        return ResponseDTO.ok(activityEnrollmentService.queryMyEnrollment(queryForm));
    }

    @Operation(summary = "取消报名（仅免费活动，报名截止前） @author akkkka114514")
    @PostMapping("/activityEnrollment/cancel")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> cancel(@RequestBody Long activityId) {
        activityEnrollmentService.cancelEnrollment(activityId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "生成签到/签退二维码（含UUID Token，30秒过期，重新生成即刷新） @author akkkka114514")
    @GetMapping("/activityEnrollment/signIn/QRCode")
    public ResponseDTO<SignInQRCodeVO> signInQRCode() {
        return ResponseDTO.ok(activityEnrollmentService.signInQRCode());
    }

    @Operation(summary = "扫码签到（须处于活动签到时间窗口内） @author akkkka114514")
    @PostMapping("/activityEnrollment/signIn/byQRCode")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> signInByQRCode(@RequestBody @Valid QRCodeSignInForm form) {
        activityEnrollmentService.signIn(form.getActivityId(), form.getTargetUserId(), form.getToken());
        return ResponseDTO.ok();
    }

    @Operation(summary = "扫码签退（须活动需要签退且处于签退时间窗口内） @author akkkka114514")
    @PostMapping("/activityEnrollment/signOut/byQRCode")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> signOutByQRCode(@RequestBody @Valid QRCodeSignInForm form) {
        activityEnrollmentService.signOut(form.getActivityId(), form.getTargetUserId(), form.getToken());
        return ResponseDTO.ok();
    }
}
