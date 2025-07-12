package com.akkkka.funcampusnotice.mapper;

import java.util.List;
import com.akkkka.funcampusnotice.domain.ActivityComment;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface ActivityCommentMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ActivityComment selectActivityCommentByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activityComment 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ActivityComment> selectActivityCommentList(ActivityComment activityComment);

    /**
     * 新增【请填写功能名称】
     * 
     * @param activityComment 【请填写功能名称】
     * @return 结果
     */
    public int insertActivityComment(ActivityComment activityComment);

    /**
     * 修改【请填写功能名称】
     * 
     * @param activityComment 【请填写功能名称】
     * @return 结果
     */
    public int updateActivityComment(ActivityComment activityComment);

    /**
     * 删除【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteActivityCommentByUid(String uid);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteActivityCommentByUids(String[] uids);
}
