package com.akkkka.admin.module.business.funcampus.creditApplication.controller;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.creditApplication.domain.form.CreditApplicationApplyForm;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.form.CreditApplicationUpdateForm;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.vo.CreditApplicationVO;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.vo.CreditReviewerVO;
import com.akkkka.admin.module.business.funcampus.creditApplication.service.CreditApplicationService;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 学分认定申请 Controller（门户端）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "学分认定（门户）")
@RequestMapping("portal")
public class CreditApplicationController {

    @Resource
    private CreditApplicationService creditApplicationService;

    @Operation(summary = "提交学分认定申请 @author akkkka114514")
    @PostMapping("/creditApplication/apply")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> apply(@RequestBody @Valid CreditApplicationApplyForm applyForm) {
        creditApplicationService.apply(applyForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "编辑申请（仅待审核） @author akkkka114514")
    @PostMapping("/creditApplication/update")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> update(@RequestBody @Valid CreditApplicationUpdateForm updateForm) {
        creditApplicationService.update(updateForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "删除申请（仅待审核） @author akkkka114514")
    @PostMapping("/creditApplication/delete")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> delete(@RequestBody Long id) {
        creditApplicationService.delete(id);
        return ResponseDTO.ok();
    }

    @Operation(summary = "申请详情 @author akkkka114514")
    @GetMapping("/creditApplication/detail/{id}")
    public ResponseDTO<CreditApplicationVO> detail(@PathVariable Long id) {
        return ResponseDTO.ok(creditApplicationService.detail(id));
    }

    @Operation(summary = "我的申请列表（可选状态筛选：0-待审核 1-已通过 2-已驳回） @author akkkka114514")
    @GetMapping("/creditApplication/myList")
    public ResponseDTO<List<CreditApplicationVO>> myList(@RequestParam(required = false) Integer status) {
        return ResponseDTO.ok(creditApplicationService.queryMyList(status));
    }

    @Operation(summary = "审核人候选列表（本校，可按院系/组织筛选） @author akkkka114514")
    @GetMapping("/creditApplication/reviewer/query")
    public ResponseDTO<List<CreditReviewerVO>> reviewerQuery(@RequestParam(required = false) Long organizationId) {
        return ResponseDTO.ok(creditApplicationService.queryReviewerList(organizationId));
    }
}
