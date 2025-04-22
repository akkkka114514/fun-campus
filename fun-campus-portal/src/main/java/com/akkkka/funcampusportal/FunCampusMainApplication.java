package com.akkkka.funcampusportal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author akkkka
 */
@SpringBootApplication
@MapperScan("com.akkkka.funcampusportal.mapper")
@EnableTransactionManagement
@Enable
public class FunCampusMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunCampusMainApplication.class, args);

    }

}
