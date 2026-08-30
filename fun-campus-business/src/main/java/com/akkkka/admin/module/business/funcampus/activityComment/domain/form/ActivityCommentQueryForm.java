package com.akkkka.admin.module.business.funcampus.activityComment.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论分页查询 表单
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityCommentQueryForm extends PageParam {

    @Schema(description = "活动id")
    @NotNull(message = "活动id不能为空")
    private Long activityId;

}
