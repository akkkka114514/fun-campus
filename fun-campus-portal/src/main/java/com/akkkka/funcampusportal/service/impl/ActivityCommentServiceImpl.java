package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import com.akkkka.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.ActivityCommentMapper;
import com.akkkka.funcampusnotice.domain.ActivityComment;
import com.akkkka.funcampusnotice.service.IActivityCommentService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class ActivityCommentServiceImpl implements IActivityCommentService 
{
    @Autowired
    private ActivityCommentMapper activityCommentMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ActivityComment selectActivityCommentByUid(String uid)
    {
        return activityCommentMapper.selectActivityCommentByUid(uid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activityComment 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ActivityComment> selectActivityCommentList(ActivityComment activityComment)
    {
        return activityCommentMapper.selectActivityCommentList(activityComment);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param activityComment 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertActivityComment(ActivityComment activityComment)
    {
        activityComment.setCreateTime(DateUtils.getNowDate());
        return activityCommentMapper.insertActivityComment(activityComment);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param activityComment 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateActivityComment(ActivityComment activityComment)
    {
        activityComment.setUpdateTime(DateUtils.getNowDate());
        return activityCommentMapper.updateActivityComment(activityComment);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityCommentByUids(String[] uids)
    {
        return activityCommentMapper.deleteActivityCommentByUids(uids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityCommentByUid(String uid)
    {
        return activityCommentMapper.deleteActivityCommentByUid(uid);
    }
}
