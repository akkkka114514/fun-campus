package com.akkkka.module.support.file.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.InputStream;

/**
 * 文件下载
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
public class FileDownloadVO {

    /**
     * 文件数据流（调用方负责关闭，避免大文件整体载入内存）
     */
    @Schema(description = "文件数据流")
    private InputStream data;

    /**
     * 文件元数据
     */
    @Schema(description = "文件元数据")
    private FileMetadataVO metadata;


}
