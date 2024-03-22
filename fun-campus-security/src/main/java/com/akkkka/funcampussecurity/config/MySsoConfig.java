package com.akkkka.funcampussecurity.config;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.common.ResponseEnum;
import com.akkkka.funcampusutil.util.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static cn.dev33.satoken.SaManager.config;

@Configuration
@Slf4j
public class MySsoConfig implements WebMvcConfigurer {
    /** 注册 [Sa-Token全局过滤器] */
//    @Bean
//    public SaServletFilter getSaServletFilter() {
//        return new SaServletFilter()
//                .addInclude("/**")
//                .addExclude("/sso/**")
//                .setAuth(obj -> {
//                    String token = SaHolder.getRequest().getHeader("satoken");
//                    log.info("saServletFilter auth:{}",token);
//                    if(token==null){
//                        throw new CustomException(ResponseEnum.NOT_LOGIN_OR_TOKEN_EXPIRED);
//                    }
//                    Long loginId = (Long) StpUtil.getLoginIdByToken(token);
//                    if(loginId == null){
//                        throw new CustomException(ResponseEnum.NOT_LOGIN_OR_TOKEN_EXPIRED);
//                    }
//                });
//    }

//    @Bean
//    @Primary
//    public SaSsoConfig ssoConfigBean() {
//        SaSsoConfig config = new SaSsoConfig();
//        config.setNotLoginView(
//                () -> CommonResponse.fail(ResponseEnum.NOT_LOGIN_OR_TOKEN_EXPIRED)
//        );
//        return config;
//    }
}
