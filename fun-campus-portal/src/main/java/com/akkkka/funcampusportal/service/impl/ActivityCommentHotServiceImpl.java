package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.ActivityCommentHotMapper;
import com.akkkka.funcampusnotice.domain.ActivityCommentHot;
import com.akkkka.funcampusnotice.service.IActivityCommentHotService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class ActivityCommentHotServiceImpl implements IActivityCommentHotService 
{
    @Autowired
    private ActivityCommentHotMapper activityCommentHotMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param commentUid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ActivityCommentHot selectActivityCommentHotByCommentUid(String commentUid)
    {
        return activityCommentHotMapper.selectActivityCommentHotByCommentUid(commentUid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activityCommentHot 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ActivityCommentHot> selectActivityCommentHotList(ActivityCommentHot activityCommentHot)
    {
        return activityCommentHotMapper.selectActivityCommentHotList(activityCommentHot);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param activityCommentHot 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertActivityCommentHot(ActivityCommentHot activityCommentHot)
    {
        return activityCommentHotMapper.insertActivityCommentHot(activityCommentHot);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param activityCommentHot 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateActivityCommentHot(ActivityCommentHot activityCommentHot)
    {
        return activityCommentHotMapper.updateActivityCommentHot(activityCommentHot);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param commentUids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityCommentHotByCommentUids(String[] commentUids)
    {
        return activityCommentHotMapper.deleteActivityCommentHotByCommentUids(commentUids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param commentUid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityCommentHotByCommentUid(String commentUid)
    {
        return activityCommentHotMapper.deleteActivityCommentHotByCommentUid(commentUid);
    }
}
