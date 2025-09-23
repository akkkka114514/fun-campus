package net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.controller;

import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserAddForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.vo.PortalOrganizerUserVO;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.service.PortalOrganizerUserService;
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
 * 组织账号运营者 Controller
 *
 * @Author akkkka114514
 * @Date 2025-09-19 10:12:39
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "组织账号运营者")
public class PortalOrganizerUserController {

    @Resource
    private PortalOrganizerUserService portalOrganizerUserService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/portalOrganizerUser/queryPage")
    @SaCheckPermission("portalOrganizerUser:query")
    public ResponseDTO<PageResult<PortalOrganizerUserVO>> queryPage(@RequestBody @Valid PortalOrganizerUserQueryForm queryForm) {
        return ResponseDTO.ok(portalOrganizerUserService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/portalOrganizerUser/add")
    @SaCheckPermission("portalOrganizerUser:add")
    public ResponseDTO<String> add(@RequestBody @Valid PortalOrganizerUserAddForm addForm) {
        return portalOrganizerUserService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/portalOrganizerUser/update")
    @SaCheckPermission("portalOrganizerUser:update")
    public ResponseDTO<String> update(@RequestBody @Valid PortalOrganizerUserUpdateForm updateForm) {
        return portalOrganizerUserService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/portalOrganizerUser/batchDelete")
    @SaCheckPermission("portalOrganizerUser:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return portalOrganizerUserService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/portalOrganizerUser/delete/{id}")
    @SaCheckPermission("portalOrganizerUser:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return portalOrganizerUserService.delete(id);
    }

    @Operation(summary = "批量禁用 @author akkkka114514")
    @PostMapping("/portalOrganizerUser/batchDisable")
    @SaCheckPermission("portalOrganizerUser:disable")
    public ResponseDTO<String> batchDisable(@RequestBody ValidateList<Long> idList) {
        return portalOrganizerUserService.batchDisable(idList);
    }
}
