package com.akkkka.funcampusportal;

import com.akkkka.funcampusportal.mapper.ActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
class FunCampusPortalApplicationTests {

	@Resource
	private ActivityMapper activityMapper;
	@Test
    public void increaseEnrollNum(){
		int i = activityMapper.increaseEnrollNum(1);
		log.error(i+"");
	}
}
