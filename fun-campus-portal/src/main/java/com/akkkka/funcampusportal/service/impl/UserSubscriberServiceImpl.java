package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.UserSubscriberMapper;
import com.akkkka.funcampusnotice.domain.UserSubscriber;
import com.akkkka.funcampusnotice.service.IUserSubscriberService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class UserSubscriberServiceImpl implements IUserSubscriberService 
{
    @Autowired
    private UserSubscriberMapper userSubscriberMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public UserSubscriber selectUserSubscriberByUid(String uid)
    {
        return userSubscriberMapper.selectUserSubscriberByUid(uid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param userSubscriber 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<UserSubscriber> selectUserSubscriberList(UserSubscriber userSubscriber)
    {
        return userSubscriberMapper.selectUserSubscriberList(userSubscriber);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param userSubscriber 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertUserSubscriber(UserSubscriber userSubscriber)
    {
        return userSubscriberMapper.insertUserSubscriber(userSubscriber);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param userSubscriber 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateUserSubscriber(UserSubscriber userSubscriber)
    {
        return userSubscriberMapper.updateUserSubscriber(userSubscriber);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteUserSubscriberByUids(String[] uids)
    {
        return userSubscriberMapper.deleteUserSubscriberByUids(uids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteUserSubscriberByUid(String uid)
    {
        return userSubscriberMapper.deleteUserSubscriberByUid(uid);
    }
}
