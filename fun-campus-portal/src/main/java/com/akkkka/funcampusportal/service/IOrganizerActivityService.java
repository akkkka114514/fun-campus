package com.akkkka.funcampusnotice.service;

import java.util.List;
import com.akkkka.funcampusnotice.domain.OrganizerActivity;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface IOrganizerActivityService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public OrganizerActivity selectOrganizerActivityByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param organizerActivity 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<OrganizerActivity> selectOrganizerActivityList(OrganizerActivity organizerActivity);

    /**
     * 新增【请填写功能名称】
     * 
     * @param organizerActivity 【请填写功能名称】
     * @return 结果
     */
    public int insertOrganizerActivity(OrganizerActivity organizerActivity);

    /**
     * 修改【请填写功能名称】
     * 
     * @param organizerActivity 【请填写功能名称】
     * @return 结果
     */
    public int updateOrganizerActivity(OrganizerActivity organizerActivity);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteOrganizerActivityByUids(String[] uids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteOrganizerActivityByUid(String uid);
}
