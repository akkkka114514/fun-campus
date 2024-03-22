package com.akkkka.funcampussecurity.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.akkkka.funcampussecurityapi.api.ITokenAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
/**
 * @author akkkka
 */
@Slf4j
@Service
@DubboService
public class TokenAuthServiceImpl implements ITokenAuthService {
    @Override
    public boolean tokenAuth(String token) {
        log.info("token auth:{}",token);
        if(StrUtil.isEmpty(token)){
            return false;
        }
        Object loginId = StpUtil.getLoginIdByToken(token);
        return loginId != null;
    }

}
