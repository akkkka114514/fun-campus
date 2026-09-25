package com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.controller;


import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.service.ActivityCanEnrollCollegeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.vo.ActivityCanEnrollCollegeVO;
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
 * 活动能报名的学院 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-01 20:24:18
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动能报名的学院")
public class ActivityCanEnrollCollegeController {

    @Resource
    private ActivityCanEnrollCollegeService activityCanEnrollCollegeService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/activityCanEnrollCollege/queryPage")
    @SaCheckPermission("activityCanEnrollCollege:query")
    public ResponseDTO<PageResult<ActivityCanEnrollCollegeVO>> queryPage(@RequestBody @Valid ActivityCanEnrollCollegeQueryForm queryForm) {
        return ResponseDTO.ok(activityCanEnrollCollegeService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/activityCanEnrollCollege/add")
    @SaCheckPermission("activityCanEnrollCollege:add")
    public ResponseDTO<String> add(@RequestBody @Valid ActivityCanEnrollCollegeAddForm addForm) {
        return activityCanEnrollCollegeService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/activityCanEnrollCollege/update")
    @SaCheckPermission("activityCanEnrollCollege:update")
    public ResponseDTO<String> update(@RequestBody @Valid ActivityCanEnrollCollegeUpdateForm updateForm) {
        return activityCanEnrollCollegeService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/activityCanEnrollCollege/batchDelete")
    @SaCheckPermission("activityCanEnrollCollege:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return activityCanEnrollCollegeService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/activityCanEnrollCollege/delete/{id}")
    @SaCheckPermission("activityCanEnrollCollege:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return activityCanEnrollCollegeService.delete(id);
    }
}
