package com.akkkka.handler;

import cn.dev33.satoken.exception.NotPermissionException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.SystemEnvironment;
import com.akkkka.common.enumeration.SystemEnvironmentEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.repository.DisableIpDocument;
import com.akkkka.common.repository.DisableUserDocument;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常拦截
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2020/8/25 21:57
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Resource
    private SystemEnvironment systemEnvironment;

    @Value("${dangerous-user.detect}")
    private boolean detectDangerousUser;

    @Value("${dangerous-user.tolerance-time}")
    private int tolerateDangerousUserTime;

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * json 格式错误 缺少请求体
     */
    @ResponseBody
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseDTO<?> jsonFormatExceptionHandler(Exception e) {
        if (!systemEnvironment.isProd()) {
            log.error("全局JSON格式错误异常,URL:{}", getCurrentRequestUrl(), e);
        }
        return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数JSON格式错误");
    }

    /**
     * json 格式错误 缺少请求体
     */
    @ResponseBody
    @ExceptionHandler({TypeMismatchException.class, BindException.class})
    public ResponseDTO<?> paramExceptionHandler(Exception e) {
        if (e instanceof BindException) {
            if (e instanceof MethodArgumentNotValidException) {
                List<FieldError> fieldErrors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
                List<String> msgList = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, String.join(",", msgList));
            }

            List<FieldError> fieldErrors = ((BindException) e).getFieldErrors();
            List<String> error = fieldErrors.stream().map(field -> field.getField() + ":" + field.getRejectedValue()).collect(Collectors.toList());
            String errorMsg = UserErrorCode.PARAM_ERROR.getMsg() + ":" + error;
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, errorMsg);
        }
        return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
    }

    /**
     * sa-token 权限异常处理
     *
     * @param e 权限异常
     * @return 错误结果
     */
    @ResponseBody
    @ExceptionHandler(NotPermissionException.class)
    public ResponseDTO<String> permissionException(NotPermissionException e) {
        // 开发环境 方便调试
        if (SystemEnvironmentEnum.PROD != systemEnvironment.getCurrentEnvironment()) {
            return ResponseDTO.error(UserErrorCode.NO_PERMISSION, e.getMessage());
        }
        return ResponseDTO.error(UserErrorCode.NO_PERMISSION);
    }


    /**
     * 业务异常
     */
    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public ResponseDTO<?> businessExceptionHandler(BusinessException e) {
        log.warn(e.getMessage(),e.getDetailMsg(),e.getCause());
        return new ResponseDTO<Void>(e.getCode(),null,false,e.getMessage());
    }

/**
 * 处理危险用户异常的控制器方法
 * 当检测到危险用户行为时触发此异常处理器
 * @param e BusinessException 异常对象，包含危险用户行为的详细信息
 * @return Void 返回空，此方法主要用于处理异常并记录日志
 */
    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public Void dangerousUserException (BusinessException e){
    // 记录危险用户行为的警告日志，包含异常消息、详情、用户ID和IP地址
        log.warn(e.getMessage(),e.getDetailMsg(),e.getUserId(),e.getIp());
        //如果没开启
        if(!detectDangerousUser){
            throw e;
        }
        Query userIdCountQuery=new Query(
                Criteria.where("userId").is(e.getUserId())
                .andOperator(Criteria.where("system").is(e.getSystem()))
                .andOperator(Criteria.where("deleted").is(false))
        );
        Query ipCountQuery=new Query(
                Criteria.where("ip").is(e.getIp())
                .andOperator(Criteria.where("deleted").is(false))
        );
        if(mongoTemplate.count(userIdCountQuery,"dangerousUser")>=tolerateDangerousUserTime){
            DisableUserDocument disableUserDocument=new DisableUserDocument();
            disableUserDocument.setSystem(e.getSystem());
            disableUserDocument.setUserId(e.getUserId());
            disableUserDocument.setCreateTime(LocalDateTime.now());
            disableUserDocument.setUpdateTime(LocalDateTime.now());
            disableUserDocument.setDeleted(false);
            mongoTemplate.save(disableUserDocument);
        }
        if(mongoTemplate.count(ipCountQuery,"dangerousUser")>=tolerateDangerousUserTime){
            DisableIpDocument disableIpDocument=new DisableIpDocument();
            disableIpDocument.setIp(e.getIp());
            disableIpDocument.setCreateTime(LocalDateTime.now());
            disableIpDocument.setUpdateTime(LocalDateTime.now());
            disableIpDocument.setDeleted(false);
            mongoTemplate.save(disableIpDocument);
        }
        return null;
    }

    /**
     * 其他全部异常
     *
     * @param e 全局异常
     * @return 错误结果
     */
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ResponseDTO<?> errorHandler(Throwable e) {
        log.error("捕获全局异常,URL:{}", getCurrentRequestUrl(), e);
        return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, systemEnvironment.isProd() ? null : e.toString());
    }

    /**
     * 获取当前请求url
     */
    private String getCurrentRequestUrl() {
        RequestAttributes request = RequestContextHolder.getRequestAttributes();
        if (null == request) {
            return null;
        }
        ServletRequestAttributes servletRequest = (ServletRequestAttributes) request;
        return servletRequest.getRequest().getRequestURI();
    }

}
