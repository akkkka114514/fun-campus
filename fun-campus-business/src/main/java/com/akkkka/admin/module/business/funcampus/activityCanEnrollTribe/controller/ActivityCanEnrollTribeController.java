package com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.controller;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.vo.ActivityCanEnrollTribeVO;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.service.ActivityCanEnrollTribeService;
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
 * 活动能报名的部落 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动能报名的部落")
public class ActivityCanEnrollTribeController {

    @Resource
    private ActivityCanEnrollTribeService activityCanEnrollTribeService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activityCanEnrollTribe/queryPage")
    @SaCheckPermission("activityCanEnrollTribe:query")
    public ResponseDTO<PageResult<ActivityCanEnrollTribeVO>> queryPage(@RequestBody @Valid ActivityCanEnrollTribeQueryForm queryForm) {
        return ResponseDTO.ok(activityCanEnrollTribeService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/activityCanEnrollTribe/add")
    @SaCheckPermission("activityCanEnrollTribe:add")
    public ResponseDTO<String> add(@RequestBody @Valid ActivityCanEnrollTribeAddForm addForm) {
        return activityCanEnrollTribeService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/activityCanEnrollTribe/update")
    @SaCheckPermission("activityCanEnrollTribe:update")
    public ResponseDTO<String> update(@RequestBody @Valid ActivityCanEnrollTribeUpdateForm updateForm) {
        return activityCanEnrollTribeService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/activityCanEnrollTribe/batchDelete")
    @SaCheckPermission("activityCanEnrollTribe:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return activityCanEnrollTribeService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/activityCanEnrollTribe/delete/{id}")
    @SaCheckPermission("activityCanEnrollTribe:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return activityCanEnrollTribeService.delete(id);
    }
}
