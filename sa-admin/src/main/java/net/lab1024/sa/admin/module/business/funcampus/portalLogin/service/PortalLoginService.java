package net.lab1024.sa.admin.module.business.funcampus.portalLogin.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.manager.PortalLoginManager;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.manager.PortalOrganizerUserManager;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.system.login.domain.LoginForm;
import net.lab1024.sa.admin.module.system.login.domain.LoginResultVO;
import net.lab1024.sa.admin.module.system.role.service.RoleMenuService;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.constant.RequestHeaderConst;
import net.lab1024.sa.base.common.constant.StringConst;
import net.lab1024.sa.base.common.domain.RequestUser;
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
import net.lab1024.sa.base.module.support.config.ConfigService;
import net.lab1024.sa.base.module.support.loginlog.domain.LoginLogEntity;
import net.lab1024.sa.base.module.support.loginlog.domain.LoginLogVO;
import net.lab1024.sa.base.module.support.mail.MailService;
import net.lab1024.sa.base.module.support.redis.RedisService;
import net.lab1024.sa.base.module.support.securityprotect.domain.LoginFailEntity;
import net.lab1024.sa.base.module.support.securityprotect.service.Level3ProtectConfigService;
import net.lab1024.sa.base.module.support.securityprotect.service.SecurityLoginService;
import net.lab1024.sa.base.module.support.securityprotect.service.SecurityPasswordService;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.service.PortalUserService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.loginlog.LoginLogResultEnum;
import net.lab1024.sa.base.module.support.loginlog.LoginLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.dev33.satoken.SaManager.log;

/**
* author:akkkka114514
* create at 2025-10-10 10:21
*/
@Service
public class PortalLoginService implements StpInterface {

    /**
     * 万能密码的 sa token loginId 前缀
     */
    private static final String SUPER_PASSWORD_LOGIN_ID_PREFIX = "S";

    @Resource
    private PortalUserService portalUserService;

    @Resource
    private PortalUserManager portalUserManager;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private ConfigService configService;

    @Resource
    private LoginLogService loginLogService;

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
    private PortalLoginManager portalLoginManager;

    @Resource
    private PortalOrganizerUserManager portalOrganizerUserManager;

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
        LambdaQueryWrapper<PortalUserEntity> queryWrapper = new LambdaQueryWrapper<PortalUserEntity>()
                .eq(PortalUserEntity::getUsername, loginForm.getUsername());
        PortalUserEntity portalUserEntity = portalUserManager.getOne(queryWrapper);
        if (null == portalUserEntity) {
            return ResponseDTO.userErrorParam("登录名或密码错误！");
        }

        // 验证账号状态
        if (portalUserEntity.getDeletedFlag()) {
            saveLoginLog(portalUserEntity, ip, userAgent, "账号已删除", LoginLogResultEnum.LOGIN_FAIL, loginDeviceEnum);
            return ResponseDTO.userErrorParam("您的账号已被删除,请联系工作人员！");
        }

        // 解密前端加密的密码
        String requestPassword = apiEncryptService.decrypt(loginForm.getPassword());

        // 按照等保登录要求，进行登录失败次数校验
        ResponseDTO<LoginFailEntity> loginFailEntityResponseDTO = securityLoginService.checkLogin(portalUserEntity.getId(), UserTypeEnum.ADMIN_BACKEND_USER);
        if (!loginFailEntityResponseDTO.getOk()) {
            return ResponseDTO.error(loginFailEntityResponseDTO);
        }

        // 密码错误
        if (!SecurityPasswordService.matchesPwd(requestPassword, portalUserEntity.getPassword())) {
            // 记录登录失败
            saveLoginLog(portalUserEntity, ip, userAgent, "密码错误", LoginLogResultEnum.LOGIN_FAIL, loginDeviceEnum);
            // 记录等级保护次数
            String msg = securityLoginService.recordLoginFail(portalUserEntity.getId(), UserTypeEnum.ADMIN_BACKEND_USER, portalUserEntity.getUsername(), loginFailEntityResponseDTO.getData());
            return msg == null ? ResponseDTO.userErrorParam("登录名或密码错误！") : ResponseDTO.error(UserErrorCode.LOGIN_FAIL_WILL_LOCK, msg);
        }

