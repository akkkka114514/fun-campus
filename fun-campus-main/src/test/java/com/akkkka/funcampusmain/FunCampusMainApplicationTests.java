package test.java.com.akkkka.funcampusmain;

import com.akkkka.funcampusmainapi.api.IActivityService;
import com.akkkka.funcampusmainapi.api.IActivityUserMapService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@SpringBootTest
@Slf4j
class FunCampusMainApplicationTests {
	@Resource
	private IActivityUserMapService activityUserMapService;
	@Resource
	private IActivityService activityService;

	private class TestQuatzJob implements Job {
		@Override
		public void execute(JobExecutionContext context) {
			log.info("执行任务");
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// 获取job的自定义参数
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			System.out.println( " " + sf.format(date) + " 任务1执行了，" + dataMap.getString("cxf"));
		}
	}


	@Test
	void contextLoads() {}

	@Test
	void testActivityUserMapService() {
		Random random = new Random();
		for (int i = 0; i < 5000; i++) {
			try {
				activityUserMapService.checkUnique(random.nextInt(1000), random.nextInt(1000));
			}catch (Exception e){
				System.out.println("重复");
			}
		}
	}

	@Test
	void testEnroll(){
		Random random = new Random();
		for (int i = 0; i < 5000; i++) {
			try {
				activityService.enroll(random.nextInt(1100), random.nextInt(1100));
			}catch (Exception e){
				log.info(e.getMessage());
			}
		}
	}

	@Test
	void testQuatz() throws SchedulerException {
		// JobDetail 把 Job 进一步包装成 JobDetail
		JobDetail jobDetail = JobBuilder.newJob(TestQuatzJob.class)
				// 必须要指定 JobName 和 groupName，两个合起来是唯一标识符
				.withIdentity("job1", "group1") // 任务名 + 任务组 同一个组中包含许多任务
				// 可以携带 KV 的数据（JobDataMap），用于扩展属性，在运行的时候可以从 context获取到
				.usingJobData("cxf","加油") // 添加额外自定义参数
				.usingJobData("moon",5.21F)
				.build();

		// Trigger
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger1", "group1") // 定义trigger名 + 组名
				.startAt(new Date(System.currentTimeMillis()+60000))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule() // 简单触发器
						.withIntervalInSeconds(2) // 2秒一次
						.repeatForever()) // 持续不断执行
				.endAt(new Date(System.currentTimeMillis()+120000))
				.build();

		// SchedulerFactory
		SchedulerFactory  factory = new StdSchedulerFactory();

		// Scheduler 一定是单例的
		Scheduler scheduler = factory.getScheduler();

		// 绑定关系是1：N ，把 JobDetail 和 Trigger绑定，注册到容器中
		scheduler.scheduleJob(jobDetail, trigger);
		// Scheduler 先启动后启动无所谓，只要有 Trigger 到达触发条件，就会执行任务
		scheduler.start();
	}
}
