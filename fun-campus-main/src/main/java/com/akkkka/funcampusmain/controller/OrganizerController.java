package com.akkkka.funcampusmain.controller;

import com.akkkka.funcampusmainmodel.entity.Organizer;
import com.akkkka.funcampusmainmodel.entity.scope.ScopeInsert;
import com.akkkka.funcampusmainapi.api.IOrganizerService;
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
 * @author akkkka
 */
@RestController
@RequestMapping("/organizer")
public class OrganizerController {
    @Resource
    private IOrganizerService organizerService;

    @RequestMapping("/add")
    @Validated(ScopeInsert.class)
    public CommonResponse<Void> add(@RequestBody @Valid Organizer organizer) {
        organizerService.addOrganizer(organizer);
        return CommonResponse.success();
    }

    @RequestMapping("/update")
    @Validated(ScopeInsert.class)
    public CommonResponse<Void> update(@RequestBody @Valid Organizer organizer) {
        organizerService.updateOrganizer(organizer);
        return CommonResponse.success();
    }

    @RequestMapping("/delete")
    @Validated(ScopeInsert.class)
    public CommonResponse<Void> delete(@RequestParam Integer id) {
        ExceptionUtil.throwIfIdNotValid(id);
        organizerService.deleteOrganizer(id);
        return CommonResponse.success();
    }

}
