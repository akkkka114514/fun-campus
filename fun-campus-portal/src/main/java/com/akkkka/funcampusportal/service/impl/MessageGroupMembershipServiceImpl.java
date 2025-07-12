package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.MessageGroupMembershipMapper;
import com.akkkka.funcampusnotice.domain.MessageGroupMembership;
import com.akkkka.funcampusnotice.service.IMessageGroupMembershipService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class MessageGroupMembershipServiceImpl implements IMessageGroupMembershipService 
{
    @Autowired
    private MessageGroupMembershipMapper messageGroupMembershipMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public MessageGroupMembership selectMessageGroupMembershipByUid(String uid)
    {
        return messageGroupMembershipMapper.selectMessageGroupMembershipByUid(uid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param messageGroupMembership 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MessageGroupMembership> selectMessageGroupMembershipList(MessageGroupMembership messageGroupMembership)
    {
        return messageGroupMembershipMapper.selectMessageGroupMembershipList(messageGroupMembership);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param messageGroupMembership 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMessageGroupMembership(MessageGroupMembership messageGroupMembership)
    {
        return messageGroupMembershipMapper.insertMessageGroupMembership(messageGroupMembership);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param messageGroupMembership 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMessageGroupMembership(MessageGroupMembership messageGroupMembership)
    {
        return messageGroupMembershipMapper.updateMessageGroupMembership(messageGroupMembership);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteMessageGroupMembershipByUids(String[] uids)
    {
        return messageGroupMembershipMapper.deleteMessageGroupMembershipByUids(uids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteMessageGroupMembershipByUid(String uid)
    {
        return messageGroupMembershipMapper.deleteMessageGroupMembershipByUid(uid);
    }
}
