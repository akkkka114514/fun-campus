package com.akkkka.module.support.helpdoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.akkkka.common.controller.SupportBaseController;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.constant.SwaggerTagConst;
import com.akkkka.module.support.helpdoc.domain.form.HelpDocViewRecordQueryForm;
import com.akkkka.module.support.helpdoc.domain.vo.HelpDocCatalogVO;
import com.akkkka.module.support.helpdoc.domain.vo.HelpDocDetailVO;
import com.akkkka.module.support.helpdoc.domain.vo.HelpDocVO;
import com.akkkka.module.support.helpdoc.domain.vo.HelpDocViewRecordVO;
import com.akkkka.module.support.helpdoc.service.HelpDocCatalogService;
import com.akkkka.module.support.helpdoc.service.HelpDocUserService;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帮助文档
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-20 23:11:42
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Tag(name = SwaggerTagConst.Support.HELP_DOC)
@RestController
public class HelpDocController extends SupportBaseController {

    @Resource
    private HelpDocCatalogService helpDocCatalogService;

    @Resource
    private HelpDocUserService helpDocUserService;

    // --------------------- 帮助文档 【目录】 -------------------------

    @Operation(summary = "帮助文档目录-获取全部 @author 卓大")
    @GetMapping("/helpDoc/helpDocCatalog/getAll")
    public ResponseDTO<List<HelpDocCatalogVO>> getAll() {
        return ResponseDTO.ok(helpDocCatalogService.getAll());
    }

    // --------------------- 帮助文档 【用户】-------------------------

    @Operation(summary = "【用户】帮助文档-查看详情 @author 卓大")
    @GetMapping("/helpDoc/user/view/{helpDocId}")
    @RepeatSubmit
    public ResponseDTO<HelpDocDetailVO> view(@PathVariable Long helpDocId, HttpServletRequest request) {
        return helpDocUserService.view(
                SmartRequestUtil.getRequestUser(),
                helpDocId);
    }

    @Operation(summary = "【用户】帮助文档-查询全部 @author 卓大")
    @GetMapping("/helpDoc/user/queryAllHelpDocList")
    @RepeatSubmit
    public ResponseDTO<List<HelpDocVO>> queryAllHelpDocList() {
        return helpDocUserService.queryAllHelpDocList();
    }


    @Operation(summary = "【用户】帮助文档-查询 查看记录 @author 卓大")
    @PostMapping("/helpDoc/user/queryViewRecord")
    @RepeatSubmit
    public ResponseDTO<PageResult<HelpDocViewRecordVO>> queryViewRecord(@RequestBody @Valid HelpDocViewRecordQueryForm helpDocViewRecordQueryForm) {
        return ResponseDTO.ok(helpDocUserService.queryViewRecord(helpDocViewRecordQueryForm));
    }
}