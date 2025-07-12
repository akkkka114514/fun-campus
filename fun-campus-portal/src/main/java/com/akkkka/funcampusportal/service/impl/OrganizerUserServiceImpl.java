package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import com.akkkka.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.OrganizerUserMapper;
import com.akkkka.funcampusnotice.domain.OrganizerUser;
import com.akkkka.funcampusnotice.service.IOrganizerUserService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class OrganizerUserServiceImpl implements IOrganizerUserService 
{
    @Autowired
    private OrganizerUserMapper organizerUserMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public OrganizerUser selectOrganizerUserByUid(String uid)
    {
        return organizerUserMapper.selectOrganizerUserByUid(uid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param organizerUser 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<OrganizerUser> selectOrganizerUserList(OrganizerUser organizerUser)
    {
        return organizerUserMapper.selectOrganizerUserList(organizerUser);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param organizerUser 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertOrganizerUser(OrganizerUser organizerUser)
    {
        organizerUser.setCreateTime(DateUtils.getNowDate());
        return organizerUserMapper.insertOrganizerUser(organizerUser);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param organizerUser 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateOrganizerUser(OrganizerUser organizerUser)
    {
        organizerUser.setUpdateTime(DateUtils.getNowDate());
        return organizerUserMapper.updateOrganizerUser(organizerUser);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteOrganizerUserByUids(String[] uids)
    {
        return organizerUserMapper.deleteOrganizerUserByUids(uids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteOrganizerUserByUid(String uid)
    {
        return organizerUserMapper.deleteOrganizerUserByUid(uid);
    }
}
