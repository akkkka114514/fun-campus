package com.akkkka.admin.module.business.funcampus.activityCategory.controller;

import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryAddForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.vo.ActivityCategoryVO;
import com.akkkka.admin.module.business.funcampus.activityCategory.service.ActivityCategoryService;
import com.akkkka.common.domain.ValidateList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动分类 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-10 19:46:44
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动分类")
public class ActivityCategoryController {

    @Resource
    private ActivityCategoryService activityCategoryService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activityCategory/queryPage")
    @SaCheckPermission("activityCategory:query")
    public ResponseDTO<PageResult<ActivityCategoryVO>> queryPage(@RequestBody @Valid ActivityCategoryQueryForm queryForm) {
        return ResponseDTO.ok(activityCategoryService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/activityCategory/add")
    @SaCheckPermission("activityCategory:add")
    public ResponseDTO<String> add(@RequestBody @Valid ActivityCategoryAddForm addForm) {
        return activityCategoryService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/activityCategory/update")
    @SaCheckPermission("activityCategory:update")
    public ResponseDTO<String> update(@RequestBody @Valid ActivityCategoryUpdateForm updateForm) {
        return activityCategoryService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/activityCategory/batchDelete")
    @SaCheckPermission("activityCategory:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return activityCategoryService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/activityCategory/delete/{id}")
    @SaCheckPermission("activityCategory:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return activityCategoryService.delete(id);
    }
}
