package com.akkkka.admin.module.business.funcampus.portalLogin.service;

import cn.dev33.satoken.stp.StpUtil;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.PortalLoginResultVO;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalLogin.manager.PortalLoginManager;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserService;
import com.akkkka.admin.module.system.login.domain.LoginForm;
import com.akkkka.admin.module.system.login.domain.LoginResultVO;
import com.akkkka.admin.module.system.role.service.RoleMenuService;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.module.support.apiencrypt.service.ApiEncryptService;
import com.akkkka.module.support.captcha.CaptchaService;
import com.akkkka.module.support.config.ConfigService;
import com.akkkka.module.support.loginlog.LoginLogResultEnum;
import com.akkkka.module.support.loginlog.LoginLogService;
import com.akkkka.module.support.loginlog.domain.LoginLogEntity;
import com.akkkka.module.support.loginlog.domain.LoginLogVO;
import com.akkkka.module.support.mail.MailService;
import com.akkkka.module.support.redis.RedisService;
import com.akkkka.module.support.securityprotect.service.Level3ProtectConfigService;
import com.akkkka.module.support.securityprotect.service.SecurityLoginService;
import com.akkkka.module.support.securityprotect.service.SecurityPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 前台登录 Service 单元测试
 * <p>
 * 覆盖：login 全部分支（设备/验证码/未注册/已删除/安全校验/密码错误/锁定提示/成功）、
 * logout（有无请求用户）、loginId 解析、sendEmailCode、getLoginResult 上次登录信息填充
 *
 * @Author akkkka114514
 * @Date 2026-09-03
 */
@ExtendWith(MockitoExtension.class)
public class PortalLoginServiceTest {

    @Mock
    private PortalUserService portalUserService;
    @Mock
    private PortalUserManager portalUserManager;
    @Mock
    private CaptchaService captchaService;
    @Mock
    private ConfigService configService;
    @Mock
    private LoginLogService loginLogService;
    @Mock
    private RoleMenuService roleMenuService;
    @Mock
    private SecurityLoginService securityLoginService;
    @Mock
    private SecurityPasswordService protectPasswordService;
    @Mock
    private ApiEncryptService apiEncryptService;
    @Mock
    private Level3ProtectConfigService level3ProtectConfigService;
    @Mock
    private MailService mailService;
    @Mock
    private RedisService redisService;
    @Mock
    private PortalLoginManager portalLoginManager;

    @InjectMocks
    private PortalLoginService service;

    private LoginForm loginForm(Integer loginDevice) {
        LoginForm form = new LoginForm();
        form.setUsername("zhangsan");
        form.setPassword("123456");
        form.setLoginDevice(loginDevice);
        form.setCaptchaUuid("uuid-1");
        form.setCaptchaCode("a1b2");
        return form;
    }

    private PortalUserEntity portalUser(Long id, String username, boolean deletedFlag) {
        PortalUserEntity entity = new PortalUserEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setPassword("enc-pwd");
        entity.setDeletedFlag(deletedFlag);
        return entity;
    }

    private RequestPortalUser requestPortalUser() {
        RequestPortalUser requestPortalUser = new RequestPortalUser();
        requestPortalUser.setId(100L);
        requestPortalUser.setUsername("zhangsan");
        requestPortalUser.setUserType(UserTypeEnum.PORTAL_USER);
        requestPortalUser.setDeletedFlag(false);
        requestPortalUser.setIp("1.2.3.4");
        requestPortalUser.setUserAgent("Mozilla/5.0");
        return requestPortalUser;
    }

    // ---------------------------------- login：前置校验分支 ----------------------------------

    @Test
    void login_whenDeviceUnsupported_returnParamError() {
        ResponseDTO<PortalLoginResultVO> result = service.login(loginForm(99), "", "");
        assertFalse(result.getOk());
        assertTrue(result.getMsg().contains("登录设备暂不支持"));
        verify(captchaService, never()).checkCaptcha(any());
        verify(portalUserManager, never()).getOne(any());
    }

