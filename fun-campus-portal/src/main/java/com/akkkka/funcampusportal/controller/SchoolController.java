package com.akkkka.funcampusportal.controller;

import com.akkkka.funcampusportal.domain.School;
import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.service.ISchoolService;
import com.akkkka.common.core.domain.R;
import com.akkkka.funcampusportal.util.ParamCheckUtil;
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
@RequestMapping("/school")
@Tag(name = "school")
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
    @Operation(description = "add school")
    public R<Void> add(@RequestBody @Valid School school) {
        schoolService.add(school);
        return R.ok();
    }

    @RequestMapping("/update")
    @Operation(description = "update school")
    public R<Void> update(@RequestBody @Valid School school) {
        schoolService.update(school);
        return R.ok();
    }

    @RequestMapping("/delete")
    @Operation(description = "delete school")
    public R<Void> delete(@RequestParam Integer id) {
        ParamCheckUtil.checkPositiveInteger(id);
        schoolService.delete(id);
        return R.ok();
    }
}
