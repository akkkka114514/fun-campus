package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import com.akkkka.admin.module.business.funcampus.MybatisPlusTestRegistry;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 活动审核日志 校验器单元测试
 * <p>
 * 覆盖：审核阶段顺序校验、审核动作权限（审阅人只有建议权）、当前审核人身份校验
 *
 * @Author akkkka114514
 * @Date 2026-09-03
 */
@ExtendWith(MockitoExtension.class)
public class ActivityReviewLogValidatorTest {

    static {
        // validateCurrentReviewStage/validateNextReviewStage 的 select 会立即解析实体列
        MybatisPlusTestRegistry.register(ActivityReviewLogEntity.class);
    }

    @Mock
    private ActivityReviewLogManager reviewLogManager;
    @Mock
    private BackendUserManager backendUserManager;

    @InjectMocks
    private ActivityReviewLogValidator validator;

    private ActivityReviewLogEntity logEntity(ActivityReviewStage stage, ActivityReviewEvent action, Long reviewerId) {
        ActivityReviewLogEntity entity = new ActivityReviewLogEntity();
        entity.setId(1L);
        entity.setActivityId(7L);
        entity.setReviewStage(stage);
        entity.setAction(action);
        entity.setReviewerId(reviewerId);
        entity.setDeletedFlag(false);
        return entity;
    }

    private BackendUserEntity backendUser(boolean canReview) {
        BackendUserEntity user = new BackendUserEntity();
        user.setId(2L);
        user.setUsername("reviewer2");
        user.setCanReview(canReview);
        user.setDeletedFlag(false);
        user.setDisabledFlag(false);
        return user;
    }

