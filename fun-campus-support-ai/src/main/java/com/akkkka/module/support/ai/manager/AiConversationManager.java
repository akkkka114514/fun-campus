package com.akkkka.module.support.ai.manager;

import com.akkkka.module.support.ai.dao.AiConversationDao;
import com.akkkka.module.support.ai.domain.entity.AiConversationEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * AI 会话 Manager
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Service
public class AiConversationManager extends ServiceImpl<AiConversationDao, AiConversationEntity> {

    /**
     * 查询用户的会话列表（按最后消息时间倒序）
     */
    public LambdaQueryWrapper<AiConversationEntity> qwByUserId(Long userId) {
        return Wrappers.lambdaQuery(AiConversationEntity.class)
                .eq(AiConversationEntity::getUserId, userId)
                .eq(AiConversationEntity::getDeletedFlag, false)
                .orderByDesc(AiConversationEntity::getLastMessageTime);
    }

    /**
     * 按会话id和用户id定位（防越权访问他人会话）
     */
    public LambdaQueryWrapper<AiConversationEntity> qwByIdAndUserId(Long id, Long userId) {
        return Wrappers.lambdaQuery(AiConversationEntity.class)
                .eq(AiConversationEntity::getId, id)
                .eq(AiConversationEntity::getUserId, userId)
                .eq(AiConversationEntity::getDeletedFlag, false);
    }
}
