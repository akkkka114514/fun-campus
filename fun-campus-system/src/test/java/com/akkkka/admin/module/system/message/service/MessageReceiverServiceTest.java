package com.akkkka.admin.module.system.message.service;

import com.akkkka.admin.module.system.MybatisPlusTestRegistry;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.admin.module.system.message.domain.MessageReceiverQueryForm;
import com.akkkka.admin.module.system.message.domain.MessageReceiverVO;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 消息接收人查询服务 单元测试
 * <p>
 * 覆盖：按接收人类型分发（后台直查 backend_user、前台委托 PortalMessageReceiverService）、
 * 未支持类型（组织干事）报错
 *
 * @Author akkkka114514
 * @Date 2026-09-22
 */
@ExtendWith(MockitoExtension.class)
class MessageReceiverServiceTest {

    static {
        // select 会立即解析实体列，需注册 TableInfo
        MybatisPlusTestRegistry.register(BackendUserEntity.class);
    }

    @Mock
    private BackendUserManager backendUserManager;
    @Mock
    private PortalMessageReceiverService portalMessageReceiverService;

    private MessageReceiverService service;

    @BeforeEach
    void setUp() {
        service = new MessageReceiverService(backendUserManager, portalMessageReceiverService);
    }

    private MessageReceiverQueryForm queryForm(Integer receiverUserType, String keyword) {
        MessageReceiverQueryForm form = new MessageReceiverQueryForm();
        form.setReceiverUserType(receiverUserType);
        form.setKeyword(keyword);
        form.setPageNum(1L);
        form.setPageSize(10L);
        return form;
    }

    @Test
    void queryReceiverPage_whenBackendUserType_queryBackendUserAndMapVo() {
        BackendUserEntity backendUser = new BackendUserEntity();
        backendUser.setId(5L);
        backendUser.setUsername("管理员甲");
        Page<BackendUserEntity> resultPage = new Page<>(1, 10);
        resultPage.setRecords(List.of(backendUser));
        resultPage.setTotal(1L);
        doReturn(resultPage).when(backendUserManager).page(any(Page.class), any());

        PageResult<MessageReceiverVO> result = service.queryReceiverPage(
                queryForm(UserTypeEnum.ADMIN_BACKEND_USER.getValue(), "管理"));

        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());
        MessageReceiverVO vo = result.getList().get(0);
        assertEquals(5L, vo.getId());
        assertEquals(UserTypeEnum.ADMIN_BACKEND_USER.getValue(), vo.getUserType());
        assertEquals("管理员甲", vo.getUsername());
        // 后台类型不应委托前台查询
        verify(portalMessageReceiverService, never()).queryPortalReceiverPage(any());
    }

    @Test
    void queryReceiverPage_whenPortalUserType_delegateToPortalMessageReceiverService() {
        PageResult<MessageReceiverVO> portalResult = new PageResult<>();
        MessageReceiverVO vo = new MessageReceiverVO();
        vo.setId(9L);
        vo.setUserType(UserTypeEnum.PORTAL_USER.getValue());
        vo.setUsername("学生乙");
        vo.setPhone("13800000000");
        portalResult.setList(List.of(vo));
        portalResult.setTotal(1L);
        doReturn(portalResult).when(portalMessageReceiverService).queryPortalReceiverPage(any());

        PageResult<MessageReceiverVO> result = service.queryReceiverPage(
                queryForm(UserTypeEnum.PORTAL_USER.getValue(), null));

        assertEquals(1, result.getList().size());
        assertEquals("学生乙", result.getList().get(0).getUsername());
        // 门户类型不应触达 backend_user
        verify(backendUserManager, never()).page(any(Page.class), any());
    }

    @Test
    void queryReceiverPage_whenOrganizerCadreType_throwBusinessException() {
        // 组织干事(3)暂未接入接收人检索
        BusinessException ex = assertThrows(BusinessException.class, () -> service.queryReceiverPage(
                queryForm(UserTypeEnum.ORGANIZER_CADRE.getValue(), null)));
        assertTrue(ex.getMessage().contains("不支持的接收人类型"));
        verify(backendUserManager, never()).page(any(Page.class), any());
        verify(portalMessageReceiverService, never()).queryPortalReceiverPage(any());
    }
}
