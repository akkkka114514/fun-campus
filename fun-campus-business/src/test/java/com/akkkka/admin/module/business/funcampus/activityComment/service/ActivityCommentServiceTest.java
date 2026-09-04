package com.akkkka.admin.module.business.funcampus.activityComment.service;

import com.akkkka.admin.module.business.funcampus.activityComment.dao.ActivityCommentHotDao;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.entity.ActivityCommentEntity;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.form.ActivityCommentAddForm;
import com.akkkka.admin.module.business.funcampus.activityComment.manager.ActivityCommentHotManager;
import com.akkkka.admin.module.business.funcampus.activityComment.manager.ActivityCommentManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 活动评论 Service 单元测试
 * <p>
 * 覆盖：发表评论的 rootId 推导（根评论/回复根评论/回复子评论）、删除评论
 * 的内容替换与逻辑删除、点赞/撤销点赞的评论存在性校验
 *
 * @Author akkkka114514
 * @Date 2026-09-03
 */
@ExtendWith(MockitoExtension.class)
public class ActivityCommentServiceTest {

    @Mock
    private ActivityCommentManager activityCommentManager;
    @Mock
    private ActivityCommentHotManager activityCommentHotManager;
    @Mock
    private ActivityCommentHotDao activityCommentHotDao;
    @Mock
    private ActivityValidator activityValidator;

    @InjectMocks
    private ActivityCommentService service;

    @AfterEach
    void cleanRequestUser() {
        SmartRequestUtil.remove();
    }

    private void setPortalUser(Long userId) {
        RequestPortalUser portalUser = new RequestPortalUser();
        portalUser.setId(userId);
        portalUser.setUsername("student" + userId);
        SmartRequestUtil.setRequestUser(portalUser);
    }

    private ActivityCommentEntity comment(Long id, Long rootId, Long toCommentId, Long userId) {
        ActivityCommentEntity entity = new ActivityCommentEntity();
        entity.setId(id);
        entity.setRootId(rootId);
        entity.setToCommentId(toCommentId);
        entity.setUserId(userId);
        entity.setDeleted(false);
        return entity;
    }

    private ActivityCommentAddForm addForm(Long activityId, Long toCommentId, String content) {
        ActivityCommentAddForm form = new ActivityCommentAddForm();
        form.setActivityId(activityId);
        form.setToCommentId(toCommentId);
        form.setContent(content);
        return form;
    }

    @Test
    void addComment_whenNotPortalUser_throwNoPermission() {
        SmartRequestUtil.remove();
        assertThrows(BusinessException.class,
                () -> service.addComment(addForm(7L, null, "太棒了")));
    }

    @Test
    void addComment_whenRootComment_saveWithNullRootId() {
        setPortalUser(12L);
        service.addComment(addForm(7L, null, "太棒了"));

        ArgumentCaptor<ActivityCommentEntity> captor = ArgumentCaptor.forClass(ActivityCommentEntity.class);
        verify(activityCommentManager).save(captor.capture());
        ActivityCommentEntity saved = captor.getValue();
        assertEquals(7L, saved.getActivityId());
        assertEquals(12L, saved.getUserId());
        assertNull(saved.getToCommentId());
        assertNull(saved.getRootId());
        assertEquals("太棒了", saved.getContent());
        assertFalse(saved.getDeleted());
    }

    @Test
    void addComment_whenReplyRootComment_rootIdEqualsParentId() {
        setPortalUser(12L);
        // 父评论是根评论（rootId 为 null），回复它的评论 rootId 应为父评论 id
        when(activityCommentManager.getById(5L)).thenReturn(comment(5L, null, null, 1L));

        service.addComment(addForm(7L, 5L, "同意"));

        ArgumentCaptor<ActivityCommentEntity> captor = ArgumentCaptor.forClass(ActivityCommentEntity.class);
        verify(activityCommentManager).save(captor.capture());
        assertEquals(5L, captor.getValue().getToCommentId());
        assertEquals(5L, captor.getValue().getRootId());
    }

