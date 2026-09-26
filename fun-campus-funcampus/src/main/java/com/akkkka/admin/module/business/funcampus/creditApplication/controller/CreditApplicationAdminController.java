package com.akkkka.admin.module.business.funcampus.creditApplication.controller;

import com.akkkka.admin.module.business.funcampus.creditApplication.domain.form.CreditApplicationQueryForm;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.form.CreditApplicationReviewForm;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.vo.CreditApplicationVO;
import com.akkkka.admin.module.business.funcampus.creditApplication.service.CreditApplicationService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 学分认定申请 Controller（管理端审核）
 * <p>
 * 请求路径包含 backend 段：由 AdminInterceptor 解析为管理端登录用户
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "学分认定（管理端）")
@RequestMapping("backend")
public class CreditApplicationAdminController {

    @Resource
    private CreditApplicationService creditApplicationService;

    @Operation(summary = "指派给我的申请分页查询 @author akkkka114514")
    @PostMapping("/creditApplication/queryPage")
    @SaCheckPermission("creditApplication:query")
    public ResponseDTO<PageResult<CreditApplicationVO>> queryPage(@RequestBody @Valid CreditApplicationQueryForm queryForm) {
        return ResponseDTO.ok(creditApplicationService.queryPage(queryForm));
    }

    @Operation(summary = "审核申请（通过/驳回） @author akkkka114514")
    @PostMapping("/creditApplication/review")
    @SaCheckPermission("creditApplication:review")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> review(@RequestBody @Valid CreditApplicationReviewForm reviewForm) {
        creditApplicationService.review(reviewForm);
        return ResponseDTO.ok();
    }
}
