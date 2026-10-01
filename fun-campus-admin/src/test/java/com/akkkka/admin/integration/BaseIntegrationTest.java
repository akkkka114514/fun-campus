package com.akkkka.admin.integration;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityRefundManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEnrollNum;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 集成测试基类（SpringBootTest + 本地真实环境）
 * <p>
 * 设计要点：
 * - 加载完整 Spring 容器，直连本地 MySQL(fc_portal) 与 Redis(db1)，验证真实 SQL/事务/Redis 交互；
 * - smart.job.enabled=false 关闭 SmartJob 调度线程（避免测试期间自动执行定时任务），
 *   Job Bean 仍注册在容器中，可注入后手动调用 run()；
 * - @Transactional 使每个测试方法整体运行在一个事务内，方法结束后自动回滚，不污染开发库；
 *   业务代码的事务（TransactionTemplate 默认 REQUIRED）会加入测试事务（项目内无 REQUIRES_NEW），
 *   因此内层"提交"不会被真正提交，业务数据最终随测试事务一起回滚；
 * - Redis 写入不参与事务回滚：基类在 @AfterEach 统一清理「当天关键活动名单」key，
 *   扫码 token 等短时效 key 由用例自行清理或等待过期。
 * <p>
 * author:akkkka114514
 * create at 2026-10-01
 */
@SpringBootTest(properties = "smart.job.enabled=false")
@Transactional
public abstract class BaseIntegrationTest {

    /**
     * 当天关键活动名单 key（与 ActivityStatusCacheManager 保持一致）
     */
    protected static final String TODAY_CRITICAL_ACTIVITIES_KEY = "funcampus:today_critical_activities";

    /**
     * 名单生成日期 key（与 ActivityStatusCacheManager 保持一致）
     */
    protected static final String TODAY_CRITICAL_ACTIVITIES_DATE_KEY = "funcampus:today_critical_activities:generated_date";

    @Resource
    protected ActivityManager activityManager;

    @Resource
    protected ActivityScheduleManager activityScheduleManager;

    @Resource
    protected ActivityEnrollNumDao activityEnrollNumDao;

    @Resource
    protected ActivityEnrollmentManager activityEnrollmentManager;

    @Resource
    protected PortalUserManager portalUserManager;

    @Resource
    protected BackendUserManager backendUserManager;

    @Resource
    protected ActivitySigninManagerManager activitySigninManagerManager;

    @Resource
    protected ActivityReviewLogManager activityReviewLogManager;

    @Resource
    protected ActivityOrderManager activityOrderManager;

    @Resource
    protected ActivityRefundManager activityRefundManager;

    /** 通用模板（值序列化为 JSON），与名单类业务读写保持一致 */
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    /** 纯字符串模板（StringRedisSerializer），与扫码 token 类业务读写保持一致 */
    @Resource
    protected StringRedisTemplate stringRedisTemplate;

    @AfterEach
    void tearDownBase() {
        // 清理线程登录态，避免测试间串扰
        SmartRequestUtil.remove();
        // 清理名单 Redis key（测试写入不随事务回滚；删除后线上调度会自愈重建）
        clearTodayCriticalActivitiesCache();
    }

    // ==================== 登录态 ====================

    /**
     * 模拟门户用户登录（SmartRequestUtil 为 ThreadLocal，测试内直接设置）
     */
    protected PortalUserEntity loginAsPortalUser(PortalUserEntity user) {
        RequestPortalUser requestUser = new RequestPortalUser();
        requestUser.setId(user.getId());
        requestUser.setUsername(user.getUsername());
        requestUser.setUserType(UserTypeEnum.PORTAL_USER);
        SmartRequestUtil.setRequestUser(requestUser);
        return user;
    }

    /**
     * 模拟后台用户登录
     */
    protected BackendUserEntity loginAsBackendUser(BackendUserEntity user) {
        RequestBackendUser requestUser = new RequestBackendUser();
        requestUser.setId(user.getId());
        requestUser.setUsername(user.getUsername());
        requestUser.setUserType(UserTypeEnum.ADMIN_BACKEND_USER);
        SmartRequestUtil.setRequestUser(requestUser);
        return user;
    }

    // ==================== 数据构造 ====================

