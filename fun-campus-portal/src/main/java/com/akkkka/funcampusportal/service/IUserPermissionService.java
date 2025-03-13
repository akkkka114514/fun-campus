package com.akkkka.funcampusportal.service;

import com.akkkka.funcampusportal.domain.UserPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: Zoe Wu
 * @create: 2025-03-07 21:54
 * @Description:
 */
public interface IUserPermissionService extends IService<UserPermission> {
    List<String> getUserPermissionListByUserId(Integer userId);
}
