package com.akkkka.admin.module.business.funcampus.portalUser.controller;

import com.akkkka.admin.module.business.funcampus.portalUser.domain.form.PortalUserProfileUpdateForm;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.CreditScoreLogVO;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.CurrentPortalUserVO;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.GradeScoreDetailVO;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserService;
import com.akkkka.common.domain.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端用户-个人中心 Controller（门户端）
 * <p>
 * 请求路径包含 portal 段：由 AdminInterceptor 解析为门户登录用户
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "前端用户-个人中心")
@RequestMapping("portal")
public class PortalUserSelfController {

    @Resource
    private PortalUserService portalUserService;

    @Operation(summary = "查询当前登录用户信息 @author akkkka114514")
    @GetMapping("/portalUser/current")
    public ResponseDTO<CurrentPortalUserVO> current() {
        return ResponseDTO.ok(portalUserService.getCurrentUserInfo());
    }

    @Operation(summary = "更新个人资料（昵称/头像/手机号/性别） @author akkkka114514")
    @PostMapping("/portalUser/updateProfile")
    public ResponseDTO<String> updateProfile(@RequestBody @Valid PortalUserProfileUpdateForm updateForm) {
        portalUserService.updateProfile(updateForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "实践积分明细列表（已结束且已签到的活动） @author akkkka114514")
    @GetMapping("/portalUser/gradeScoreDetail/list")
    public ResponseDTO<List<GradeScoreDetailVO>> gradeScoreDetailList() {
        return ResponseDTO.ok(portalUserService.queryGradeScoreDetailList());
    }

    @Operation(summary = "信誉分变动记录列表（活动结束未签到的扣分） @author akkkka114514")
    @GetMapping("/portalUser/creditScoreLog/list")
    public ResponseDTO<List<CreditScoreLogVO>> creditScoreLogList() {
        return ResponseDTO.ok(portalUserService.queryCreditScoreLogList());
    }
}
