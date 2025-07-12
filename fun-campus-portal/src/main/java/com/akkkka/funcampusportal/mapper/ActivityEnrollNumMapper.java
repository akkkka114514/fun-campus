package com.akkkka.funcampusnotice.mapper;

import java.util.List;
import com.akkkka.funcampusnotice.domain.ActivityEnrollNum;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface ActivityEnrollNumMapper 
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
     * 删除【请填写功能名称】
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteActivityEnrollNumByActivityUid(String activityUid);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param activityUids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteActivityEnrollNumByActivityUids(String[] activityUids);
}
