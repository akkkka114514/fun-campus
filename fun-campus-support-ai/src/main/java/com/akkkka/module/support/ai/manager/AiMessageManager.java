package com.akkkka.module.support.ai.manager;

import com.akkkka.module.support.ai.dao.AiMessageDao;
import com.akkkka.module.support.ai.domain.entity.AiMessageEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * AI 消息 Manager
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Service
public class AiMessageManager extends ServiceImpl<AiMessageDao, AiMessageEntity> {

    /**
     * 查询会话内消息（按id正序，id 自增保证插入顺序）
     */
    public LambdaQueryWrapper<AiMessageEntity> qwByConversationId(Long conversationId) {
        return Wrappers.lambdaQuery(AiMessageEntity.class)
                .eq(AiMessageEntity::getConversationId, conversationId)
                .eq(AiMessageEntity::getDeletedFlag, false)
                .orderByAsc(AiMessageEntity::getId);
    }

    /**
     * 查询会话内最近消息（按id倒序，配合 LIMIT n 取最近 n 条历史上下文）
     */
    public LambdaQueryWrapper<AiMessageEntity> qwRecentByConversationId(Long conversationId) {
        return Wrappers.lambdaQuery(AiMessageEntity.class)
                .eq(AiMessageEntity::getConversationId, conversationId)
                .eq(AiMessageEntity::getDeletedFlag, false)
                .orderByDesc(AiMessageEntity::getId);
    }
}