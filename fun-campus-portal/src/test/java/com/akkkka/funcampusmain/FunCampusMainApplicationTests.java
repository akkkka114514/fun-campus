package com.akkkka.funcampusmain;

import com.akkkka.funcampusportal.mapper.ActivityMapper;
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
	private ActivityMapper activityMapper;
	@Test
    public void increaseEnrollNum(){
		activityMapper.IncreaseEnrollNum(1);
	}
}