    @Test
    void login_whenCaptchaFailed_returnCaptchaErrorMsg() {
        when(captchaService.checkCaptcha(any())).thenReturn(
                ResponseDTO.error(UserErrorCode.PARAM_ERROR, "验证码错误或已过期"));

        ResponseDTO<PortalLoginResultVO> result = service.login(loginForm(1), "", "");

        assertFalse(result.getOk());
        assertTrue(result.getMsg().contains("验证码错误"));
        verify(portalUserManager, never()).getOne(any());
    }

    @Test
    void login_whenUserNotRegistered_returnPleaseRegister() {
        when(captchaService.checkCaptcha(any())).thenReturn(ResponseDTO.ok());
        when(portalUserManager.getOne(any())).thenReturn(null);

        ResponseDTO<PortalLoginResultVO> result = service.login(loginForm(1), "", "");

        assertFalse(result.getOk());
        assertTrue(result.getMsg().contains("请先注册"));
        verify(loginLogService, never()).log(any());
    }

    @Test
    void login_whenUserDeleted_returnDeletedMsgAndSaveLoginLog() {
        when(captchaService.checkCaptcha(any())).thenReturn(ResponseDTO.ok());
        when(portalUserManager.getOne(any())).thenReturn(portalUser(100L, "zhangsan", true));

        ResponseDTO<PortalLoginResultVO> result = service.login(loginForm(1), "", "UA");

        assertFalse(result.getOk());
        assertTrue(result.getMsg().contains("账号已被删除"));
        ArgumentCaptor<LoginLogEntity> captor = ArgumentCaptor.forClass(LoginLogEntity.class);
        verify(loginLogService).log(captor.capture());
        LoginLogEntity logEntity = captor.getValue();
        assertEquals("账号已删除", logEntity.getRemark());
        assertEquals(LoginLogResultEnum.LOGIN_FAIL.getValue(), logEntity.getLoginResult());
        assertEquals(100L, logEntity.getUserId());
        verify(securityLoginService, never()).checkLogin(any(), any());
    }

    @Test
    void login_whenSecurityCheckFailed_returnErrorAndStop() {
        when(captchaService.checkCaptcha(any())).thenReturn(ResponseDTO.ok());
        when(portalUserManager.getOne(any())).thenReturn(portalUser(100L, "zhangsan", false));
        when(securityLoginService.checkLogin(eq(100L), eq(UserTypeEnum.ADMIN_BACKEND_USER)))
                .thenReturn(ResponseDTO.error(UserErrorCode.PARAM_ERROR, "失败次数过多,账号已锁定"));

        ResponseDTO<PortalLoginResultVO> result = service.login(loginForm(1), "", "");

        assertFalse(result.getOk());
        assertTrue(result.getMsg().contains("锁定"));
        verify(loginLogService, never()).log(any());
    }

    // ---------------------------------- login：密码错误分支 ----------------------------------

    @Test
    void login_whenPasswordWrong_returnPwdErrorAndRecordFail() {
        when(captchaService.checkCaptcha(any())).thenReturn(ResponseDTO.ok());
        when(portalUserManager.getOne(any())).thenReturn(portalUser(100L, "zhangsan", false));
        when(securityLoginService.checkLogin(eq(100L), eq(UserTypeEnum.ADMIN_BACKEND_USER)))
                .thenReturn(ResponseDTO.ok());
        when(securityLoginService.recordLoginFail(eq(100L), eq(UserTypeEnum.PORTAL_USER),
                eq("zhangsan"), any())).thenReturn(null);
        try (MockedStatic<SecurityPasswordService> pwdStatic = mockStatic(SecurityPasswordService.class)) {
            pwdStatic.when(() -> SecurityPasswordService.matchesPwd(anyString(), anyString())).thenReturn(false);

            ResponseDTO<PortalLoginResultVO> result = service.login(loginForm(1), "", "UA");

            assertFalse(result.getOk());
            assertTrue(result.getMsg().contains("登录名或密码错误"));
            verify(securityLoginService).recordLoginFail(100L, UserTypeEnum.PORTAL_USER, "zhangsan", null);
            // 失败同样记录登录日志
            ArgumentCaptor<LoginLogEntity> captor = ArgumentCaptor.forClass(LoginLogEntity.class);
            verify(loginLogService).log(captor.capture());
            assertEquals("密码错误", captor.getValue().getRemark());
        }
    }

