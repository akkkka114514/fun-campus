package com.akkkka.admin.module.system.message;

import com.akkkka.admin.module.system.message.service.MessageSelfService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.message.domain.MessageQueryForm;
import com.akkkka.module.support.message.domain.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 门户 消息路由：当前登录的前台用户（学生/组织者）查询、标记自己的消息
 * <p>
 * 请求路径必须包含 portal 段：由 AdminInterceptor 解析为门户登录用户
 *
 * @Author akkkka114514
 * @Date 2026-09-22
 * @Copyright akkkka114514
 */
@Tag(name = "门户-通知消息")
@RestController
@RequestMapping("portal")
public class PortalMessageController {

    @Resource
    private MessageSelfService messageSelfService;

    @Operation(summary = "通知消息-分页查询我收到的消息")
    @PostMapping("/message/queryMyMessage")
    public ResponseDTO<PageResult<MessageVO>> queryMyMessage(@RequestBody @Valid MessageQueryForm queryForm) {
        return ResponseDTO.ok(messageSelfService.queryMyMessage(queryForm));
    }

    @Operation(summary = "通知消息-查询我的未读消息数量")
    @GetMapping("/message/getUnreadCount")
    public ResponseDTO<Long> getMyUnreadCount() {
        return ResponseDTO.ok(messageSelfService.getMyUnreadCount());
    }

    @Operation(summary = "通知消息-将我的消息标记为已读")
    @GetMapping("/message/read/{messageId}")
    public ResponseDTO<String> readMyMessage(@PathVariable Long messageId) {
        messageSelfService.readMyMessage(messageId);
        return ResponseDTO.ok();
    }
}
