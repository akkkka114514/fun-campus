package net.lab1024.sa.admin.module.system.backendUser.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.dao.BackendUserDao;
import net.lab1024.sa.admin.module.system.backendUser.domain.form.*;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.BackendUserVO;
import net.lab1024.sa.admin.module.system.backendUser.manager.BackendUserManager;
import net.lab1024.sa.admin.module.system.login.service.LoginService;
import net.lab1024.sa.admin.module.system.role.dao.RoleBackendUserDao;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleBackendUserVO;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.constant.StringConst;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.module.support.securityprotect.service.SecurityPasswordService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台用户 Service
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2025-09-08 20:47:06
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
public class BackendUserService extends ServiceImpl<BackendUserDao, BackendUserEntity> {
    @Resource
    private BackendUserDao backendUserDao;

    @Resource
    private BackendUserManager backendUserManager;

    @Resource
    private RoleBackendUserDao roleBackendUserDao;

    @Resource
    private SecurityPasswordService securityPasswordService;

    @Resource
    @Lazy
    private LoginService loginService;

    public BackendUserEntity getById(Long backendUserId) {
        return backendUserDao.selectById(backendUserId);
    }


    /**
     * 查询后台用户列表
     */
    public ResponseDTO<PageResult<BackendUserVO>> queryBackendUser(BackendUserQueryForm backendUserQueryForm) {
        backendUserQueryForm.setDeletedFlag(false);
        Page pageParam = SmartPageUtil.convert2PageQuery(backendUserQueryForm);

        List<BackendUserVO> backendUserList = backendUserDao.queryBackendUser(pageParam, backendUserQueryForm);
        if (CollectionUtils.isEmpty(backendUserList)) {
            PageResult<BackendUserVO> pageResult = SmartPageUtil.convert2PageResult(pageParam, backendUserList);
            return ResponseDTO.ok(pageResult);
        }

        // 查询后台用户角色
        List<Long> backendUserIdList = backendUserList.stream().map(BackendUserVO::getId).collect(Collectors.toList());
        List<RoleBackendUserVO> roleBackendUserEntityList = backendUserIdList.isEmpty() ? Collections.emptyList() : roleBackendUserDao.selectRoleByBackendUserIdList(backendUserIdList);
        Map<Long, List<Long>> backendUserRoleIdListMap = roleBackendUserEntityList.stream().collect(Collectors.groupingBy(RoleBackendUserVO::getBackendUserId, Collectors.mapping(RoleBackendUserVO::getRoleId, Collectors.toList())));
        Map<Long, List<String>> backendUserRoleNameListMap = roleBackendUserEntityList.stream().collect(Collectors.groupingBy(RoleBackendUserVO::getBackendUserId, Collectors.mapping(RoleBackendUserVO::getRoleName, Collectors.toList())));

        backendUserList.forEach(e -> {
            e.setRoleIdList(backendUserRoleIdListMap.getOrDefault(e.getId(), Lists.newArrayList()));
            e.setRoleNameList(backendUserRoleNameListMap.getOrDefault(e.getId(), Lists.newArrayList()));
        });
        PageResult<BackendUserVO> pageResult = SmartPageUtil.convert2PageResult(pageParam, backendUserList);
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 新增后台用户
     */
    public synchronized ResponseDTO<String> addBackendUser(BackendUserAddForm backendUserAddForm) {
        // 校验登录名是否重复
        BackendUserEntity backendUserEntity = backendUserDao.getByUsername(backendUserAddForm.getUsername(), null);
        if (null != backendUserEntity) {
            return ResponseDTO.userErrorParam("登录名重复");
        }

        BackendUserEntity entity = SmartBeanUtil.copy(backendUserAddForm, BackendUserEntity.class);
        // 设置密码 随机密码
        String randomPassword = securityPasswordService.randomPassword();
        entity.setPassword(SecurityPasswordService.getEncryptPwd(randomPassword));

        // 保存数据
        entity.setDeletedFlag(Boolean.FALSE);
        backendUserManager.saveBackendUser(entity, backendUserAddForm.getRoleIdList());

        return ResponseDTO.ok(randomPassword);
    }

    /**
     * 更新后台用户
     */
    public synchronized ResponseDTO<String> updateBackendUser(BackendUserUpdateForm backendUserUpdateForm) {

        Long backendUserId = backendUserUpdateForm.getId();
        BackendUserEntity backendUserEntity = backendUserDao.selectById(backendUserId);
        if (null == backendUserEntity) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }
        // 检查唯一性
        Map<String, Object> fieldValues = new HashMap<>(2);
        fieldValues.put("username", backendUserUpdateForm.getUsername());
        fieldValues.put("email", backendUserUpdateForm.getEmail());
        ResponseDTO<String> checkResponse = checkUniqueness(backendUserId, fieldValues);
        if (!checkResponse.getOk()) {
            return checkResponse;
        }

        BackendUserEntity entity = SmartBeanUtil.copy(backendUserUpdateForm, BackendUserEntity.class);
        // 不更新密码
        entity.setPassword(null);

        // 更新数据
        backendUserManager.updateBackendUser(entity, backendUserUpdateForm.getRoleIdList());

        // 清除后台用户缓存
        loginService.clearLoginBackendUserCache(backendUserId);

        return ResponseDTO.ok();
    }

