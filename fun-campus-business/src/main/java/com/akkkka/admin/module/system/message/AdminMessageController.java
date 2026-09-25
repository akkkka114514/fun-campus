package com.akkkka.admin.module.system.message;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.akkkka.admin.constant.AdminSwaggerTagConst;
import com.akkkka.admin.module.system.message.domain.MessageReceiverQueryForm;
import com.akkkka.admin.module.system.message.domain.MessageReceiverVO;
import com.akkkka.admin.module.system.message.service.MessageReceiverService;
import com.akkkka.admin.module.system.message.service.MessageSelfService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.ValidateList;
import com.akkkka.module.support.message.domain.MessageQueryForm;
import com.akkkka.module.support.message.domain.MessageSendForm;
import com.akkkka.module.support.message.domain.MessageVO;
import com.akkkka.module.support.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 后管 消息路由
 *
 * @author: 卓大
 * @date: 2025/04/09 20:55
 */
@Tag(name = AdminSwaggerTagConst.System.SYSTEM_MESSAGE)
@RestController
@RequestMapping("backend")
public class AdminMessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageSelfService messageSelfService;

    @Autowired
    private MessageReceiverService messageReceiverService;

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

    @Operation(summary = "通知消息-分页查询消息接收人")
    @PostMapping("/message/receiver/query")
    @SaCheckPermission("system:message:send")
    public ResponseDTO<PageResult<MessageReceiverVO>> queryReceiverPage(@RequestBody @Valid MessageReceiverQueryForm queryForm) {
        return ResponseDTO.ok(messageReceiverService.queryReceiverPage(queryForm));
    }

    @Operation(summary = "通知消息-新建  @author 卓大")
    @PostMapping("/message/sendMessages")
    @SaCheckPermission("system:message:send")
    public ResponseDTO<String> sendMessages(@RequestBody @Valid ValidateList<MessageSendForm> messageList) {
        messageService.sendMessage(messageList);
        return ResponseDTO.ok();
    }

    @Operation(summary = "通知消息-分页查询   @author 卓大")
    @PostMapping("/message/query")
    @SaCheckPermission("system:message:query")
    public ResponseDTO<PageResult<MessageVO>> query(@RequestBody @Valid MessageQueryForm queryForm) {
        return ResponseDTO.ok(messageService.query(queryForm));
    }

    @Operation(summary = "通知消息-删除   @author 卓大")
    @GetMapping("/message/delete/{messageId}")
    @SaCheckPermission("system:message:delete")
    public ResponseDTO<String> delete(@PathVariable Long messageId) {
        return messageService.delete(messageId);
    }

}
