package com.akkkka.auth.controller;

import com.akkkka.common.core.domain.R;
import com.akkkka.common.security.auth.AuthUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: akkkka114514
 * @create: 2025-06-23 15:54
 * @description:
 */
@RestController("/auth/step-up")
public class StepUpAuthController {
    @GetMapping("/open")
    public R<Void> openStepUpAuth() {
        AuthUtil.openStepUpAuth();
        return R.ok();
    }

    @GetMapping("/verify")
    public R<Void> verifyStepUpAuthPassword(String encryptPassword) {
        AuthUtil.verifyStepUpAuthPassword(encryptPassword);
        return R.ok();
    }

    @GetMapping("/enable")
    public R<Void> enableSafe() {
        AuthUtil.enableSafe();
        return R.ok();
    }

    @GetMapping("/check")
    public R<Void> checkSafe() {
        AuthUtil.checkSafe();
        return R.ok();
    }

    @GetMapping("/redis-key-prefix")
    public R<String> getStepUpAuthRedisKeyPrefix() {
        return R.ok(AuthUtil.getStepUpAuthRedisKeyPrefix());
    }
}
