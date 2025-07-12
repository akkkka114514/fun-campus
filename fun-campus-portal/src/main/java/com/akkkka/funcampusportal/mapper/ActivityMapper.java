package com.akkkka.funcampusnotice.mapper;

import java.util.List;
import com.akkkka.funcampusnotice.domain.Activity;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface ActivityMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public Activity selectActivityByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param activity 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Activity> selectActivityList(Activity activity);

    /**
     * 新增【请填写功能名称】
     * 
     * @param activity 【请填写功能名称】
     * @return 结果
     */
    public int insertActivity(Activity activity);

    /**
     * 修改【请填写功能名称】
     * 
     * @param activity 【请填写功能名称】
     * @return 结果
     */
    public int updateActivity(Activity activity);

    /**
     * 删除【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteActivityByUid(String uid);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteActivityByUids(String[] uids);
}
