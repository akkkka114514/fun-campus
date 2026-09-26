package com.akkkka.module.support.ai.domain.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI 消息 VO
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
public class AiMessageVO {

    @Schema(description = "消息id")
    private Long id;

    @Schema(description = "角色：1-用户 2-助手")
    private Integer role;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
