package com.akkkka.admin.module.business.funcampus.gradeInfo.controller;

import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoAddForm;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.vo.GradeInfoVO;
import com.akkkka.admin.module.business.funcampus.gradeInfo.service.GradeInfoService;
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
 * 年级信息 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:47:10
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "年级信息")
public class GradeInfoController {

    @Resource
    private GradeInfoService gradeInfoService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/gradeInfo/queryPage")
    @SaCheckPermission("gradeInfo:query")
    public ResponseDTO<PageResult<GradeInfoVO>> queryPage(@RequestBody @Valid GradeInfoQueryForm queryForm) {
        return ResponseDTO.ok(gradeInfoService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/gradeInfo/add")
    @SaCheckPermission("gradeInfo:add")
    public ResponseDTO<String> add(@RequestBody @Valid GradeInfoAddForm addForm) {
        return gradeInfoService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/gradeInfo/update")
    @SaCheckPermission("gradeInfo:update")
    public ResponseDTO<String> update(@RequestBody @Valid GradeInfoUpdateForm updateForm) {
        return gradeInfoService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/gradeInfo/batchDelete")
    @SaCheckPermission("gradeInfo:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return gradeInfoService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/gradeInfo/delete/{id}")
    @SaCheckPermission("gradeInfo:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return gradeInfoService.delete(id);
    }
}
