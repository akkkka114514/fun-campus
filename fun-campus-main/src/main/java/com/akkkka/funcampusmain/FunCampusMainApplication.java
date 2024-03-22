package com.akkkka.funcampusmain;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * @author akkkka
 */
@SpringBootApplication
@MapperScan("com.akkkka.funcampusmain.mapper")
@EnableTransactionManagement
@EnableDubbo
@EnableDiscoveryClient
public class FunCampusMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunCampusMainApplication.class, args);

    }

}
