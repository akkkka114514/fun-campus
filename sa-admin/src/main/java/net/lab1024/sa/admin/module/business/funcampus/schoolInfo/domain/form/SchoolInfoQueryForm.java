package net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学校信息表 分页查询表单
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SchoolInfoQueryForm extends PageParam {

    @Schema(description = "关键字")
    private String keywords;

    @Schema(description = "学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)")
    private Integer type;

    @Schema(description = "学校编码")
    private String code;

}
