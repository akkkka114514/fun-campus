package com.akkkka.admin.module.business.funcampus.tribe.controller;

import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeAddForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.SimpleTribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.service.TribeService;
import com.akkkka.common.domain.ValidateList;
import org.springframework.web.bind.annotation.*;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 部落 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "部落")
@RequestMapping("portal")
public class TribeController {

    @Resource
    private TribeService tribeService;

    @Operation(summary = "查询 @author akkkka114514")
    @GetMapping("/tribe/query/simple")
    public ResponseDTO<List<SimpleTribeVO>> querySimpleList(@RequestParam String keyword) {
        return ResponseDTO.ok(tribeService.querySimpleList(keyword));
    }
}
