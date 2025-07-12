package com.akkkka.funcampusnotice.service;

import java.util.List;
import com.akkkka.funcampusnotice.domain.ActivityEnrollment;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface IActivityEnrollmentService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ActivityEnrollment selectActivityEnrollmentByActivityUid(String activityUid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activityEnrollment 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ActivityEnrollment> selectActivityEnrollmentList(ActivityEnrollment activityEnrollment);

    /**
     * 新增【请填写功能名称】
     * 
     * @param activityEnrollment 【请填写功能名称】
     * @return 结果
     */
    public int insertActivityEnrollment(ActivityEnrollment activityEnrollment);

    /**
     * 修改【请填写功能名称】
     * 
     * @param activityEnrollment 【请填写功能名称】
     * @return 结果
     */
    public int updateActivityEnrollment(ActivityEnrollment activityEnrollment);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param activityUids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteActivityEnrollmentByActivityUids(String[] activityUids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteActivityEnrollmentByActivityUid(String activityUid);
}
