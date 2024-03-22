package com.akkkka.funcampussecurityapi.api;

import java.util.List;
/**
 * @author akkkka
 */
public interface IPermissionService {
    //授予其他用户权限
    void addPermission(Integer userId, List<String> permissionList);
    //修改其他用户的权限
    void updatePermission(Integer userId, List<String> permissionList);
}