    @Test
    void validateReviewLogId_whenNotFound_throwParamError() {
        when(reviewLogManager.getById(99L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validateReviewLogId(99L));
        assertEquals(UserErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void validateReviewLogId_whenExists_returnEntity() {
        ActivityReviewLogEntity entity = logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L);
        when(reviewLogManager.getById(1L)).thenReturn(entity);
        assertSame(entity, validator.validateReviewLogId(1L));
    }

    @Test
    void validateCurrentReviewStage_whenStageLogNotExist_throwParamError() {
        when(reviewLogManager.getOne(any())).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateCurrentReviewStage(7L, ActivityReviewStage.INITIAL_CONTENT_REVIEW));
        assertTrue(ex.getMessage().contains("不存在activityId"));
    }

    @Test
    void validateCurrentReviewStage_whenStageAlreadyFinished_throwParamError() {
        // action 非 null 表示该阶段已出结果
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, ActivityReviewEvent.INITIAL_REVIEW_PASS, 2L));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateCurrentReviewStage(7L, ActivityReviewStage.INITIAL_CONTENT_REVIEW));
        assertTrue(ex.getMessage().contains("该审核阶段已结束"));
    }

    @Test
    void validateCurrentReviewStage_whenStageInProgress_pass() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        assertDoesNotThrow(() -> validator.validateCurrentReviewStage(7L, ActivityReviewStage.INITIAL_CONTENT_REVIEW));
    }

    @Test
    void validateNextReviewStage_whenOrderNotContinuous_throwParamError() {
        // 数据库最新阶段是初审(order=1)，下一阶段却传终审(order=3)，跳过了审阅阶段
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        assertThrows(BusinessException.class,
                () -> validator.validateNextReviewStage(7L, ActivityReviewStage.FINAL_CONTENT_REVIEW));
    }

    @Test
    void validateNextReviewStage_whenOrderContinuous_pass() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        assertDoesNotThrow(() -> validator.validateNextReviewStage(7L, ActivityReviewStage.CONTENT_CHECK));
    }

    @Test
    void validateReviewAction_inContentCheckStage_onlyCheckPassAllowed() {
        // 审阅(建议)阶段：只允许 CHECK_PASS
        assertDoesNotThrow(() -> validator.validateReviewAction(ActivityReviewStage.CONTENT_CHECK, ActivityReviewEvent.CHECK_PASS));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateReviewAction(ActivityReviewStage.CONTENT_CHECK, ActivityReviewEvent.FINAL_REVIEW_PASS));
        assertEquals(UserErrorCode.NO_PERMISSION.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("建议之外的权限"));
    }

    @Test
    void validateReviewAction_inOtherStage_checkPassNotAllowed() {
        // 非审阅阶段：不允许 CHECK_PASS
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateReviewAction(ActivityReviewStage.INITIAL_CONTENT_REVIEW, ActivityReviewEvent.CHECK_PASS));
        assertEquals(UserErrorCode.NO_PERMISSION.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("建议权限"));
        assertDoesNotThrow(() -> validator.validateReviewAction(ActivityReviewStage.INITIAL_CONTENT_REVIEW,
                ActivityReviewEvent.INITIAL_REVIEW_PASS));
        assertDoesNotThrow(() -> validator.validateReviewAction(ActivityReviewStage.FINAL_CONTENT_REVIEW,
                ActivityReviewEvent.FINAL_REVIEW_PASS));
    }

    @Test
    void validateReviewPermission_whenBackendUserCannotReview_throwNoPermission() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateReviewPermission(backendUser(false)));
        assertEquals(UserErrorCode.NO_PERMISSION.getCode(), ex.getCode());
    }

    @Test
    void validateReviewerName_whenMismatch_throwParamError() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateReviewerName("someone", backendUser(true)));
        assertTrue(ex.getMessage().contains("不一致"));
    }

    @Test
    void validateReviewerName_whenMatch_pass() {
        assertDoesNotThrow(() -> validator.validateReviewerName("reviewer2", backendUser(true)));
    }

    @Test
    void isCurrentReviewFinished_noLog_returnFalse() {
        when(reviewLogManager.getOne(any())).thenReturn(null);
        assertFalse(validator.isCurrentReviewFinished(7L));
    }

    @Test
    void isCurrentReviewFinished_actionNull_returnFalse() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        assertFalse(validator.isCurrentReviewFinished(7L));
    }

    @Test
    void isCurrentReviewFinished_actionNotNull_returnTrue() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, ActivityReviewEvent.INITIAL_REVIEW_PASS, 2L));
        assertTrue(validator.isCurrentReviewFinished(7L));
    }

    @Test
    void validateReviewerIdentity_whenNoLatestLog_throwNoPermission() {
        when(reviewLogManager.getOne(any())).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateReviewerIdentity(7L, 2L, "reviewer2"));
        assertEquals(UserErrorCode.NO_PERMISSION.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("您不是当前审核人"));
    }

    @Test
    void validateReviewerIdentity_whenNotCurrentReviewer_throwNoPermission() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 5L));
        assertThrows(BusinessException.class, () -> validator.validateReviewerIdentity(7L, 2L, "reviewer2"));
    }

    @Test
    void validateReviewerIdentity_whenCurrentStageFinished_throwNoPermission() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, ActivityReviewEvent.INITIAL_REVIEW_PASS, 2L));
        assertThrows(BusinessException.class, () -> validator.validateReviewerIdentity(7L, 2L, "reviewer2"));
    }

    @Test
    void validateReviewerIdentity_whenBackendUserInvalid_throwNoPermission() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        BackendUserEntity deleted = backendUser(true);
        deleted.setDeletedFlag(true);
        when(backendUserManager.getById(2L)).thenReturn(deleted);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateReviewerIdentity(7L, 2L, "reviewer2"));
        assertTrue(ex.getMessage().contains("不是有效的后台用户"));
    }

    @Test
    void validateReviewerIdentity_whenNameMismatch_throwParamError() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        when(backendUserManager.getById(2L)).thenReturn(backendUser(true));
        assertThrows(BusinessException.class, () -> validator.validateReviewerIdentity(7L, 2L, "wrong-name"));
    }

    @Test
    void validateReviewerIdentity_whenAllValid_returnBackendUser() {
        when(reviewLogManager.getOne(any()))
                .thenReturn(logEntity(ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        BackendUserEntity user = backendUser(true);
        when(backendUserManager.getById(2L)).thenReturn(user);
        assertSame(user, validator.validateReviewerIdentity(7L, 2L, "reviewer2"));
    }
}
