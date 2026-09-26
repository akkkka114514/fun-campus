package com.akkkka.module.support.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI 流式事件 VO（SSE 下发，序列化为 JSON 后作为 data 推送）
 * <p>
 * 事件类型约定：
 * meta - 流开始，下发 conversationId；
 * delta - 增量文本，前端拼接渲染；
 * done - 正常结束；
 * error - 异常兜底提示。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
public class AiStreamEventVO {

    @Schema(description = "事件类型：meta-元信息 delta-增量内容 done-结束 error-错误")
    private String type;

    @Schema(description = "内容（delta 为增量文本，error 为兜底提示）")
    private String content;

    @Schema(description = "会话id（meta 事件下发，新会话时前端据此绑定后续消息）")
    private Long conversationId;

    public static AiStreamEventVO meta(Long conversationId) {
        AiStreamEventVO vo = new AiStreamEventVO();
        vo.setType("meta");
        vo.setConversationId(conversationId);
        return vo;
    }

    public static AiStreamEventVO delta(String content) {
        AiStreamEventVO vo = new AiStreamEventVO();
        vo.setType("delta");
        vo.setContent(content);
        return vo;
    }

    public static AiStreamEventVO done() {
        AiStreamEventVO vo = new AiStreamEventVO();
        vo.setType("done");
        return vo;
    }

    public static AiStreamEventVO error(String content) {
        AiStreamEventVO vo = new AiStreamEventVO();
        vo.setType("error");
        vo.setContent(content);
        return vo;
    }

}
