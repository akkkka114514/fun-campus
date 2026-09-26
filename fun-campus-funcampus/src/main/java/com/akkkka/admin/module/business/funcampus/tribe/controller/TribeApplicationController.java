package com.akkkka.admin.module.business.funcampus.tribe.controller;

import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeApplicationApplyForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeApplicationVO;
import com.akkkka.admin.module.business.funcampus.tribe.service.TribeApplicationService;
import org.springframework.web.bind.annotation.*;
import com.akkkka.common.domain.ResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 部落加入申请 Controller（门户端）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "部落加入申请（门户）")
@RequestMapping("portal")
public class TribeApplicationController {

    @Resource
    private TribeApplicationService tribeApplicationService;

    @Operation(summary = "申请加入部落 @author akkkka114514")
    @PostMapping("/tribe/application/apply")
    public ResponseDTO<String> apply(@RequestBody @Valid TribeApplicationApplyForm applyForm) {
        tribeApplicationService.apply(applyForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "我的申请列表 @author akkkka114514")
    @GetMapping("/tribe/application/myList")
    public ResponseDTO<List<TribeApplicationVO>> myList() {
        return ResponseDTO.ok(tribeApplicationService.queryMyList());
    }
}
