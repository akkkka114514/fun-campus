package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityFavorite.domain.entity.ActivityFavoriteEntity;
import com.akkkka.admin.module.business.funcampus.activityFavorite.manager.ActivityFavoriteManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.module.support.message.constant.MessageTemplateEnum;
import com.akkkka.module.support.message.domain.MessageTemplateSendForm;
import com.akkkka.module.support.message.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 活动状态变更站内信 单元测试
 * <p>
 * 覆盖：报名开始通知收藏者（去重、群发一次）、活动即将开始/已结束通知有效报名者、
 * 无接收人跳过、报名结束等中间节点不发送、消息发送失败与查询异常均不影响主流程。
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 */
@ExtendWith(MockitoExtension.class)
public class ActivityStatusNoticeServiceTest {

    @Mock
    private ActivityFavoriteManager activityFavoriteManager;
    @Mock
    private ActivityEnrollmentManager activityEnrollmentManager;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private ActivityStatusNoticeService activityStatusNoticeService;

    private ActivityEntity activity(Long id, String title) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(id);
        activity.setTitle(title);
        return activity;
    }

    private ActivityFavoriteEntity favorite(Long activityId, Long userId) {
        ActivityFavoriteEntity favorite = new ActivityFavoriteEntity();
        favorite.setActivityId(activityId);
        favorite.setUserId(userId);
        return favorite;
    }

    private ActivityEnrollmentEntity enrollment(Long activityId, Long userId) {
        ActivityEnrollmentEntity enrollment = new ActivityEnrollmentEntity();
        enrollment.setActivityId(activityId);
        enrollment.setUserId(userId);
        return enrollment;
    }

    private void mockFavorites(List<ActivityFavoriteEntity> favorites) {
        LambdaQueryWrapper<ActivityFavoriteEntity> qw = new LambdaQueryWrapper<>();
        when(activityFavoriteManager.qwByActivityId(1L)).thenReturn(qw);
        when(activityFavoriteManager.list(qw)).thenReturn(favorites);
    }

    private void mockEnrollments(List<ActivityEnrollmentEntity> enrollments) {
        LambdaQueryWrapper<ActivityEnrollmentEntity> qw = new LambdaQueryWrapper<>();
        when(activityEnrollmentManager.qwByActivityId(1L)).thenReturn(qw);
        when(activityEnrollmentManager.list(qw)).thenReturn(enrollments);
    }

    @Test
    void notify_whenAdvanceToEnrolling_sendToFavoritesOnce() {
        mockFavorites(List.of(favorite(1L, 20L), favorite(1L, 20L), favorite(1L, 21L)));

        activityStatusNoticeService.notifyStatusAdvanced(activity(1L, "春游活动"), ActivityStatus.ENROLLING);

        // 收藏用户按活动查询、去重后群发一次
        verify(activityFavoriteManager).qwByActivityId(1L);
        ArgumentCaptor<MessageTemplateSendForm> captor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(captor.capture());
        MessageTemplateSendForm form = captor.getValue();
        assertEquals(MessageTemplateEnum.ACTIVITY_ENROLL_START, form.getMessageTemplateEnum());
        assertEquals(UserTypeEnum.PORTAL_USER, form.getReceiverUserType());
        assertEquals(List.of(20L, 21L), form.getReceiverUserIdList());
        assertEquals(1L, form.getDataId());
        assertEquals("春游活动", form.getContentParam().get("activityTitle"));
    }

    @Test
    void notify_whenAdvanceToEnrolling_noFavorite_skipSend() {
        mockFavorites(List.of());

        activityStatusNoticeService.notifyStatusAdvanced(activity(1L, "春游活动"), ActivityStatus.ENROLLING);

        verify(messageService, never()).sendTemplateMessage(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void notify_whenAdvanceToOngoing_sendAboutToStartToEnrolled() {
        mockEnrollments(List.of(enrollment(1L, 30L), enrollment(1L, 31L)));

        activityStatusNoticeService.notifyStatusAdvanced(activity(1L, "羽毛球比赛"), ActivityStatus.ONGOING);

        verify(activityEnrollmentManager).qwByActivityId(1L);
        ArgumentCaptor<MessageTemplateSendForm> captor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(captor.capture());
        MessageTemplateSendForm form = captor.getValue();
        assertEquals(MessageTemplateEnum.ACTIVITY_ABOUT_TO_START, form.getMessageTemplateEnum());
        assertEquals(List.of(30L, 31L), form.getReceiverUserIdList());
        assertEquals("羽毛球比赛", form.getContentParam().get("activityTitle"));
    }

    @Test
    void notify_whenAdvanceToFinished_sendFinishedToEnrolled() {
        mockEnrollments(List.of(enrollment(1L, 30L)));

        activityStatusNoticeService.notifyStatusAdvanced(activity(1L, "羽毛球比赛"), ActivityStatus.FINISHED);

        ArgumentCaptor<MessageTemplateSendForm> captor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(captor.capture());
        assertEquals(MessageTemplateEnum.ACTIVITY_FINISHED, captor.getValue().getMessageTemplateEnum());
        assertEquals(List.of(30L), captor.getValue().getReceiverUserIdList());
        assertEquals(1L, captor.getValue().getDataId());
    }

    @Test
    void notify_whenAdvanceToOngoing_noEnrollment_skipSend() {
        mockEnrollments(List.of());

        activityStatusNoticeService.notifyStatusAdvanced(activity(1L, "羽毛球比赛"), ActivityStatus.ONGOING);

        verify(messageService, never()).sendTemplateMessage(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void notify_whenAdvanceToEnrollEnded_noSend() {
        // 报名结束为中间节点，不发送通知，且不触发任何接收人查询
        activityStatusNoticeService.notifyStatusAdvanced(activity(1L, "春游活动"), ActivityStatus.ENROLL_ENDED);

        verify(activityFavoriteManager, never()).qwByActivityId(org.mockito.ArgumentMatchers.anyLong());
        verify(activityEnrollmentManager, never()).qwByActivityId(org.mockito.ArgumentMatchers.anyLong());
        verify(messageService, never()).sendTemplateMessage(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void notify_whenTitleMissing_useEmptyTitle() {
        mockEnrollments(List.of(enrollment(1L, 30L)));

        activityStatusNoticeService.notifyStatusAdvanced(activity(1L, null), ActivityStatus.FINISHED);

        ArgumentCaptor<MessageTemplateSendForm> captor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(captor.capture());
        assertEquals("", captor.getValue().getContentParam().get("activityTitle"));
    }

    @Test
    void notify_whenSendFails_swallowException() {
        mockEnrollments(List.of(enrollment(1L, 30L)));
        doThrow(new RuntimeException("消息服务不可用")).when(messageService)
                .sendTemplateMessage(org.mockito.ArgumentMatchers.any(MessageTemplateSendForm.class));

        // 站内信发送失败仅日志，不影响状态推进主流程
        assertDoesNotThrow(() -> activityStatusNoticeService
                .notifyStatusAdvanced(activity(1L, "羽毛球比赛"), ActivityStatus.FINISHED));
    }

    @Test
    void notify_whenReceiverQueryFails_swallowException() {
        doThrow(new RuntimeException("db down")).when(activityEnrollmentManager)
                .qwByActivityId(org.mockito.ArgumentMatchers.anyLong());

        assertDoesNotThrow(() -> activityStatusNoticeService
                .notifyStatusAdvanced(activity(1L, "羽毛球比赛"), ActivityStatus.ONGOING));

        verify(messageService, never()).sendTemplateMessage(org.mockito.ArgumentMatchers.any());
    }
}
