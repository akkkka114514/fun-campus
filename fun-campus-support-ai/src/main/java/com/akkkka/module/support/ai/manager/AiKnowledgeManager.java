package com.akkkka.module.support.ai.manager;

import com.akkkka.module.support.ai.dao.AiKnowledgeDao;
import com.akkkka.module.support.ai.domain.entity.AiKnowledgeEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * AI 知识库 Manager
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Service
public class AiKnowledgeManager extends ServiceImpl<AiKnowledgeDao, AiKnowledgeEntity> {

    /**
     * 知识状态：启用
     */
    public static final int STATUS_ENABLED = 1;

    /**
     * 查询全部启用中的知识条目（供 RAG 懒加载）
     */
    public LambdaQueryWrapper<AiKnowledgeEntity> qwByEnabled() {
        return Wrappers.lambdaQuery(AiKnowledgeEntity.class)
                .eq(AiKnowledgeEntity::getStatus, STATUS_ENABLED)
                .eq(AiKnowledgeEntity::getDeletedFlag, false);
    }
}
