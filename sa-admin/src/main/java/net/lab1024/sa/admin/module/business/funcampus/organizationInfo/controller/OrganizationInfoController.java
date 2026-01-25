package net.lab1024.sa.admin.module.business.funcampus.organizationInfo.controller;

import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoAddForm;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo.OrganizationInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoService;
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
 * 各学校组织信息 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:24:05
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "各学校组织信息")
public class OrganizationInfoController {

    @Resource
    private OrganizationInfoService organizationInfoService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/organizationInfo/queryPage")
    @SaCheckPermission("organizationInfo:query")
    public ResponseDTO<PageResult<OrganizationInfoVO>> queryPage(@RequestBody @Valid OrganizationInfoQueryForm queryForm) {
        return ResponseDTO.ok(organizationInfoService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/organizationInfo/add")
    @SaCheckPermission("organizationInfo:add")
    public ResponseDTO<String> add(@RequestBody @Valid OrganizationInfoAddForm addForm) {
        return organizationInfoService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/organizationInfo/update")
    @SaCheckPermission("organizationInfo:update")
    public ResponseDTO<String> update(@RequestBody @Valid OrganizationInfoUpdateForm updateForm) {
        return organizationInfoService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/organizationInfo/batchDelete")
    @SaCheckPermission("organizationInfo:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return organizationInfoService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/organizationInfo/delete/{id}")
    @SaCheckPermission("organizationInfo:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return organizationInfoService.delete(id);
    }
}
