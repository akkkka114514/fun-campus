package com.akkkka.funcampusportal.controller;


import com.akkkka.funcampusportal.domain.College;
import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.service.ICollegeService;
import com.akkkka.funcampusutil.util.CommonResponse;
import com.akkkka.funcampusutil.constant.ResponseEnum;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.annotation.Resource;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "college")
public class CollegeController {
    @Resource
    private ICollegeService collegeService;

    @RequestMapping("/add")
    @Validated(ScopeInsert.class)
    @ApiOperation(value = "add college")
    public CommonResponse<Void> add(@RequestBody @Valid College college) {
        collegeService.add(college);
        return CommonResponse.success();
    }

    /**
     * 不支持修改所属学校
     * */
    @RequestMapping("/update")
    @ApiOperation(value = "update college")
    public CommonResponse<Void> update(@RequestBody @Valid College college) {
        collegeService.update(college);
        return CommonResponse.success();
    }

    @RequestMapping("/delete")
    @ApiOperation(value = "delete college")
    public CommonResponse<Void> delete(@RequestParam Integer id) {
        ExceptionUtil.throwIfIdNotValid(id);
        collegeService.delete(id);
        return CommonResponse.success();
    }

    @RequestMapping("/get")
    @ApiOperation(value = "get college")
    public CommonResponse<College> get(@RequestParam Integer id) {
        if(id == null || id < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        return CommonResponse.success(collegeService.getById(id));
    }

    @RequestMapping("/list")
    @ApiOperation(value = "list college")
    public CommonResponse<Page<College>> list(@RequestParam Integer page, @RequestParam Integer size) {
        if (page == null || page < 0 || size == null || size < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        return CommonResponse.success(
                collegeService.page(
                        new Page<>(page, size),
                        new QueryWrapper<College>().eq("is_deleted",0)
                )
        );
    }
}
