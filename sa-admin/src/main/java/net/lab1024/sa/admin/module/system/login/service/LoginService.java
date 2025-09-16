package net.lab1024.sa.admin.module.system.login.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.servlet.JakartaServletUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.service.BackendUserService;
import net.lab1024.sa.admin.module.system.login.domain.LoginForm;
import net.lab1024.sa.admin.module.system.login.domain.LoginResultVO;
import net.lab1024.sa.admin.module.system.login.domain.RequestBackendUser;
import net.lab1024.sa.admin.module.system.login.manager.LoginManager;
import net.lab1024.sa.admin.module.system.menu.domain.vo.MenuVO;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import net.lab1024.sa.admin.module.system.role.service.RoleBackendUserService;
import net.lab1024.sa.admin.module.system.role.service.RoleMenuService;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.constant.RequestHeaderConst;
import net.lab1024.sa.base.common.constant.StringConst;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.UserPermission;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartIpUtil;
import net.lab1024.sa.base.common.util.SmartStringUtil;
import net.lab1024.sa.base.constant.LoginDeviceEnum;
import net.lab1024.sa.base.constant.RedisKeyConst;
import net.lab1024.sa.base.module.support.apiencrypt.service.ApiEncryptService;
import net.lab1024.sa.base.module.support.captcha.CaptchaService;
import net.lab1024.sa.base.module.support.captcha.domain.CaptchaVO;
import net.lab1024.sa.base.module.support.config.ConfigKeyEnum;
import net.lab1024.sa.base.module.support.config.ConfigService;
import net.lab1024.sa.base.module.support.loginlog.LoginLogResultEnum;
import net.lab1024.sa.base.module.support.loginlog.LoginLogService;
import net.lab1024.sa.base.module.support.loginlog.domain.LoginLogEntity;
import net.lab1024.sa.base.module.support.loginlog.domain.LoginLogVO;
import net.lab1024.sa.base.module.support.mail.MailService;
import net.lab1024.sa.base.module.support.redis.RedisService;
import net.lab1024.sa.base.module.support.securityprotect.domain.LoginFailEntity;
import net.lab1024.sa.base.module.support.securityprotect.service.Level3ProtectConfigService;
import net.lab1024.sa.base.module.support.securityprotect.service.SecurityLoginService;
import net.lab1024.sa.base.module.support.securityprotect.service.SecurityPasswordService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录
 *
 * @Author 1024创新实验室: 卓大
 * @Date 2025-05-03 22:56:34
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Service
public class LoginService implements StpInterface {

    /**
     * 万能密码的 sa token loginId 前缀
     */
    private static final String SUPER_PASSWORD_LOGIN_ID_PREFIX = "S";

    @Resource
    private BackendUserService backendUserService;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private ConfigService configService;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private RoleBackendUserService roleBackendUserService;

    @Resource
    private RoleMenuService roleMenuService;

    @Resource
    private SecurityLoginService securityLoginService;

    @Resource
    private SecurityPasswordService protectPasswordService;

    @Resource
    private ApiEncryptService apiEncryptService;

    @Resource
    private Level3ProtectConfigService level3ProtectConfigService;

    @Resource
    private MailService mailService;

    @Resource
    private RedisService redisService;

    @Resource
    private LoginManager loginManager;

    /**
     * 获取验证码
     */
    public ResponseDTO<CaptchaVO> getCaptcha() {
        return ResponseDTO.ok(captchaService.generateCaptcha());
    }

