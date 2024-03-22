package com.akkkka.funcampussecurity.controller;

import com.akkkka.funcampussecurityapi.api.IPermissionService;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author akkkka
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    private IPermissionService permissionService;

    @RequestMapping("/add")
    public CommonResponse<Void> addPermission(Integer userId, List<String> permissionList) {
        ExceptionUtil.throwIfIdNotValid(userId);
        permissionService.addPermission(userId, permissionList);
        return CommonResponse.success();
    }
    @RequestMapping("/update")
    public CommonResponse<Void> updatePermission(Integer userId, List<String> permissionList) {
        ExceptionUtil.throwIfIdNotValid(userId);
        permissionService.updatePermission(userId, permissionList);
        return CommonResponse.success();
    }

}
