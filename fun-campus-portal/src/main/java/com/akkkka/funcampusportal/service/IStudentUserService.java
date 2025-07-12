package com.akkkka.funcampusnotice.service;

import java.util.List;
import com.akkkka.funcampusnotice.domain.StudentUser;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public interface IStudentUserService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public StudentUser selectStudentUserByUid(String uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param studentUser 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<StudentUser> selectStudentUserList(StudentUser studentUser);

    /**
     * 新增【请填写功能名称】
     * 
     * @param studentUser 【请填写功能名称】
     * @return 结果
     */
    public int insertStudentUser(StudentUser studentUser);

    /**
     * 修改【请填写功能名称】
     * 
     * @param studentUser 【请填写功能名称】
     * @return 结果
     */
    public int updateStudentUser(StudentUser studentUser);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteStudentUserByUids(String[] uids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteStudentUserByUid(String uid);
}
