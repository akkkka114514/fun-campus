package com.akkkka.admin.module.business.funcampus.tribe.controller;

import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeAddForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeApplicationQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeApplicationReviewForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeApplicationVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.service.TribeApplicationService;
import com.akkkka.admin.module.business.funcampus.tribe.service.TribeService;
import com.akkkka.common.domain.ValidateList;
import org.springframework.web.bind.annotation.*;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 部落 Controller（管理端）
 * <p>
 * 请求路径包含 backend 段：由 AdminInterceptor 解析为管理端登录用户
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "部落（管理端）")
@RequestMapping("backend")
public class TribeAdminController {

    @Resource
    private TribeService tribeService;

    @Resource
    private TribeApplicationService tribeApplicationService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/tribe/queryPage")
    @SaCheckPermission("tribe:query")
    public ResponseDTO<PageResult<TribeVO>> queryPage(@RequestBody @Valid TribeQueryForm queryForm) {
        return ResponseDTO.ok(tribeService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/tribe/add")
    @SaCheckPermission("tribe:add")
    public ResponseDTO<String> add(@RequestBody @Valid TribeAddForm addForm) {
        return tribeService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/tribe/update")
    @SaCheckPermission("tribe:update")
    public ResponseDTO<String> update(@RequestBody @Valid TribeUpdateForm updateForm) {
        return tribeService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/tribe/batchDelete")
    @SaCheckPermission("tribe:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return tribeService.batchDelete(idList);
    }

    @Operation(summary = "删除 @author akkkka114514")
    @GetMapping("/tribe/delete/{id}")
    @SaCheckPermission("tribe:delete")
    public ResponseDTO<String> delete(@PathVariable Long id) {
        return tribeService.delete(id);
    }

    @Operation(summary = "部落加入申请-分页查询 @author akkkka114514")
    @PostMapping("/tribeApplication/queryPage")
    public ResponseDTO<PageResult<TribeApplicationVO>> applicationQueryPage(@RequestBody @Valid TribeApplicationQueryForm queryForm) {
        return ResponseDTO.ok(tribeApplicationService.queryPage(queryForm));
    }

    @Operation(summary = "部落加入申请-审核（通过时自动加入部落） @author akkkka114514")
    @PostMapping("/tribeApplication/review")
    public ResponseDTO<String> applicationReview(@RequestBody @Valid TribeApplicationReviewForm reviewForm) {
        tribeApplicationService.review(reviewForm);
        return ResponseDTO.ok();
    }
}
