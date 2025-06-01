package com.akkkka.funcampusportal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author akkkka
 */
@SpringBootApplication
@MapperScan("com.akkkka.funcampusportal.mapper")
@EnableTransactionManagement
public class FunCampusPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunCampusPortalApplication.class, args);

    }

}
