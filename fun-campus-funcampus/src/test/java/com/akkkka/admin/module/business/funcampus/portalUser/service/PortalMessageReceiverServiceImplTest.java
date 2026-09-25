package com.akkkka.admin.module.business.funcampus.portalUser.service;

import com.akkkka.admin.module.business.funcampus.MybatisPlusTestRegistry;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.system.message.domain.MessageReceiverQueryForm;
import com.akkkka.admin.module.system.message.domain.MessageReceiverVO;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.enumeration.UserTypeEnum;
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
 * 前台用户消息接收人查询实现 单元测试
 * <p>
 * 覆盖：portal_user 分页结果到 MessageReceiverVO 的映射（含手机号）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 */
@ExtendWith(MockitoExtension.class)
class PortalMessageReceiverServiceImplTest {

    static {
        // select 会立即解析实体列，需注册 TableInfo
        MybatisPlusTestRegistry.register(PortalUserEntity.class);
    }

    @Mock
    private PortalUserManager portalUserManager;

    private PortalMessageReceiverServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PortalMessageReceiverServiceImpl(portalUserManager);
    }

    @Test
    void queryPortalReceiverPage_mapsVoWithPhone() {
        PortalUserEntity student = new PortalUserEntity();
        student.setId(9L);
        student.setUsername("学生乙");
        student.setPhone("13800000000");
        Page<PortalUserEntity> resultPage = new Page<>(1, 10);
        resultPage.setRecords(List.of(student));
        resultPage.setTotal(1L);
        doReturn(resultPage).when(portalUserManager).page(any(Page.class), any());

        MessageReceiverQueryForm form = new MessageReceiverQueryForm();
        form.setReceiverUserType(UserTypeEnum.PORTAL_USER.getValue());
        form.setPageNum(1L);
        form.setPageSize(10L);

        PageResult<MessageReceiverVO> result = service.queryPortalReceiverPage(form);

        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());
        MessageReceiverVO vo = result.getList().get(0);
        assertEquals(9L, vo.getId());
        assertEquals(UserTypeEnum.PORTAL_USER.getValue(), vo.getUserType());
        assertEquals("学生乙", vo.getUsername());
        assertEquals("13800000000", vo.getPhone());
    }
}
