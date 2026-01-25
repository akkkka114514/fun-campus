package net.lab1024.sa.admin.module.business.funcampus.collegeInfo.controller;

import javassist.Loader;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoAddForm;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.vo.CollegeInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.vo.SimpleCollegeInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
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

import java.util.List;

/**
 * 学院信息 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:45:43
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "学院信息")
public class CollegeInfoController {

    @Resource
    private CollegeInfoService collegeInfoService;

    @Operation(summary = "分页查询 @author akkkka114514")
    @PostMapping("/collegeInfo/queryPage")
    @SaCheckPermission("collegeInfo:query")
    public ResponseDTO<PageResult<CollegeInfoVO>> queryPage(@RequestBody @Valid CollegeInfoQueryForm queryForm) {
        return ResponseDTO.ok(collegeInfoService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author akkkka114514")
    @PostMapping("/collegeInfo/add")
    @SaCheckPermission("collegeInfo:add")
    public ResponseDTO<String> add(@RequestBody @Valid CollegeInfoAddForm addForm) {
        return collegeInfoService.add(addForm);
    }

    @Operation(summary = "更新 @author akkkka114514")
    @PostMapping("/collegeInfo/update")
    @SaCheckPermission("collegeInfo:update")
    public ResponseDTO<String> update(@RequestBody @Valid CollegeInfoUpdateForm updateForm) {
        return collegeInfoService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author akkkka114514")
    @PostMapping("/collegeInfo/batchDelete")
    @SaCheckPermission("collegeInfo:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return collegeInfoService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author akkkka114514")
    @GetMapping("/collegeInfo/delete/{id}")
    @SaCheckPermission("collegeInfo:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return collegeInfoService.delete(id);
    }

}
