package com.akkkka.funcampusmain.controller;

import com.akkkka.funcampusmainmodel.entity.College;
import com.akkkka.funcampusmainmodel.entity.scope.ScopeInsert;
import com.akkkka.funcampusmainapi.api.ICollegeService;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.common.ResponseEnum;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.annotation.Resource;
import javax.validation.Valid;
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
public class CollegeController {
    @Resource
    private ICollegeService collegeService;

    @RequestMapping("/add")
    @Validated(ScopeInsert.class)
    public CommonResponse<Void> add(@RequestBody @Valid College college) {
        collegeService.addCollege(college);
        return CommonResponse.success();
    }

    /**
     * 不支持修改所属学校
     * */
    @RequestMapping("/update")
    public CommonResponse<Void> update(@RequestBody @Valid College college) {
        collegeService.updateCollege(college);
        return CommonResponse.success();
    }

    @RequestMapping("/delete")
    public CommonResponse<Void> delete(@RequestParam Integer id) {
        ExceptionUtil.throwIfIdNotValid(id);
        collegeService.deleteCollege(id);
        return CommonResponse.success();
    }

    @RequestMapping("/get")
    public CommonResponse<College> get(@RequestParam Integer id) {
        if(id == null || id < 0){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        return CommonResponse.success(collegeService.getById(id));
    }

    @RequestMapping("/list")
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