    @Test
    void login_whenPasswordWrongWillLock_returnLockMsg() {
        when(captchaService.checkCaptcha(any())).thenReturn(ResponseDTO.ok());
        when(portalUserManager.getOne(any())).thenReturn(portalUser(100L, "zhangsan", false));
        when(securityLoginService.checkLogin(eq(100L), eq(UserTypeEnum.ADMIN_BACKEND_USER)))
                .thenReturn(ResponseDTO.ok());
        when(securityLoginService.recordLoginFail(eq(100L), eq(UserTypeEnum.PORTAL_USER),
                eq("zhangsan"), any())).thenReturn("连续失败 5 次,账号将锁定");
        try (MockedStatic<SecurityPasswordService> pwdStatic = mockStatic(SecurityPasswordService.class)) {
            pwdStatic.when(() -> SecurityPasswordService.matchesPwd(anyString(), anyString())).thenReturn(false);

            ResponseDTO<PortalLoginResultVO> result = service.login(loginForm(1), "", "");

            assertFalse(result.getOk());
            assertEquals(UserErrorCode.LOGIN_FAIL_WILL_LOCK.getCode(), result.getCode());
            assertTrue(result.getMsg().contains("锁定"));
        }
    }

    // ---------------------------------- login：成功路径 ----------------------------------

    @Test
    void login_whenSuccess_loginSaTokenAndReturnResult() {
        when(captchaService.checkCaptcha(any())).thenReturn(ResponseDTO.ok());
        PortalUserEntity user = portalUser(100L, "zhangsan", false);
        when(portalUserManager.getOne(any())).thenReturn(user);
        when(securityLoginService.checkLogin(eq(100L), eq(UserTypeEnum.ADMIN_BACKEND_USER)))
                .thenReturn(ResponseDTO.ok());
        when(portalLoginManager.loadLoginInfo(user)).thenReturn(requestPortalUser());
        when(protectPasswordService.checkNeedChangePassword(anyInt(), anyLong())).thenReturn(true);
        when(redisService.generateRedisKey(anyString(), anyString())).thenReturn("login-code-key");

        try (MockedStatic<SecurityPasswordService> pwdStatic = mockStatic(SecurityPasswordService.class);
             MockedStatic<StpUtil> stpStatic = mockStatic(StpUtil.class)) {
            pwdStatic.when(() -> SecurityPasswordService.matchesPwd(anyString(), anyString())).thenReturn(true);
            stpStatic.when(StpUtil::getTokenValue).thenReturn("mock-token");

            ResponseDTO<PortalLoginResultVO> result = service.login(loginForm(1), "", "");

            assertTrue(result.getOk());
            PortalLoginResultVO vo = result.getData();
            assertNotNull(vo);
            assertEquals("mock-token", vo.getToken());
            assertEquals(100L, vo.getId());
            assertEquals("zhangsan", vo.getUserName());
            assertEquals(UserTypeEnum.PORTAL_USER, vo.getUserType());
            // sa-token 登录 loginId 为 "类型值:用户id"，设备参数为枚举描述
            stpStatic.verify(() -> StpUtil.login("2:100", "电脑端"));
            // 清除登录失败次数与邮箱验证码
            verify(securityLoginService).removeLoginFail(100L, UserTypeEnum.PORTAL_USER);
            verify(redisService).delete("login-code-key");
            assertTrue(vo.getNeedUpdatePwdFlag());
        }
    }

    @Test
    void login_whenTokenStillValid_clearNeedUpdatePwdFlag() {
        when(captchaService.checkCaptcha(any())).thenReturn(ResponseDTO.ok());
        PortalUserEntity user = portalUser(100L, "zhangsan", false);
        when(portalUserManager.getOne(any())).thenReturn(user);
        when(securityLoginService.checkLogin(eq(100L), eq(UserTypeEnum.ADMIN_BACKEND_USER)))
                .thenReturn(ResponseDTO.ok());
        when(portalLoginManager.loadLoginInfo(user)).thenReturn(requestPortalUser());
        when(protectPasswordService.checkNeedChangePassword(anyInt(), anyLong())).thenReturn(true);

        try (MockedStatic<SecurityPasswordService> pwdStatic = mockStatic(SecurityPasswordService.class);
             MockedStatic<StpUtil> stpStatic = mockStatic(StpUtil.class)) {
            pwdStatic.when(() -> SecurityPasswordService.matchesPwd(anyString(), anyString())).thenReturn(true);
            stpStatic.when(StpUtil::getTokenValue).thenReturn("mock-token");
            // token 仍可解析出 loginId（如"记住我"场景），不应强制改密
            stpStatic.when(() -> StpUtil.getLoginIdByToken("mock-token")).thenReturn("2:100");

            PortalLoginResultVO vo = service.login(loginForm(1), "", "").getData();

            assertFalse(vo.getNeedUpdatePwdFlag());
            stpStatic.verify(() -> StpUtil.getLoginIdByToken("mock-token"));
        }
    }

