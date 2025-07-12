package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import com.akkkka.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.ActivityScheduleMapper;
import com.akkkka.funcampusnotice.domain.ActivitySchedule;
import com.akkkka.funcampusnotice.service.IActivityScheduleService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class ActivityScheduleServiceImpl implements IActivityScheduleService 
{
    @Autowired
    private ActivityScheduleMapper activityScheduleMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ActivitySchedule selectActivityScheduleByActivityUid(String activityUid)
    {
        return activityScheduleMapper.selectActivityScheduleByActivityUid(activityUid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activitySchedule 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ActivitySchedule> selectActivityScheduleList(ActivitySchedule activitySchedule)
    {
        return activityScheduleMapper.selectActivityScheduleList(activitySchedule);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param activitySchedule 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertActivitySchedule(ActivitySchedule activitySchedule)
    {
        activitySchedule.setCreateTime(DateUtils.getNowDate());
        return activityScheduleMapper.insertActivitySchedule(activitySchedule);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param activitySchedule 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateActivitySchedule(ActivitySchedule activitySchedule)
    {
        activitySchedule.setUpdateTime(DateUtils.getNowDate());
        return activityScheduleMapper.updateActivitySchedule(activitySchedule);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param activityUids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityScheduleByActivityUids(String[] activityUids)
    {
        return activityScheduleMapper.deleteActivityScheduleByActivityUids(activityUids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityScheduleByActivityUid(String activityUid)
    {
        return activityScheduleMapper.deleteActivityScheduleByActivityUid(activityUid);
    }
}
