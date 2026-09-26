package com.akkkka.admin.module.business.funcampus.creditApplication.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * 学分认定申请 表单（门户提交）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class CreditApplicationApplyForm {

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题最多255字符")
    private String title;

    @Schema(description = "学期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学期不能为空")
    @Size(max = 64, message = "学期最多64字符")
    private String semester;

    @Schema(description = "申请内容")
    @Size(max = 2000, message = "申请内容最多2000字符")
    private String content;

    @Schema(description = "证明材料图片fileKey列表（最多9张）")
    @Size(max = 9, message = "证明材料最多9张")
    private List<String> imageList;

    @Schema(description = "审核人id（backend_user，can_review=1）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核人不能为空")
    private Long reviewUserId;

}
