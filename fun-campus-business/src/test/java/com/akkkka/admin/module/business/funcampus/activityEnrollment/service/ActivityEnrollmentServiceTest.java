package com.akkkka.admin.module.business.funcampus.activityEnrollment.service;

import com.akkkka.admin.module.business.funcampus.MybatisPlusTestRegistry;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.dao.ActivityEnrollmentDao;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.SignInQRCodeVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.dto.EnrollersChangeDTO;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.SignInManagerValidator;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.EnrollerVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 活动报名 Service 单元测试
 * <p>
 * 覆盖：报名事务流程（人数上限、插入失败）、签到二维码生成（30s 过期、刷新覆盖）、
 * 扫码签到/签退（token 过期、签到/签退时间窗口、needSignOut、重复签到/签退、未签到签退）、
 * 报名人员差集转换、报名列表标记
 *
 * @Author akkkka114514
 * @Date 2026-09-03
 */
@ExtendWith(MockitoExtension.class)
public class ActivityEnrollmentServiceTest {

    static {
        // signIn/signOut 的 LambdaUpdateWrapper.set、listEnrollUser 的 select 会立即解析实体列
        MybatisPlusTestRegistry.register(ActivityEnrollmentEntity.class);
    }

    @Mock
    private ActivityEnrollmentDao activityEnrollmentDao;
    @Mock
    private ActivityEnrollmentManager activityEnrollmentManager;
    @Mock
    private ActivityEnrollmentManager enrollmentManager;
    @Mock
    private ActivityManager activityManager;
    @Mock
    private ActivityEnrollNumDao activityEnrollNumDao;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private PortalUserManager portalUserManager;
    @Mock
    private PortalUserValidator portalUserValidator;
    @Mock
    private ActivityValidator activityValidator;
    @Mock
    private ActivityEnrollmentValidator enrollmentDomainService;
    @Mock
    private SignInManagerValidator signInManagerValidator;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private ActivitySigninManagerService signinManagerService;
    @Mock
    private ActivityScheduleManager activityScheduleManager;

    private ActivityEnrollmentService service;

    /**
     * 构造器含两个 ActivityEnrollmentManager 同类型参数，
     * @InjectMocks 按类型注入时会随机错位，因此按字段声明顺序显式构造
     */
    @BeforeEach
    void setUp() {
        service = new ActivityEnrollmentService(
                activityEnrollmentDao, activityEnrollmentManager, activityManager,
                activityEnrollNumDao, transactionTemplate, portalUserManager, portalUserValidator,
                activityValidator, enrollmentDomainService, signInManagerValidator, redisTemplate,
                enrollmentManager, signinManagerService, activityScheduleManager);
    }

    @AfterEach
    void cleanRequestUser() {
        SmartRequestUtil.remove();
    }

    /** 让事务模板真正执行传入的消费逻辑 */
    private void runTransactionNow() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> consumer = invocation.getArgument(0);
            consumer.accept(mock(TransactionStatus.class));
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    private void setPortalUser(Long userId) {
        RequestPortalUser portalUser = new RequestPortalUser();
        portalUser.setId(userId);
        portalUser.setUsername("student" + userId);
        portalUser.setUserType(UserTypeEnum.PORTAL_USER);
        SmartRequestUtil.setRequestUser(portalUser);
    }

