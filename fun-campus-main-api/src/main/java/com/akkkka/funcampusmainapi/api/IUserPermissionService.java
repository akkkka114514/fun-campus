package com.akkkka.funcampusmainapi.api;

import com.akkkka.funcampusmainmodel.entity.UserPermission;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.javassist.bytecode.analysis.ControlFlow;

import java.util.List;
import java.util.Map;

public interface IUserPermissionService extends IService<UserPermission> {

    List<String> getUserPermissionListByUserId(Integer userId);
}