    /**
     * 后台用户登录
     *
     * @return 返回用户登录信息
     */
    public ResponseDTO<LoginResultVO> login(LoginForm loginForm, String ip, String userAgent) {
        LoginDeviceEnum loginDeviceEnum = SmartEnumUtil.getEnumByValue(loginForm.getLoginDevice(), LoginDeviceEnum.class);
        if (loginDeviceEnum == null) {
            return ResponseDTO.userErrorParam("登录设备暂不支持！");
        }

        // 校验 图形验证码
        ResponseDTO<String> checkCaptcha = captchaService.checkCaptcha(loginForm);
        if (!checkCaptcha.getOk()) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, checkCaptcha.getMsg());
        }

        // 验证登录名
        BackendUserEntity backendUserEntity = backendUserService.getByUsername(loginForm.getUsername());
        if (null == backendUserEntity) {
            return ResponseDTO.userErrorParam("登录名或密码错误！");
        }

        // 验证账号状态
        if (backendUserEntity.getDeletedFlag()) {
            saveLoginLog(backendUserEntity, ip, userAgent, "账号已删除", LoginLogResultEnum.LOGIN_FAIL, loginDeviceEnum);
            return ResponseDTO.userErrorParam("您的账号已被删除,请联系工作人员！");
        }
        // 注意：BackendUserEntity没有disabledFlag字段，跳过此检查

        // 解密前端加密的密码
        String requestPassword = apiEncryptService.decrypt(loginForm.getPassword());

        // 验证密码 是否为万能密码
        String superPassword = configService.getConfigValue(ConfigKeyEnum.SUPER_PASSWORD);
        boolean superPasswordFlag = superPassword.equals(requestPassword);

        // 校验双因子登录
        ResponseDTO<String> validateEmailCode = validateEmailCode(loginForm, backendUserEntity, superPasswordFlag);
        if (!validateEmailCode.getOk()) {
            return ResponseDTO.error(validateEmailCode);
        }

        // 万能密码特殊操作
        if (superPasswordFlag) {

            // 对于万能密码：受限制sa token 要求loginId唯一，万能密码只能插入一段uuid
            String saTokenLoginId = SUPER_PASSWORD_LOGIN_ID_PREFIX + StringConst.COLON + UUID.randomUUID().toString().replace("-", "") + StringConst.COLON + backendUserEntity.getId();
            // 万能密码登录只能登录30分钟
            StpUtil.login(saTokenLoginId, 1800);

        } else {

            // 按照等保登录要求，进行登录失败次数校验
            ResponseDTO<LoginFailEntity> loginFailEntityResponseDTO = securityLoginService.checkLogin(backendUserEntity.getId(), UserTypeEnum.ADMIN_BACKEND_USER);
            if (!loginFailEntityResponseDTO.getOk()) {
                return ResponseDTO.error(loginFailEntityResponseDTO);
            }

            // 密码错误
            if (!SecurityPasswordService.matchesPwd(requestPassword, backendUserEntity.getPassword())) {
                // 记录登录失败
                saveLoginLog(backendUserEntity, ip, userAgent, "密码错误", LoginLogResultEnum.LOGIN_FAIL, loginDeviceEnum);
                // 记录等级保护次数
                String msg = securityLoginService.recordLoginFail(backendUserEntity.getId(), UserTypeEnum.ADMIN_BACKEND_USER, backendUserEntity.getUsername(), loginFailEntityResponseDTO.getData());
                return msg == null ? ResponseDTO.userErrorParam("登录名或密码错误！") : ResponseDTO.error(UserErrorCode.LOGIN_FAIL_WILL_LOCK, msg);
            }

            String saTokenLoginId = UserTypeEnum.ADMIN_BACKEND_USER.getValue() + StringConst.COLON + backendUserEntity.getId();

            // 登录
            StpUtil.login(saTokenLoginId, String.valueOf(loginDeviceEnum.getDesc()));

            // 删除邮箱验证码
            deleteEmailCode(backendUserEntity.getId());
        }

        // 清除登录失败次数
        securityLoginService.removeLoginFail(backendUserEntity.getId(), UserTypeEnum.ADMIN_BACKEND_USER);

        // 获取菜单权限等信息
        loginManager.loadUserPermission(backendUserEntity.getId());
        RequestBackendUser requestBackendUser = loginManager.loadLoginInfo(backendUserEntity);

        // 获取角色权限
        List<RoleVO> roleList = roleBackendUserService.getRoleIdList(requestBackendUser.getUserId());
        // 注意：RequestBackendUser没有administratorFlag字段，使用默认值false
        List<MenuVO> menuAndPointsList = roleMenuService.getMenuList(roleList.stream().map(RoleVO::getRoleId).collect(Collectors.toList()), false);
        UserPermission userPermission = new UserPermission();
        userPermission.setPermissionList(new ArrayList<>());
        userPermission.setRoleList(new ArrayList<>());

        // 缓存用户权限
        StpUtil.getSession().set("permission", userPermission);

        // 返回登录结果
        LoginResultVO loginResultVO = new LoginResultVO();
        loginResultVO.setToken(StpUtil.getTokenValue());
        loginResultVO.setId(requestBackendUser.getId());
        loginResultVO.setUsername(requestBackendUser.getUserName());
        loginResultVO.setUserType(requestBackendUser.getUserType());
        loginResultVO.setDeletedFlag(requestBackendUser.getDeletedFlag());
        loginResultVO.setIp(requestBackendUser.getIp());
        loginResultVO.setUserAgent(requestBackendUser.getUserAgent());

        // 上次登录信息
        LoginLogVO loginLogVO = loginLogService.queryLastByUserId(requestBackendUser.getUserId(), UserTypeEnum.ADMIN_BACKEND_USER, LoginLogResultEnum.LOGIN_SUCCESS);
        if (loginLogVO != null) {
            loginResultVO.setLastLoginIp(loginLogVO.getLoginIp());
            loginResultVO.setLastLoginIpRegion(loginLogVO.getLoginIpRegion());
            loginResultVO.setLastLoginTime(loginLogVO.getCreateTime());
            loginResultVO.setLastLoginUserAgent(loginLogVO.getUserAgent());
        }

        // 是否需要强制修改密码
        boolean needChangePasswordFlag = protectPasswordService.checkNeedChangePassword(requestBackendUser.getUserType().getValue(), requestBackendUser.getUserId());
        loginResultVO.setNeedUpdatePwdFlag(needChangePasswordFlag);

        // 万能密码登录，则不需要设置强制修改密码
        String loginIdByToken = (String) StpUtil.getLoginIdByToken(loginResultVO.getToken());
        if (loginIdByToken != null && loginIdByToken.startsWith(SUPER_PASSWORD_LOGIN_ID_PREFIX)) {
            loginResultVO.setNeedUpdatePwdFlag(false);
        }

        return ResponseDTO.ok(loginResultVO);
    }


    /**
     * 获取登录结果信息
     */
    public LoginResultVO getLoginResult(RequestBackendUser requestBackendUser, String token) {

        // 基础信息
        LoginResultVO loginResultVO = new LoginResultVO();
        loginResultVO.setToken(token);
        loginResultVO.setId(requestBackendUser.getId());
        loginResultVO.setUsername(requestBackendUser.getUserName());
        loginResultVO.setUserType(requestBackendUser.getUserType());
        loginResultVO.setDeletedFlag(requestBackendUser.getDeletedFlag());
        loginResultVO.setIp(requestBackendUser.getIp());
        loginResultVO.setUserAgent(requestBackendUser.getUserAgent());

        // 获取角色权限
        List<RoleVO> roleList = roleBackendUserService.getRoleIdList(requestBackendUser.getUserId());
        if (CollectionUtils.isNotEmpty(roleList)) {
            // 注意：LoginResultVO没有setRoleList方法，暂时注释掉
            // loginResultVO.setRoleList(roleList);
            // 获取菜单权限
            List<Long> roleIdList = roleList.stream().map(RoleVO::getRoleId).collect(Collectors.toList());
            // 注意：RequestBackendUser没有administratorFlag字段，使用默认值false
            List<MenuVO> menuAndPointsList = roleMenuService.getMenuList(roleIdList, false);
            loginResultVO.setMenuList(menuAndPointsList);
        }

        // 上次登录信息
        LoginLogVO loginLogVO = loginLogService.queryLastByUserId(requestBackendUser.getUserId(), UserTypeEnum.ADMIN_BACKEND_USER, LoginLogResultEnum.LOGIN_SUCCESS);
        if (loginLogVO != null) {
            loginResultVO.setLastLoginIp(loginLogVO.getLoginIp());
            loginResultVO.setLastLoginIpRegion(loginLogVO.getLoginIpRegion());
            loginResultVO.setLastLoginTime(loginLogVO.getCreateTime());
            loginResultVO.setLastLoginUserAgent(loginLogVO.getUserAgent());
        }

        return loginResultVO;
    }


    /**
     * 根据登录token 获取员请求工信息
     */
    public RequestBackendUser getLoginBackendUser(String loginId, HttpServletRequest request) {
        if (loginId == null) {
            return null;
        }

        Long requestBackendUserId = getBackendUserIdByLoginId(loginId);
        if (requestBackendUserId == null) {
            return null;
        }

        RequestBackendUser requestBackendUser = loginManager.getRequestBackendUser(requestBackendUserId);

        // 更新请求ip和user agent
        requestBackendUser.setUserAgent(JakartaServletUtil.getHeaderIgnoreCase(request, RequestHeaderConst.USER_AGENT));
        requestBackendUser.setIp(JakartaServletUtil.getClientIP(request));

        return requestBackendUser;
    }

    /**
     * 根据 loginId 获取 后台用户id
     */
    Long getBackendUserIdByLoginId(String loginId) {

        if (loginId == null) {
            return null;
        }

        try {
            // 如果是 万能密码 登录的用户
            String employeeIdStr = null;
            if (loginId.startsWith(SUPER_PASSWORD_LOGIN_ID_PREFIX)) {
                employeeIdStr = loginId.split(StringConst.COLON)[2];
            } else {
                employeeIdStr = loginId.substring(2);
            }

            return Long.parseLong(employeeIdStr);
        } catch (Exception e) {
            log.error("loginId parse error , loginId : {}", loginId, e);
            return null;
        }
    }


    /**
     * 退出登录
     */
    public ResponseDTO<String> logout(RequestUser requestUser) {

        // sa token 登出
        StpUtil.logout();

        // 清除用户登录信息缓存和权限信息
        this.clearLoginBackendUserCache(requestUser.getUserId());

        //保存登出日志
        LoginLogEntity loginEntity = LoginLogEntity.builder()
                .userId(requestUser.getUserId())
                .userType(requestUser.getUserType().getValue())
                .userName(requestUser.getUserName())
                .userAgent(requestUser.getUserAgent())
                .loginIp(requestUser.getIp())
                .loginIpRegion(SmartIpUtil.getRegion(requestUser.getIp()))
                .loginResult(LoginLogResultEnum.LOGIN_OUT.getValue())
                .createTime(LocalDateTime.now())
                .build();
        loginLogService.log(loginEntity);

        return ResponseDTO.ok();
    }

    /**
     * 保存登录日志
     */
    private void saveLoginLog(BackendUserEntity backendUserEntity, String ip, String userAgent, String remark, LoginLogResultEnum result, LoginDeviceEnum loginDeviceEnum) {
        LoginLogEntity loginEntity = LoginLogEntity.builder()
                .userId(backendUserEntity.getId())
                .userType(UserTypeEnum.ADMIN_BACKEND_USER.getValue())
                .userName(backendUserEntity.getUsername())
                .userAgent(userAgent)
                .loginIp(ip)
                .loginIpRegion(SmartIpUtil.getRegion(ip))
                .remark(remark)
                .loginDevice(loginDeviceEnum.getDesc())
                .loginResult(result.getValue())
                .createTime(LocalDateTime.now())
                .build();
        loginLogService.log(loginEntity);
    }


    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }
        
        String loginIdStr = (String) loginId;
        Long employeeId = this.getBackendUserIdByLoginId(loginIdStr);
        if (employeeId == null) {
            return Collections.emptyList();
        }

        UserPermission userPermission = loginManager.getUserPermission(employeeId);
        if (userPermission == null) {
            return Collections.emptyList();
        }
        
        return userPermission.getPermissionList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }
        
        String loginIdStr = (String) loginId;
        Long employeeId = this.getBackendUserIdByLoginId(loginIdStr);
        if (employeeId == null) {
            return Collections.emptyList();
        }

        UserPermission userPermission = loginManager.getUserPermission(employeeId);
        if (userPermission == null) {
            return Collections.emptyList();
        }
        
        return userPermission.getRoleList();
    }


    /**
     * 发送 邮箱 验证码
     */
    public ResponseDTO<String> sendEmailCode(String loginName) {

        // 开启双因子登录
        if (!level3ProtectConfigService.isTwoFactorLoginEnabled()) {
            return ResponseDTO.userErrorParam("无需使用邮箱验证码");
        }

        // 验证登录名
        BackendUserEntity backendUserEntity = backendUserService.getByUsername(loginName);
        if (null == backendUserEntity) {
            return ResponseDTO.ok();
        }

        // 验证账号状态
        if (backendUserEntity.getDeletedFlag()) {
            return ResponseDTO.userErrorParam("您的账号已被删除,请联系工作人员！");
        }

        // 注意：BackendUserEntity没有disabledFlag字段，跳过此检查

        // 注意：BackendUserEntity没有email字段，跳过邮箱相关处理
        return ResponseDTO.userErrorParam("该账户不支持邮箱验证");
    }


    /**
     * 校验邮箱验证码
     */
    private ResponseDTO<String> validateEmailCode(LoginForm loginForm, BackendUserEntity backendUserEntity, boolean superPasswordFlag) {
        // 开启双因子登录 并且 不是万能密码
        if (level3ProtectConfigService.isTwoFactorLoginEnabled() && !superPasswordFlag) {
            if (SmartStringUtil.isEmpty(loginForm.getEmailCode())) {
                return ResponseDTO.userErrorParam("请输入邮箱验证码");
            }

            // 校验验证码
            String redisVerificationCodeKey = redisService.generateRedisKey(RedisKeyConst.Support.LOGIN_VERIFICATION_CODE, UserTypeEnum.ADMIN_BACKEND_USER.getValue() + RedisKeyConst.SEPARATOR + backendUserEntity.getId());
            String emailCode = redisService.get(redisVerificationCodeKey);
            if (SmartStringUtil.isEmpty(emailCode)) {
                return ResponseDTO.userErrorParam("邮箱验证码已过期");
            }
            
            if (!emailCode.equalsIgnoreCase(loginForm.getEmailCode())) {
                return ResponseDTO.userErrorParam("邮箱验证码错误");
            }
        }

        return ResponseDTO.ok();
    }

    /**
     * 移除邮箱验证码
     */
    private void deleteEmailCode(Long employeeId) {
        String redisVerificationCodeKey = redisService.generateRedisKey(RedisKeyConst.Support.LOGIN_VERIFICATION_CODE, UserTypeEnum.ADMIN_BACKEND_USER.getValue() + RedisKeyConst.SEPARATOR + employeeId);
        redisService.delete(redisVerificationCodeKey);
    }

    public void clearLoginBackendUserCache(Long employeeId) {
        loginManager.clearUserPermission(employeeId);
        loginManager.clearUserLoginInfo(employeeId);
    }
}
