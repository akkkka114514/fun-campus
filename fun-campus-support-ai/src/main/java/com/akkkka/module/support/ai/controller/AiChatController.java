package com.akkkka.module.support.ai.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.ai.domain.form.AiChatForm;
import com.akkkka.module.support.ai.domain.vo.AiConversationVO;
import com.akkkka.module.support.ai.domain.vo.AiMessageVO;
import com.akkkka.module.support.ai.service.AiChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * AI 助手 Controller（门户端）
 * <p>
 * 请求路径包含 portal 段：由 AdminInterceptor 解析为门户登录用户。
 * 会话接口为普通 JSON 响应；对话接口为 SSE 流式响应（text/event-stream），
 * 事件格式见 {@link com.akkkka.module.support.ai.domain.vo.AiStreamEventVO}。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Slf4j
@Tag(name = "AI 助手（学生端）")
@RestController
@RequestMapping("portal")
public class AiChatController {

    @Resource
    private AiChatService aiChatService;

    @Operation(summary = "AI 助手-流式对话（SSE） @author akkkka114514")
    @PostMapping(value = "/ai/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody @Valid AiChatForm chatForm) {
        return aiChatService.chat(getCurrentPortalUserId(), chatForm);
    }

    @Operation(summary = "AI 助手-会话列表 @author akkkka114514")
    @GetMapping("/ai/conversation/list")
    public ResponseDTO<List<AiConversationVO>> conversationList() {
        return ResponseDTO.ok(aiChatService.listConversations(getCurrentPortalUserId()));
    }

    @Operation(summary = "AI 助手-会话内消息列表 @author akkkka114514")
    @GetMapping("/ai/message/list")
    public ResponseDTO<List<AiMessageVO>> messageList(@RequestParam Long conversationId) {
        return ResponseDTO.ok(aiChatService.listMessages(getCurrentPortalUserId(), conversationId));
    }

    @Operation(summary = "AI 助手-删除会话 @author akkkka114514")
    @PostMapping("/ai/conversation/delete")
    public ResponseDTO<String> deleteConversation(@RequestParam Long conversationId) {
        aiChatService.deleteConversation(getCurrentPortalUserId(), conversationId);
        return ResponseDTO.ok();
    }

    /**
     * 获取当前登录的门户用户id（AI 助手仅允许学生用户访问）
     */
    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (requestUser == null || requestUser.getUserType() != UserTypeEnum.PORTAL_USER) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "仅学生用户可使用 AI 助手");
        }
        return requestUser.getUserId();
    }
}