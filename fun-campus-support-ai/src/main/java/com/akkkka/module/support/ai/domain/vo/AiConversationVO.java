package com.akkkka.module.support.ai.domain.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI 会话 VO
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
public class AiConversationVO {

    @Schema(description = "会话id")
    private Long id;

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
