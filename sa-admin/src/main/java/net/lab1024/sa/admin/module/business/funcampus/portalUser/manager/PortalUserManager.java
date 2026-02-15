package net.lab1024.sa.admin.module.business.funcampus.portalUser.manager;

import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.dao.PortalUserDao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 前端用户  Manager
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */
@Service
public class PortalUserManager extends ServiceImpl<PortalUserDao, PortalUserEntity> {

    public PortalUserEntity getNotDeletedOne(){

    }

}
