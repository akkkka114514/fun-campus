package com.akkkka.admin.module.business.funcampus.tribe.controller;

import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeActivityQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeMemberQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribePortalQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.SimpleTribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeActivityVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeDetailVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeMemberVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribePortalVO;
import com.akkkka.admin.module.business.funcampus.tribe.service.TribeService;
import org.springframework.web.bind.annotation.*;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 部落 Controller（门户端）
 * <p>
 * 管理端 CRUD 见 TribeAdminController（/backend/tribe/*）
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "部落（门户）")
@RequestMapping("portal")
public class TribeController {

    @Resource
    private TribeService tribeService;

    @Operation(summary = "查询（简单列表，按关键词） @author akkkka114514")
    @GetMapping("/tribe/query/simple")
    public ResponseDTO<List<SimpleTribeVO>> querySimpleList(@RequestParam String keyword) {
        return ResponseDTO.ok(tribeService.querySimpleList(keyword));
    }

    @Operation(summary = "分页查询本校部落（支持关键词与热度排序） @author akkkka114514")
    @PostMapping("/tribe/queryPage")
    public ResponseDTO<PageResult<TribePortalVO>> queryPage(@RequestBody @Valid TribePortalQueryForm queryForm) {
        return ResponseDTO.ok(tribeService.queryPortalPage(queryForm));
    }

    @Operation(summary = "部落详情 @author akkkka114514")
    @GetMapping("/tribe/detail")
    public ResponseDTO<TribeDetailVO> detail(@RequestParam Long tribeId) {
        return ResponseDTO.ok(tribeService.detail(tribeId));
    }

    @Operation(summary = "部落成员分页 @author akkkka114514")
    @PostMapping("/tribe/member/queryPage")
    public ResponseDTO<PageResult<TribeMemberVO>> memberQueryPage(@RequestBody @Valid TribeMemberQueryForm queryForm) {
        return ResponseDTO.ok(tribeService.queryMemberPage(queryForm));
    }

    @Operation(summary = "部落发起的活动分页 @author akkkka114514")
    @PostMapping("/tribe/activity/queryPage")
    public ResponseDTO<PageResult<TribeActivityVO>> activityQueryPage(@RequestBody @Valid TribeActivityQueryForm queryForm) {
        return ResponseDTO.ok(tribeService.queryActivityPage(queryForm));
    }
}
