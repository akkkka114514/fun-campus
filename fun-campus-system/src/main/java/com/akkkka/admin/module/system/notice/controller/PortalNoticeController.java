package com.akkkka.admin.module.system.notice.controller;

import com.akkkka.admin.module.system.notice.domain.form.PortalNoticeQueryForm;
import com.akkkka.admin.module.system.notice.domain.vo.NoticeTypeVO;
import com.akkkka.admin.module.system.notice.domain.vo.PortalNoticeDetailVO;
import com.akkkka.admin.module.system.notice.domain.vo.PortalNoticeVO;
import com.akkkka.admin.module.system.notice.service.NoticeTypeService;
import com.akkkka.admin.module.system.notice.service.PortalNoticeService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门户 通知公告路由：当前登录的前台用户（学生/组织者）浏览校内公告
 * <p>
 * 请求路径必须包含 portal 段：由 AdminInterceptor 解析为门户登录用户；
 * 仅返回全员可见且已发布的通知公告，后管侧 /backend/notice/* 保持不变
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */
@Tag(name = "门户-通知公告")
@RestController
@RequestMapping("portal")
public class PortalNoticeController {

    @Resource
    private PortalNoticeService portalNoticeService;

    @Resource
    private NoticeTypeService noticeTypeService;

    @Operation(summary = "通知公告-分页查询公告列表")
    @PostMapping("/notice/queryNotice")
    public ResponseDTO<PageResult<PortalNoticeVO>> queryNotice(@RequestBody @Valid PortalNoticeQueryForm queryForm) {
        return portalNoticeService.queryNotice(queryForm);
    }

    @Operation(summary = "通知公告-查询公告详情")
    @GetMapping("/notice/detail/{noticeId}")
    public ResponseDTO<PortalNoticeDetailVO> detail(@PathVariable Long noticeId) {
        return portalNoticeService.detail(noticeId);
    }

    @Operation(summary = "通知公告-查询公告分类列表")
    @GetMapping("/notice/typeList")
    public ResponseDTO<List<NoticeTypeVO>> typeList() {
        return ResponseDTO.ok(noticeTypeService.getAll());
    }

}