    @Test
    void addComment_whenReplyChildComment_rootIdInheritedFromParent() {
        setPortalUser(12L);
        // 父评论是子评论（rootId=5），回复它的评论 rootId 继续继承 5
        when(activityCommentManager.getById(8L)).thenReturn(comment(8L, 5L, 5L, 2L));

        service.addComment(addForm(7L, 8L, "再+1"));

        ArgumentCaptor<ActivityCommentEntity> captor = ArgumentCaptor.forClass(ActivityCommentEntity.class);
        verify(activityCommentManager).save(captor.capture());
        assertEquals(5L, captor.getValue().getRootId());
        assertEquals(8L, captor.getValue().getToCommentId());
    }

    @Test
    void addComment_whenParentDeleted_throwParamError() {
        setPortalUser(12L);
        ActivityCommentEntity deleted = comment(5L, null, null, 1L);
        deleted.setDeleted(true);
        when(activityCommentManager.getById(5L)).thenReturn(deleted);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.addComment(addForm(7L, 5L, "回复被删除的评论")));
        assertTrue(ex.getMessage().contains("被回复的评论不存在"));
        verify(activityCommentManager, never()).save(any());
    }

    @Test
    void addComment_whenParentNotExist_throwParamError() {
        setPortalUser(12L);
        when(activityCommentManager.getById(9L)).thenReturn(null);

        assertThrows(BusinessException.class,
                () -> service.addComment(addForm(7L, 9L, "回复不存在的评论")));
    }

    @Test
    void deleteComment_whenNotOwnerOrNotExist_throwParamError() {
        setPortalUser(12L);
        when(activityCommentManager.getOne(any())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deleteComment(1L));
        assertTrue(ex.getMessage().contains("评论不存在或无权删除"));
        verify(activityCommentManager, never()).updateById(any());
    }

    @Test
    void deleteComment_whenOwner_replaceContentAndSetDeleted() {
        setPortalUser(12L);
        when(activityCommentManager.getOne(any())).thenReturn(comment(1L, null, null, 12L));

        service.deleteComment(1L);

        ArgumentCaptor<ActivityCommentEntity> captor = ArgumentCaptor.forClass(ActivityCommentEntity.class);
        verify(activityCommentManager).updateById(captor.capture());
        assertEquals("内容已删除", captor.getValue().getContent());
        assertTrue(captor.getValue().getDeleted());
    }

    @Test
    void likeComment_whenCommentNotExist_throwParamError() {
        when(activityCommentManager.getById(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> service.likeComment(1L));
        verify(activityCommentHotDao, never()).likeComment(any());
    }

    @Test
    void likeComment_whenCommentDeleted_throwParamError() {
        ActivityCommentEntity deleted = comment(1L, null, null, 12L);
        deleted.setDeleted(true);
        when(activityCommentManager.getById(1L)).thenReturn(deleted);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.likeComment(1L));
        assertTrue(ex.getMessage().contains("评论不存在"));
    }

    @Test
    void likeComment_whenValid_increaseHot() {
        when(activityCommentManager.getById(1L)).thenReturn(comment(1L, null, null, 12L));
        service.likeComment(1L);
        verify(activityCommentHotDao).likeComment(1L);
    }

    @Test
    void unlikeComment_whenCommentNotExist_throwParamError() {
        when(activityCommentManager.getById(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> service.unlikeComment(1L));
        verify(activityCommentHotDao, never()).unlikeComment(any());
    }

    @Test
    void unlikeComment_whenValid_decreaseHot() {
        when(activityCommentManager.getById(1L)).thenReturn(comment(1L, null, null, 12L));
        service.unlikeComment(1L);
        verify(activityCommentHotDao).unlikeComment(1L);
    }

    @Test
    void likeComment_throwErrorCodeIsParamError() {
        when(activityCommentManager.getById(1L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.likeComment(1L));
        assertEquals(UserErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }
}
