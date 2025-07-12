package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import com.akkkka.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.OrganizerActivityMapper;
import com.akkkka.funcampusnotice.domain.OrganizerActivity;
import com.akkkka.funcampusnotice.service.IOrganizerActivityService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class OrganizerActivityServiceImpl implements IOrganizerActivityService 
{
    @Autowired
    private OrganizerActivityMapper organizerActivityMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public OrganizerActivity selectOrganizerActivityByUid(String uid)
    {
        return organizerActivityMapper.selectOrganizerActivityByUid(uid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param organizerActivity 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<OrganizerActivity> selectOrganizerActivityList(OrganizerActivity organizerActivity)
    {
        return organizerActivityMapper.selectOrganizerActivityList(organizerActivity);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param organizerActivity 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertOrganizerActivity(OrganizerActivity organizerActivity)
    {
        organizerActivity.setCreateTime(DateUtils.getNowDate());
        return organizerActivityMapper.insertOrganizerActivity(organizerActivity);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param organizerActivity 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateOrganizerActivity(OrganizerActivity organizerActivity)
    {
        organizerActivity.setUpdateTime(DateUtils.getNowDate());
        return organizerActivityMapper.updateOrganizerActivity(organizerActivity);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteOrganizerActivityByUids(String[] uids)
    {
        return organizerActivityMapper.deleteOrganizerActivityByUids(uids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteOrganizerActivityByUid(String uid)
    {
        return organizerActivityMapper.deleteOrganizerActivityByUid(uid);
    }
}
