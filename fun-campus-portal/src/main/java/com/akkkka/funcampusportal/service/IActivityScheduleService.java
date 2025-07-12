package com.akkkka.funcampusnotice.service;

import java.util.List;
import com.akkkka.funcampusnotice.domain.ActivitySchedule;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface IActivityScheduleService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ActivitySchedule selectActivityScheduleByActivityUid(String activityUid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activitySchedule 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ActivitySchedule> selectActivityScheduleList(ActivitySchedule activitySchedule);

    /**
     * 新增【请填写功能名称】
     * 
     * @param activitySchedule 【请填写功能名称】
     * @return 结果
     */
    public int insertActivitySchedule(ActivitySchedule activitySchedule);

    /**
     * 修改【请填写功能名称】
     * 
     * @param activitySchedule 【请填写功能名称】
     * @return 结果
     */
    public int updateActivitySchedule(ActivitySchedule activitySchedule);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param activityUids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteActivityScheduleByActivityUids(String[] activityUids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteActivityScheduleByActivityUid(String activityUid);
}
