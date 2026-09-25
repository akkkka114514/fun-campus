package com.akkkka.module.support.datatracer.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.akkkka.common.domain.PageParam;
import com.akkkka.common.swagger.SchemaEnum;
import com.akkkka.module.support.datatracer.constant.DataTracerTypeEnum;

/**
 * 查询表单
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
public class DataTracerQueryForm extends PageParam {

    @SchemaEnum(DataTracerTypeEnum.class)
    private Integer type;

    @Schema(description = "业务id")
    @NotNull(message = "业务id不能为空")
    private Long dataId;

    @Schema(description = "关键字")
    private String keywords;
}
