package com.akkkka.funcampusportal.controller;

import com.akkkka.funcampusportal.domain.Organizer;
import com.akkkka.funcampusportal.domain.scope.ScopeInsert;
import com.akkkka.funcampusportal.service.IOrganizerService;
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
 * @author akkkka
 */
@RestController
@RequestMapping("/organizer")
@Tag(name = "organizer")
public class OrganizerController {
    @Resource
    private IOrganizerService organizerService;

    @RequestMapping("/add")
    @Validated(ScopeInsert.class)
    @Operation(description = "add organizer")
    public R<Void> add(@RequestBody @Valid Organizer organizer) {
        organizerService.add(organizer);
        return R.ok();
    }

    @RequestMapping("/update")
    @Validated(ScopeInsert.class)
    @Operation(description = "update organizer")
    public R<Void> update(@RequestBody @Valid Organizer organizer) {
        organizerService.update(organizer);
        return R.ok();
    }

    @RequestMapping("/delete")
    @Validated(ScopeInsert.class)
    @Operation(description = "delete organizer")
    public R<Void> delete(@RequestParam Integer id) {
        ParamCheckUtil.checkPositiveInteger(id);
        organizerService.delete(id);
        return R.ok();
    }

}
