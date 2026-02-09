package net.lab1024.sa.admin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.common.util.SmartStringUtil;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志拦截器
 * 用于记录HTTP请求的详细信息，包括请求参数、响应结果、执行时间等
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2024/01/31 10:00:00
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */
@Component
@Slf4j
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "requestStartTime";
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        // 生成请求ID（用于链路追踪）
        String requestId = java.util.UUID.randomUUID().toString().replace("-", "");
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);

        // 只处理HandlerMethod类型的处理器
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String className = handlerMethod.getBeanType().getSimpleName();
        String methodName = handlerMethod.getMethod().getName();
        
        // 获取请求基本信息
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String remoteAddr = getClientIpAddress(request);
        
        // 获取用户信息
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        String userInfo = getUserInfo(requestUser);
        
        // 记录请求开始日志
        log.info("""
                        [REQUEST_START] requestId={}, method={}, 
                        uri={}, queryString={},
                        remoteAddr={}, user={}, 
                        className={}, methodName={}""",
                requestId, method, uri, queryString, remoteAddr, userInfo, className, methodName);
        
        // 记录请求头信息（DEBUG级别）
        if (log.isDebugEnabled()) {
            log.debug("[REQUEST_HEADERS] requestId={}, headers={}", requestId, getHeadersInfo(request));
        }
        
        // 记录请求参数（DEBUG级别，避免敏感信息泄露）
        if (log.isDebugEnabled()) {
            log.debug("[REQUEST_PARAMS] requestId={}, params={}", requestId, getParametersInfo(request));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 可以在这里记录响应前的信息
        if (log.isDebugEnabled()) {
            String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
            log.debug("[RESPONSE_PREPARE] requestId={}, status={}", requestId, response.getStatus());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 获取请求开始时间和请求ID
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
        
        if (startTime == null || requestId == null) {
            return;
        }

        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        // 获取请求基本信息
        String uri = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();
        
        // 获取用户信息
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        String userInfo = getUserInfo(requestUser);
        
        // 记录完成日志
        if (ex != null) {
            // 有异常的情况
            log.error("[REQUEST_ERROR] requestId={}, method={}, uri={}, status={}, executeTime={}ms, user={}, error={}", 
                    requestId, method, uri, status, executeTime, userInfo, ex.getMessage(), ex);
        } else {
            // 正常完成的情况
            if (executeTime > 5000) {
                // 执行时间超过5秒，记录警告日志
                log.warn("[REQUEST_SLOW] requestId={}, method={}, uri={}, status={}, executeTime={}ms, user={}", 
                        requestId, method, uri, status, executeTime, userInfo);
            } else {
                log.info("[REQUEST_COMPLETE] requestId={}, method={}, uri={}, status={}, executeTime={}ms, user={}", 
                        requestId, method, uri, status, executeTime, userInfo);
            }
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String xfor = request.getHeader("X-Forwarded-For");
        
        if (SmartStringUtil.isNotEmpty(xfor) && !"unKnown".equalsIgnoreCase(xfor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = xfor.indexOf(",");
            if (index != -1) {
                return xfor.substring(0, index);
            } else {
                return xfor;
            }
        }
        if (SmartStringUtil.isNotEmpty(xip) && !"unKnown".equalsIgnoreCase(xip)) {
            return xip;
        }
        if (SmartStringUtil.isNotEmpty(xfor) && !"unKnown".equalsIgnoreCase(xfor)) {
            return xfor;
        }
        if (SmartStringUtil.isNotEmpty(xip) && !"unKnown".equalsIgnoreCase(xip)) {
            return xip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取用户信息字符串
     */
    private String getUserInfo(RequestUser requestUser) {
        if (requestUser == null) {
            return "anonymous";
        }
        return String.format("userId:%s,userType:%s,userName:%s", 
                requestUser.getUserId(), 
                requestUser.getUserType(), 
                requestUser.getUserName());
    }

    /**
     * 获取请求头信息
     */
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            // 过滤敏感头信息
            if (!"authorization".equalsIgnoreCase(key) && 
                !"cookie".equalsIgnoreCase(key) && 
                !"x-forwarded-for".equalsIgnoreCase(key)) {
                map.put(key, request.getHeader(key));
            }
        }
        return map;
    }

    /**
     * 获取请求参数信息
     */
    private Map<String, String[]> getParametersInfo(HttpServletRequest request) {
        Map<String, String[]> paramMap = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            
            // 过滤敏感参数
            if (!"password".equalsIgnoreCase(key) && 
                !"pwd".equalsIgnoreCase(key) && 
                !"token".equalsIgnoreCase(key)) {
                paramMap.put(key, values);
            }
        }
        return paramMap;
    }
}