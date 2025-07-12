package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.ActivityEnrollNumMapper;
import com.akkkka.funcampusnotice.domain.ActivityEnrollNum;
import com.akkkka.funcampusnotice.service.IActivityEnrollNumService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class ActivityEnrollNumServiceImpl implements IActivityEnrollNumService 
{
    @Autowired
    private ActivityEnrollNumMapper activityEnrollNumMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public ActivityEnrollNum selectActivityEnrollNumByActivityUid(String activityUid)
    {
        return activityEnrollNumMapper.selectActivityEnrollNumByActivityUid(activityUid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activityEnrollNum 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ActivityEnrollNum> selectActivityEnrollNumList(ActivityEnrollNum activityEnrollNum)
    {
        return activityEnrollNumMapper.selectActivityEnrollNumList(activityEnrollNum);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param activityEnrollNum 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertActivityEnrollNum(ActivityEnrollNum activityEnrollNum)
    {
        return activityEnrollNumMapper.insertActivityEnrollNum(activityEnrollNum);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param activityEnrollNum 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateActivityEnrollNum(ActivityEnrollNum activityEnrollNum)
    {
        return activityEnrollNumMapper.updateActivityEnrollNum(activityEnrollNum);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param activityUids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityEnrollNumByActivityUids(String[] activityUids)
    {
        return activityEnrollNumMapper.deleteActivityEnrollNumByActivityUids(activityUids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param activityUid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteActivityEnrollNumByActivityUid(String activityUid)
    {
        return activityEnrollNumMapper.deleteActivityEnrollNumByActivityUid(activityUid);
    }
}
