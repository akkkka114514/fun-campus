package com.akkkka.funcampusmain.service.impl;

import com.akkkka.funcampusmain.mapper.UserPermissionMapper;
import com.akkkka.funcampusmainapi.api.IUserPermissionService;
import com.akkkka.funcampusmainmodel.entity.UserPermission;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author akkkka
 * 2024/3/6 19:43
 * */
@Service
@DubboService
public class UserPermissionServiceImpl extends ServiceImpl<UserPermissionMapper, UserPermission> implements IUserPermissionService {
    @Override
    public List<String> getUserPermissionListByUserId(Integer userId) {
        UserPermission permission= this.getById(userId);
        List<String> permissionList = new ArrayList<>();
        if(permission.getAudit() == 1){
            permissionList.add("audit");
        }
        if(permission.getEditSchool() == 1){
            permissionList.add("editSchool");
        }
        if(permission.getPublishActivity() == 1){
            permissionList.add("publishActivity");
        }
        if(permission.getManageSignIn() == 1){
            permissionList.add("manageSignIn");
        }if(permission.getGrantPermission() == 1){
            permissionList.add("grantPermission");
        }
        return permissionList;
    }
}
