package com.akkkka.funcampusnotice.mapper;

import java.util.List;
import com.akkkka.funcampusnotice.domain.MessageGroup;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface MessageGroupMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MessageGroup selectMessageGroupByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param messageGroup 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MessageGroup> selectMessageGroupList(MessageGroup messageGroup);

    /**
     * 新增【请填写功能名称】
     * 
     * @param messageGroup 【请填写功能名称】
     * @return 结果
     */
    public int insertMessageGroup(MessageGroup messageGroup);

    /**
     * 修改【请填写功能名称】
     * 
     * @param messageGroup 【请填写功能名称】
     * @return 结果
     */
    public int updateMessageGroup(MessageGroup messageGroup);

    /**
     * 删除【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteMessageGroupByUid(String uid);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMessageGroupByUids(String[] uids);
}
