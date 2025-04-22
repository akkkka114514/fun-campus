package com.akkkka.funcampusportal.controller;


import com.akkkka.funcampusportal.domain.College;
import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.service.ICollegeService;
import com.akkkka.common.core.domain.R;
import com.akkkka.funcampusportal.util.ParamCheckUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.annotation.Resource;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
@RestController
@RequestMapping("/college")
@Tag(name = "college")
public class CollegeController {
    @Resource
    private ICollegeService collegeService;

    @RequestMapping("/add")
    @Validated(ScopeInsert.class)
    @Operation(description = "add college")
    public R<Void> add(@RequestBody @Valid College college) {
        collegeService.add(college);
        return R.ok();
    }

    /**
     * 不支持修改所属学校
     * */
    @RequestMapping("/update")
    @Operation(description = "update college")
    public R<Void> update(@RequestBody @Valid College college) {
        collegeService.update(college);
        return R.ok();
    }

    @RequestMapping("/delete")
    @Operation(description = "delete college")
    public R<Void> delete(@RequestParam Integer id) {
        ParamCheckUtil.checkPositiveInteger(id);
        collegeService.delete(id);
        return R.ok();
    }

    @RequestMapping("/get")
    @Operation(description = "get college")
    public R<College> get(@RequestParam Integer id) {
        ParamCheckUtil.checkPositiveInteger(id);
        return R.ok(collegeService.getById(id));
    }

    @RequestMapping("/list")
    @Operation(description = "list college")
    public R<Page<College>> list(@RequestParam Integer page, @RequestParam Integer size) {
        ParamCheckUtil.checkPositiveInteger(page, size);
        return R.ok(
                collegeService.page(
                        new Page<>(page, size),
                        new QueryWrapper<College>().eq("is_deleted",0)
                )
        );
    }
}
