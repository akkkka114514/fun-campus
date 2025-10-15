package net.lab1024.sa.admin.module.business.funcampus.portalLogin.manager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.constant.AdminCacheConst;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.service.PortalUserService;
import net.lab1024.sa.admin.module.system.menu.domain.vo.MenuVO;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import net.lab1024.sa.admin.module.system.role.service.RoleMenuService;
import net.lab1024.sa.base.common.domain.UserPermission;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author:akkkka114514
 * create at 2025-10-15 10:30
 */
@Slf4j
@Service
public class PortalLoginManager {

    @Resource
    private PortalUserService portalUserService;

    @Resource
    private RoleMenuService roleMenuService;

    /**
     * 获取请求用户信息
     */
    @Cacheable(AdminCacheConst.Login.REQUEST_USER)
    public RequestPortalUser getRequestPortalUser(Long requestPortalUserId) {
        if (requestPortalUserId == null) {
            return null;
        }
        // 用户基本信息
        PortalUserEntity portalUserEntity = portalUserService.getById(requestPortalUserId);
        if (portalUserEntity == null) {
            return null;
        }

        return this.loadLoginInfo(portalUserEntity);
    }

    /**
     * 获取登录的用户信息
     */
    @CachePut(value = AdminCacheConst.Login.REQUEST_EMPLOYEE, key = "#portalUserEntity.id")
    public RequestPortalUser loadLoginInfo(PortalUserEntity portalUserEntity) {
        // 基础信息
        RequestPortalUser requestPortalUser = SmartBeanUtil.copy(portalUserEntity, RequestPortalUser.class);
        requestPortalUser.setUserType(UserTypeEnum.ADMIN_BACKEND_USER);
        return requestPortalUser;
    }

    /**
     * 获取用户权限
     */
    @Cacheable(AdminCacheConst.Login.USER_PERMISSION)
    public UserPermission getUserPermission(Long employeeId) {
        UserPermission userPermission = new UserPermission();
        userPermission.setRoleList(new ArrayList<>());
        userPermission.setPermissionList(new ArrayList<>());

        // 角色列表
        List<RoleVO> roleList = rolePortalUserService.getRoleIdList(employeeId);
        userPermission.getRoleList().addAll(roleList.stream().map(RoleVO::getRoleCode).collect(Collectors.toSet()));

        // 前端菜单和功能点清单
        // 注意：PortalUser没有administratorFlag字段，使用默认值false
        List<MenuVO> menuAndPointsList = roleMenuService.getMenuList(
                roleList.stream().map(RoleVO::getRoleId).collect(Collectors.toList()), false);

        // 权限列表
        HashSet<String> permissionSet = new HashSet<>();
        for (MenuVO menu : menuAndPointsList) {
            if (menu.getPermsType() == null) {
                continue;
            }

            String perms = menu.getApiPerms();
            if (StringUtils.isEmpty(perms)) {
                continue;
            }

            String[] split = perms.split(",");
            permissionSet.addAll(Arrays.asList(split));
        }
        userPermission.getPermissionList().addAll(permissionSet);

        return userPermission;
    }

    /**
     * 更新用户权限
     */
    @CachePut(value = AdminCacheConst.Login.USER_PERMISSION)
    public UserPermission loadUserPermission(Long id) {
        UserPermission userPermission = new UserPermission();
        userPermission.setPermissionList(new ArrayList<>());
        userPermission.setRoleList(new ArrayList<>());

        // 角色列表
        List<RoleVO> roleList = rolePortalUserService.getRoleIdList(id);
        userPermission.getRoleList().addAll(roleList.stream().map(RoleVO::getRoleCode).collect(Collectors.toSet()));

        // 前端菜单和功能点清单
        // 注意：PortalUser没有administratorFlag字段，使用默认值false
        List<MenuVO> menuAndPointsList = roleMenuService.getMenuList(
                roleList.stream().map(RoleVO::getRoleId).collect(Collectors.toList()), false);

        // 权限列表
        HashSet<String> permissionSet = new HashSet<>();
        for (MenuVO menu : menuAndPointsList) {
            if (menu.getPermsType() == null) {
                continue;
            }

            String perms = menu.getApiPerms();
            if (StringUtils.isEmpty(perms)) {
                continue;
            }

            String[] split = perms.split(",");
            permissionSet.addAll(Arrays.asList(split));
        }
        userPermission.getPermissionList().addAll(permissionSet);

        return userPermission;
    }

    /**
     * 清除用户权限
     */
    @CacheEvict(value = AdminCacheConst.Login.USER_PERMISSION)
    public void clearUserPermission(Long userId) {
        log.info("clear user permission cache, userId:{}", userId);
    }

    /**
     * 清除用户登录信息
     */
    @CacheEvict(value = AdminCacheConst.Login.REQUEST_EMPLOYEE)
    public void clearUserLoginInfo(Long userId) {
        log.info("clear user login info cache, userId:{}", userId);
    }
}