    /**
     * 构造并保存一个门户用户（姓名即用户名，便于审核链路姓名匹配）
     */
    protected PortalUserEntity createPortalUser(String username) {
        PortalUserEntity user = new PortalUserEntity();
        user.setUsername(username);
        user.setNickname(username);
        user.setPassword("test-password");
        user.setDeletedFlag(false);
        user.setDisableFlag(false);
        user.setGender(true);
        user.setPhone("13800000000");
        user.setSchoolId(1L);
        user.setCollegeId(1L);
        user.setCanPublishActivity(true);
        user.setGradeId(1L);
        user.setGradeScore(BigDecimal.ZERO);
        user.setCreditScore(100);
        Assertions.assertTrue(portalUserManager.save(user), "保存门户用户失败");
        return user;
    }

    /**
     * 构造并保存一个后台用户（canReview 决定审核权限）
     */
    protected BackendUserEntity createBackendUser(String username, boolean canReview) {
        BackendUserEntity user = new BackendUserEntity();
        user.setUsername(username);
        user.setPassword("test-password");
        user.setDeletedFlag(false);
        user.setDisabledFlag(false);
        user.setEmail(username + "@integration.test");
        user.setRoleId(1L);
        user.setSchoolId(1L);
        user.setCollegeId(1L);
        user.setCanReview(canReview);
        Assertions.assertTrue(backendUserManager.save(user), "保存后台用户失败");
        return user;
    }

    /**
     * 构造活动实体（免费、无需签退；付费活动请用 {@link #newPaidActivity} 补齐付费配置）
     */
    protected ActivityEntity newActivity(String title, ActivityStatus status) {
        ActivityEntity activity = new ActivityEntity();
        activity.setTitle(title);
        activity.setStatus(status);
        activity.setPosition("集成测试场地");
        activity.setScoreCanGet(BigDecimal.ONE);
        activity.setEnrollNumLimit(10);
        // 活动表 school_id/organization_id 为 NOT NULL，用 1/0 占位
        activity.setActivityBelongToSchoolId(1L);
        activity.setActivityBelongToOrganizationId(0L);
        activity.setDeletedFlag(false);
        activity.setDescription("集成测试活动：" + title);
        activity.setEnrollNeedReview(false);
        activity.setNeedSignOut(false);
        activity.setAttachment("");
        activity.setCoverImg("");
        activity.setPaidFlag(false);
        return activity;
    }

    /**
     * 在 {@link #newActivity} 基础上补齐付费配置
     */
    protected ActivityEntity newPaidActivity(String title, ActivityStatus status, int priceFen) {
        ActivityEntity activity = newActivity(title, status);
        activity.setPaidFlag(true);
        activity.setPriceFen(priceFen);
        return activity;
    }

    /**
     * 保存活动，并同步插入 activity_enroll_num 计数行（业务 increase/decrease 依赖该行存在）
     */
    protected ActivityEntity saveActivity(ActivityEntity activity) {
        Assertions.assertTrue(activityManager.save(activity), "保存活动失败");
        ActivityEnrollNum enrollNum = new ActivityEnrollNum();
        enrollNum.setActivityId(activity.getId());
        enrollNum.setEnrollNum(0);
        activityEnrollNumDao.insert(enrollNum);
        return activity;
    }