    private ActivityEntity activityWithStatus(ActivityStatus status) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(7L);
        activity.setStatus(status);
        return activity;
    }

    private ActivityEntity signOutActivity(boolean needSignOut) {
        ActivityEntity activity = activityWithStatus(ActivityStatus.ONGOING);
        activity.setNeedSignOut(needSignOut);
        return activity;
    }

    private ActivityScheduleEntity scheduleWithSignInWindow(LocalDateTime start, LocalDateTime end) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setSigninStartTime(start);
        schedule.setSigninEndTime(end);
        return schedule;
    }

    /** 当前时间处于签到窗口内的活动时间表（真实时间动态生成） */
    private ActivityScheduleEntity openSignInSchedule() {
        LocalDateTime now = LocalDateTime.now();
        return scheduleWithSignInWindow(now.minusMinutes(30), now.plusMinutes(30));
    }

    private ActivityScheduleEntity scheduleWithSignOutWindow(LocalDateTime start, LocalDateTime end) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setSignoutStartTime(start);
        schedule.setSignoutEndTime(end);
        return schedule;
    }

    /** 当前时间处于签退窗口内的活动时间表（真实时间动态生成） */
    private ActivityScheduleEntity openSignOutSchedule() {
        LocalDateTime now = LocalDateTime.now();
        return scheduleWithSignOutWindow(now.minusMinutes(30), now.plusMinutes(30));
    }

    private ActivityEnrollmentEntity enrollment(Long userId, Boolean signIn, Boolean signOut) {
        ActivityEnrollmentEntity enrollment = new ActivityEnrollmentEntity();
        enrollment.setActivityId(7L);
        enrollment.setUserId(userId);
        enrollment.setSignInStatus(signIn);
        enrollment.setSignOutStatus(signOut);
        enrollment.setDeletedFlag(false);
        return enrollment;
    }

    private PortalUserEntity portalUserEntity(Long id) {
        PortalUserEntity user = new PortalUserEntity();
        user.setId(id);
        user.setUsername("student" + id);
        user.setAvatar("avatar-" + id);
        return user;
    }

    // ---------------------------------- 报名 enroll ----------------------------------

    @Test
    void enroll_whenNotPortalUser_throwParamError() {
        SmartRequestUtil.setRequestUser(new RequestBackendUser());
        BusinessException ex = assertThrows(BusinessException.class, () -> service.enroll(7L));
        assertTrue(ex.getMessage().contains("用户类型错误"));
    }

    @Test
    void enroll_whenActivityNotInEnrollStatus_throw() {
        setPortalUser(12L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.WAIT_ENROLL));
        BusinessException ex = assertThrows(BusinessException.class, () -> service.enroll(7L));
        assertTrue(ex.getMessage().contains("活动未开始报名或报名已结束"));
    }

    @Test
    void enroll_whenEnrollNumFull_throwAndNotInsert() {
        setPortalUser(12L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        runTransactionNow();
        when(activityEnrollNumDao.increaseEnrollNum(7L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.enroll(7L));
        assertTrue(ex.getMessage().contains("活动报名人数已满"));
        verify(activityEnrollmentDao, never()).insert(any(ActivityEnrollmentEntity.class));
    }

    @Test
    void enroll_whenInsertFails_throw() {
        setPortalUser(12L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        runTransactionNow();
        when(activityEnrollNumDao.increaseEnrollNum(7L)).thenReturn(true);
        when(activityEnrollmentDao.insert(any(ActivityEnrollmentEntity.class))).thenReturn(0);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.enroll(7L));
        assertTrue(ex.getMessage().contains("报名失败"));
    }

    @Test
    void enroll_whenAllValid_insertEnrollment() {
        setPortalUser(12L);
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        when(portalUserManager.getById(12L)).thenReturn(portalUserEntity(12L));
        runTransactionNow();
        when(activityEnrollNumDao.increaseEnrollNum(7L)).thenReturn(true);
        when(activityEnrollmentDao.insert(any(ActivityEnrollmentEntity.class))).thenReturn(1);

        assertDoesNotThrow(() -> service.enroll(7L));
        verify(activityEnrollNumDao).increaseEnrollNum(7L);
        ArgumentCaptor<ActivityEnrollmentEntity> captor = ArgumentCaptor.forClass(ActivityEnrollmentEntity.class);
        verify(activityEnrollmentDao).insert(captor.capture());
        assertEquals(7L, captor.getValue().getActivityId());
        assertEquals(12L, captor.getValue().getUserId());
        assertFalse(captor.getValue().getSignInStatus());
        assertFalse(captor.getValue().getDeletedFlag());
    }

    // ---------------------------------- 签到二维码 ----------------------------------

    @Test
    void signInQRCode_generateQrCodeAndCacheToken() {
        setPortalUser(12L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        SignInQRCodeVO vo = service.signInQRCode();

        assertNotNull(vo);
        assertEquals(12L, vo.getUserId());
        assertEquals(30, vo.getExpireSeconds());
        assertNotNull(vo.getToken());
        assertTrue(vo.getQrCodeImage().startsWith("data:image/png;base64,"));
        // 二维码内容包含 userId 与 token
        assertTrue(vo.getQrCodeImage().length() > "data:image/png;base64,".length());

        // 直接覆盖写入新 token 即完成刷新（无需先删除旧 token）
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(eq("sign_in:qr_code_token:12"), tokenCaptor.capture(), eq(30L), eq(TimeUnit.SECONDS));
        assertEquals(vo.getToken(), tokenCaptor.getValue());
        verify(redisTemplate, never()).delete(anyString());
    }

    // ---------------------------------- 扫码签到 signIn ----------------------------------

    @Test
    void signIn_whenTokenExpiredOrMismatch_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signIn(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("二维码已过期"));
    }

    @Test
    void signIn_whenScheduleMissing_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        when(activityScheduleManager.getById(7L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signIn(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("活动时间表不存在"));
    }

    @Test
    void signIn_whenSigninWindowNotOpen_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        LocalDateTime now = LocalDateTime.now();
        when(activityScheduleManager.getById(7L))
                .thenReturn(scheduleWithSignInWindow(now.plusMinutes(30), now.plusHours(1)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signIn(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("活动签到尚未开始"));
    }

    @Test
    void signIn_whenSigninWindowClosed_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        LocalDateTime now = LocalDateTime.now();
        when(activityScheduleManager.getById(7L))
                .thenReturn(scheduleWithSignInWindow(now.minusHours(1), now.minusMinutes(30)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signIn(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("活动签到已结束"));
    }

    @Test
    void signIn_whenSigninWindowNotConfigured_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        when(activityScheduleManager.getById(7L)).thenReturn(new ActivityScheduleEntity());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signIn(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("活动未配置签到时间窗口"));
    }

    @Test
    void signIn_whenAlreadySignedIn_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        when(activityScheduleManager.getById(7L)).thenReturn(openSignInSchedule());
        when(enrollmentDomainService.validateEnrollmentExist(7L, 12L))
                .thenReturn(enrollment(12L, true, false));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signIn(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("请勿重复签到"));
    }

    @Test
    void signIn_whenValid_updateAndDeleteToken() {
        setPortalUser(99L); // 签到员
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        when(activityScheduleManager.getById(7L)).thenReturn(openSignInSchedule());
        when(enrollmentDomainService.validateEnrollmentExist(7L, 12L))
                .thenReturn(enrollment(12L, false, false));
        when(portalUserValidator.validatePortalUserId(12L)).thenReturn(portalUserEntity(12L));
        when(activityEnrollmentManager.update(any())).thenReturn(true);

        assertDoesNotThrow(() -> service.signIn(7L, 12L, "uuid-1"));

        verify(activityEnrollmentManager).update(any());
        verify(redisTemplate).delete("sign_in:qr_code_token:12");
    }

    @Test
    void signIn_whenUpdateFails_throwServiceBusy() {
        setPortalUser(99L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(activityWithStatus(ActivityStatus.ENROLLING));
        when(activityScheduleManager.getById(7L)).thenReturn(openSignInSchedule());
        when(enrollmentDomainService.validateEnrollmentExist(7L, 12L))
                .thenReturn(enrollment(12L, false, false));
        when(portalUserValidator.validatePortalUserId(12L)).thenReturn(portalUserEntity(12L));
        when(activityEnrollmentManager.update(any())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signIn(7L, 12L, "uuid-1"));
        assertEquals(UserErrorCode.SERVICE_BUSY.getCode(), ex.getCode());
    }

    // ---------------------------------- 扫码签退 signOut ----------------------------------

    @Test
    void signOut_whenActivityNotNeedSignOut_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(signOutActivity(false));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signOut(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("该活动无需签退"));
        verify(activityScheduleManager, never()).getById(any());
    }

    @Test
    void signOut_whenSignoutWindowNotOpen_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(signOutActivity(true));
        LocalDateTime now = LocalDateTime.now();
        when(activityScheduleManager.getById(7L))
                .thenReturn(scheduleWithSignOutWindow(now.plusMinutes(30), now.plusHours(1)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signOut(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("活动签退尚未开始"));
    }

    @Test
    void signOut_whenSignoutWindowClosed_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(signOutActivity(true));
        LocalDateTime now = LocalDateTime.now();
        when(activityScheduleManager.getById(7L))
                .thenReturn(scheduleWithSignOutWindow(now.minusHours(1), now.minusMinutes(30)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signOut(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("活动签退已结束"));
    }

    @Test
    void signOut_whenUserNotSignedIn_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(signOutActivity(true));
        when(activityScheduleManager.getById(7L)).thenReturn(openSignOutSchedule());
        when(enrollmentDomainService.validateEnrollmentExist(7L, 12L))
                .thenReturn(enrollment(12L, false, false));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signOut(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("用户尚未签到，无法签退"));
    }

    @Test
    void signOut_whenAlreadySignedOut_throw() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(signOutActivity(true));
        when(activityScheduleManager.getById(7L)).thenReturn(openSignOutSchedule());
        when(enrollmentDomainService.validateEnrollmentExist(7L, 12L))
                .thenReturn(enrollment(12L, true, true));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.signOut(7L, 12L, "uuid-1"));
        assertTrue(ex.getMessage().contains("请勿重复签退"));
    }

    @Test
    void signOut_whenValid_updateAndDeleteToken() {
        setPortalUser(99L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sign_in:qr_code_token:12")).thenReturn("uuid-1");
        when(activityValidator.validateActivityId(7L)).thenReturn(signOutActivity(true));
        when(activityScheduleManager.getById(7L)).thenReturn(openSignOutSchedule());
        when(enrollmentDomainService.validateEnrollmentExist(7L, 12L))
                .thenReturn(enrollment(12L, true, false));
        when(portalUserValidator.validatePortalUserId(12L)).thenReturn(portalUserEntity(12L));
        when(activityEnrollmentManager.update(any())).thenReturn(true);

        assertDoesNotThrow(() -> service.signOut(7L, 12L, "uuid-1"));
        verify(activityEnrollmentManager).update(any());
        verify(redisTemplate).delete("sign_in:qr_code_token:12");
    }

    // ---------------------------------- 报名人员差集转换 ----------------------------------

    @Test
    void convertToEnrollmentChanges_whenNewUsersAdded_onlyAddThem() {
        List<Long> formIds = new ArrayList<>(List.of(1L, 2L, 3L));
        List<Long> dbIds = new ArrayList<>(List.of(2L, 3L));

        EnrollersChangeDTO dto = service.convertToEnrollmentChanges(7L, formIds, dbIds);

        assertEquals(1, dto.getAddList().size());
        assertEquals(1L, dto.getAddList().get(0).getUserId());
        assertEquals(7L, dto.getAddList().get(0).getActivityId());
        assertEquals(false, dto.getAddList().get(0).getSignInStatus());
        assertEquals(false, dto.getAddList().get(0).getDeletedFlag());
        assertTrue(dto.getDelList().isEmpty());
    }

    @Test
    void convertToEnrollmentChanges_whenUsersRemoved_onlyDeleteThem() {
        List<Long> formIds = new ArrayList<>(List.of(1L, 2L));
        List<Long> dbIds = new ArrayList<>(List.of(1L, 2L, 3L));

        EnrollersChangeDTO dto = service.convertToEnrollmentChanges(7L, formIds, dbIds);

        assertTrue(dto.getAddList().isEmpty());
        assertEquals(1, dto.getDelList().size());
        assertEquals(3L, dto.getDelList().get(0).getUserId());
        assertEquals(true, dto.getDelList().get(0).getDeletedFlag());
    }

    @Test
    void convertToEnrollmentChanges_whenNoChange_bothEmpty() {
        List<Long> formIds = new ArrayList<>(List.of(1L, 2L));
        List<Long> dbIds = new ArrayList<>(List.of(2L, 1L));

        EnrollersChangeDTO dto = service.convertToEnrollmentChanges(7L, formIds, dbIds);

        assertTrue(dto.getAddList().isEmpty());
        assertTrue(dto.getDelList().isEmpty());
    }

    // ---------------------------------- 报名用户列表 ----------------------------------

    @Test
    void listEnrollUserAsEnrollerVO_whenNoEnrollment_returnEmpty() {
        LambdaQueryWrapper<ActivityEnrollmentEntity> qw = new LambdaQueryWrapper<>();
        when(activityEnrollmentManager.qwByActivityId(7L)).thenReturn(qw);
        when(activityEnrollmentManager.list(qw)).thenReturn(null);
        assertTrue(service.listEnrollUserAsEnrollerVO(7L).isEmpty());
    }

    @Test
    void listEnrollUserAsEnrollerVO_markManagerAndSigninManager() {
        ActivityEnrollmentEntity e1 = enrollment(1L, true, false);
        ActivityEnrollmentEntity e2 = enrollment(2L, false, false);
        LambdaQueryWrapper<ActivityEnrollmentEntity> qw = new LambdaQueryWrapper<>();
        when(activityEnrollmentManager.qwByActivityId(7L)).thenReturn(qw);
        when(activityEnrollmentManager.list(qw)).thenReturn(List.of(e1, e2));

        ActivityEntity activity = new ActivityEntity();
        activity.setActivityManagerId(1L);
        when(activityManager.getById(7L)).thenReturn(activity);
        when(portalUserManager.getById(1L)).thenReturn(portalUserEntity(1L));
        when(portalUserManager.getById(2L)).thenReturn(portalUserEntity(2L));
        when(signinManagerService.getSignInManagerIds(7L)).thenReturn(List.of(2L));

        List<EnrollerVO> result = service.listEnrollUserAsEnrollerVO(7L);
        assertEquals(2, result.size());
        assertEquals("student1", result.get(0).getName());
        assertTrue(result.get(0).isActivityManagerFlag());
        assertFalse(result.get(0).isSigninManagerFlag());
        assertTrue(result.get(0).isSignInStatus());
        assertFalse(result.get(1).isActivityManagerFlag());
        assertTrue(result.get(1).isSigninManagerFlag());
    }

    @Test
    void listEnrollUserAsEnrollerVO_skipMissingPortalUser() {
        LambdaQueryWrapper<ActivityEnrollmentEntity> qw = new LambdaQueryWrapper<>();
        when(activityEnrollmentManager.qwByActivityId(7L)).thenReturn(qw);
        when(activityEnrollmentManager.list(qw))
                .thenReturn(List.of(enrollment(1L, false, false), enrollment(2L, false, false)));
        when(activityManager.getById(7L)).thenReturn(new ActivityEntity());
        when(portalUserManager.getById(1L)).thenReturn(null);
        when(portalUserManager.getById(2L)).thenReturn(portalUserEntity(2L));
        when(signinManagerService.getSignInManagerIds(7L)).thenReturn(List.of());

        List<EnrollerVO> result = service.listEnrollUserAsEnrollerVO(7L);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }
}
