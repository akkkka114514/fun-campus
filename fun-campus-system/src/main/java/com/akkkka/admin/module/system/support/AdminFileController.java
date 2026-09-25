package com.akkkka.admin.module.system.support;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import com.akkkka.common.controller.SupportBaseController;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.constant.SwaggerTagConst;
import com.akkkka.module.support.file.domain.form.FileQueryForm;
import com.akkkka.module.support.file.domain.vo.FileVO;
import com.akkkka.module.support.file.service.FileService;
import org.springframework.web.bind.annotation.*;

/**
 * 文件服务
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
@Tag(name = SwaggerTagConst.Support.FILE)
@RequestMapping("backend")
public class AdminFileController extends SupportBaseController {

    @Resource
    private FileService fileService;

    @Operation(summary = "分页查询 @author 1024创新实验室-主任-卓大")
    @PostMapping("/file/queryPage")
    @SaCheckPermission("support:file:query")
    public ResponseDTO<PageResult<FileVO>> queryPage(@RequestBody @Valid FileQueryForm queryForm) {
        return ResponseDTO.ok(fileService.queryPage(queryForm));
    }

}
