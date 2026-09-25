package com.akkkka.admin.module.business.funcampus.portalUser.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.manager.ActivityCanEnrollCollegeManager;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.manager.ActivityCanEnrollGradeManager;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.manager.ActivityCanEnrollTribeManager;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.entity.TribeUserEntity;
import com.akkkka.admin.module.business.funcampus.tribeUser.manager.TribeUserManager;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * 前端用户 校验器单元测试
 * <p>
 * 覆盖：报名范围判定（学院/年级/部落：范围为空=全校可报、命中=可报、未命中=拒绝）
 * 与用户状态、归属校验
 *
 * @Author akkkka114514
 * @Date 2026-09-03
 */
@ExtendWith(MockitoExtension.class)
public class PortalUserValidatorTest {

    @Mock
    private PortalUserManager portalUserManager;
    @Mock
    private ActivityCanEnrollCollegeManager canEnrollCollegeManager;
    @Mock
    private ActivityCanEnrollGradeManager canEnrollGradeManager;
    @Mock
    private ActivityCanEnrollTribeManager canEnrollTribeManager;
    @Mock
    private TribeUserManager tribeUserManager;

    @InjectMocks
    private PortalUserValidator validator;

    @AfterEach
    void cleanRequestUser() {
        SmartRequestUtil.remove();
    }

    private PortalUserEntity portalUser(Long id, Long schoolId, Long collegeId, Long gradeId) {
        PortalUserEntity user = new PortalUserEntity();
        user.setId(id);
        user.setUsername("student" + id);
        user.setSchoolId(schoolId);
        user.setCollegeId(collegeId);
        user.setGradeId(gradeId);
        user.setDeletedFlag(false);
        user.setDisableFlag(false);
        return user;
    }

    private ActivityCanEnrollCollegeEntity collegeScope(Long collegeId) {
        ActivityCanEnrollCollegeEntity entity = new ActivityCanEnrollCollegeEntity();
        entity.setCanEnrollCollege(collegeId);
        return entity;
    }

    private ActivityCanEnrollGradeEntity gradeScope(Long gradeId) {
        ActivityCanEnrollGradeEntity entity = new ActivityCanEnrollGradeEntity();
        entity.setCanEnrollGrade(gradeId);
        return entity;
    }

    private ActivityCanEnrollTribeEntity tribeScope(Long tribeId) {
        ActivityCanEnrollTribeEntity entity = new ActivityCanEnrollTribeEntity();
        entity.setCanEnrollTribe(tribeId);
        return entity;
    }

    // ---------------------------------- 当前用户类型 ----------------------------------

    @Test
    void validateIsCurrentUserPortal_whenNoRequestUser_throwNoPermission() {
        SmartRequestUtil.remove();
        assertThrows(BusinessException.class, validator::validateIsCurrentUserPortal);
    }

    @Test
    void validateIsCurrentUserPortal_whenBackendUser_throwNoPermission() {
        SmartRequestUtil.setRequestUser(new RequestBackendUser());
        assertThrows(BusinessException.class, validator::validateIsCurrentUserPortal);
    }

    @Test
    void validateIsCurrentUserPortal_whenPortalUser_pass() {
        SmartRequestUtil.setRequestUser(new RequestPortalUser());
        assertDoesNotThrow(validator::validateIsCurrentUserPortal);
    }

    // ---------------------------------- 用户状态 ----------------------------------

