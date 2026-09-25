package com.akkkka.admin.module.system.message.service;

import com.akkkka.admin.module.business.funcampus.MybatisPlusTestRegistry;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
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
 * 覆盖：按接收人类型分发到 backend_user/portal_user、分页结果与 VO 映射、
 * 未支持类型（组织干事）报错
 *
 * @Author akkkka114514
 * @Date 2026-09-22
 */
@ExtendWith(MockitoExtension.class)
class MessageReceiverServiceTest {

    static {
        // select 会立即解析实体列，需注册 TableInfo
        MybatisPlusTestRegistry.register(BackendUserEntity.class, PortalUserEntity.class);
    }

    @Mock
    private BackendUserManager backendUserManager;
    @Mock
    private PortalUserManager portalUserManager;

    private MessageReceiverService service;

    @BeforeEach
    void setUp() {
        service = new MessageReceiverService(backendUserManager, portalUserManager);
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
        // 后台类型不应触达 portal_user
        verify(portalUserManager, never()).page(any(Page.class), any());
    }

    @Test
    void queryReceiverPage_whenPortalUserType_queryPortalUserAndMapVoWithPhone() {
        PortalUserEntity student = new PortalUserEntity();
        student.setId(9L);
        student.setUsername("学生乙");
        student.setPhone("13800000000");
        Page<PortalUserEntity> resultPage = new Page<>(1, 10);
        resultPage.setRecords(List.of(student));
        resultPage.setTotal(1L);
        doReturn(resultPage).when(portalUserManager).page(any(Page.class), any());

        PageResult<MessageReceiverVO> result = service.queryReceiverPage(
                queryForm(UserTypeEnum.PORTAL_USER.getValue(), null));

        MessageReceiverVO vo = result.getList().get(0);
        assertEquals(9L, vo.getId());
        assertEquals(UserTypeEnum.PORTAL_USER.getValue(), vo.getUserType());
        assertEquals("学生乙", vo.getUsername());
        assertEquals("13800000000", vo.getPhone());
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
        verify(portalUserManager, never()).page(any(Page.class), any());
    }
}
