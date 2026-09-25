package com.akkkka.admin.module.business.funcampus.portalLogin.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.PortalLoginResultVO;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalLogin.manager.PortalLoginManager;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.system.login.domain.LoginForm;
import com.akkkka.admin.module.system.login.domain.LoginResultVO;
import com.akkkka.admin.module.system.role.service.RoleMenuService;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.constant.RequestHeaderConst;
import com.akkkka.common.constant.StringConst;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.util.SmartEnumUtil;
import com.akkkka.common.util.SmartIpUtil;
import com.akkkka.common.util.SmartStringUtil;
import com.akkkka.constant.LoginDeviceEnum;
import com.akkkka.constant.RedisKeyConst;
import com.akkkka.module.support.apiencrypt.service.ApiEncryptService;
import com.akkkka.module.support.captcha.CaptchaService;
import com.akkkka.module.support.captcha.domain.CaptchaVO;
import com.akkkka.module.support.config.ConfigService;
import com.akkkka.module.support.loginlog.domain.LoginLogEntity;
import com.akkkka.module.support.loginlog.domain.LoginLogVO;
import com.akkkka.module.support.mail.MailService;
import com.akkkka.module.support.redis.RedisService;
import com.akkkka.module.support.securityprotect.domain.LoginFailEntity;
import com.akkkka.module.support.securityprotect.service.Level3ProtectConfigService;
import com.akkkka.module.support.securityprotect.service.SecurityLoginService;
import com.akkkka.module.support.securityprotect.service.SecurityPasswordService;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserService;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.loginlog.LoginLogResultEnum;
import com.akkkka.module.support.loginlog.LoginLogService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
* author:akkkka114514
* create at 2025-10-10 10:21
*/
@Slf4j
@Service
public class PortalLoginService {

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


    /**
     * 获取验证码
     */
    public ResponseDTO<CaptchaVO> getCaptcha() {
        log.info("PortalLoginService.getCaptcha called");
        ResponseDTO<CaptchaVO> result = ResponseDTO.ok(captchaService.generateCaptcha());
        log.info("PortalLoginService.getCaptcha result: ok={}", result.getOk());
        return result;
    }

    /**
     * 后台用户登录
     *
     * @return 返回用户登录信息
     */
    public ResponseDTO<PortalLoginResultVO> login(LoginForm loginForm, String ip, String userAgent) {
        log.info("PortalLoginService.login called, username={}, ip={}", loginForm.getUsername(), ip);
        LoginDeviceEnum loginDeviceEnum = SmartEnumUtil.getEnumByValue(loginForm.getLoginDevice(), LoginDeviceEnum.class);
        if (loginDeviceEnum == null) {
            log.warn("PortalLoginService.login failed: unsupported login device, device={}", loginForm.getLoginDevice());
            return ResponseDTO.userErrorParam("登录设备暂不支持！");
        }

        // 校验 图形验证码
        ResponseDTO<String> checkCaptcha = captchaService.checkCaptcha(loginForm);
        if (!checkCaptcha.getOk()) {
            log.warn("PortalLoginService.login failed: captcha check failed, msg={}", checkCaptcha.getMsg());
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, checkCaptcha.getMsg());
        }

        // 验证登录名
        LambdaQueryWrapper<PortalUserEntity> queryWrapper = new LambdaQueryWrapper<PortalUserEntity>()
                .eq(PortalUserEntity::getUsername, loginForm.getUsername());
        PortalUserEntity portalUserEntity = portalUserManager.getOne(queryWrapper);
        if (null == portalUserEntity) {
            log.warn("PortalLoginService.login failed: user not found, username={}", loginForm.getUsername());
            return ResponseDTO.userErrorParam("请先注册！");
        }

        // 验证账号状态
        if (portalUserEntity.getDeletedFlag()) {
            log.warn("PortalLoginService.login failed: user deleted, username={}", loginForm.getUsername());
            saveLoginLog(portalUserEntity, ip, userAgent, "账号已删除", LoginLogResultEnum.LOGIN_FAIL, loginDeviceEnum);
            return ResponseDTO.userErrorParam("您的账号已被删除,请联系工作人员！");
        }

        // 解密前端加密的密码
        //String requestPassword = apiEncryptService.decrypt(loginForm.getPassword());
        log.debug("PortalLoginService.login: password decrypted");

        // 按照等保登录要求，进行登录失败次数校验
        ResponseDTO<LoginFailEntity> loginFailEntityResponseDTO = securityLoginService.checkLogin(portalUserEntity.getId(), UserTypeEnum.ADMIN_BACKEND_USER);
        if (!loginFailEntityResponseDTO.getOk()) {
            log.warn("PortalLoginService.login failed: security login check failed, msg={}", loginFailEntityResponseDTO.getMsg());
            return ResponseDTO.error(loginFailEntityResponseDTO);
        }

