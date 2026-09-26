package com.akkkka.module.support.ai.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI 助手提问 表单
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
public class AiChatForm {

    @Schema(description = "会话id，为空表示新建会话")
    private Long conversationId;

    @Schema(description = "用户提问内容")
    @NotBlank(message = "提问内容不能为空")
    @Size(max = 500, message = "提问内容最多500字")
    private String content;

}
