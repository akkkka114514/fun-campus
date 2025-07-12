package com.akkkka.funcampusnotice.service;

import java.util.List;
import com.akkkka.funcampusnotice.domain.ActivityEnrollNum;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface IActivityEnrollNumService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ActivityEnrollNum selectActivityEnrollNumByActivityUid(String activityUid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activityEnrollNum 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ActivityEnrollNum> selectActivityEnrollNumList(ActivityEnrollNum activityEnrollNum);

    /**
     * 新增【请填写功能名称】
     * 
     * @param activityEnrollNum 【请填写功能名称】
     * @return 结果
     */
    public int insertActivityEnrollNum(ActivityEnrollNum activityEnrollNum);

    /**
     * 修改【请填写功能名称】
     * 
     * @param activityEnrollNum 【请填写功能名称】
     * @return 结果
     */
    public int updateActivityEnrollNum(ActivityEnrollNum activityEnrollNum);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param activityUids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteActivityEnrollNumByActivityUids(String[] activityUids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteActivityEnrollNumByActivityUid(String activityUid);
}
