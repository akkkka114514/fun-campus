package net.lab1024.sa.admin.module.business.funcampus.schoolInfo.controller;

import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoAddForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.vo.SchoolInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.service.SchoolInfoService;
import net.lab1024.sa.base.common.domain.ValidateList;
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
 * 学校信息表 Controller
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "学校信息表")
public class SchoolInfoController {

    @Resource
    private SchoolInfoService schoolInfoService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/schoolInfo/queryPage")
    @SaCheckPermission("schoolInfo:query")
    public ResponseDTO<PageResult<SchoolInfoVO>> queryPage(@RequestBody @Valid SchoolInfoQueryForm queryForm) {
        return ResponseDTO.ok(schoolInfoService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/schoolInfo/add")
    @SaCheckPermission("schoolInfo:add")
    public ResponseDTO<String> add(@RequestBody @Valid SchoolInfoAddForm addForm) {
        return schoolInfoService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/schoolInfo/update")
    @SaCheckPermission("schoolInfo:update")
    public ResponseDTO<String> update(@RequestBody @Valid SchoolInfoUpdateForm updateForm) {
        return schoolInfoService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/schoolInfo/batchDelete")
    @SaCheckPermission("schoolInfo:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return schoolInfoService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/schoolInfo/delete/{id}")
    @SaCheckPermission("schoolInfo:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return schoolInfoService.delete(id);
    }
}