        String saTokenLoginId = UserTypeEnum.ADMIN_BACKEND_USER.getValue() + StringConst.COLON + portalUserEntity.getId();

        // 登录
        StpUtil.login(saTokenLoginId, String.valueOf(loginDeviceEnum.getDesc()));

        // 删除邮箱验证码
        deleteEmailCode(portalUserEntity.getId());

        // 清除登录失败次数
        securityLoginService.removeLoginFail(portalUserEntity.getId(), UserTypeEnum.ADMIN_BACKEND_USER);

        // 获取菜单权限等信息
        portalLoginManager.loadUserPermission(portalUserEntity.getId());
        RequestPortalUser requestPortalUser = portalLoginManager.loadLoginInfo(portalUserEntity);

        UserPermission userPermission = new UserPermission();
        userPermission.setPermissionList(new ArrayList<>());
        userPermission.setRoleList(new ArrayList<>());

        // 缓存用户权限
        StpUtil.getSession().set("permission", userPermission);

        // 返回登录结果
        LoginResultVO loginResultVO = new LoginResultVO();
        loginResultVO.setToken(StpUtil.getTokenValue());
        loginResultVO.setId(requestPortalUser.getId());
        loginResultVO.setUsername(requestPortalUser.getUserName());
        loginResultVO.setUserType(requestPortalUser.getUserType());
        loginResultVO.setDeletedFlag(requestPortalUser.getDeletedFlag());
        loginResultVO.setIp(requestPortalUser.getIp());
        loginResultVO.setUserAgent(requestPortalUser.getUserAgent());

        // 上次登录信息
        LoginLogVO loginLogVO = loginLogService.queryLastByUserId(requestPortalUser.getUserId(), UserTypeEnum.ADMIN_BACKEND_USER, LoginLogResultEnum.LOGIN_SUCCESS);
        if (loginLogVO != null) {
            loginResultVO.setLastLoginIp(loginLogVO.getLoginIp());
            loginResultVO.setLastLoginIpRegion(loginLogVO.getLoginIpRegion());
            loginResultVO.setLastLoginTime(loginLogVO.getCreateTime());
            loginResultVO.setLastLoginUserAgent(loginLogVO.getUserAgent());
        }

        // 是否需要强制修改密码
        boolean needChangePasswordFlag = protectPasswordService.checkNeedChangePassword(requestPortalUser.getUserType().getValue(), requestPortalUser.getUserId());
        loginResultVO.setNeedUpdatePwdFlag(needChangePasswordFlag);

        String loginIdByToken = (String) StpUtil.getLoginIdByToken(loginResultVO.getToken());
        if (loginIdByToken != null && loginIdByToken.startsWith(SUPER_PASSWORD_LOGIN_ID_PREFIX)) {
            loginResultVO.setNeedUpdatePwdFlag(false);
        }