        // 密码错误
        if (!SecurityPasswordService.matchesPwd(loginForm.getPassword(), portalUserEntity.getPassword())) {
            log.warn("PortalLoginService.login failed: password mismatch, username={}", loginForm.getUsername());
            // 记录登录失败
            saveLoginLog(portalUserEntity, ip, userAgent, "密码错误", LoginLogResultEnum.LOGIN_FAIL, loginDeviceEnum);
            // 记录等级保护次数
            String msg = securityLoginService.recordLoginFail(portalUserEntity.getId(), UserTypeEnum.PORTAL_USER, portalUserEntity.getUsername(), loginFailEntityResponseDTO.getData());
            return msg == null ? ResponseDTO.userErrorParam("登录名或密码错误！") : ResponseDTO.error(UserErrorCode.LOGIN_FAIL_WILL_LOCK, msg);
        }

        String saTokenLoginId = UserTypeEnum.PORTAL_USER.getValue() + StringConst.COLON + portalUserEntity.getId();

        // 登录
        StpUtil.login(saTokenLoginId, String.valueOf(loginDeviceEnum.getDesc()));
        log.info("PortalLoginService.login: user logged in, userId={}", portalUserEntity.getId());

        // 删除邮箱验证码
        deleteEmailCode(portalUserEntity.getId());

        // 清除登录失败次数
        securityLoginService.removeLoginFail(portalUserEntity.getId(), UserTypeEnum.PORTAL_USER);

        RequestPortalUser requestPortalUser = portalLoginManager.loadLoginInfo(portalUserEntity);

        // 返回登录结果
        PortalLoginResultVO loginResultVO = new PortalLoginResultVO();
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
        if (loginIdByToken != null) {
            loginResultVO.setNeedUpdatePwdFlag(false);
        }

