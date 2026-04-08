package com.akkkka.admin.module.business.funcampus.tribeUser.controller;

import com.akkkka.admin.module.business.funcampus.tribeUser.domain.form.TribeUserAddForm;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.form.TribeUserQueryForm;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.form.TribeUserUpdateForm;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.vo.TribeUserVO;
import com.akkkka.admin.module.business.funcampus.tribeUser.service.TribeUserService;
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
 * 参与部落的用户 Controller
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:42:50
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "参与部落的用户")
public class TribeUserController {

    @Resource
    private TribeUserService tribeUserService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/tribeUser/queryPage")
    @SaCheckPermission("tribeUser:query")
    public ResponseDTO<PageResult<TribeUserVO>> queryPage(@RequestBody @Valid TribeUserQueryForm queryForm) {
        return ResponseDTO.ok(tribeUserService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/tribeUser/add")
    @SaCheckPermission("tribeUser:add")
    public ResponseDTO<String> add(@RequestBody @Valid TribeUserAddForm addForm) {
        return tribeUserService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/tribeUser/update")
    @SaCheckPermission("tribeUser:update")
    public ResponseDTO<String> update(@RequestBody @Valid TribeUserUpdateForm updateForm) {
        return tribeUserService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/tribeUser/batchDelete")
    @SaCheckPermission("tribeUser:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return tribeUserService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/tribeUser/delete/{id}")
    @SaCheckPermission("tribeUser:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return tribeUserService.delete(id);
    }
}
