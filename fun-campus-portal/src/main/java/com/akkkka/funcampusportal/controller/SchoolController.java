package com.akkkka.funcampusportal.controller;

import com.akkkka.funcampusportal.domain.School;
import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.service.ISchoolService;
import com.akkkka.common.core.domain.R;
import com.akkkka.funcampusportal.util.ParamCheckUtil;
import com.akkkka.funcampusutil.util.ExceptionUtil;
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
@RequestMapping("/school")
@Api(tags = "school")
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
    @ApiOperation(value = "add school")
    public R<Void> add(@RequestBody @Valid School school) {
        schoolService.add(school);
        return R.ok();
    }

    @RequestMapping("/update")
    @ApiOperation(value = "update school")
    public R<Void> update(@RequestBody @Valid School school) {
        schoolService.update(school);
        return R.ok();
    }

    @RequestMapping("/delete")
    @ApiOperation(value = "delete school")
    public R<Void> delete(@RequestParam Integer id) {
        ParamCheckUtil.checkPositiveInteger(id);
        schoolService.delete(id);
        return R.ok();
    }
}