    @Test
    void login_whenHasLastLoginLog_fillLastLoginInfo() {
        when(captchaService.checkCaptcha(any())).thenReturn(ResponseDTO.ok());
        PortalUserEntity user = portalUser(100L, "zhangsan", false);
        when(portalUserManager.getOne(any())).thenReturn(user);
        when(securityLoginService.checkLogin(eq(100L), eq(UserTypeEnum.ADMIN_BACKEND_USER)))
                .thenReturn(ResponseDTO.ok());
        when(portalLoginManager.loadLoginInfo(user)).thenReturn(requestPortalUser());
        when(protectPasswordService.checkNeedChangePassword(anyInt(), anyLong())).thenReturn(false);
        LocalDateTime lastTime = LocalDateTime.of(2026, 8, 1, 12, 0);
        LoginLogVO lastLog = new LoginLogVO();
        lastLog.setLoginIp("9.9.9.9");
        lastLog.setLoginIpRegion("本地");
        lastLog.setCreateTime(lastTime);
        lastLog.setUserAgent("last-UA");
        when(loginLogService.queryLastByUserId(anyLong(), any(), any())).thenReturn(lastLog);

        try (MockedStatic<SecurityPasswordService> pwdStatic = mockStatic(SecurityPasswordService.class);
             MockedStatic<StpUtil> stpStatic = mockStatic(StpUtil.class)) {
            pwdStatic.when(() -> SecurityPasswordService.matchesPwd(anyString(), anyString())).thenReturn(true);
            stpStatic.when(StpUtil::getTokenValue).thenReturn("mock-token");

            PortalLoginResultVO vo = service.login(loginForm(1), "", "").getData();

            assertEquals("9.9.9.9", vo.getLastLoginIp());
            assertEquals("本地", vo.getLastLoginIpRegion());
            assertEquals(lastTime, vo.getLastLoginTime());
            assertEquals("last-UA", vo.getLastLoginUserAgent());
            assertFalse(vo.getNeedUpdatePwdFlag());
        }
    }

    // ---------------------------------- logout ----------------------------------

    @Test
    void logout_whenRequestUserNotNull_logoutAndClearCacheAndSaveLog() {
        RequestPortalUser requestUser = requestPortalUser();
        requestUser.setIp("");
        try (MockedStatic<StpUtil> stpStatic = mockStatic(StpUtil.class)) {
            ResponseDTO<String> result = service.logout(requestUser);

            assertTrue(result.getOk());
            stpStatic.verify(StpUtil::logout);
            verify(portalLoginManager).clearUserPermission(100L);
            verify(portalLoginManager).clearUserLoginInfo(100L);
            ArgumentCaptor<LoginLogEntity> captor = ArgumentCaptor.forClass(LoginLogEntity.class);
            verify(loginLogService).log(captor.capture());
            assertEquals(LoginLogResultEnum.LOGIN_OUT.getValue(), captor.getValue().getLoginResult());
            assertEquals(UserTypeEnum.PORTAL_USER.getValue(), captor.getValue().getUserType());
        }
    }

    @Test
    void logout_whenRequestUserNull_onlySaTokenLogout() {
        try (MockedStatic<StpUtil> stpStatic = mockStatic(StpUtil.class)) {
            ResponseDTO<String> result = service.logout(null);

            assertTrue(result.getOk());
            stpStatic.verify(StpUtil::logout);
            verify(portalLoginManager, never()).clearUserPermission(any());
            verify(loginLogService, never()).log(any());
        }
    }

