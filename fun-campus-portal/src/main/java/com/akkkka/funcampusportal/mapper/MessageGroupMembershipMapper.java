package com.akkkka.funcampusnotice.mapper;

import java.util.List;
import com.akkkka.funcampusnotice.domain.MessageGroupMembership;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface MessageGroupMembershipMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MessageGroupMembership selectMessageGroupMembershipByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param messageGroupMembership 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MessageGroupMembership> selectMessageGroupMembershipList(MessageGroupMembership messageGroupMembership);

    /**
     * 新增【请填写功能名称】
     * 
     * @param messageGroupMembership 【请填写功能名称】
     * @return 结果
     */
    public int insertMessageGroupMembership(MessageGroupMembership messageGroupMembership);

    /**
     * 修改【请填写功能名称】
     * 
     * @param messageGroupMembership 【请填写功能名称】
     * @return 结果
     */
    public int updateMessageGroupMembership(MessageGroupMembership messageGroupMembership);

    /**
     * 删除【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteMessageGroupMembershipByUid(String uid);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMessageGroupMembershipByUids(String[] uids);
}
