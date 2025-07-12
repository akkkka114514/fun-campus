package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import com.akkkka.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.ActivityEnrollmentMapper;
import com.akkkka.funcampusnotice.domain.ActivityEnrollment;
import com.akkkka.funcampusnotice.service.IActivityEnrollmentService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class ActivityEnrollmentServiceImpl implements IActivityEnrollmentService 
{
    @Autowired
    private ActivityEnrollmentMapper activityEnrollmentMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ActivityEnrollment selectActivityEnrollmentByActivityUid(String activityUid)
    {
        return activityEnrollmentMapper.selectActivityEnrollmentByActivityUid(activityUid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activityEnrollment 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ActivityEnrollment> selectActivityEnrollmentList(ActivityEnrollment activityEnrollment)
    {
        return activityEnrollmentMapper.selectActivityEnrollmentList(activityEnrollment);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param activityEnrollment 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertActivityEnrollment(ActivityEnrollment activityEnrollment)
    {
        activityEnrollment.setCreateTime(DateUtils.getNowDate());
        return activityEnrollmentMapper.insertActivityEnrollment(activityEnrollment);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param activityEnrollment 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateActivityEnrollment(ActivityEnrollment activityEnrollment)
    {
        activityEnrollment.setUpdateTime(DateUtils.getNowDate());
        return activityEnrollmentMapper.updateActivityEnrollment(activityEnrollment);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param activityUids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityEnrollmentByActivityUids(String[] activityUids)
    {
        return activityEnrollmentMapper.deleteActivityEnrollmentByActivityUids(activityUids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityEnrollmentByActivityUid(String activityUid)
    {
        return activityEnrollmentMapper.deleteActivityEnrollmentByActivityUid(activityUid);
    }
}
