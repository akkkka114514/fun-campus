package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import com.akkkka.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.MessageGroupMapper;
import com.akkkka.funcampusnotice.domain.MessageGroup;
import com.akkkka.funcampusnotice.service.IMessageGroupService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class MessageGroupServiceImpl implements IMessageGroupService 
{
    @Autowired
    private MessageGroupMapper messageGroupMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public MessageGroup selectMessageGroupByUid(String uid)
    {
        return messageGroupMapper.selectMessageGroupByUid(uid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param messageGroup 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MessageGroup> selectMessageGroupList(MessageGroup messageGroup)
    {
        return messageGroupMapper.selectMessageGroupList(messageGroup);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param messageGroup 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMessageGroup(MessageGroup messageGroup)
    {
        messageGroup.setCreateTime(DateUtils.getNowDate());
        return messageGroupMapper.insertMessageGroup(messageGroup);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param messageGroup 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMessageGroup(MessageGroup messageGroup)
    {
        messageGroup.setUpdateTime(DateUtils.getNowDate());
        return messageGroupMapper.updateMessageGroup(messageGroup);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteMessageGroupByUids(String[] uids)
    {
        return messageGroupMapper.deleteMessageGroupByUids(uids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteMessageGroupByUid(String uid)
    {
        return messageGroupMapper.deleteMessageGroupByUid(uid);
    }
}
