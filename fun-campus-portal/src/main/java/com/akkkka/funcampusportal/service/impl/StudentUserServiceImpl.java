package com.akkkka.funcampusnotice.service.impl;

import java.util.List;
import com.akkkka.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akkkka.funcampusnotice.mapper.StudentUserMapper;
import com.akkkka.funcampusnotice.domain.StudentUser;
import com.akkkka.funcampusnotice.service.IStudentUserService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@Service
public class StudentUserServiceImpl implements IStudentUserService 
{
    @Autowired
    private StudentUserMapper studentUserMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public StudentUser selectStudentUserByUid(String uid)
    {
        return studentUserMapper.selectStudentUserByUid(uid);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param studentUser 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<StudentUser> selectStudentUserList(StudentUser studentUser)
    {
        return studentUserMapper.selectStudentUserList(studentUser);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param studentUser 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertStudentUser(StudentUser studentUser)
    {
        studentUser.setCreateTime(DateUtils.getNowDate());
        return studentUserMapper.insertStudentUser(studentUser);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param studentUser 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateStudentUser(StudentUser studentUser)
    {
        studentUser.setUpdateTime(DateUtils.getNowDate());
        return studentUserMapper.updateStudentUser(studentUser);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteStudentUserByUids(String[] uids)
    {
        return studentUserMapper.deleteStudentUserByUids(uids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteStudentUserByUid(String uid)
    {
        return studentUserMapper.deleteStudentUserByUid(uid);
    }
}
