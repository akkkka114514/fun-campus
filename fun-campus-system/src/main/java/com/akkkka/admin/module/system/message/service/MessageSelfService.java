package com.akkkka.admin.module.system.message.service;

import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.message.domain.MessageQueryForm;
import com.akkkka.module.support.message.domain.MessageVO;
import com.akkkka.module.support.message.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 消息自助服务：查询/标记已读 当前登录用户自己的消息
 * <p>
 * 后台端（/backend/message/*）与门户端（/portal/message/*）共用；
 * 请求用户由 AdminInterceptor 按 URL 前缀解析，这里只做“以登录用户为准”的数据隔离。
 *
 * @Author akkkka114514
 * @Date 2026-09-22
 * @Copyright akkkka114514
 */
@Service
@AllArgsConstructor
public class MessageSelfService {

    private final MessageService messageService;

    /**
     * 分页查询当前登录用户收到的消息
     */
    public PageResult<MessageVO> queryMyMessage(MessageQueryForm queryForm) {
        RequestUser requestUser = getRequestUser();
        // 以登录用户为准，忽略前端传入的接收人参数，防止越权查看他人消息
        queryForm.setReceiverUserId(requestUser.getUserId());
        queryForm.setReceiverUserType(requestUser.getUserType().getValue());
        return messageService.query(queryForm);
    }

    /**
     * 查询当前登录用户未读消息数量
     */
    public Long getMyUnreadCount() {
        RequestUser requestUser = getRequestUser();
        return messageService.getUnreadCount(requestUser.getUserType(), requestUser.getUserId());
    }

    /**
     * 将当前登录用户的指定消息标记为已读（SQL 带接收人条件，无法操作他人消息）
     */
    public void readMyMessage(Long messageId) {
        RequestUser requestUser = getRequestUser();
        messageService.updateReadFlag(messageId, requestUser.getUserType(), requestUser.getUserId());
    }

    private RequestUser getRequestUser() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (requestUser == null || requestUser.getUserId() == null) {
            throw new BusinessException(UserErrorCode.LOGIN_STATE_INVALID);
        }
        return requestUser;
    }
}
