package net.lab1024.sa.admin.module.system.backendUser.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.system.backendUser.dao.BackendUserDao;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.service.BackendUserService;
import net.lab1024.sa.admin.module.system.login.domain.RequestBackendUser;
import net.lab1024.sa.admin.module.system.role.dao.RoleBackendUserDao;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleBackendUserEntity;
import net.lab1024.sa.admin.module.system.role.service.RoleBackendUserService;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后台用户 Manager
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2025-09-08 20:47:06
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
public class BackendUserManager extends ServiceImpl<BackendUserDao, BackendUserEntity> {

    @Resource
    private BackendUserDao backendUserDao;

    @Resource
    private RoleBackendUserService roleBackendUserService;

    @Resource
    private RoleBackendUserDao roleBackendUserDao;

    /**
     * 保存后台用户
     *
     */
    @Transactional(rollbackFor = Throwable.class)
    public void saveBackendUser(BackendUserEntity backendUser, List<Long> roleIdList) {
        // 保存后台用户 获得id
        backendUserDao.insert(backendUser);

        if (CollectionUtils.isNotEmpty(roleIdList)) {
            List<RoleBackendUserEntity> roleBackendUserList = roleIdList.stream().map(e -> new RoleBackendUserEntity(e, backendUser.getId())).toList();
            roleBackendUserService.batchInsert(roleBackendUserList);
        }
    }

    /**
     * 更新后台用户
     *
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateBackendUser(BackendUserEntity backendUser, List<Long> roleIdList) {
        // 保存后台用户 获得id
        backendUserDao.updateById(backendUser);

        // 若为空，则删除所有角色
        if (CollectionUtils.isEmpty(roleIdList)) {
            roleBackendUserDao.deleteByBackendUserId(backendUser.getId());
            return;
        }

        List<RoleBackendUserEntity> roleBackendUserList = roleIdList.stream().map(e -> new RoleBackendUserEntity(e, backendUser.getId())).toList();
        this.updateBackendUserRole(backendUser.getId(), roleBackendUserList);
    }

    /**
     * 更新后台用户角色
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateBackendUserRole(Long backendUserId, List<RoleBackendUserEntity> roleBackendUserList) {

        roleBackendUserDao.deleteByBackendUserId(backendUserId);

        if (CollectionUtils.isNotEmpty(roleBackendUserList)) {
            roleBackendUserService.batchInsert(roleBackendUserList);
        }
    }

}