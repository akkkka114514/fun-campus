package com.akkkka.admin.module.business.funcampus.tribe.controller;

import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeAddForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.SimpleTribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeVO;
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

import java.util.List;

/**
 * 部落 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "部落")
@RequestMapping("portal")
public class TribeController {

    @Resource
    private TribeService tribeService;

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

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/tribe/delete/{id}")
    @SaCheckPermission("tribe:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return tribeService.delete(id);
    }

    @Operation(summary = "查询 @author akkkka114514")
    @GetMapping("/tribe/query/simple")
    public ResponseDTO<List<SimpleTribeVO>> querySimpleList(@RequestParam String keyword) {
        return ResponseDTO.ok(tribeService.querySimpleList(keyword));
    }
}
