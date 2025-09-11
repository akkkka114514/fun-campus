package net.lab1024.sa.admin.module.system.backendUser.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.system.backendUser.domain.form.*;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.BackendUserVO;
import net.lab1024.sa.admin.module.system.backendUser.service.BackendUserService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.apiencrypt.annotation.ApiDecrypt;
import net.lab1024.sa.base.module.support.securityprotect.service.Level3ProtectConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2025-09-11 20:24
 */
@RestController
@Tag(name = AdminSwaggerTagConst.System.SYSTEM_BACKEND_USER)
public class BackendUserController {

    @Resource
    private BackendUserService backendUserService;

    @Resource
    private Level3ProtectConfigService level3ProtectConfigService;

    @PostMapping("/backendUser/query")
    @Operation(summary = "员工管理查询 @author 卓大")
    public ResponseDTO<PageResult<BackendUserVO>> query(@Valid @RequestBody BackendUserQueryForm query) {
        return backendUserService.queryBackendUser(query);
    }

    @Operation(summary = "添加员工(返回添加员工的密码) @author 卓大")
    @PostMapping("/backendUser/add")
    @SaCheckPermission("system:backendUser:add")
    public ResponseDTO<String> addBackendUser(@Valid @RequestBody BackendUserAddForm backendUserAddForm) {
        return backendUserService.addBackendUser(backendUserAddForm);
    }

    @Operation(summary = "更新员工 @author 卓大")
    @PostMapping("/backendUser/update")
    @SaCheckPermission("system:backendUser:update")
    public ResponseDTO<String> updateBackendUser(@Valid @RequestBody BackendUserUpdateForm backendUserUpdateForm) {
        return backendUserService.updateBackendUser(backendUserUpdateForm);
    }

    @Operation(summary = "更新员工个人中心信息 @author 善逸")
    @PostMapping("/backendUser/update/center")
    public ResponseDTO<String> updateCenter(@Valid @RequestBody BackendUserUpdateCenterForm updateCenterForm) {
        updateCenterForm.setId(SmartRequestUtil.getRequestUserId());
        return backendUserService.updateCenter(updateCenterForm);
    }

    @Operation(summary = "更新员工禁用/启用状态 @author 卓大")
    @GetMapping("/backendUser/update/disabled/{backendUserId}")
    @SaCheckPermission("system:backendUser:disabled")
    public ResponseDTO<String> updateDisableFlag(@PathVariable Long backendUserId) {
        return backendUserService.updateDisableFlag(backendUserId);
    }

    @Operation(summary = "批量删除员工 @author 卓大")
    @PostMapping("/backendUser/update/batch/delete")
    @SaCheckPermission("system:backendUser:delete")
    public ResponseDTO<String> batchUpdateDeleteFlag(@RequestBody List<Long> backendUserIdList) {
        return backendUserService.batchUpdateDeleteFlag(backendUserIdList);
    }

    @Operation(summary = "修改密码 @author 卓大")
    @PostMapping("/backendUser/update/password")
    @ApiDecrypt
    public ResponseDTO<String> updatePassword(@Valid @RequestBody BackendUserUpdatePasswordForm updatePasswordForm) {
        updatePasswordForm.setId(SmartRequestUtil.getRequestUserId());
        return backendUserService.updatePassword(SmartRequestUtil.getRequestUser(), updatePasswordForm);
    }

    @Operation(summary = "获取密码复杂度 @author 卓大")
    @GetMapping("/backendUser/getPasswordComplexityEnabled")
    @ApiDecrypt
    public ResponseDTO<Boolean> getPasswordComplexityEnabled() {
        return ResponseDTO.ok(level3ProtectConfigService.isPasswordComplexityEnabled());
    }

    @Operation(summary = "重置员工密码 @author 卓大")
    @GetMapping("/backendUser/update/password/reset/{backendUserId}")
    @SaCheckPermission("system:backendUser:password:reset")
    public ResponseDTO<String> resetPassword(@PathVariable Long backendUserId) {
        return backendUserService.resetPassword(backendUserId);
    }
    @Operation(summary = "查询所有员工 @author 卓大")
    @GetMapping("/backendUser/queryAll")
    public ResponseDTO<List<BackendUserVO>> queryAllBackendUser(@RequestParam(value = "disabledFlag", required = false) Boolean disabledFlag) {
        return backendUserService.queryAllBackendUser(disabledFlag);
    }

}
