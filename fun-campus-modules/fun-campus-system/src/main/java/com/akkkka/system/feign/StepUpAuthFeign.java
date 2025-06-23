package com.akkkka.system.feign;

import com.akkkka.common.core.domain.R;
import com.akkkka.common.security.auth.AuthUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: akkkka114514
 * @create: 2025-06-23 19:41
 * @description: 二级认证feign
 */
@Component
@FeignClient(name = "fun-campus-step-up-auth", url = "http://127.0.0.1:9200")
public interface StepUpAuthFeign {
    @GetMapping("/open")
    R<Void> openStepUpAuth();

    @GetMapping("/verify")
    R<Void> verifyStepUpAuthPassword(String encryptPassword);

    @GetMapping("/enable")
    R<Void> enableSafe();

    @GetMapping("/check")
    R<Void> checkSafe();

    @GetMapping("/redis-key-prefix")
    R<String> getStepUpAuthRedisKeyPrefix();
}
