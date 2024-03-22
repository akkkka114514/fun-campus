package com.akkkka.funcampusmain.controller;

import com.akkkka.funcampusmainmodel.entity.School;
import com.akkkka.funcampusmainmodel.entity.scope.ScopeInsert;
import com.akkkka.funcampusmainapi.api.ISchoolService;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.util.ExceptionUtil;
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
@RequestMapping("/school")
public class SchoolController {
    @Resource
    private ISchoolService schoolService;
    /**
     * 添加学校
     * @param school 学校信息
     * @return 成功返回成功信息
     */
    @Validated(ScopeInsert.class)
    @RequestMapping("/add")
    public CommonResponse<Void> add(@RequestBody @Valid School school) {
        schoolService.addSchool(school);
        return CommonResponse.success();
    }

    @RequestMapping("/update")
    public CommonResponse<Void> update(@RequestBody @Valid School school) {
        schoolService.updateSchool(school);
        return CommonResponse.success();
    }

    @RequestMapping("/delete")
    public CommonResponse<Void> delete(@RequestParam Integer id) {
        ExceptionUtil.throwIfIdNotValid(id);
        schoolService.deleteSchool(id);
        return CommonResponse.success();
    }
}
