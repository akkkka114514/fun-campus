package com.akkkka.admin.module.system.message.service;

import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.message.domain.MessageQueryForm;
import com.akkkka.module.support.message.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 消息自助服务 单元测试
 * <p>
 * 覆盖：查询/未读数/标记已读均以登录用户为准（忽略前端传入的接收人参数）、未登录时报错
 *
 * @Author akkkka114514
 * @Date 2026-09-22
 */
@ExtendWith(MockitoExtension.class)
class MessageSelfServiceTest {

    @Mock
    private MessageService messageService;

    private MessageSelfService service;

    @BeforeEach
    void setUp() {
        service = new MessageSelfService(messageService);
    }

    @AfterEach
    void tearDown() {
        SmartRequestUtil.remove();
    }

    @Test
    void queryMyMessage_overridesReceiverWithLoginUser() {
        setLoginUser(2L, UserTypeEnum.ADMIN_BACKEND_USER);
        when(messageService.query(any())).thenReturn(new PageResult<>());

        // 伪造的接收人参数会被覆盖为登录用户，防止越权查看他人消息
        MessageQueryForm form = new MessageQueryForm();
        form.setReceiverUserId(999L);
        form.setReceiverUserType(UserTypeEnum.PORTAL_USER.getValue());
        form.setSearchCount(true);
        service.queryMyMessage(form);

        assertEquals(2L, form.getReceiverUserId());
        assertEquals(UserTypeEnum.ADMIN_BACKEND_USER.getValue(), form.getReceiverUserType());
        // searchCount 由调用方决定（前端分页器需要 total 展示总条数），不被强制关闭
        assertEquals(Boolean.TRUE, form.getSearchCount());
        verify(messageService).query(form);
    }

    @Test
    void getMyUnreadCount_usesLoginUser() {
        setLoginUser(9L, UserTypeEnum.PORTAL_USER);
        when(messageService.getUnreadCount(UserTypeEnum.PORTAL_USER, 9L)).thenReturn(3L);

        assertEquals(3L, service.getMyUnreadCount());
    }

    @Test
    void readMyMessage_passesLoginUserToUpdate() {
        setLoginUser(9L, UserTypeEnum.PORTAL_USER);

        service.readMyMessage(66L);

        verify(messageService).updateReadFlag(66L, UserTypeEnum.PORTAL_USER, 9L);
    }

    @Test
    void whenNotLogin_throwBusinessException() {
        assertThrows(BusinessException.class, () -> service.queryMyMessage(new MessageQueryForm()));
        assertThrows(BusinessException.class, () -> service.getMyUnreadCount());
        assertThrows(BusinessException.class, () -> service.readMyMessage(1L));
    }

    private void setLoginUser(Long userId, UserTypeEnum userType) {
        RequestBackendUser requestUser = new RequestBackendUser();
        requestUser.setId(userId);
        requestUser.setUserType(userType);
        SmartRequestUtil.setRequestUser(requestUser);
    }
}
