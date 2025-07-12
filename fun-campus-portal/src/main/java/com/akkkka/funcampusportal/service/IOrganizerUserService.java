package com.akkkka.funcampusnotice.service;

import java.util.List;
import com.akkkka.funcampusnotice.domain.OrganizerUser;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface IOrganizerUserService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public OrganizerUser selectOrganizerUserByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param organizerUser 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<OrganizerUser> selectOrganizerUserList(OrganizerUser organizerUser);

    /**
     * 新增【请填写功能名称】
     * 
     * @param organizerUser 【请填写功能名称】
     * @return 结果
     */
    public int insertOrganizerUser(OrganizerUser organizerUser);

    /**
     * 修改【请填写功能名称】
     * 
     * @param organizerUser 【请填写功能名称】
     * @return 结果
     */
    public int updateOrganizerUser(OrganizerUser organizerUser);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteOrganizerUserByUids(String[] uids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteOrganizerUserByUid(String uid);
}
