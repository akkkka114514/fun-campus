package net.lab1024.sa.admin.module.system.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.BackendUserVO;
import net.lab1024.sa.admin.module.system.role.domain.form.RoleBackendUserQueryForm;
import net.lab1024.sa.admin.module.system.role.domain.form.RoleBackendUserUpdateForm;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleSelectedVO;
import net.lab1024.sa.admin.module.system.role.service.RoleBackendUserService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色的后台用户
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-02-26 22:09:59
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
@Tag(name = AdminSwaggerTagConst.System.SYSTEM_ROLE_BACKEND_USER)
public class RoleBackendUserController {

    @Resource
    private RoleBackendUserService roleBackendUserService;

    @Operation(summary = "查询某个角色下的后台用户列表  @author 卓大")
    @PostMapping("/role/backendUser/queryBackendUser")
    public ResponseDTO<PageResult<BackendUserVO>> queryBackendUser(@Valid @RequestBody RoleBackendUserQueryForm roleBackendUserQueryForm) {
        return roleBackendUserService.queryBackendUser(roleBackendUserQueryForm);
    }

    @Operation(summary = "获取某个角色下的所有后台用户列表(无分页)  @author 卓大")
    @GetMapping("/role/backendUser/getAllBackendUserByRoleId/{roleId}")
    public ResponseDTO<List<BackendUserVO>> listAllBackendUserRoleId(@PathVariable Long roleId) {
        return ResponseDTO.ok(roleBackendUserService.getAllBackendUserByRoleId(roleId));
    }

    @Operation(summary = "从角色成员列表中移除后台用户 @author 卓大")
    @GetMapping("/role/backendUser/removeBackendUser")
    @SaCheckPermission("system:role:backendUser:delete")
    public ResponseDTO<String> removeBackendUser(Long backendUserId, Long roleId) {
        return roleBackendUserService.removeRoleBackendUser(backendUserId, roleId);
    }

    @Operation(summary = "从角色成员列表中批量移除后台用户 @author 卓大")
    @PostMapping("/role/backendUser/batchRemoveRoleBackendUser")
    @SaCheckPermission("system:role:backendUser:batch:delete")
    public ResponseDTO<String> batchRemoveBackendUser(@Valid @RequestBody RoleBackendUserUpdateForm updateForm) {
        return roleBackendUserService.batchRemoveRoleBackendUser(updateForm);
    }

    @Operation(summary = "角色成员列表中批量添加后台用户 @author 卓大")
    @PostMapping("/role/backendUser/batchAddRoleBackendUser")
    @SaCheckPermission("system:role:backendUser:add")
    public ResponseDTO<String> addBackendUserList(@Valid @RequestBody RoleBackendUserUpdateForm addForm) {
        return roleBackendUserService.batchAddRoleBackendUser(addForm);
    }

    @Operation(summary = "获取后台用户所有选中的角色和所有角色 @author 卓大")
    @GetMapping("/role/backendUser/getRoles/{backendUserId}")
    public ResponseDTO<List<RoleSelectedVO>> getRoleByBackendUserId(@PathVariable Long backendUserId) {
        return ResponseDTO.ok(roleBackendUserService.getRoleInfoListByBackendUserId(backendUserId));
    }
}
