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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 活动状态变更站内信服务：由 ActivityStatusUpdateJob 在状态前进成功后调用
 * <p>
 * 节点与接收人：
 * - 推进到「报名中」→ 通知收藏该活动的学生（报名开始）
 * - 推进到「进行中」→ 通知有效报名的学生（活动即将开始）
 * - 推进到「已结束」→ 通知有效报名的学生（活动已结束）
 * <p>
 * 发送失败仅记录日志，不影响状态推进与名单消费确认；状态倒退不发送通知
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityStatusNoticeService {

    private final ActivityFavoriteManager activityFavoriteManager;

    private final ActivityEnrollmentManager activityEnrollmentManager;

    private final MessageService messageService;

    /**
     * 状态前进成功后发送对应节点站内信；非通知节点（如报名结束）不发送
     *
     * @param activity       已推进状态的活动（含标题）
     * @param expectedStatus 推进后的状态
     */
    public void notifyStatusAdvanced(ActivityEntity activity, ActivityStatus expectedStatus) {
        try {
            switch (expectedStatus) {
                case ENROLLING -> notifyEnrollStart(activity);
                case ONGOING -> notifyAboutToStart(activity);
                case FINISHED -> notifyFinished(activity);
                default -> {
                    // 报名结束等中间节点不发送通知
                }
            }
        } catch (Exception e) {
            log.warn("活动状态变更站内信发送失败，activityId:{}，status:{}", activity.getId(), expectedStatus, e);
        }
    }

    /**
     * 报名开始：通知收藏该活动的学生
     */
    private void notifyEnrollStart(ActivityEntity activity) {
        List<Long> receiverIds = activityFavoriteManager.list(activityFavoriteManager.qwByActivityId(activity.getId()))
                .stream()
                .map(ActivityFavoriteEntity::getUserId)
                .distinct()
                .toList();
        send(activity, receiverIds, MessageTemplateEnum.ACTIVITY_ENROLL_START);
    }

    /**
     * 活动即将开始：通知有效报名的学生
     */
    private void notifyAboutToStart(ActivityEntity activity) {
        send(activity, listEnrolledUserIds(activity.getId()), MessageTemplateEnum.ACTIVITY_ABOUT_TO_START);
    }

    /**
     * 活动已结束：通知有效报名的学生
     */
    private void notifyFinished(ActivityEntity activity) {
        send(activity, listEnrolledUserIds(activity.getId()), MessageTemplateEnum.ACTIVITY_FINISHED);
    }

    private List<Long> listEnrolledUserIds(Long activityId) {
        return activityEnrollmentManager.list(activityEnrollmentManager.qwByActivityId(activityId))
                .stream()
                .map(ActivityEnrollmentEntity::getUserId)
                .distinct()
                .toList();
    }

    private void send(ActivityEntity activity, List<Long> receiverIds, MessageTemplateEnum template) {
        if (receiverIds.isEmpty()) {
            log.info("活动状态变更站内信跳过（无接收人）：activityId:{}，template:{}", activity.getId(), template.name());
            return;
        }
        MessageTemplateSendForm sendForm = new MessageTemplateSendForm();
        sendForm.setMessageTemplateEnum(template);
        sendForm.setReceiverUserType(UserTypeEnum.PORTAL_USER);
        sendForm.setReceiverUserIdList(receiverIds);
        sendForm.setDataId(activity.getId());
        sendForm.setContentParam(Map.of("activityTitle", Objects.toString(activity.getTitle(), "")));
        messageService.sendTemplateMessage(sendForm);
        log.info("活动状态变更站内信已发送：activityId:{}，template:{}，接收人{}人",
                activity.getId(), template.name(), receiverIds.size());
    }

}
