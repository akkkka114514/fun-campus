package net.lab1024.sa.admin.module.system.role.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.BackendUserVO;
import net.lab1024.sa.admin.module.system.role.dao.RoleDao;
import net.lab1024.sa.admin.module.system.role.dao.RoleBackendUserDao;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleBackendUserEntity;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleEntity;
import net.lab1024.sa.admin.module.system.role.domain.form.RoleBackendUserQueryForm;
import net.lab1024.sa.admin.module.system.role.domain.form.RoleBackendUserUpdateForm;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleSelectedVO;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import net.lab1024.sa.admin.module.system.role.manager.RoleBackendUserManager;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色-后台用户
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2021-10-22 23:17:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
public class RoleBackendUserService {

    @Resource
    private RoleBackendUserDao roleBackendUserDao;
    @Resource
    private RoleDao roleDao;
    @Resource
    private RoleBackendUserManager roleBackendUserManager;


    /**
     * 批量插入
     *
     */
    public void batchInsert(List<RoleBackendUserEntity> roleBackendUserList) {
        roleBackendUserManager.saveBatch(roleBackendUserList);
    }

    /**
     * 通过角色id，分页获取成员后台用户列表
     *
     */
    public ResponseDTO<PageResult<BackendUserVO>> queryBackendUser(RoleBackendUserQueryForm roleBackendUserQueryForm) {
        Page page = SmartPageUtil.convert2PageQuery(roleBackendUserQueryForm);
        List<BackendUserVO> backendUserList = roleBackendUserDao.selectRoleBackendUserByName(page, roleBackendUserQueryForm)
                .stream()
                .filter(Objects::nonNull)
                .toList();
        PageResult<BackendUserVO> pageResult = SmartPageUtil.convert2PageResult(page, backendUserList, BackendUserVO.class);
        return ResponseDTO.ok(pageResult);
    }

    public List<BackendUserVO> getAllBackendUserByRoleId(Long roleId) {
        return roleBackendUserDao.selectBackendUserByRoleId(roleId);
    }

    /**
     * 移除后台用户角色
     *
     */
    public ResponseDTO<String> removeRoleBackendUser(Long employeeId, Long roleId) {
        if (null == employeeId || null == roleId) {
            return ResponseDTO.userErrorParam();
        }
        roleBackendUserDao.deleteByBackendUserIdRoleId(employeeId, roleId);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除角色的成员后台用户
     *
     */
    public ResponseDTO<String> batchRemoveRoleBackendUser(RoleBackendUserUpdateForm roleBackendUserUpdateForm) {
        roleBackendUserDao.batchDeleteBackendUserRole(roleBackendUserUpdateForm.getRoleId(), roleBackendUserUpdateForm.getBackendUserIdList());
        return ResponseDTO.ok();
    }

    /**
     * 批量添加角色的成员后台用户
     *
     */
    public ResponseDTO<String> batchAddRoleBackendUser(RoleBackendUserUpdateForm roleBackendUserUpdateForm) {
        Long roleId = roleBackendUserUpdateForm.getRoleId();

        // 已选择的后台用户id列表
        Set<Long> selectedBackendUserIdList = roleBackendUserUpdateForm.getBackendUserIdList();
        // 数据库里已有的后台用户id列表
        Set<Long> dbBackendUserIdList = roleBackendUserDao.selectBackendUserIdByRoleIdList(Lists.newArrayList(roleId));
        // 从已选择的后台用户id列表里 过滤数据库里不存在的 即需要添加的后台用户 id
        Set<Long> addBackendUserIdList = selectedBackendUserIdList.stream().filter(id -> !dbBackendUserIdList.contains(id)).collect(Collectors.toSet());

        // 添加角色后台用户
        if (CollectionUtils.isNotEmpty(addBackendUserIdList)) {
            List<RoleBackendUserEntity> roleBackendUserList = addBackendUserIdList.stream()
                    .map(employeeId -> new RoleBackendUserEntity(roleId, employeeId))
                    .collect(Collectors.toList());
            roleBackendUserManager.saveBatch(roleBackendUserList);
        }
        return ResponseDTO.ok();
    }

    /**
     * 通过后台用户id获取后台用户角色
     *
     */
    public List<RoleSelectedVO> getRoleInfoListByBackendUserId(Long employeeId) {
        List<Long> roleIds = roleBackendUserDao.selectRoleIdByBackendUserId(employeeId);
        List<RoleEntity> roleList = roleDao.selectList(null);
        List<RoleSelectedVO> result = SmartBeanUtil.copyList(roleList, RoleSelectedVO.class);
        result.forEach(item -> item.setSelected(roleIds.contains(item.getRoleId())));
        return result;
    }

    /**
     * 根据后台用户id 查询角色id集合
     *
     */
    public List<RoleVO> getRoleIdList(Long employeeId) {
        return roleBackendUserDao.selectRoleByBackendUserId(employeeId);
    }


}
