package com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.controller;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.vo.ActivityCanEnrollGradeVO;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.service.ActivityCanEnrollGradeService;
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
 * 活动能报名的年级 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:42:17
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动能报名的年级")
public class ActivityCanEnrollGradeController {

    @Resource
    private ActivityCanEnrollGradeService activityCanEnrollGradeService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activityCanEnrollGrade/queryPage")
    @SaCheckPermission("activityCanEnrollGrade:query")
    public ResponseDTO<PageResult<ActivityCanEnrollGradeVO>> queryPage(@RequestBody @Valid ActivityCanEnrollGradeQueryForm queryForm) {
        return ResponseDTO.ok(activityCanEnrollGradeService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/activityCanEnrollGrade/add")
    @SaCheckPermission("activityCanEnrollGrade:add")
    public ResponseDTO<String> add(@RequestBody @Valid ActivityCanEnrollGradeAddForm addForm) {
        return activityCanEnrollGradeService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/activityCanEnrollGrade/update")
    @SaCheckPermission("activityCanEnrollGrade:update")
    public ResponseDTO<String> update(@RequestBody @Valid ActivityCanEnrollGradeUpdateForm updateForm) {
        return activityCanEnrollGradeService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/activityCanEnrollGrade/batchDelete")
    @SaCheckPermission("activityCanEnrollGrade:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return activityCanEnrollGradeService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/activityCanEnrollGrade/delete/{id}")
    @SaCheckPermission("activityCanEnrollGrade:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return activityCanEnrollGradeService.delete(id);
    }
}