    /**
     * 保存活动时间表（签到窗口默认与报名窗口一致，满足 signin_* 非空约束）
     */
    protected ActivityScheduleEntity saveSchedule(Long activityId,
                                                 LocalDateTime enrollStart, LocalDateTime enrollEnd,
                                                 LocalDateTime activityStart, LocalDateTime activityEnd) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(activityId);
        schedule.setEnrollStartTime(enrollStart);
        schedule.setEnrollEndTime(enrollEnd);
        schedule.setActivityStartTime(activityStart);
        schedule.setActivityEndTime(activityEnd);
        schedule.setSigninStartTime(enrollStart);
        schedule.setSigninEndTime(enrollEnd);
        schedule.setDeletedFlag(false);
        activityScheduleManager.save(schedule);
        return schedule;
    }

    /**
     * 写入指定签到/签退窗口的时间表（需自定义签到窗口的用例使用）
     */
    protected ActivityScheduleEntity saveScheduleWithSignWindows(Long activityId,
                                                                LocalDateTime enrollStart, LocalDateTime enrollEnd,
                                                                LocalDateTime activityStart, LocalDateTime activityEnd,
                                                                LocalDateTime signinStart, LocalDateTime signinEnd,
                                                                LocalDateTime signoutStart, LocalDateTime signoutEnd) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(activityId);
        schedule.setEnrollStartTime(enrollStart);
        schedule.setEnrollEndTime(enrollEnd);
        schedule.setActivityStartTime(activityStart);
        schedule.setActivityEndTime(activityEnd);
        schedule.setSigninStartTime(signinStart);
        schedule.setSigninEndTime(signinEnd);
        schedule.setSignoutStartTime(signoutStart);
        schedule.setSignoutEndTime(signoutEnd);
        schedule.setDeletedFlag(false);
        activityScheduleManager.save(schedule);
        return schedule;
    }

    /**
     * 添加活动签到员
     */
    protected ActivitySigninManagerEntity addSigninManager(Long activityId, PortalUserEntity manager) {
        ActivitySigninManagerEntity entity = new ActivitySigninManagerEntity();
        entity.setActivityId(activityId);
        entity.setPortalUserId(manager.getId());
        entity.setUsername(manager.getUsername());
        entity.setDeletedFlag(false);
        Assertions.assertTrue(activitySigninManagerManager.save(entity), "保存签到员失败");
        return entity;
    }

    // ==================== 查询辅助 ====================

    /**
     * 当前活动报名计数
     */
    protected int enrollNumOf(Long activityId) {
        ActivityEnrollNum row = activityEnrollNumDao.selectById(activityId);
        return row == null ? 0 : row.getEnrollNum();
    }

    /**
     * 查询报名记录（绕过逻辑删除条件，可观察已逻辑删除的记录）
     */
    protected ActivityEnrollmentEntity enrollmentRow(Long activityId, Long userId) {
        return activityEnrollmentManager.getOne(Wrappers.lambdaQuery(ActivityEnrollmentEntity.class)
                .eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId));
    }

    /**
     * 活动当前数据库状态
     */
    protected ActivityStatus statusOf(Long activityId) {
        return activityManager.getById(activityId).getStatus();
    }

    // ==================== Redis 名单辅助 ====================

    /**
     * 播种「当天关键活动名单」：仅包含给定活动，并把生成日期置为今天（保证名单新鲜、不触发全量重建）
     * <p>
     * 采用「临时替换名单」而非 rebuild() 的原因：rebuild 会把开发库中当天所有关键活动拉入名单，
     * 测试对其余活动的推进虽随事务回滚，但"消费确认"是 Redis 写入不会回滚，可能误移真实活动的名单条目；
     * 临时替换后测试结束删除 key，调度侧下一轮自动回源重建，影响有界且可自愈。
     */
    protected void seedTodayCriticalActivities(Long... activityIds) {
        redisTemplate.delete(TODAY_CRITICAL_ACTIVITIES_KEY);
        redisTemplate.opsForValue().set(TODAY_CRITICAL_ACTIVITIES_DATE_KEY, LocalDate.now().toString());
        redisTemplate.opsForSet().add(TODAY_CRITICAL_ACTIVITIES_KEY, (Object[]) activityIds);
    }

    /**
     * 判断活动是否仍在名单中（兼容反序列化出的 Integer/Long 数字类型）
     */
    protected boolean inTodayCriticalActivities(Long activityId) {
        Set<Object> members = redisTemplate.opsForSet().members(TODAY_CRITICAL_ACTIVITIES_KEY);
        if (members == null || members.isEmpty()) {
            return false;
        }
        return members.stream().anyMatch(member ->
                member instanceof Number number
                        ? number.longValue() == activityId
                        : String.valueOf(activityId).equals(String.valueOf(member)));
    }

    /**
     * 清理名单相关 Redis key
     */
    protected void clearTodayCriticalActivitiesCache() {
        redisTemplate.delete(List.of(TODAY_CRITICAL_ACTIVITIES_KEY, TODAY_CRITICAL_ACTIVITIES_DATE_KEY));
    }
}
