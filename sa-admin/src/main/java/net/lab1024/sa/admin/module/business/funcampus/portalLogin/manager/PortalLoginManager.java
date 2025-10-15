package net.lab1024.sa.admin.module.business.funcampus.portalLogin.manager;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.constant.AdminCacheConst;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.service.PortalUserService;
import net.lab1024.sa.admin.module.system.role.service.RoleMenuService;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    @Resource
    private PortalUserManager portalUserManager;

    /**
     * 获取请求用户信息
     */
    @Cacheable(AdminCacheConst.Login.REQUEST_USER)
    public RequestPortalUser getRequestPortalUser(Long requestPortalUserId) {
        if (requestPortalUserId == null) {
            return null;
        }
        // 用户基本信息
        PortalUserEntity portalUserEntity = portalUserManager.getById(requestPortalUserId);
        if (portalUserEntity == null) {
            return null;
        }

        return this.loadLoginInfo(portalUserEntity);
    }

    /**
     * 获取登录的用户信息
     */
    @CachePut(value = AdminCacheConst.Login.REQUEST_USER, key = "#portalUserEntity.id")
    public RequestPortalUser loadLoginInfo(PortalUserEntity portalUserEntity) {
        // 基础信息
        RequestPortalUser requestPortalUser = SmartBeanUtil.copy(portalUserEntity, RequestPortalUser.class);
        requestPortalUser.setUserType(UserTypeEnum.ADMIN_BACKEND_USER);
        return requestPortalUser;
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
    @CacheEvict(value = AdminCacheConst.Login.REQUEST_USER)
    public void clearUserLoginInfo(Long userId) {
        log.info("clear user login info cache, userId:{}", userId);
    }
}
