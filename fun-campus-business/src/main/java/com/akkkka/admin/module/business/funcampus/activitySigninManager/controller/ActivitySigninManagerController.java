package com.akkkka.admin.module.business.funcampus.activitySigninManager.controller;

import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerAddForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerQueryForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerUpdateForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.vo.ActivitySigninManagerVO;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
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
 * 活动签到管理员 Controller
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动签到管理员")
public class ActivitySigninManagerController {

    @Resource
    private ActivitySigninManagerService activitySigninManagerService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activitySigninManager/queryPage")
    @SaCheckPermission("activitySigninManager:query")
    public ResponseDTO<PageResult<ActivitySigninManagerVO>> queryPage(@RequestBody @Valid ActivitySigninManagerQueryForm queryForm) {
        return ResponseDTO.ok(activitySigninManagerService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/activitySigninManager/add")
    @SaCheckPermission("activitySigninManager:add")
    public ResponseDTO<String> add(@RequestBody @Valid ActivitySigninManagerAddForm addForm) {
        return activitySigninManagerService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/activitySigninManager/update")
    @SaCheckPermission("activitySigninManager:update")
    public ResponseDTO<String> update(@RequestBody @Valid ActivitySigninManagerUpdateForm updateForm) {
        return activitySigninManagerService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/activitySigninManager/batchDelete")
    @SaCheckPermission("activitySigninManager:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return activitySigninManagerService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/activitySigninManager/delete/{id}")
    @SaCheckPermission("activitySigninManager:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return activitySigninManagerService.delete(id);
    }
}
