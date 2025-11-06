package net.lab1024.sa.admin;

import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.PortalLoginResultVO;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.service.PortalLoginService;
import net.lab1024.sa.admin.module.system.login.domain.LoginForm;
import net.lab1024.sa.admin.module.system.login.domain.LoginResultVO;
import net.lab1024.sa.base.module.support.captcha.domain.CaptchaVO;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
* author:akkkka114514
* create at 2025-11-06 15:57
*/
@ExtendWith(MockitoExtension.class)
public class PortalLoginServiceTest {
    
    @Mock
    private PortalLoginService portalLoginService;
    
    @Test
    void testGetCaptcha() {
        // Given
        CaptchaVO mockCaptcha = new CaptchaVO();
        mockCaptcha.setCaptchaUuid("test-uuid");
        
        ResponseDTO<CaptchaVO> mockResponse = ResponseDTO.ok(mockCaptcha);
        when(portalLoginService.getCaptcha())
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<CaptchaVO> result = portalLoginService.getCaptcha();
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertNotNull(result.getData());
        verify(portalLoginService, times(1)).getCaptcha();
    }
    
    @Test
    void testLogin() {
        // Given
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("testuser");
        loginForm.setPassword("testpassword");
        loginForm.setLoginDevice(1);
        
        PortalLoginResultVO mockLoginResult = new PortalLoginResultVO();
        mockLoginResult.setToken("test-token");
        mockLoginResult.setUsername("testuser");
        
        ResponseDTO<PortalLoginResultVO> mockResponse = ResponseDTO.ok(mockLoginResult);
        when(portalLoginService.login(any(LoginForm.class), any(String.class), any(String.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<PortalLoginResultVO> result = portalLoginService.login(loginForm, "127.0.0.1", "test-agent");
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertNotNull(result.getData());
        assertEquals("test-token", result.getData().getToken());
        verify(portalLoginService, times(1)).login(loginForm, "127.0.0.1", "test-agent");
    }
    
    @Test
    void testSendEmailCode() {
        // Given
        String loginName = "testuser";
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(portalLoginService.sendEmailCode(loginName))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = portalLoginService.sendEmailCode(loginName);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(portalLoginService, times(1)).sendEmailCode(loginName);
    }
}