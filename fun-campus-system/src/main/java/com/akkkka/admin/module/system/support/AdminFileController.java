package com.akkkka.admin.module.system.support;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.extra.servlet.JakartaServletUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.akkkka.common.constant.RequestHeaderConst;
import com.akkkka.common.controller.SupportBaseController;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.common.util.SmartResponseUtil;
import com.akkkka.constant.SwaggerTagConst;
import com.akkkka.module.support.file.domain.form.FileQueryForm;
import com.akkkka.module.support.file.domain.vo.FileDownloadVO;
import com.akkkka.module.support.file.domain.vo.FileUploadVO;
import com.akkkka.module.support.file.domain.vo.FileVO;
import com.akkkka.module.support.file.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @Operation(summary = "文件上传 @author 胡克")
    @PostMapping("/file/upload")
    public ResponseDTO<FileUploadVO> upload(@RequestParam MultipartFile file, @RequestParam Integer folder) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        return fileService.fileUpload(file, folder, requestUser);
    }

    @Operation(summary = "获取文件URL：根据fileKey @author 胡克")
    @GetMapping("/file/getFileUrl")
    public ResponseDTO<String> getUrl(@RequestParam String fileKey) {
        return fileService.getFileUrl(fileKey);
    }

    @Operation(summary = "下载文件流（根据fileKey） @author 胡克")
    @GetMapping("/file/downLoad")
    public void downLoad(@RequestParam String fileKey, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userAgent = JakartaServletUtil.getHeaderIgnoreCase(request, RequestHeaderConst.USER_AGENT);
        ResponseDTO<FileDownloadVO> downloadFileResult = fileService.getDownloadFile(fileKey, userAgent);
        if (!downloadFileResult.getOk()) {
            SmartResponseUtil.write(response, downloadFileResult);
            return;
        }
        // 下载文件信息
        FileDownloadVO fileDownloadVO = downloadFileResult.getData();
        // 设置下载消息头
        SmartResponseUtil.setDownloadFileHeader(response, fileDownloadVO.getMetadata().getFileName(), fileDownloadVO.getMetadata().getFileSize());
        // 下载
        response.getOutputStream().write(fileDownloadVO.getData());
    }

}
