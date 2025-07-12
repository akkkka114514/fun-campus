package com.akkkka.funcampusnotice.mapper;

import java.util.List;
import com.akkkka.funcampusnotice.domain.NoticeMessage;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface NoticeMessageMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public NoticeMessage selectNoticeMessageByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param noticeMessage 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<NoticeMessage> selectNoticeMessageList(NoticeMessage noticeMessage);

    /**
     * 新增【请填写功能名称】
     * 
     * @param noticeMessage 【请填写功能名称】
     * @return 结果
     */
    public int insertNoticeMessage(NoticeMessage noticeMessage);

    /**
     * 修改【请填写功能名称】
     * 
     * @param noticeMessage 【请填写功能名称】
     * @return 结果
     */
    public int updateNoticeMessage(NoticeMessage noticeMessage);

    /**
     * 删除【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteNoticeMessageByUid(String uid);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNoticeMessageByUids(String[] uids);
}
