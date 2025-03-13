package com.akkkka.funcampusportal.controller;

import com.akkkka.funcampusportal.domain.Organizer;
import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.service.IOrganizerService;
import com.akkkka.funcampusutil.util.CommonResponse;
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
 * @author akkkka
 */
@RestController
@RequestMapping("/organizer")
@Api(tags = "organizer")
public class OrganizerController {
    @Resource
    private IOrganizerService organizerService;

    @RequestMapping("/add")
    @Validated(ScopeInsert.class)
    @ApiOperation(value = "add organizer")
    public CommonResponse<Void> add(@RequestBody @Valid Organizer organizer) {
        organizerService.add(organizer);
        return CommonResponse.success();
    }

    @RequestMapping("/update")
    @Validated(ScopeInsert.class)
    @ApiOperation(value = "update organizer")
    public CommonResponse<Void> update(@RequestBody @Valid Organizer organizer) {
        organizerService.update(organizer);
        return CommonResponse.success();
    }

    @RequestMapping("/delete")
    @Validated(ScopeInsert.class)
    @ApiOperation(value = "delete organizer")
    public CommonResponse<Void> delete(@RequestParam Integer id) {
        ExceptionUtil.throwIfIdNotValid(id);
        organizerService.delete(id);
        return CommonResponse.success();
    }

}
