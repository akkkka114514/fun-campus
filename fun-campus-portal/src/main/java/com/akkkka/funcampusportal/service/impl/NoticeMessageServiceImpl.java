package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import com.akkkka.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.NoticeMessageMapper;
import com.akkkka.funcampusnotice.domain.NoticeMessage;
import com.akkkka.funcampusnotice.service.INoticeMessageService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class NoticeMessageServiceImpl implements INoticeMessageService 
{
    @Autowired
    private NoticeMessageMapper noticeMessageMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public NoticeMessage selectNoticeMessageByUid(String uid)
    {
        return noticeMessageMapper.selectNoticeMessageByUid(uid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param noticeMessage 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<NoticeMessage> selectNoticeMessageList(NoticeMessage noticeMessage)
    {
        return noticeMessageMapper.selectNoticeMessageList(noticeMessage);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param noticeMessage 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertNoticeMessage(NoticeMessage noticeMessage)
    {
        noticeMessage.setCreateTime(DateUtils.getNowDate());
        return noticeMessageMapper.insertNoticeMessage(noticeMessage);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param noticeMessage 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateNoticeMessage(NoticeMessage noticeMessage)
    {
        noticeMessage.setUpdateTime(DateUtils.getNowDate());
        return noticeMessageMapper.updateNoticeMessage(noticeMessage);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteNoticeMessageByUids(String[] uids)
    {
        return noticeMessageMapper.deleteNoticeMessageByUids(uids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteNoticeMessageByUid(String uid)
    {
        return noticeMessageMapper.deleteNoticeMessageByUid(uid);
    }
}
