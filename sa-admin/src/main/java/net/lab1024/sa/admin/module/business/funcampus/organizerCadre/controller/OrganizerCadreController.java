package net.lab1024.sa.admin.module.business.funcampus.organizerCadre.controller;

import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form.OrganizerCadreAddForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form.OrganizerCadreQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form.OrganizerCadreUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.vo.OrganizerCadreVO;
import net.lab1024.sa.admin.module.business.funcampus.organizerCadre.service.OrganizerCadreService;
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
 * 组织干事用户 Controller
 *
 * @Author akkkka114514
 * @Date 2025-11-08 14:09:30
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "组织干事用户")
public class OrganizerCadreController {

    @Resource
    private OrganizerCadreService organizerCadreService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/organizerCadre/queryPage")
    @SaCheckPermission("organizerCadre:query")
    public ResponseDTO<PageResult<OrganizerCadreVO>> queryPage(@RequestBody @Valid OrganizerCadreQueryForm queryForm) {
        return ResponseDTO.ok(organizerCadreService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/organizerCadre/add")
    @SaCheckPermission("organizerCadre:add")
    public ResponseDTO<String> add(@RequestBody @Valid OrganizerCadreAddForm addForm) {
        return organizerCadreService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/organizerCadre/update")
    @SaCheckPermission("organizerCadre:update")
    public ResponseDTO<String> update(@RequestBody @Valid OrganizerCadreUpdateForm updateForm) {
        return organizerCadreService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/organizerCadre/batchDelete")
    @SaCheckPermission("organizerCadre:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return organizerCadreService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/organizerCadre/delete/{id}")
    @SaCheckPermission("organizerCadre:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return organizerCadreService.delete(id);
    }

    @Operation(summary = "确认签到码 @author akkkka114514")
    @GetMapping("/organizerCadre/confirmSignInCode/{userId}/{activityId}/{uuid}")
    @SaCheckPermission("organizerCadre:update")
    public ResponseDTO<Void> confirmSignInCode(@PathVariable Long userId,@PathVariable Long activityId,@PathVariable String uuid) {
        return organizerCadreService.confirmSignInCode(userId,activityId,uuid);
    }

}