        return ResponseDTO.ok(loginResultVO);
    }


    /**
     * 获取登录结果信息
     */
    public LoginResultVO getLoginResult(RequestPortalUser requestPortalUser, String token) {

        // 基础信息
        LoginResultVO loginResultVO = new LoginResultVO();
        loginResultVO.setToken(token);
        loginResultVO.setId(requestPortalUser.getId());
        loginResultVO.setUsername(requestPortalUser.getUserName());
        loginResultVO.setUserType(requestPortalUser.getUserType());
        loginResultVO.setDeletedFlag(requestPortalUser.getDeletedFlag());
        loginResultVO.setIp(requestPortalUser.getIp());
        loginResultVO.setUserAgent(requestPortalUser.getUserAgent());

        // 上次登录信息
        LoginLogVO loginLogVO = loginLogService.queryLastByUserId(requestPortalUser.getUserId(), UserTypeEnum.ADMIN_BACKEND_USER, LoginLogResultEnum.LOGIN_SUCCESS);
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
    public RequestPortalUser getLoginPortalUser(String loginId, HttpServletRequest request) {
        if (loginId == null) {
            return null;
        }

        Long requestPortalUserId = getPortalUserIdByLoginId(loginId);
        if (requestPortalUserId == null) {
            return null;
        }

        RequestPortalUser requestPortalUser = portalLoginManager.getRequestPortalUser(requestPortalUserId);

        // 更新请求ip和user agent
        requestPortalUser.setUserAgent(JakartaServletUtil.getHeaderIgnoreCase(request, RequestHeaderConst.USER_AGENT));
        requestPortalUser.setIp(JakartaServletUtil.getClientIP(request));

        return requestPortalUser;
    }

    /**
     * 根据 loginId 获取 后台用户id
     */
    Long getPortalUserIdByLoginId(String loginId) {

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
        this.clearLoginPortalUserCache(requestUser.getUserId());

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



    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }

        String loginIdStr = (String) loginId;
        Long userId = this.getPortalUserIdByLoginId(loginIdStr);
        if (userId == null) {
            return Collections.emptyList();
        }

        UserPermission userPermission = portalLoginManager.getUserPermission(userId);
        if (userPermission == null) {
            return Collections.emptyList();
        }

        return userPermission.getPermissionList();
    }

    /**
     * 保存登录日志
     */
    private void saveLoginLog(PortalUserEntity portalUserEntity, String ip, String userAgent, String remark, LoginLogResultEnum result, LoginDeviceEnum loginDeviceEnum) {
        LoginLogEntity loginEntity = LoginLogEntity.builder()
                .userId(portalUserEntity.getId())
                .userType(UserTypeEnum.ADMIN_BACKEND_USER.getValue())
                .userName(portalUserEntity.getUsername())
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
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }

        String loginIdStr = (String) loginId;
        Long employeeId = this.getPortalUserIdByLoginId(loginIdStr);
        if (employeeId == null) {
            return Collections.emptyList();
        }

        UserPermission userPermission = portalLoginManager.getUserPermission(employeeId);
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

        LambdaQueryWrapper<PortalUserEntity> queryWrapper = new LambdaQueryWrapper<PortalUserEntity>()
                .eq(PortalUserEntity::getUsername, loginName)
                .eq(PortalUserEntity::getDeletedFlag, false);
        PortalUserEntity portalUserEntity = portalUserManager.getOne(queryWrapper);
        if (null == portalUserEntity) {
            return ResponseDTO.ok();
        }

        // 验证账号状态
        if (portalUserEntity.getDeletedFlag()) {
            return ResponseDTO.userErrorParam("您的账号已被删除,请联系工作人员！");
        }

        // 注意：PortalUserEntity没有disabledFlag字段，跳过此检查

        // 注意：PortalUserEntity没有email字段，跳过邮箱相关处理
        return ResponseDTO.userErrorParam("该账户不支持邮箱验证");
    }


    /**
     * 校验邮箱验证码
     */
    private ResponseDTO<String> validateEmailCode(LoginForm loginForm, PortalUserEntity portalUserEntity, boolean superPasswordFlag) {
        // 开启双因子登录 并且 不是万能密码
        if (level3ProtectConfigService.isTwoFactorLoginEnabled() && !superPasswordFlag) {
            if (SmartStringUtil.isEmpty(loginForm.getEmailCode())) {
                return ResponseDTO.userErrorParam("请输入邮箱验证码");
            }

            // 校验验证码
            String redisVerificationCodeKey = redisService.generateRedisKey(RedisKeyConst.Support.LOGIN_VERIFICATION_CODE, UserTypeEnum.ADMIN_BACKEND_USER.getValue() + RedisKeyConst.SEPARATOR + portalUserEntity.getId());
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

    public void clearLoginPortalUserCache(Long employeeId) {
        portalLoginManager.clearUserPermission(employeeId);
        portalLoginManager.clearUserLoginInfo(employeeId);
    }
}
