package net.lab1024.sa.admin.interceptor;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.service.PortalLoginService;
import net.lab1024.sa.admin.module.system.login.domain.RequestBackendUser;
import net.lab1024.sa.admin.module.system.login.service.LoginService;
import net.lab1024.sa.base.common.annoation.NoNeedLogin;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.common.util.SmartResponseUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * admin 拦截器
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/7/26 20:20:33
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */

@Component
@Slf4j
public class AdminInterceptor implements HandlerInterceptor {
    @Resource
    private LoginService loginService;
    @Resource
    private PortalLoginService portalLoginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("AdminInterceptor.preHandle called, method={}, URI={}", request.getMethod(), request.getRequestURI());

        // OPTIONS请求直接return
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            log.debug("OPTIONS request detected, setting NO_CONTENT status and returning false");
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return false;
        }

        boolean isHandler = handler instanceof HandlerMethod;
        if (!isHandler) {
            log.debug("Handler is not a HandlerMethod, skipping authentication checks");
            return true;
        }

        try {
            // --------------- 第一步： 根据token 获取用户 ---------------
            String tokenValue = StpUtil.getTokenValue();
            log.debug("Retrieved tokenValue: {}", tokenValue != null ? "***" : "null");

            String loginId = (String) StpUtil.getLoginIdByToken(tokenValue);
            log.debug("Parsed loginId from token: {}", loginId != null ? loginId : "null");
            
            String url = request.getRequestURL().toString();
            log.debug("Processing request URL: {}", url);
            
            RequestUser requestUser = null;
            if (url.contains("portal")) {
                log.debug("Identified as portal request, attempting to get portal user");
                requestUser = portalLoginService.getLoginPortalUser(loginId, request);
                log.debug("Portal user retrieved: {}", requestUser != null ? requestUser.getUserId() : "null");
            } else if (url.contains("backend")) {
                log.debug("Identified as backend request, attempting to get backend user");
                requestUser = loginService.getLoginBackendUser(loginId, request);
                log.debug("Backend user retrieved: {}", requestUser != null ? requestUser.getUserId() : "null");
            } else {
                log.warn("Unknown request type for URL: {}, falling back to backend user", url);
                requestUser = loginService.getLoginBackendUser(loginId, request);
                log.debug("Fallback user retrieved: {}", requestUser != null ? requestUser.getUserId() : "null");
            }

            // --------------- 第二步： 校验 登录 ---------------
            Method method = ((HandlerMethod) handler).getMethod();
            log.debug("Checking authentication for method: {}.{} with user: {}", 
                     method.getDeclaringClass().getSimpleName(), method.getName(),
                     requestUser != null ? requestUser.getUserId() : "null");

            NoNeedLogin noNeedLogin = ((HandlerMethod) handler).getMethodAnnotation(NoNeedLogin.class);
            if (noNeedLogin != null) {
                log.debug("Method {} is annotated with NoNeedLogin, skipping authentication", method.getName());
                updateActiveTimeout(requestUser);
                SmartRequestUtil.setRequestUser(requestUser);
                log.debug("Authentication bypassed successfully for method: {}", method.getName());
                return true;
            }

            if (requestUser == null) {
                log.warn("Authentication failed: requestUser is null for URL: {}, method: {}", url, method.getName());
                SmartResponseUtil.write(response, ResponseDTO.error(UserErrorCode.LOGIN_STATE_INVALID));
                return false;
            }

            log.debug("User authenticated successfully: id={}, type={}", requestUser.getUserId(), requestUser.getUserType());

            // 更新活跃
            updateActiveTimeout(requestUser);
            log.debug("Active timeout updated for user: {}", requestUser.getUserId());


            // --------------- 第三步： 校验 权限 ---------------
            SmartRequestUtil.setRequestUser(requestUser);
            if (SaAnnotationStrategy.instance.isAnnotationPresent.apply(method, SaIgnore.class)) {
                log.debug("Method {} is annotated with SaIgnore, skipping permission checks", method.getName());
                return true;
            }

            log.debug("Performing permission check for method: {}", method.getName());
            SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
            log.debug("Permission check passed for method: {}", method.getName());

        } catch (SaTokenException e) {
            log.warn("SaTokenException caught: code={}, message={}", e.getCode(), e.getMessage());
            /*
             * sa-token 异常状态码
             * 具体请看： https://sa-token.cc/doc.html#/fun/exception-code
             */
            int code = e.getCode();
            if (code == 11041 || code == 11051) {
                log.warn("Permission denied for request: {}", request.getRequestURI());
                SmartResponseUtil.write(response, ResponseDTO.error(UserErrorCode.NO_PERMISSION));
            } else if (code == 11016) {
                log.warn("Login active timeout for request: {}", request.getRequestURI());
                SmartResponseUtil.write(response, ResponseDTO.error(UserErrorCode.LOGIN_ACTIVE_TIMEOUT));
            } else if (code >= 11011 && code <= 11015) {
                log.warn("Login state invalid for request: {}", request.getRequestURI());
                SmartResponseUtil.write(response, ResponseDTO.error(UserErrorCode.LOGIN_STATE_INVALID));
            } else {
                log.warn("Parameter error for request: {}, SaToken code: {}", request.getRequestURI(), code);
                SmartResponseUtil.write(response, ResponseDTO.error(UserErrorCode.PARAM_ERROR));
            }
            return false;
        } catch (Throwable e) {
            log.error("Unexpected error in AdminInterceptor for request: {}, method: {}, error: {}", 
                     request.getRequestURI(), request.getMethod(), e.getMessage(), e);
            SmartResponseUtil.write(response, ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR));
            return false;
        }

        log.debug("Request passed all checks in AdminInterceptor: {}", request.getRequestURI());
        // 通过验证
        return true;
    }


    /**
     * 更新活跃时间
     */
    private void updateActiveTimeout(RequestUser requestUser) {
        if (requestUser == null) {
            log.debug("Skipping active timeout update: requestUser is null");
            return;
        }
        log.debug("Updating last active time for user: {}", requestUser.getUserId());
        StpUtil.updateLastActiveToNow();
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("AdminInterceptor.afterCompletion called for URI: {}, status: {}", 
                 request.getRequestURI(), response.getStatus());
        if (ex != null) {
            log.error("Exception occurred during request processing: {}", ex.getMessage(), ex);
        }
        // 清除上下文
        SmartRequestUtil.remove();
        log.debug("Request context cleared for URI: {}", request.getRequestURI());
    }

}