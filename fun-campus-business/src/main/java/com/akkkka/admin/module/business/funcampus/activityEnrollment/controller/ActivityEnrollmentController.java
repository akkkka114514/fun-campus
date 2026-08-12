package com.akkkka.admin.module.business.funcampus.activityEnrollment.controller;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.QRCodeSignInForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.SignInQRCodeVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import com.akkkka.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(summary = "生成签到二维码 @author akkkka114514")
    @GetMapping("/activityEnrollment/signIn/QRCode")
    public ResponseDTO<SignInQRCodeVO> signInQRCode() {
        return ResponseDTO.ok(activityEnrollmentService.signInQRCode());
    }

    @Operation(summary = "扫码签到 @author akkkka114514")
    @PostMapping("/activityEnrollment/signIn/byQRCode")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> signInByQRCode(@RequestBody @Valid QRCodeSignInForm form) {
        activityEnrollmentService.signIn(form.getActivityId(), form.getTargetUserId(), form.getToken());
        return ResponseDTO.ok();
    }

    @Operation(summary = "扫码签退 @author akkkka114514")
    @PostMapping("/activityEnrollment/signOut/byQRCode")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> signOutByQRCode(@RequestBody @Valid QRCodeSignInForm form) {
        activityEnrollmentService.signOut(form.getActivityId(), form.getTargetUserId(), form.getToken());
        return ResponseDTO.ok();
    }
}
