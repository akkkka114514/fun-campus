package com.akkkka.module.support.ai.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.module.support.ai.config.AiProperties;
import com.akkkka.module.support.ai.constant.AiMessageRoleEnum;
import com.akkkka.module.support.ai.domain.entity.AiConversationEntity;
import com.akkkka.module.support.ai.domain.entity.AiKnowledgeEntity;
import com.akkkka.module.support.ai.domain.entity.AiMessageEntity;
import com.akkkka.module.support.ai.domain.form.AiChatForm;
import com.akkkka.module.support.ai.domain.vo.AiConversationVO;
import com.akkkka.module.support.ai.domain.vo.AiMessageVO;
import com.akkkka.module.support.ai.domain.vo.AiStreamEventVO;
import com.akkkka.module.support.ai.manager.AiConversationManager;
import com.akkkka.module.support.ai.manager.AiMessageManager;
import com.akkkka.module.support.ai.tools.ActivityAgentTools;
import com.akkkka.module.support.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * AI 对话核心服务
 * <p>
 * 一次提问的完整链路：频控校验 -> 会话定位/创建 -> 历史消息组装 -> RAG 知识检索
 * -> 工具调用流式生成 -> 事件流（meta/delta/done/error）输出 -> 消息落库。
 * 模型未配置或调用异常时优雅降级，保证接口始终返回合法事件流。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class AiChatService {

    /**
     * 频控 Redis key 前缀（按天 + 用户维度）
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "ai:rate:daily:";

    /**
     * 会话标题最大长度（首条提问截断）
     */
    private static final int TITLE_MAX_LENGTH = 15;

    /**
     * AI 未配置/关闭时的降级回复
     */
    private static final String DEGRADED_REPLY = "AI 小助手暂时不可用，请稍后再试～";

    /**
     * 流式过程异常时的兜底回复
     */
    private static final String ERROR_REPLY = "抱歉，本次回答出现异常中断，请稍后再试～";

    /**
     * 流式闲置超时（秒）：相邻两个 token 间隔超过该值视为模型卡死
     */
    private static final long STREAM_IDLE_TIMEOUT_SECONDS = 40;

    /**
     * 系统提示词：角色设定与回答规则
     */
    private static final String SYSTEM_PROMPT = """
            你是「Fun Campus 校园活动平台」学生端的 AI 助手，负责帮助学生解答平台使用问题（报名、签到签退、支付退款、实践学分等），以及查找可报名活动、查询个人报名情况。

            回答要求：
            1. 使用简体中文，语气友好、表达简洁，避免冗长；
            2. 涉及平台规则与流程时，必须依据下文提供的「平台知识」，不得自行编造；
            3. 需要活动数据（有哪些活动可报名、我报了哪些活动等）时，调用工具查询，不得编造活动信息；
            4. 如知识中没有相关内容，如实说明并给出合理建议。""";

    /**
     * 平台知识段落模板头
     */
    private static final String KNOWLEDGE_HEADER = "\n\n以下是与学生问题可能相关的「平台知识」，回答平台规则类问题时严格依据这些内容：\n";

    private final AiProperties aiProperties;
    private final AiConversationManager aiConversationManager;
    private final AiMessageManager aiMessageManager;
    private final AiRagService aiRagService;
    private final ActivityAgentTools activityAgentTools;
    private final ChatClient aiChatClient;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 流式对话：返回 SSE 事件流（每个元素为一条 JSON 事件）
     */
    public Flux<String> chat(Long userId, AiChatForm form) {
        // 1. 频控校验
        checkRateLimit(userId);

        // 2. 会话定位或创建
        AiConversationEntity conversation = getOrCreateConversation(userId, form.getConversationId(), form.getContent());

        // 3. 历史消息（在保存本轮提问之前取，避免包含本轮）
        List<Message> history = loadHistory(conversation.getId());

        // 4. 保存用户提问
        saveUserMessage(conversation, userId, form.getContent());

        // 5. 元信息事件
        Flux<String> head = Flux.just(toJson(AiStreamEventVO.meta(conversation.getId())));

        // 6. 降级判断：开关关闭或对话模型未配置
        if (!aiAvailable()) {
            log.info("AI 对话降级：enabled={}, chat apiKey configured={}",
                    aiProperties.getEnabled(), StringUtils.hasText(aiProperties.getChat().getApiKey()));
            return degradedStream(conversation, userId, head);
        }

        // 7. RAG 知识检索（内部已兜底：失败时返回空列表）
        List<AiKnowledgeEntity> knowledgeList = aiRagService.retrieve(form.getContent());
        String systemPrompt = buildSystemPrompt(knowledgeList);
        log.info("AI 对话开始：userId={}, conversationId={}, 命中知识 {} 条, 历史 {} 条",
                userId, conversation.getId(), knowledgeList.size(), history.size());

        // 8. 流式生成：累积增量用于落库
        StringBuilder buffer = new StringBuilder();
        Flux<String> body = Flux.defer(() -> aiChatClient.prompt()
                        .system(systemPrompt)
                        .messages(history)
                        .user(form.getContent())
                        .tools(activityAgentTools)
                        .toolContext(Map.of(ActivityAgentTools.CONTEXT_USER_ID, userId))
                        .stream()
                        .content())
                .map(token -> {
                    buffer.append(token);
                    return toJson(AiStreamEventVO.delta(token));
                })
                .timeout(Duration.ofSeconds(STREAM_IDLE_TIMEOUT_SECONDS));

        // 9. 收尾：保存助手回复并发送 done 事件
        Flux<String> tail = Flux.defer(() -> {
            String reply = buffer.isEmpty() ? ERROR_REPLY : buffer.toString();
            saveAssistantMessage(conversation, userId, reply);
            return Flux.just(toJson(AiStreamEventVO.done()));
        });

        return head.concatWith(body).concatWith(tail)
                .onErrorResume(e -> {
                    log.warn("AI 流式对话异常：userId={}, conversationId={}, error={}",
                            userId, conversation.getId(), e.getMessage());
                    String partial = buffer.toString();
                    saveAssistantMessage(conversation, userId, partial.isEmpty() ? ERROR_REPLY : partial);
                    return Flux.just(toJson(AiStreamEventVO.error(ERROR_REPLY)), toJson(AiStreamEventVO.done()));
                });
    }

    /**
     * 会话列表（按最后消息时间倒序）
     */
    public List<AiConversationVO> listConversations(Long userId) {
        return aiConversationManager.list(aiConversationManager.qwByUserId(userId)).stream()
                .map(this::toConversationVO)
                .toList();
    }

    /**
     * 会话内消息列表（按id正序）
     */
    public List<AiMessageVO> listMessages(Long userId, Long conversationId) {
        requireOwnedConversation(userId, conversationId);
        return aiMessageManager.list(aiMessageManager.qwByConversationId(conversationId)).stream()
                .map(this::toMessageVO)
                .toList();
    }

    /**
     * 删除会话（逻辑删除，幂等）
     */
    public void deleteConversation(Long userId, Long conversationId) {
        AiConversationEntity conversation = aiConversationManager.getOne(
                aiConversationManager.qwByIdAndUserId(conversationId, userId));
        if (conversation == null) {
            return;
        }
        conversation.setDeletedFlag(true);
        aiConversationManager.updateById(conversation);
        log.info("AI 会话已删除：userId={}, conversationId={}", userId, conversationId);
    }

    /**
     * 频控：每用户每日提问次数上限（Redis 按天计数）
     */
    private void checkRateLimit(Long userId) {
        int dailyLimit = aiProperties.getRateLimit().getDailyLimit();
        if (dailyLimit <= 0) {
            return;
        }
        String key = RATE_LIMIT_KEY_PREFIX + LocalDate.now() + ":" + userId;
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(key, RedisService.currentDaySecond(), TimeUnit.SECONDS);
        }
        if (count != null && count > dailyLimit) {
            log.warn("AI 提问超过每日限额：userId={}, count={}, limit={}", userId, count, dailyLimit);
            throw new BusinessException(UserErrorCode.SERVICE_BUSY, "今日提问次数已达上限，请明天再来");
        }
    }

    /**
     * 会话定位：携带会话id时校验归属，不存在或越权则新建
     */
    private AiConversationEntity getOrCreateConversation(Long userId, Long conversationId, String question) {
        if (conversationId != null) {
            AiConversationEntity exist = aiConversationManager.getOne(
                    aiConversationManager.qwByIdAndUserId(conversationId, userId));
            if (exist != null) {
                return exist;
            }
            log.info("AI 会话不存在或不属于当前用户，将新建会话：conversationId={}, userId={}", conversationId, userId);
        }
        AiConversationEntity conversation = new AiConversationEntity();
        conversation.setUserId(userId);
        conversation.setTitle(buildTitle(question));
        conversation.setLastMessageTime(LocalDateTime.now());
        conversation.setDeletedFlag(false);
        aiConversationManager.save(conversation);
        log.info("AI 新会话已创建：userId={}, conversationId={}", userId, conversation.getId());
        return conversation;
    }

    /**
     * 会话标题：首条提问截断
     */
    private String buildTitle(String question) {
        String text = question.trim();
        if (text.length() <= TITLE_MAX_LENGTH) {
            return text;
        }
        return text.substring(0, TITLE_MAX_LENGTH) + "…";
    }

    /**
     * 加载最近历史消息（最多 maxHistory 条，转为模型消息，按时间正序）
     */
    private List<Message> loadHistory(Long conversationId) {
        int maxHistory = aiProperties.getMaxHistory();
        if (maxHistory <= 0) {
            return List.of();
        }
        List<AiMessageEntity> entities = aiMessageManager.list(
                aiMessageManager.qwRecentByConversationId(conversationId).last("LIMIT " + maxHistory));
        List<Message> messages = new ArrayList<>(entities.size());
        for (int i = entities.size() - 1; i >= 0; i--) {
            AiMessageEntity entity = entities.get(i);
            if (entity.getRole() == null || !StringUtils.hasText(entity.getContent())) {
                continue;
            }
            messages.add(switch (entity.getRole()) {
                case USER -> new UserMessage(entity.getContent());
                case ASSISTANT -> new AssistantMessage(entity.getContent());
            });
        }
        return messages;
    }

    /**
     * 组装系统提示词：角色设定 + RAG 知识段落（受 maxContextLength 限制）
     */
    private String buildSystemPrompt(List<AiKnowledgeEntity> knowledgeList) {
        if (knowledgeList.isEmpty()) {
            return SYSTEM_PROMPT;
        }
        StringBuilder prompt = new StringBuilder(SYSTEM_PROMPT).append(KNOWLEDGE_HEADER);
        int maxContextLength = aiProperties.getRag().getMaxContextLength();
        int used = 0;
        for (AiKnowledgeEntity knowledge : knowledgeList) {
            String section = "【" + knowledge.getTitle() + "】" + knowledge.getContent() + "\n";
            if (maxContextLength > 0 && used + section.length() > maxContextLength) {
                break;
            }
            prompt.append(section);
            used += section.length();
        }
        return prompt.toString();
    }

    /**
     * 模型可用性：总开关开启 且 对话模型 api-key 已配置
     */
    private boolean aiAvailable() {
        return Boolean.TRUE.equals(aiProperties.getEnabled())
                && StringUtils.hasText(aiProperties.getChat().getApiKey());
    }

    /**
     * 降级事件流：meta -> delta(固定文案) -> done，并落库
     */
    private Flux<String> degradedStream(AiConversationEntity conversation, Long userId, Flux<String> head) {
        saveAssistantMessage(conversation, userId, DEGRADED_REPLY);
        return head.concatWith(Flux.just(
                toJson(AiStreamEventVO.delta(DEGRADED_REPLY)),
                toJson(AiStreamEventVO.done())));
    }

    /**
     * 保存用户提问并刷新会话最后消息时间
     */
    private void saveUserMessage(AiConversationEntity conversation, Long userId, String content) {
        AiMessageEntity message = new AiMessageEntity();
        message.setConversationId(conversation.getId());
        message.setUserId(userId);
        message.setRole(AiMessageRoleEnum.USER);
        message.setContent(content);
        message.setDeletedFlag(false);
        aiMessageManager.save(message);
        touchConversation(conversation);
    }

    /**
     * 保存助手回复（流式收尾在异步线程执行，失败仅记录日志不抛出）
     */
    private void saveAssistantMessage(AiConversationEntity conversation, Long userId, String content) {
        try {
            AiMessageEntity message = new AiMessageEntity();
            message.setConversationId(conversation.getId());
            message.setUserId(userId);
            message.setRole(AiMessageRoleEnum.ASSISTANT);
            message.setContent(content);
            message.setDeletedFlag(false);
            aiMessageManager.save(message);
            touchConversation(conversation);
        } catch (Exception e) {
            log.error("AI 助手回复保存失败：conversationId={}", conversation.getId(), e);
        }
    }

    /**
     * 刷新会话最后消息时间（会话列表排序依据）
     */
    private void touchConversation(AiConversationEntity conversation) {
        conversation.setLastMessageTime(LocalDateTime.now());
        aiConversationManager.updateById(conversation);
    }

    /**
     * 校验会话归属（防越权查看他人会话）
     */
    private void requireOwnedConversation(Long userId, Long conversationId) {
        AiConversationEntity conversation = aiConversationManager.getOne(
                aiConversationManager.qwByIdAndUserId(conversationId, userId));
        if (conversation == null) {
            throw new BusinessException(UserErrorCode.DATA_NOT_EXIST, "会话不存在或无权访问");
        }
    }

    private AiConversationVO toConversationVO(AiConversationEntity entity) {
        AiConversationVO vo = new AiConversationVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setLastMessageTime(entity.getLastMessageTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    private AiMessageVO toMessageVO(AiMessageEntity entity) {
        AiMessageVO vo = new AiMessageVO();
        vo.setId(entity.getId());
        vo.setRole(entity.getRole() == null ? null : entity.getRole().getCode());
        vo.setContent(entity.getContent());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 事件序列化（delta 文本为模型增量，不含裸换行，作为 SSE 单行 data 安全）
     */
    private String toJson(AiStreamEventVO event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("AI 流式事件序列化失败", e);
            return "";
        }
    }
}