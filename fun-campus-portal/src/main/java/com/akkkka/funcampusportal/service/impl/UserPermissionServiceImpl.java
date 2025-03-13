package com.akkkka.funcampusportal.service.impl;

import com.akkkka.funcampusportal.domain.UserPermission;
import com.akkkka.funcampusportal.mapper.UserPermissionMapper;
import com.akkkka.funcampusportal.service.IUserPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author akkkka
 * 2024/3/6 19:43
 * */
@Service
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
