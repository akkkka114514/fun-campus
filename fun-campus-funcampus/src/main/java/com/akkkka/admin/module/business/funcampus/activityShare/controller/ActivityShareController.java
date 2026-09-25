package com.akkkka.admin.module.business.funcampus.activityShare.controller;

import com.akkkka.admin.module.business.funcampus.activityShare.domain.vo.ActivityShareVO;
import com.akkkka.admin.module.business.funcampus.activityShare.service.ActivityShareService;
import com.akkkka.common.annoation.NoNeedLogin;
import com.akkkka.common.domain.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 活动分享 Controller
 *
 * @Author akkkka114514
 * @Date 2026-08-16
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动分享")
@RequestMapping("portal")
public class ActivityShareController {

    @Resource
    private ActivityShareService activityShareService;

    @Operation(summary = "生成活动分享链接 @author akkkka114514")
    @PostMapping("/activity/share/generate")
    public ResponseDTO<ActivityShareVO> generateShareLink(@RequestBody Long activityId) {
        return ResponseDTO.ok(activityShareService.generateShareLink(activityId));
    }

    @Operation(summary = "解析活动分享链接 @author akkkka114514")
    @GetMapping("/activity/share/resolve/{token}")
    @NoNeedLogin
    public ResponseDTO<Long> resolveShareToken(@PathVariable String token) {
        return ResponseDTO.ok(activityShareService.resolveShareToken(token));
    }
}
