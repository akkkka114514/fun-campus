package com.akkkka.module.support.reload.core.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * t_reload_result 表 实体类
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
public class SmartReloadResult {

    /**
     * 项名称
     */
    @Schema(description = "项名称")
    private String tag;

    /**
     * 参数
     */
    @Schema(description = "参数")
    private String args;

    /**
     * 标识
     */
    @Schema(description = "标识")
    private String identification;

    /**
     * 处理结果
     */
    @Schema(description = "处理结果")
    private boolean result;

    /**
     * 异常说明
     */
    @Schema(description = "异常说明")
    private String exception;


}
