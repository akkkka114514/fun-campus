package net.lab1024.sa.admin.module.business.funcampus.portalUser.controller;

import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserAddForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.service.PortalUserService;
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
 * 前端用户 Controller
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "前端用户")
public class PortalUserController {

    @Resource
    private PortalUserService portalUserService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/portalUser/queryPage")
    @SaCheckPermission("portalUser:query")
    public ResponseDTO<PageResult<PortalUserVO>> queryPage(@RequestBody @Valid PortalUserQueryForm queryForm) {
        return ResponseDTO.ok(portalUserService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/portalUser/add")
    @SaCheckPermission("portalUser:add")
    public ResponseDTO<String> add(@RequestBody @Valid PortalUserAddForm addForm) {
        return portalUserService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/portalUser/update")
    @SaCheckPermission("portalUser:update")
    public ResponseDTO<String> update(@RequestBody @Valid PortalUserUpdateForm updateForm) {
        return portalUserService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/portalUser/batchDelete")
    @SaCheckPermission("portalUser:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return portalUserService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/portalUser/delete/{id}")
    @SaCheckPermission("portalUser:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return portalUserService.delete(id);
    }

}
