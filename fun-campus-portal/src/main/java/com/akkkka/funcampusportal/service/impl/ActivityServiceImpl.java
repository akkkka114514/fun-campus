package com.akkkka.funcampusportal.service.impl;


import com.akkkka.funcampusportal.domain.*;
import com.akkkka.funcampusportal.mapper.ActivityMapper;
import com.akkkka.funcampusportal.event.AddActivityEvent;
import com.akkkka.funcampusportal.quatz.ChangeActivityStatusJob;
import com.akkkka.funcampusportal.service.*;
import com.akkkka.funcampusutil.constant.ResponseEnum;
import com.akkkka.funcampusutil.util.CustomException;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
@Service
@Slf4j
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

    @Resource
    private IUserService userService;
    @Resource
    private IOrganizerService organizerService;
    @Resource
    private IActivityUserMapService activityUserMapService;
    @Resource
    private ISchoolService schoolService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void add(Activity activity) {
        activity.setId(null);
        activity.setStatus((byte)1);
        activity.setIsDeleted((byte)0);
        activity.setEnrollNum(0);

        School toCheckSchool = schoolService.getById(activity.getActivitySchoolId());
        Organizer toCheckOrganizer = organizerService.getById(activity.getActivityOrganizerId());

        ExceptionUtil.throwIfNullInDb(toCheckSchool,":school");
        ExceptionUtil.throwIfNullInDb(toCheckOrganizer,":organizer");

        ExceptionUtil.throwIfIsDeletedInDb(toCheckSchool.getIsDeleted(),":school");
        ExceptionUtil.throwIfIsDeletedInDb(toCheckOrganizer.getIsDeleted(),":organizer");

        ExceptionUtil.throwIfNotCorrespondToRecordInDb(toCheckOrganizer.getSchoolId(), toCheckSchool.getId(),":school and organizer");

        this.save(activity);
        Integer activityId = this.getByTitle(activity.getTitle()).getId();

        /*
         * 向活动的定时任务发布事件
         * @see {changeStatusOnTime(Integer activityId)}
         * */

        applicationEventPublisher.publishEvent(new AddActivityEvent("add-activity-event"+activityId,activityId));
    }

    @Override
    public void update(Activity activity) {
        Activity toCheckActivity = this.getById(activity.getId());

        ExceptionUtil.throwIfNullInDb(toCheckActivity,":activity");
        ExceptionUtil.throwIfIsDeletedInDb(toCheckActivity.getIsDeleted(),":activity");

        //不允许更改活动学校和活动主办方
        activity.setActivitySchoolId(null);
        activity.setActivityOrganizerId(null);
        activity.setStatus(null);
        activity.setIsDeleted(null);

        this.updateById(activity);
    }

    @Override
    public void delete(Integer activityId) {
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setIsDeleted((byte)1);

        this.updateById(activity);
    }

    @Override
    public List<Activity> listByUserId(Integer userId) {
        return this.getBaseMapper().listByUserId(userId);
    }

    /**
     * 报名活动
     * */
    @Override
    public void enroll(Integer userId, Integer activityId) {
        User user = this.userService.getById(userId);
        Activity activity = this.getById(activityId);
        //判空
        ExceptionUtil.throwIfNullInDb(user,":user");
        ExceptionUtil.throwIfNullInDb(activity,":activity");

        Integer enrollNum = activity.getEnrollNum();

        log.info("查看活动是否在等待报名状态");
        if (activity.getStatus()!=1){
            throw new CustomException(ResponseEnum.NOT_ENROLL_TIME,":activity");
        }

        log.info("查看操作的user和activity是否已被逻辑删除");
        ExceptionUtil.throwIfIsDeletedInDb(user.getIsDeleted(),":user");
        ExceptionUtil.throwIfIsDeletedInDb(activity.getIsDeleted(),":activity");

        log.info("查看报名人数是否达到上限");
        if (enrollNum >= activity.getEnrollNumLimit()){
            throw new CustomException(ResponseEnum.GOT_ENROLL_NUM_MAX,"activity");
        }

        log.info("查看是否已报名过");
        if (!activityUserMapService.checkUnique(userId, activityId)){
            throw new CustomException(ResponseEnum.EXISTS_IN_DB,"activityUserMap");
        }

        Organizer organizer=organizerService.getById(activity.getActivityOrganizerId());
        ExceptionUtil.throwIfNullInDb(organizer);
        log.info("判断是否在活动所属学校或活动所属学院内");
        boolean isBelongToSchool = Objects.equals(activity.getActivitySchoolId(), user.getSchoolId());
        log.info("活动组织者是否是学院");
        boolean organizerIsCollege = organizer.getCollegeId() != null;
        log.info("活动所属学院与报名人所属学院不一致");
        boolean isBelongToCollege = organizerIsCollege && Objects.equals(organizer.getCollegeId(), user.getCollegeId());
        if (!isBelongToCollege && !isBelongToSchool){
            log.info("user not in school or college:");
            log.info("activity.id={},activity.schoolId={},activity.college.organizer.collegeId={}",activityId,activity.getActivitySchoolId(),organizer.getCollegeId());
            log.info("user.id={},user.schoolId={},user.collegeId={}",userId,user.getSchoolId(),user.getCollegeId());
            throw new CustomException(ResponseEnum.NOT_IN_THE_SCHOOL_OR_COLLEGE);
        }
        log.info("报名人数+1");
        this.updateById(Activity.builder().id(activityId).enrollNum(enrollNum+1).build());
        //添加报名关系
        activityUserMapService.save(new ActivityUserMap(activityId,userId,0));

        log.info("用户[{}]报名活动[{}]", user.getUsername(), activity.getTitle());
    }
    /**
     * 定时任务改变活动状态
     * */
    @Override
    public void changeStatusOnTime(Integer activityId) throws SchedulerException {
        // JobDetail 把 Job 进一步包装成 JobDetail
        JobDetail jobDetail = JobBuilder.newJob(ChangeActivityStatusJob.class)
                // 必须要指定 JobName 和 groupName，两个合起来是唯一标识符
                .withIdentity("changeActivityJob"+activityId, "activity")
                // 任务名 + 任务组 同一个组中包含许多任务
                // 可以携带 KV 的数据（JobDataMap），用于扩展属性，在运行的时候可以从 context获取到
                .usingJobData("activityId",activityId)
                // 添加额外自定义参数
                .build();

        // Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("changeActivityTrigger", "activity")
                // 定义trigger名 + 组名
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        // 简单触发器
                        .withIntervalInSeconds(40)
                        // 40秒一次
                        .repeatForever()) // 持续不断执行
                .build();

        // SchedulerFactory
        SchedulerFactory factory = new StdSchedulerFactory();
        // Scheduler 一定是单例的
        Scheduler scheduler = factory.getScheduler();
        // 绑定关系是1：N ，把 JobDetail 和 Trigger绑定，注册到容器中
        scheduler.scheduleJob(jobDetail, trigger);
        // Scheduler 先启动后启动无所谓，只要有 Trigger 到达触发条件，就会执行任务
        scheduler.start();
    }

    @Override
    public void signIn(Integer activityId, Integer userId) {
        ActivityUserMap aum = activityUserMapService.get(activityId,userId);

        ExceptionUtil.throwIfNullInDb(aum);
        if (aum.getIsSignIn()==1){
            //检查是否重复签到
            throw new CustomException(ResponseEnum.ALREADY_SIGN_IN);
        }

        aum.setIsSignIn(1);
        activityUserMapService.update(aum);
    }

    @Override
    public IPage<Activity> pageBySchoolId(Integer pageNum, Integer pageSize, Integer schoolId) {
        IPage<Activity> page = new Page<>(pageNum,pageSize);
        return this.baseMapper.selectPage(page,
                new QueryWrapper<Activity>().eq("activity_school_id",schoolId)
        );
    }

    @Override
    public void cancelEnroll(Integer userId, Integer activityId) {
        log.info("获取活动信息");
        Activity activity = this.getById(activityId);
        ExceptionUtil.throwIfNullInDb(activity);
        log.info("检查活动是否逻辑删除");
        ExceptionUtil.throwIfIsDeletedInDb(activity.getIsDeleted(),"activity");
        log.info("检查活动是否是等待签到状态，即status-2");
        if(!Objects.equals((byte)2,activity.getStatus())){
            throw new CustomException(ResponseEnum.ACTIVITY_NOT_IN_CORRECT_STATUS);
        }
        log.info("获取用户信息");
        User user = userService.getById(userId);
        ExceptionUtil.throwIfNullInDb(user);
        log.info("检查用户是否逻辑删除");
        ExceptionUtil.throwIfIsDeletedInDb(user.getIsDeleted(),"user");
        log.info("获取activityUserMap");
        ActivityUserMap aum = activityUserMapService.get(userId,activityId);
        ExceptionUtil.throwIfNullInDb(aum);
        log.info("删除该activityUserMap");
        activityUserMapService.remove(new QueryWrapper<ActivityUserMap>()
                .eq("user_id",userId)
                .eq("activityId",activityId));
    }

    /**
     * 活动签到后给分
     * */
