package com.akkkka.admin.module.system.notice.service;

import com.akkkka.admin.module.system.notice.dao.NoticeDao;
import com.akkkka.admin.module.system.notice.domain.form.PortalNoticeQueryForm;
import com.akkkka.admin.module.system.notice.domain.vo.PortalNoticeDetailVO;
import com.akkkka.admin.module.system.notice.domain.vo.PortalNoticeVO;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 通知公告 门户查看服务 单元测试
 * <p>
 * 覆盖：列表分页透传、详情浏览量累加、浏览量空值兜底、公告不存在报错
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 */
@ExtendWith(MockitoExtension.class)
class PortalNoticeServiceTest {

    @Mock
    private NoticeDao noticeDao;

    private PortalNoticeService service;

    @BeforeEach
    void setUp() {
        service = new PortalNoticeService(noticeDao);
    }

    @Test
    void queryNotice_returnsPageResultFromDao() {
        PortalNoticeVO noticeVO = new PortalNoticeVO();
        noticeVO.setNoticeId(49L);
        noticeVO.setTitle("测试公告");
        when(noticeDao.queryPortalNotice(any(), any())).thenReturn(List.of(noticeVO));

        PortalNoticeQueryForm form = new PortalNoticeQueryForm();
        form.setPageNum(1L);
        form.setPageSize(10L);
        form.setSearchCount(true);
        ResponseDTO<PageResult<PortalNoticeVO>> response = service.queryNotice(form);

        assertTrue(response.getOk());
        assertEquals(List.of(noticeVO), response.getData().getList());
        // 查询条件原样透传给 dao（含分类、关键词）
        verify(noticeDao).queryPortalNotice(any(), eq(form));
    }

    @Test
    void detail_success_incrementsPageViewCount() {
        PortalNoticeDetailVO detailVO = new PortalNoticeDetailVO();
        detailVO.setNoticeId(49L);
        detailVO.setPageViewCount(5);
        when(noticeDao.getPortalNoticeById(49L)).thenReturn(detailVO);

        ResponseDTO<PortalNoticeDetailVO> response = service.detail(49L);

        assertTrue(response.getOk());
        assertEquals(6, response.getData().getPageViewCount());
        // 门户查看仅累加页面浏览量，不累加用户浏览量（不写查看记录）
        verify(noticeDao).updateViewCount(49L, 1, 0);
    }

    @Test
    void detail_nullPageViewCount_becomesOne() {
        PortalNoticeDetailVO detailVO = new PortalNoticeDetailVO();
        detailVO.setNoticeId(50L);
        detailVO.setPageViewCount(null);
        when(noticeDao.getPortalNoticeById(50L)).thenReturn(detailVO);

        ResponseDTO<PortalNoticeDetailVO> response = service.detail(50L);

        assertTrue(response.getOk());
        assertEquals(1, response.getData().getPageViewCount());
        verify(noticeDao).updateViewCount(50L, 1, 0);
    }

    @Test
    void detail_notFound_returnsUserErrorAndNoViewCountUpdate() {
        when(noticeDao.getPortalNoticeById(999L)).thenReturn(null);

        ResponseDTO<PortalNoticeDetailVO> response = service.detail(999L);

        assertFalse(response.getOk());
        assertEquals(UserErrorCode.PARAM_ERROR.getCode(), response.getCode());
        assertEquals("通知公告不存在", response.getMsg());
        verify(noticeDao, never()).updateViewCount(any(), any(), any());
    }
}