        log.info("PortalLoginService.login success, userId={}", portalUserEntity.getId());
        return ResponseDTO.ok(loginResultVO);
    }


    /**
     * 获取登录结果信息
     */
    public LoginResultVO getLoginResult(RequestPortalUser requestPortalUser, String token) {
        log.info("PortalLoginService.getLoginResult called, userId={}, token={}", 
            requestPortalUser != null ? requestPortalUser.getId() : null, token);

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

        log.info("PortalLoginService.getLoginResult completed, userId={}", requestPortalUser.getId());
        return loginResultVO;
    }


    /**
     * 根据登录token 获取员请求工信息
     */
    public RequestPortalUser getLoginPortalUser(String loginId, HttpServletRequest request) {
        log.info("PortalLoginService.getLoginPortalUser called, loginId={}", loginId);
        if (loginId == null) {
            log.warn("PortalLoginService.getLoginPortalUser failed: loginId is null");
            return null;
        }

        Long requestPortalUserId = getPortalUserIdByLoginId(loginId);
        if (requestPortalUserId == null) {
            log.warn("PortalLoginService.getLoginPortalUser failed: unable to get portal user id from loginId");
            return null;
        }

        RequestPortalUser requestPortalUser = portalLoginManager.getRequestPortalUser(requestPortalUserId);

        // 更新请求ip和user agent
        requestPortalUser.setUserAgent(JakartaServletUtil.getHeaderIgnoreCase(request, RequestHeaderConst.USER_AGENT));
        requestPortalUser.setIp(JakartaServletUtil.getClientIP(request));

        log.info("PortalLoginService.getLoginPortalUser completed, userId={}", requestPortalUserId);
        return requestPortalUser;
    }

    /**
     * 根据 loginId 获取 后台用户id
     */
    Long getPortalUserIdByLoginId(String loginId) {
        log.debug("PortalLoginService.getPortalUserIdByLoginId called, loginId={}", loginId);

        if (loginId == null) {
            log.warn("PortalLoginService.getPortalUserIdByLoginId failed: loginId is null");
            return null;
        }

        try {
            String employeeIdStr = loginId.substring(2);

            Long userId = Long.parseLong(employeeIdStr);
            log.debug("PortalLoginService.getPortalUserIdByLoginId completed, userId={}", userId);
            return userId;
        } catch (Exception e) {
            log.error("loginId parse error , loginId : {}", loginId, e);
            return null;
        }
    }


    /**
     * 退出登录
     */
    public ResponseDTO<String> logout(RequestUser requestUser) {
        log.info("PortalLoginService.logout called, userId={}", requestUser != null ? requestUser.getUserId() : null);

        // sa token 登出
        StpUtil.logout();

        // 清除用户登录信息缓存和权限信息
        if (requestUser != null) {
            this.clearLoginPortalUserCache(requestUser.getUserId());
        }

        //保存登出日志
        if (requestUser != null) {
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
        }

        log.info("PortalLoginService.logout completed");
        return ResponseDTO.ok();
    }

    /**
     * 保存登录日志
     */
    private void saveLoginLog(PortalUserEntity portalUserEntity, String ip, String userAgent, String remark, LoginLogResultEnum result, LoginDeviceEnum loginDeviceEnum) {
        log.debug("PortalLoginService.saveLoginLog called, userId={}, ip={}, remark={}", portalUserEntity.getId(), ip, remark);
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

    /**
     * 发送 邮箱 验证码
     */
    public ResponseDTO<String> sendEmailCode(String loginName) {
        log.info("PortalLoginService.sendEmailCode called, loginName={}", loginName);

        // 开启双因子登录
        if (!level3ProtectConfigService.isTwoFactorLoginEnabled()) {
            log.warn("PortalLoginService.sendEmailCode failed: two factor login not enabled");
            return ResponseDTO.userErrorParam("无需使用邮箱验证码");
        }

        // 验证登录名

        LambdaQueryWrapper<PortalUserEntity> queryWrapper = new LambdaQueryWrapper<PortalUserEntity>()
                .eq(PortalUserEntity::getUsername, loginName)
                .eq(PortalUserEntity::getDeletedFlag, false);
        PortalUserEntity portalUserEntity = portalUserManager.getOne(queryWrapper);
        if (null == portalUserEntity) {
            log.info("PortalLoginService.sendEmailCode: user not found, returning ok");
            return ResponseDTO.ok();
        }

        // 验证账号状态
        if (portalUserEntity.getDeletedFlag()) {
            log.warn("PortalLoginService.sendEmailCode failed: user deleted, userId={}", portalUserEntity.getId());
            return ResponseDTO.userErrorParam("您的账号已被删除,请联系工作人员！");
        }

        // 注意：PortalUserEntity没有disabledFlag字段，跳过此检查

        // 注意：PortalUserEntity没有email字段，跳过邮箱相关处理
        log.warn("PortalLoginService.sendEmailCode failed: email not supported for PortalUserEntity");
        return ResponseDTO.userErrorParam("该账户不支持邮箱验证");
    }


    /**
     * 校验邮箱验证码
     */
    private ResponseDTO<String> validateEmailCode(LoginForm loginForm, PortalUserEntity portalUserEntity, boolean superPasswordFlag) {
        log.debug("PortalLoginService.validateEmailCode called, userId={}, superPasswordFlag={}", 
            portalUserEntity != null ? portalUserEntity.getId() : null, superPasswordFlag);
        
        // 开启双因子登录 并且 不是万能密码
        if (level3ProtectConfigService.isTwoFactorLoginEnabled() && !superPasswordFlag) {
            if (SmartStringUtil.isEmpty(loginForm.getEmailCode())) {
                log.warn("PortalLoginService.validateEmailCode failed: email code is empty");
                return ResponseDTO.userErrorParam("请输入邮箱验证码");
            }

            // 校验验证码
            String redisVerificationCodeKey = redisService.generateRedisKey(RedisKeyConst.Support.LOGIN_VERIFICATION_CODE, UserTypeEnum.ADMIN_BACKEND_USER.getValue() + RedisKeyConst.SEPARATOR + portalUserEntity.getId());
            String emailCode = redisService.get(redisVerificationCodeKey);
            if (SmartStringUtil.isEmpty(emailCode)) {
                log.warn("PortalLoginService.validateEmailCode failed: email code expired or not found");
                return ResponseDTO.userErrorParam("邮箱验证码已过期");
            }

            if (!emailCode.equalsIgnoreCase(loginForm.getEmailCode())) {
                log.warn("PortalLoginService.validateEmailCode failed: email code mismatch");
                return ResponseDTO.userErrorParam("邮箱验证码错误");
            }
        }

        log.debug("PortalLoginService.validateEmailCode completed successfully");
        return ResponseDTO.ok();
    }

    /**
     * 移除邮箱验证码
     */
    private void deleteEmailCode(Long employeeId) {
        log.debug("PortalLoginService.deleteEmailCode called, employeeId={}", employeeId);
        String redisVerificationCodeKey = redisService.generateRedisKey(RedisKeyConst.Support.LOGIN_VERIFICATION_CODE, UserTypeEnum.ADMIN_BACKEND_USER.getValue() + RedisKeyConst.SEPARATOR + employeeId);
        redisService.delete(redisVerificationCodeKey);
        log.debug("PortalLoginService.deleteEmailCode completed");
    }

    public void clearLoginPortalUserCache(Long employeeId) {
        log.debug("PortalLoginService.clearLoginPortalUserCache called, employeeId={}", employeeId);
        portalLoginManager.clearUserPermission(employeeId);
        portalLoginManager.clearUserLoginInfo(employeeId);
        log.debug("PortalLoginService.clearLoginPortalUserCache completed");
    }
}