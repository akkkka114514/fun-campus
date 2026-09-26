package com.akkkka.module.support.datatracer.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.akkkka.common.swagger.SchemaEnum;
import com.akkkka.module.support.datatracer.constant.DataTracerTypeEnum;

/**
 * 数据变动表单
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataTracerForm {

    /**
     * 业务id
     */
    @Schema(description = "业务id")
    private Long dataId;

    /**
     * 业务类型
     */
    @SchemaEnum(value = DataTracerTypeEnum.class, desc = "业务类型")
    private DataTracerTypeEnum type;

    /**
     * 操作内容
     */
    @Schema(description = "操作内容")
    private String content;

    /**
     * diff 差异：旧的数据
     */
    @Schema(description = "diff 差异：旧的数据")
    private String diffOld;

    /**
     * 差异：新的数据
     */
    @Schema(description = "差异：新的数据")
    private String diffNew;

    /**
     * 扩展字段
     */
    @Schema(description = "扩展字段")
    private String extraData;

}
