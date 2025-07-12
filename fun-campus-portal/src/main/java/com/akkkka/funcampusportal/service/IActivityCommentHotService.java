package com.akkkka.funcampusnotice.service;

import java.util.List;
import com.akkkka.funcampusnotice.domain.ActivityCommentHot;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface IActivityCommentHotService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param commentUid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ActivityCommentHot selectActivityCommentHotByCommentUid(String commentUid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activityCommentHot 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ActivityCommentHot> selectActivityCommentHotList(ActivityCommentHot activityCommentHot);

    /**
     * 新增【请填写功能名称】
     * 
     * @param activityCommentHot 【请填写功能名称】
     * @return 结果
     */
    public int insertActivityCommentHot(ActivityCommentHot activityCommentHot);

    /**
     * 修改【请填写功能名称】
     * 
     * @param activityCommentHot 【请填写功能名称】
     * @return 结果
     */
    public int updateActivityCommentHot(ActivityCommentHot activityCommentHot);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param commentUids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteActivityCommentHotByCommentUids(String[] commentUids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param commentUid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteActivityCommentHotByCommentUid(String commentUid);
}
