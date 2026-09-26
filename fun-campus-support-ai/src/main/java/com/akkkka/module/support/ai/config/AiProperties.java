package com.akkkka.module.support.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 助手配置
 * 与配置文件参数对应
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@ConfigurationProperties(prefix = AiProperties.CONFIG_PREFIX)
@Data
public class AiProperties {

    public static final String CONFIG_PREFIX = "fun-campus.ai";

    /**
     * 总开关 默认开启 关闭后提问接口统一返回降级提示
     */
    private Boolean enabled = true;

    /**
     * 单轮对话携带的历史消息条数上限 默认10
     */
    private Integer maxHistory = 10;

    /**
     * 对话模型（DeepSeek）
     */
    private Chat chat = new Chat();

    /**
     * 向量模型（阿里百炼）
     */
    private Embedding embedding = new Embedding();

    /**
     * RAG 检索参数
     */
    private Rag rag = new Rag();

    /**
     * 频控参数
     */
    private RateLimit rateLimit = new RateLimit();

    @Data
    public static class Chat {

        /**
         * OpenAI 兼容 base-url
         */
        private String baseUrl = "https://api.deepseek.com";

        /**
         * api-key（环境变量 FUN_CAMPUS_AI_CHAT_API_KEY 注入）
         */
        private String apiKey;

        /**
         * 模型名
         */
        private String model = "deepseek-chat";

        /**
         * 采样温度
         */
        private Double temperature = 0.7;

        /**
         * 单次回复最大 token
         */
        private Integer maxTokens = 1024;
    }

    @Data
    public static class Embedding {

        /**
         * OpenAI 兼容 base-url
         */
        private String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode";

        /**
         * api-key（环境变量 FUN_CAMPUS_AI_EMBEDDING_API_KEY 注入）
         */
        private String apiKey;

        /**
         * 模型名
         */
        private String model = "text-embedding-v3";
    }

    @Data
    public static class Rag {

        /**
         * 检索返回条数
         */
        private Integer topK = 3;

        /**
         * 余弦相似度阈值 低于视为未命中
         */
        private Double scoreThreshold = 0.5;

        /**
         * 注入提示词的知识文本最大字符数
         */
        private Integer maxContextLength = 2000;
    }

    @Data
    public static class RateLimit {

        /**
         * 每用户每日提问上限
         */
        private Integer dailyLimit = 100;
    }
}
