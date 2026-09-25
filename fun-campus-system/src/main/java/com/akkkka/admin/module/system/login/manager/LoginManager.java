package com.akkkka.admin.module.system.login.manager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.akkkka.admin.constant.AdminCacheConst;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.service.BackendUserService;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.admin.module.system.menu.domain.vo.MenuVO;
import com.akkkka.admin.module.system.role.domain.vo.RoleVO;
import com.akkkka.admin.module.system.role.service.RoleBackendUserService;
import com.akkkka.admin.module.system.role.service.RoleMenuService;
import com.akkkka.common.domain.UserPermission;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.util.SmartBeanUtil;
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
 * 登录Manager
 *
 * @Author 1024创新实验室: 卓大
 * @Date 2025-05-03 22:56:34
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Service
public class LoginManager {

    @Resource
    private BackendUserService backendUserService;

    @Resource
    private RoleBackendUserService roleBackendUserService;

    @Resource
    private RoleMenuService roleMenuService;

    /**
     * 获取请求用户信息
     */
    @Cacheable(AdminCacheConst.Login.REQUEST_USER)
    public RequestBackendUser getRequestBackendUser(Long requestBackendUserId) {
        if (requestBackendUserId == null) {
            return null;
        }
        // 用户基本信息
        BackendUserEntity backendUserEntity = backendUserService.getById(requestBackendUserId);
        if (backendUserEntity == null) {
            return null;
        }

        return this.loadLoginInfo(backendUserEntity);
    }

    /**
     * 获取登录的用户信息
     */
    @CachePut(value = AdminCacheConst.Login.REQUEST_USER, key = "#backendUserEntity.id")
    public RequestBackendUser loadLoginInfo(BackendUserEntity backendUserEntity) {
        // 基础信息
        RequestBackendUser requestBackendUser = SmartBeanUtil.copy(backendUserEntity, RequestBackendUser.class);
        requestBackendUser.setUserType(UserTypeEnum.ADMIN_BACKEND_USER);
        return requestBackendUser;
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
        List<RoleVO> roleList = roleBackendUserService.getRoleIdList(employeeId);
        userPermission.getRoleList().addAll(roleList.stream().map(RoleVO::getRoleCode).collect(Collectors.toSet()));

        // 前端菜单和功能点清单
        // 注意：BackendUser没有administratorFlag字段，使用默认值false
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
        List<RoleVO> roleList = roleBackendUserService.getRoleIdList(id);
        userPermission.getRoleList().addAll(roleList.stream().map(RoleVO::getRoleCode).collect(Collectors.toSet()));

        // 前端菜单和功能点清单
        // 注意：BackendUser没有administratorFlag字段，使用默认值false
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
        log.info("clear user permission cache, user Id:{}", userId);
    }

    /**
     * 清除用户登录信息
     */
    @CacheEvict(value = AdminCacheConst.Login.REQUEST_USER)
    public void clearUserLoginInfo(Long userId) {
        log.info("clear user login info cache, user Id:{}", userId);
    }
}