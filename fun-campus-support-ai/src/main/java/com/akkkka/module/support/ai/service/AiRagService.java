package com.akkkka.module.support.ai.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.akkkka.module.support.ai.config.AiProperties;
import com.akkkka.module.support.ai.domain.entity.AiKnowledgeEntity;
import com.akkkka.module.support.ai.manager.AiKnowledgeManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI RAG 检索服务
 * <p>
 * 负责平台规则 FAQ 的向量检索：
 * 1. FAQ 懒加载 + 进程内缓存（TTL 5 分钟），向量懒计算并回写 DB 缓存；
 * 2. 查询向量化后手写余弦相似度取 topK（FAQ 量级小，不引入向量库）；
 * 3. 向量链路不可用（未配 key / 调用失败）时，降级为标题字符 n-gram 匹配，
 *    仍不可命中则返回空，由对话层不带 RAG 上下文正常回答。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRagService {

    /**
     * FAQ 向量缓存有效期（毫秒）
     */
    private static final long CACHE_TTL_MILLIS = 5 * 60 * 1000L;

    /**
     * 降级匹配（标题 n-gram）命中阈值
     */
    private static final double FALLBACK_SCORE_THRESHOLD = 0.1;

    private final AiProperties aiProperties;
    private final AiKnowledgeManager aiKnowledgeManager;
    private final OpenAiEmbeddingModel aiEmbeddingModel;
    private final ObjectMapper objectMapper;

    /**
     * FAQ 向量缓存（懒加载 + TTL 重建）
     */
    private volatile List<FaqVector> faqVectors;

    /**
     * 缓存加载时间
     */
    private volatile long cacheLoadedAt;

    /**
     * 检索与用户问题最相关的知识条目（最多 topK 条）
     */
    public List<AiKnowledgeEntity> retrieve(String query) {
        List<FaqVector> vectors = getFaqVectors();
        if (vectors.isEmpty()) {
            return List.of();
        }
        int topK = aiProperties.getRag().getTopK();

        // 1. 向量检索
        float[] queryVector = embedQuery(query);
        if (queryVector != null) {
            return vectors.stream()
                    .filter(faqVector -> faqVector.vector() != null)
                    .map(faqVector -> Map.entry(faqVector.knowledge(), cosine(queryVector, faqVector.vector())))
                    .filter(entry -> entry.getValue() >= aiProperties.getRag().getScoreThreshold())
                    .sorted(Map.Entry.<AiKnowledgeEntity, Double>comparingByValue().reversed())
                    .limit(topK)
                    .map(Map.Entry::getKey)
                    .toList();
        }

        // 2. 向量不可用，降级为标题 n-gram 匹配
        return keywordFallback(query, vectors, topK);
    }

    /**
     * 获取 FAQ 向量缓存（懒加载 + TTL + 双检锁）
     */
    private List<FaqVector> getFaqVectors() {
        List<FaqVector> cache = this.faqVectors;
        if (cache != null && System.currentTimeMillis() - cacheLoadedAt < CACHE_TTL_MILLIS) {
            return cache;
        }
        synchronized (this) {
            if (this.faqVectors != null && System.currentTimeMillis() - cacheLoadedAt < CACHE_TTL_MILLIS) {
                return this.faqVectors;
            }
            List<FaqVector> fresh = loadFaqVectors();
            this.faqVectors = fresh;
            this.cacheLoadedAt = System.currentTimeMillis();
            log.info("AI FAQ 向量缓存已加载，共 {} 条", fresh.size());
            return fresh;
        }
    }

    /**
     * 从数据库加载启用中的 FAQ，缺失或模型变更的向量重新计算并回写
     */
    private List<FaqVector> loadFaqVectors() {
        List<AiKnowledgeEntity> knowledgeList = aiKnowledgeManager.list(aiKnowledgeManager.qwByEnabled());
        String currentModel = aiProperties.getEmbedding().getModel();
        boolean embeddingAvailable = embeddingConfigured();
        List<FaqVector> result = new ArrayList<>(knowledgeList.size());
        for (AiKnowledgeEntity knowledge : knowledgeList) {
            float[] vector = null;
            boolean modelUpToDate = currentModel.equals(knowledge.getEmbeddingModel());
            if (modelUpToDate) {
                vector = parseVector(knowledge.getEmbedding());
            }
            if (vector == null && embeddingAvailable) {
                vector = embedAndCache(knowledge, currentModel);
            }
            result.add(new FaqVector(knowledge, vector));
        }
        return result;
    }

    /**
     * 对 FAQ 条目向量化并回写 DB 缓存
     */
    private float[] embedAndCache(AiKnowledgeEntity knowledge, String model) {
        try {
            float[] vector = aiEmbeddingModel.embed(knowledge.getTitle() + "\n" + knowledge.getContent());
            AiKnowledgeEntity update = new AiKnowledgeEntity();
            update.setId(knowledge.getId());
            update.setEmbedding(objectMapper.writeValueAsString(vector));
            update.setEmbeddingModel(model);
            aiKnowledgeManager.updateById(update);
            return vector;
        } catch (Exception e) {
            log.warn("AI FAQ 向量化失败（id={}）：{}", knowledge.getId(), e.getMessage());
            return null;
        }
    }

    /**
     * 查询文本向量化；不可用或失败返回 null（触发降级）
     */
    private float[] embedQuery(String query) {
        if (!embeddingConfigured()) {
            return null;
        }
        try {
            return aiEmbeddingModel.embed(query);
        } catch (Exception e) {
            log.warn("AI 查询向量化失败，降级为标题匹配：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 向量模型是否已配置可用
     */
    private boolean embeddingConfigured() {
        return StringUtils.hasText(aiProperties.getEmbedding().getApiKey());
    }

    /**
     * 解析 DB 中的向量 JSON 缓存
     */
    private float[] parseVector(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, float[].class);
        } catch (Exception e) {
            log.warn("AI FAQ 向量缓存解析失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 余弦相似度
     */
    private double cosine(float[] a, float[] b) {
        if (a.length == 0 || a.length != b.length) {
            return 0;
        }
        double dot = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) {
            return 0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 降级匹配：query 与标题的包含判断 + 字符 2-gram Jaccard 相似度
     */
    private List<AiKnowledgeEntity> keywordFallback(String query, List<FaqVector> vectors, int topK) {
        return vectors.stream()
                .map(faqVector -> Map.entry(faqVector.knowledge(), titleScore(query, faqVector.knowledge().getTitle())))
                .filter(entry -> entry.getValue() >= FALLBACK_SCORE_THRESHOLD)
                .sorted(Map.Entry.<AiKnowledgeEntity, Double>comparingByValue().reversed())
                .limit(topK)
                .map(Map.Entry::getKey)
                .toList();
    }

    private double titleScore(String query, String title) {
        if (title.contains(query) || query.contains(title)) {
            return 1;
        }
        return bigramJaccard(query, title);
    }

    private double bigramJaccard(String a, String b) {
        Set<String> gramsA = bigrams(a);
        Set<String> gramsB = bigrams(b);
        if (gramsA.isEmpty() || gramsB.isEmpty()) {
            return 0;
        }
        long intersection = gramsA.stream().filter(gramsB::contains).count();
        long union = gramsA.size() + gramsB.size() - intersection;
        return union == 0 ? 0 : (double) intersection / union;
    }

    private Set<String> bigrams(String text) {
        Set<String> grams = new HashSet<>();
        for (int i = 0; i + 1 < text.length(); i++) {
            grams.add(text.substring(i, i + 2));
        }
        return grams;
    }

    /**
     * FAQ 条目及其向量（vector 为 null 表示该条目暂无向量）
     */
    private record FaqVector(AiKnowledgeEntity knowledge, float[] vector) {
    }
}