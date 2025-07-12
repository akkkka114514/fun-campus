package com.akkkka.funcampusnotice.mapper;

import java.util.List;
import com.akkkka.funcampusnotice.domain.UserSubscriber;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface UserSubscriberMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public UserSubscriber selectUserSubscriberByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param userSubscriber 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<UserSubscriber> selectUserSubscriberList(UserSubscriber userSubscriber);

    /**
     * 新增【请填写功能名称】
     * 
     * @param userSubscriber 【请填写功能名称】
     * @return 结果
     */
    public int insertUserSubscriber(UserSubscriber userSubscriber);

    /**
     * 修改【请填写功能名称】
     * 
     * @param userSubscriber 【请填写功能名称】
     * @return 结果
     */
    public int updateUserSubscriber(UserSubscriber userSubscriber);

    /**
     * 删除【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteUserSubscriberByUid(String uid);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserSubscriberByUids(String[] uids);
}