    /**
     * 更新后台用户个人中心信息
     */
    public ResponseDTO<String> updateCenter(BackendUserUpdateCenterForm updateCenterForm) {

        Long backendUserId = updateCenterForm.getId();
        BackendUserEntity backendUserEntity = backendUserDao.selectById(backendUserId);
        if (null == backendUserEntity) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }


        Map<String, Object> fieldValues = new HashMap<>(2);
        fieldValues.put("email", updateCenterForm.getEmail());
        // 检查唯一性 登录账号不能修改则不需要检查
        ResponseDTO<String> checkResponse = checkUniqueness(backendUserId, fieldValues);
        if (!checkResponse.getOk()) {
            return checkResponse;
        }

        BackendUserEntity backendUser = SmartBeanUtil.copy(updateCenterForm, BackendUserEntity.class);
        // 不更新密码
        backendUser.setPassword(null);

        // 更新数据
        backendUserDao.updateById(backendUser);

        // 清除后台用户缓存
        loginService.clearLoginBackendUserCache(backendUserId);

        return ResponseDTO.ok();
    }

    /**
     * 检查唯一性
     */
    private ResponseDTO<String> checkUniqueness(Long backendUserId, Map<String, Object> fieldValues) {
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (value != null) {
                BackendUserEntity existEntity;
                existEntity = backendUserDao.getByFieldExcludingId(fieldName, (String) value, false, backendUserId);
                if (null != existEntity) {
                    return ResponseDTO.userErrorParam(fieldName + "已存在");
                }
            }
        }
        return ResponseDTO.ok();
    }


    /**
     * 更新禁用/启用状态
     */
    public ResponseDTO<String> updateDisableFlag(Long backendUserId) {
        if (null == backendUserId) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }
        BackendUserEntity backendUserEntity = backendUserDao.selectById(backendUserId);
        if (null == backendUserEntity) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }
        backendUserDao.updateDisableFlag(backendUserId, !backendUserEntity.getDisabledFlag());

        if (backendUserEntity.getDisabledFlag()) {
            // 强制退出登录
            StpUtil.logout(UserTypeEnum.ADMIN_BACKEND_USER.getValue() + StringConst.COLON + backendUserId);
        }

        return ResponseDTO.ok();
    }

    /**
     * 批量删除后台用户
     */
    public ResponseDTO<String> batchUpdateDeleteFlag(List<Long> backendUserIdList) {
        if (CollectionUtils.isEmpty(backendUserIdList)) {
            return ResponseDTO.ok();
        }
        List<BackendUserEntity> backendUserEntityList = backendUserManager.listByIds(backendUserIdList);
        if (CollectionUtils.isEmpty(backendUserEntityList)) {
            return ResponseDTO.ok();
        }
        // 更新删除
        List<BackendUserEntity> deleteList = backendUserIdList.stream().map(e -> {
            BackendUserEntity updateBackendUser = new BackendUserEntity();
            updateBackendUser.setId(e);
            updateBackendUser.setDeletedFlag(true);
            return updateBackendUser;
        }).collect(Collectors.toList());
        backendUserManager.updateBatchById(deleteList);

        for (Long backendUserId : backendUserIdList) {
            // 强制退出登录
            StpUtil.logout(UserTypeEnum.ADMIN_BACKEND_USER.getValue() + StringConst.COLON + backendUserId);
        }
        return ResponseDTO.ok();
    }


    /**
     * 更新密码
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResponseDTO<String> updatePassword(RequestUser requestUser, BackendUserUpdatePasswordForm updatePasswordForm) {
        Long backendUserId = updatePasswordForm.getId();
        BackendUserEntity backendUserEntity = backendUserDao.selectById(backendUserId);
        if (backendUserEntity == null) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }

        // 校验原始密码
        if (!SecurityPasswordService.matchesPwd(updatePasswordForm.getOldPassword(), backendUserEntity.getPassword())) {
            return ResponseDTO.userErrorParam("原密码有误，请重新输入");
        }

        // 新旧密码相同
        if (Objects.equals(updatePasswordForm.getOldPassword(), updatePasswordForm.getNewPassword())) {
            return ResponseDTO.userErrorParam("新密码与原始密码相同，请重新输入");
        }

        // 校验密码复杂度
        ResponseDTO<String> validatePassComplexity = securityPasswordService.validatePasswordComplexity(updatePasswordForm.getNewPassword());
        if (!validatePassComplexity.getOk()) {
            return validatePassComplexity;
        }

        // 根据三级等保规则，校验密码是否重复
        ResponseDTO<String> passwordRepeatTimes = securityPasswordService.validatePasswordRepeatTimes(requestUser, updatePasswordForm.getNewPassword());
        if (!passwordRepeatTimes.getOk()) {
            return ResponseDTO.error(passwordRepeatTimes);
        }

        // 更新密码
        String newEncryptPassword = SecurityPasswordService.getEncryptPwd(updatePasswordForm.getNewPassword());
        BackendUserEntity updateEntity = new BackendUserEntity();
        updateEntity.setId(backendUserId);
        updateEntity.setPassword(newEncryptPassword);
        backendUserDao.updateById(updateEntity);

        // 保存修改密码密码记录
        securityPasswordService.saveUserChangePasswordLog(requestUser, newEncryptPassword, backendUserEntity.getPassword());

        return ResponseDTO.ok();
    }

    /**
     * 重置密码
     */
    public ResponseDTO<String> resetPassword(Long backendUserId) {
        BackendUserEntity backendUserEntity = backendUserDao.selectById(backendUserId);
        if (backendUserEntity == null) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }

        String password = securityPasswordService.randomPassword();

        backendUserDao.updatePassword(backendUserId, SecurityPasswordService.getEncryptPwd(password));
        return ResponseDTO.ok(password);
    }


    /**
     * 查询全部后台用户
     */
    public ResponseDTO<List<BackendUserVO>> queryAllBackendUser(Boolean disabledFlag) {
        List<BackendUserVO> backendUserList = backendUserDao.selectBackendUserByDisabledAndDeleted(disabledFlag, Boolean.FALSE);
        return ResponseDTO.ok(backendUserList);
    }

    /**
     * 根据登录名获取后台用户
     */
    public BackendUserEntity getByUsername(String loginName) {
        return backendUserDao.getByUsername(loginName, false);
    }

}