//    @Override
//    public void giveScore(Integer activityId, Integer userId) {
//        ActivityUserMap aum = activityUserMapService.get(userId,activityId);
//        Activity activity = this.getById(activityId);
//        User user = userService.getById(userId);
//
//        //未签到不能给分
//        if (aum.getIsSignIn()==0){
//            throw ExceptionUtil.customException(ResponseEnum.NOT_SIGN_IN);
//        }
//        float userScore = user.getFunScore() + activity.getScoreCanGet();
//        user.setFunScore(userScore);
//        userService.updateById(user);
//    }

    @Override
    public void allGiveScore(Integer activityId) {
        float scoreCanGet = this.getById(activityId).getScoreCanGet();

        List<ActivityUserMap> aums = activityUserMapService.list(
                activityUserMapService.query().eq("activity_id",activityId)
        );
        List<Integer> userIds = aums.stream()
                .filter(aum -> aum.getIsSignIn()==1) //排除没有签到的人
                .map(ActivityUserMap::getUserId)
                .collect(Collectors.toList());

        List<User> users = userService.listByIds(userIds);
        //赋分
        users.forEach(
                user -> user.setFunScore(user.getFunScore() + scoreCanGet)
        );
        //更新到数据库
        userService.updateBatchById(users);
    }

    @Override
    public Activity getByTitle(String title) {
        return this.getOne(
                new QueryWrapper<Activity>().eq("title",title)
        );
    }

}