    // ---------------------------------- getLoginResult ----------------------------------

    @Test
    void getLoginResult_withoutLastLogin_fillBaseInfoOnly() {
        RequestPortalUser requestUser = requestPortalUser();
        when(loginLogService.queryLastByUserId(anyLong(), any(), any())).thenReturn(null);

        LoginResultVO vo = service.getLoginResult(requestUser, "token-1");

        assertEquals("token-1", vo.getToken());
        assertEquals(100L, vo.getId());
        assertEquals("zhangsan", vo.getUserName());
        assertEquals(UserTypeEnum.PORTAL_USER, vo.getUserType());
        assertNull(vo.getLastLoginIp());
    }

    @Test
    void getLoginResult_withLastLogin_fillLastLoginInfo() {
        RequestPortalUser requestUser = requestPortalUser();
        LoginLogVO lastLog = new LoginLogVO();
        lastLog.setLoginIp("8.8.8.8");
        lastLog.setLoginIpRegion("某地");
        LocalDateTime lastTime = LocalDateTime.of(2026, 7, 7, 8, 30);
        lastLog.setCreateTime(lastTime);
        lastLog.setUserAgent("old-UA");
        when(loginLogService.queryLastByUserId(anyLong(), any(), any())).thenReturn(lastLog);

        LoginResultVO vo = service.getLoginResult(requestUser, "token-1");

        assertEquals("8.8.8.8", vo.getLastLoginIp());
        assertEquals("某地", vo.getLastLoginIpRegion());
        assertEquals(lastTime, vo.getLastLoginTime());
        assertEquals("old-UA", vo.getLastLoginUserAgent());
        verify(loginLogService).queryLastByUserId(100L, UserTypeEnum.ADMIN_BACKEND_USER, LoginLogResultEnum.LOGIN_SUCCESS);
    }

    // ---------------------------------- 登录名解析 getPortalUserIdByLoginId ----------------------------------

    @Test
    void getPortalUserIdByLoginId_whenNormalLoginId_returnUserId() {
        assertEquals(100L, service.getPortalUserIdByLoginId("2:100"));
    }

    @Test
    void getPortalUserIdByLoginId_whenNull_returnNull() {
        assertNull(service.getPortalUserIdByLoginId(null));
    }

    @Test
    void getPortalUserIdByLoginId_whenNotNumber_returnNull() {
        assertNull(service.getPortalUserIdByLoginId("2:abc"));
    }

    @Test
    void getPortalUserIdByLoginId_whenTooShort_returnNull() {
        assertNull(service.getPortalUserIdByLoginId("1"));
    }

    // ---------------------------------- getLoginPortalUser ----------------------------------

    @Test
    void getLoginPortalUser_whenLoginIdNull_returnNull() {
        assertNull(service.getLoginPortalUser(null, null));
        verify(portalLoginManager, never()).getRequestPortalUser(any());
    }

    // ---------------------------------- sendEmailCode ----------------------------------

    @Test
    void sendEmailCode_whenTwoFactorNotEnabled_returnParamError() {
        when(level3ProtectConfigService.isTwoFactorLoginEnabled()).thenReturn(false);

        ResponseDTO<String> result = service.sendEmailCode("zhangsan");

        assertFalse(result.getOk());
        assertTrue(result.getMsg().contains("无需使用邮箱验证码"));
        verify(portalUserManager, never()).getOne(any());
    }

    @Test
    void sendEmailCode_whenUserNotFound_returnOk() {
        when(level3ProtectConfigService.isTwoFactorLoginEnabled()).thenReturn(true);
        when(portalUserManager.getOne(any())).thenReturn(null);

        ResponseDTO<String> result = service.sendEmailCode("zhangsan");

        assertTrue(result.getOk());
    }

    @Test
    void sendEmailCode_whenUserExist_returnUnsupported() {
        when(level3ProtectConfigService.isTwoFactorLoginEnabled()).thenReturn(true);
        when(portalUserManager.getOne(any())).thenReturn(portalUser(100L, "zhangsan", false));

        ResponseDTO<String> result = service.sendEmailCode("zhangsan");

        assertFalse(result.getOk());
        assertTrue(result.getMsg().contains("不支持邮箱验证"));
    }
}
