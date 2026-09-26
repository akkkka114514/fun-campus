package com.akkkka.module.support.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI 模型装配
 * <p>
 * 对话模型（DeepSeek）与向量模型（阿里百炼）均走 OpenAI 兼容协议，
 * 由于两者 base-url 不同，分别构建独立的 OpenAiApi 实例（使用库依赖而非
 * starter，无自动配置参与）；api-key 通过环境变量注入，缺失时使用占位值
 * 保证应用可启动，调用链路由服务层前置检查降级。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(AiProperties.class)
public class AiModelConfig {

    /**
     * api-key 缺失时的占位值（仅保证 OpenAiApi 构建成功，不代表可用）
     */
    public static final String PLACEHOLDER_API_KEY = "not-configured";

    private final AiProperties aiProperties;

    /**
     * 对话模型：DeepSeek
     */
    @Bean
    public OpenAiChatModel aiChatModel() {
        AiProperties.Chat chat = aiProperties.getChat();
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(chat.getBaseUrl())
                .apiKey(resolveApiKey(chat.getApiKey(), "FUN_CAMPUS_AI_CHAT_API_KEY"))
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(chat.getModel())
                        .temperature(chat.getTemperature())
                        .maxTokens(chat.getMaxTokens())
                        .build())
                // 最多重试1次：失败快速兜底，避免流式场景长时间等待
                .retryTemplate(RetryTemplate.builder().maxAttempts(2).fixedBackoff(1000).build())
                .build();
    }

    /**
     * 向量模型：阿里百炼 text-embedding-v3
     */
    @Bean
    public OpenAiEmbeddingModel aiEmbeddingModel() {
        AiProperties.Embedding embedding = aiProperties.getEmbedding();
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(embedding.getBaseUrl())
                .apiKey(resolveApiKey(embedding.getApiKey(), "FUN_CAMPUS_AI_EMBEDDING_API_KEY"))
                .build();
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.NONE,
                OpenAiEmbeddingOptions.builder()
                        .model(embedding.getModel())
                        .build());
    }

    /**
     * 对话客户端：供业务服务注入，已绑定对话模型
     */
    @Bean
    public ChatClient aiChatClient(OpenAiChatModel aiChatModel) {
        return ChatClient.builder(aiChatModel).build();
    }

    /**
     * api-key 为空时使用占位值，保证启动不失败；调用链路由服务层前置检查降级
     */
    private String resolveApiKey(String apiKey, String envName) {
        if (!StringUtils.hasText(apiKey)) {
            log.warn("AI api-key 未配置（环境变量 {}），相关能力将在调用时降级", envName);
            return PLACEHOLDER_API_KEY;
        }
        return apiKey;
    }
}