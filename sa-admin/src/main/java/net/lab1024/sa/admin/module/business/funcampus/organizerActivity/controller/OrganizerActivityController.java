package net.lab1024.sa.admin.module.business.funcampus.organizerActivity.controller;

import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityAddForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.vo.OrganizerActivityVO;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.service.OrganizerActivityService;
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
 * 运营者发布活动的对应关系 Controller
 *
 * @Author akkkka114514
 * @Date 2025-09-19 14:08:56
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "运营者发布活动的对应关系")
public class OrganizerActivityController {

    @Resource
    private OrganizerActivityService organizerActivityService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/organizerActivity/queryPage")
    @SaCheckPermission("organizerActivity:query")
    public ResponseDTO<PageResult<OrganizerActivityVO>> queryPage(@RequestBody @Valid OrganizerActivityQueryForm queryForm) {
        return ResponseDTO.ok(organizerActivityService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/organizerActivity/add")
    @SaCheckPermission("organizerActivity:add")
    public ResponseDTO<String> add(@RequestBody @Valid OrganizerActivityAddForm addForm) {
        return organizerActivityService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/organizerActivity/update")
    @SaCheckPermission("organizerActivity:update")
    public ResponseDTO<String> update(@RequestBody @Valid OrganizerActivityUpdateForm updateForm) {
        return organizerActivityService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/organizerActivity/batchDelete")
    @SaCheckPermission("organizerActivity:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return organizerActivityService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/organizerActivity/delete/{id}")
    @SaCheckPermission("organizerActivity:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return organizerActivityService.delete(id);
    }
}
