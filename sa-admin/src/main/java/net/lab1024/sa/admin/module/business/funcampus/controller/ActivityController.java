package net.lab1024.sa.admin.module.business.funcampus.controller;

import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityAddForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.vo.ActivityVO;
import net.lab1024.sa.admin.module.business.funcampus.service.ActivityService;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.annoation.SmartDebounce;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动管理 Controller
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动管理")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activity/queryPage")
    @SaCheckPermission("activity:query")
    public ResponseDTO<PageResult<ActivityVO>> queryPage(@RequestBody @Valid ActivityQueryForm queryForm) {
        return ResponseDTO.ok(activityService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/activity/add")
    @SaCheckPermission("activity:add")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000 )
    @SmartDebounce(timeout = 3, userSpecific = true, paramSpecific = true)
    public ResponseDTO<String> add(@RequestBody @Valid ActivityAddForm addForm) {
        return activityService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/activity/update")
    @SaCheckPermission("activity:update")
    @SmartDebounce(timeout = 3, userSpecific = true, paramSpecific = true)
    public ResponseDTO<String> update(@RequestBody @Valid ActivityUpdateForm updateForm) {
        return activityService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/activity/batchDelete")
    @SaCheckPermission("activity:delete")
    @SmartDebounce(timeout = 3, userSpecific = true, paramSpecific = true)
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return activityService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/activity/delete/{id}")
    @SaCheckPermission("activity:delete")
    @SmartDebounce(key = "activity:delete:#id", timeout = 3)
    public ResponseDTO<String> delete(@PathVariable Long id) {
        return activityService.delete(id);
    }
}