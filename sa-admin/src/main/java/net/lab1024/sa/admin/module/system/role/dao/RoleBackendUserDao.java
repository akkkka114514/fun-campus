package net.lab1024.sa.admin.module.system.role.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.BackendUserVO;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleBackendUserEntity;
import net.lab1024.sa.admin.module.system.role.domain.form.RoleBackendUserQueryForm;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleBackendUserVO;

import java.util.List;
import java.util.Set;


/**
 * 角色 员工 dao
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-03-07 18:54:42
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface RoleBackendUserDao extends BaseMapper<RoleBackendUserEntity> {

    /**
     * 根据员工id 查询所有的角色
     */
    List<RoleVO> selectRoleByBackendUserId(@Param("backendUserId") Long backendUserId);

    /**
     * 根据员工id 查询所有的角色io集合
     */
    List<Long> selectRoleIdByBackendUserId(@Param("backendUserId") Long backendUserId);

    /**
     * 根据员工id 查询所有的角色
     */
    List<RoleBackendUserEntity> selectRoleIdByBackendUserIdList(@Param("backendUserIdList") List<Long> backendUserIdList);

    /**
     * 根据员工id 查询所有的角色
     */
    List<RoleBackendUserVO> selectRoleByBackendUserIdList(@Param("backendUserIdList") List<Long> backendUserIdList);

    /**
     * 查询角色下的人员id
     */
    Set<Long> selectBackendUserIdByRoleIdList(@Param("roleIdList") List<Long> roleIdList);

    /**
     *
     */
    List<BackendUserVO> selectRoleBackendUserByName(Page page, @Param("queryForm") RoleBackendUserQueryForm roleBackendUserQueryForm);

    /**
     *
     */
    List<BackendUserVO> selectBackendUserByRoleId(@Param("roleId") Long roleId);
    /**
     * 根据员工信息删除
     */
    void deleteByBackendUserId(@Param("backendUserId") Long backendUserId);

    /**
     * 删除某个角色的所有关系
     */
    void deleteByRoleId(@Param("roleId")Long roleId);

    /**
     * 根据员工和 角色删除关系
     */
    void deleteByBackendUserIdRoleId(@Param("backendUserId") Long backendUserId,@Param("roleId")Long roleId);

    /**
     * 批量删除某个角色下的某批用户的关联关系
     */
    void batchDeleteBackendUserRole(@Param("roleId") Long roleId, @Param("backendUserIds") Set<Long> backendUserIds);

    /**
     * 判断某个角色下是否存在用户
     */
    Integer existsByRoleId(@Param("roleId") Long roleId);
}