    @Test
    void validatePortalUserId_whenNotExist_throwParamError() {
        when(portalUserManager.getById(1L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validatePortalUserId(1L));
        assertTrue(ex.getMessage().contains("用户不存在或已删除"));
    }

    @Test
    void validatePortalUserId_whenDeleted_throwParamError() {
        PortalUserEntity user = portalUser(1L, 1L, 1L, 1L);
        user.setDeletedFlag(true);
        when(portalUserManager.getById(1L)).thenReturn(user);
        assertThrows(BusinessException.class, () -> validator.validatePortalUserId(1L));
    }

    @Test
    void validatePortalUserId_whenDisabled_throwUserStatusError() {
        PortalUserEntity user = portalUser(1L, 1L, 1L, 1L);
        user.setDisableFlag(true);
        when(portalUserManager.getById(1L)).thenReturn(user);
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validatePortalUserId(1L));
        assertEquals(UserErrorCode.USER_STATUS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void validatePortalUserId_whenValid_returnUser() {
        PortalUserEntity user = portalUser(1L, 1L, 1L, 1L);
        when(portalUserManager.getById(1L)).thenReturn(user);
        assertSame(user, validator.validatePortalUserId(1L));
    }

    // ---------------------------------- 学院范围 ----------------------------------

    @Test
    void validateUserCanEnrollCollege_whenNoScope_pass() {
        when(canEnrollCollegeManager.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> validator.validateUserCanEnrollCollege(7L, portalUser(1L, 1L, 10L, 1L)));
    }

    @Test
    void validateUserCanEnrollCollege_whenUserCollegeInScope_pass() {
        when(canEnrollCollegeManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(collegeScope(10L)));
        assertDoesNotThrow(() -> validator.validateUserCanEnrollCollege(7L, portalUser(1L, 1L, 10L, 1L)));
    }

    @Test
    void validateUserCanEnrollCollege_whenUserCollegeOutOfScope_throwNoPermission() {
        when(canEnrollCollegeManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(collegeScope(20L)));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateUserCanEnrollCollege(7L, portalUser(1L, 1L, 10L, 1L)));
        assertEquals(UserErrorCode.NO_PERMISSION.getCode(), ex.getCode());
    }

    // ---------------------------------- 年级范围 ----------------------------------

    @Test
    void validateUserCanEnrollGrade_whenUserGradeInScope_pass() {
        when(canEnrollGradeManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(gradeScope(3L)));
        assertDoesNotThrow(() -> validator.validateUserCanEnrollGrade(7L, portalUser(1L, 1L, 10L, 3L)));
    }

    @Test
    void validateUserCanEnrollGrade_whenUserGradeOutOfScope_throwNoPermission() {
        when(canEnrollGradeManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(gradeScope(4L)));
        assertThrows(BusinessException.class,
                () -> validator.validateUserCanEnrollGrade(7L, portalUser(1L, 1L, 10L, 3L)));
    }

    // ---------------------------------- 部落范围 ----------------------------------

    @Test
    void validateUserCanEnrollTribe_whenNoScope_pass() {
        when(canEnrollTribeManager.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> validator.validateUserCanEnrollTribe(7L, portalUser(1L, 1L, 10L, 3L)));
    }

    @Test
    void validateUserCanEnrollTribe_whenUserNotInAnyTribe_throwNoPermission() {
        when(canEnrollTribeManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(tribeScope(5L)));
        when(tribeUserManager.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateUserCanEnrollTribe(7L, portalUser(1L, 1L, 10L, 3L)));
        assertEquals(UserErrorCode.NO_PERMISSION.getCode(), ex.getCode());
    }

    @Test
    void validateUserCanEnrollTribe_whenUserTribeOutOfScope_throwParamError() {
        when(canEnrollTribeManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(tribeScope(5L)));
        TribeUserEntity tribeUser = new TribeUserEntity();
        tribeUser.setTribeId(6L);
        when(tribeUserManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(tribeUser));
        assertThrows(BusinessException.class,
                () -> validator.validateUserCanEnrollTribe(7L, portalUser(1L, 1L, 10L, 3L)));
    }

    @Test
    void validateUserCanEnrollTribe_whenUserTribeInScope_pass() {
        when(canEnrollTribeManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(tribeScope(5L), tribeScope(6L)));
        TribeUserEntity tribeUser = new TribeUserEntity();
        tribeUser.setTribeId(6L);
        when(tribeUserManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(tribeUser));
        assertDoesNotThrow(() -> validator.validateUserCanEnrollTribe(7L, portalUser(1L, 1L, 10L, 3L)));
    }

    // ---------------------------------- 批量用户 ----------------------------------

    @Test
    void validatePortalUserIds_whenUserNotExist_throwParamError() {
        when(portalUserManager.listByIds(anyList())).thenReturn(List.of(portalUser(1L, 1L, 1L, 1L)));
        assertThrows(BusinessException.class, () -> validator.validatePortalUserIds(List.of(1L, 2L)));
    }

    @Test
    void validatePortalUserIds_whenUserDeletedOrDisabled_throwParamError() {
        PortalUserEntity disabled = portalUser(2L, 1L, 1L, 1L);
        disabled.setDisableFlag(true);
        when(portalUserManager.listByIds(anyList())).thenReturn(List.of(portalUser(1L, 1L, 1L, 1L), disabled));
        assertThrows(BusinessException.class, () -> validator.validatePortalUserIds(List.of(1L, 2L)));
    }

    @Test
    void validatePortalUserIds_whenAllValid_pass() {
        when(portalUserManager.listByIds(anyList()))
                .thenReturn(List.of(portalUser(1L, 1L, 1L, 1L), portalUser(2L, 1L, 1L, 1L)));
        assertDoesNotThrow(() -> validator.validatePortalUserIds(List.of(1L, 2L)));
    }

    // ---------------------------------- 归属校验 ----------------------------------

    @Test
    void validateUserInSchool_whenSameSchool_pass() {
        assertDoesNotThrow(() -> validator.validateUserInSchool(portalUser(1L, 1L, 1L, 1L), 1L));
    }

    @Test
    void validateUserInSchool_whenDifferentSchool_throwParamError() {
        assertThrows(BusinessException.class,
                () -> validator.validateUserInSchool(portalUser(1L, 1L, 1L, 1L), 2L));
    }

    @Test
    void validateUserInCollege_whenDifferentCollege_throwParamError() {
        assertThrows(BusinessException.class,
                () -> validator.validateUserInCollege(portalUser(1L, 1L, 10L, 1L), 20L));
    }

    @Test
    void validateUserInOrganization_whenUserOrgNull_throwParamError() {
        PortalUserEntity user = portalUser(1L, 1L, 1L, 1L);
        assertThrows(BusinessException.class, () -> validator.validateUserInOrganization(user, 1L));
    }

    @Test
    void validateUserInOrganization_whenDifferentOrg_throwParamError() {
        PortalUserEntity user = portalUser(1L, 1L, 1L, 1L);
        user.setOrganizationId(3L);
        assertThrows(BusinessException.class, () -> validator.validateUserInOrganization(user, 4L));
    }

    @Test
    void validateUserInOrganization_whenSameOrg_pass() {
        PortalUserEntity user = portalUser(1L, 1L, 1L, 1L);
        user.setOrganizationId(3L);
        assertDoesNotThrow(() -> validator.validateUserInOrganization(user, 3L));
    }
}
