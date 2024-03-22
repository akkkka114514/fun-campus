package com.akkkka.funcampussecurity.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.akkkka.funcampusmainapi.api.IUserPermissionService;
import com.akkkka.funcampusmainapi.api.IUserService;
import com.akkkka.funcampusmainmodel.entity.User;
import com.akkkka.funcampusmainmodel.entity.UserPermission;
import com.akkkka.funcampussecurityapi.api.IPermissionService;
import com.akkkka.funcampusutil.common.ResponseEnum;
import com.akkkka.funcampusutil.util.CustomException;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionServiceImpl implements StpInterface, IPermissionService {

    @DubboReference(check = false)
    private IUserPermissionService userPermissionService;
    @DubboReference
    private IUserService userService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        //如果user_permission表没有这个用户，则返回空List
        return userPermissionService.getUserPermissionListByUserId((int)loginId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return null;
    }

    @Override
    public void addPermission(Integer userId, List<String> permissionList) {
        User user = userService.getById(userId);
        ExceptionUtil.throwIfIsDeletedInDb(user.getIsDeleted(),":user");
        ExceptionUtil.throwIfNullInDb(user, ":user");
        //查看是否有授予权限的权限
        if(!StpUtil.hasPermission("grantPermission")){
            throw new CustomException(ResponseEnum.PERMISSION_DENIED);
        }
        UserPermission userPermission = userPermissionService.getById(userId);
        ExceptionUtil.throwIfExistsInDb(userPermission);
        userPermission = new UserPermission();

        for(String permission : permissionList){
            if("manageSignIn".equals(permission)){
                userPermission.setManageSignIn((byte)1);
            } else if ("publishActivity".equals(permission)) {
                userPermission.setPublishActivity((byte)1);
            } else if ("audit".equals(permission)) {
                userPermission.setAudit((byte)1);
            } else if ("editSchool".equals(permission)) {
                userPermission.setEditSchool((byte)1);
            }else if ("grantPermission".equals(permission)) {
                userPermission.setGrantPermission((byte)1);
            }
        }
        userPermission.setUserId(userId);
        userPermissionService.save(userPermission);
    }
    @Override
    public void updatePermission(Integer userId, List<String> permissionList){
        User user = userService.getById(userId);
        ExceptionUtil.throwIfIsDeletedInDb(user.getIsDeleted(),":user");
        ExceptionUtil.throwIfNullInDb(userId,":user");
        //查看是否有授予权限的权限
        if (!StpUtil.hasPermission("grantPermission")){
            throw new CustomException(ResponseEnum.PERMISSION_DENIED);
        }
        UserPermission userPermission = userPermissionService.getById(userId);
        ExceptionUtil.throwIfNullInDb(userPermission,":userPermission");
        for(String permission : permissionList){
            if("manageSignIn".equals(permission)){
                userPermission.setManageSignIn((byte)1);
            } else if ("publishActivity".equals(permission)) {
                userPermission.setPublishActivity((byte)1);
            } else if ("audit".equals(permission)) {
                userPermission.setAudit((byte)1);
            } else if ("editSchool".equals(permission)) {
                userPermission.setEditSchool((byte)1);
            }else if ("grantPermission".equals(permission)) {
                userPermission.setGrantPermission((byte)1);
            }
        }
        userPermission.setUserId(userId);
        userPermissionService.updateById(userPermission);
    }
}
