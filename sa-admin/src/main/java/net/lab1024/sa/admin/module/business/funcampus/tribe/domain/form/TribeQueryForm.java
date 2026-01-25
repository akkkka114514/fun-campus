package net.lab1024.sa.admin.module.business.funcampus.tribe.domain.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import net.lab1024.sa.base.common.domain.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部落 分页查询表单
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class TribeQueryForm extends PageParam {
    @Size(max = 255, message = "关键词长度不能超过255个字符")
    @NotBlank(message = "关键词不能为空")
    private String keyword;
    
    @Min(value = 0, message = "学校ID必须大于0")
    private Long schoolId;

}