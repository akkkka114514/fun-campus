package com.akkkka.funcampusgateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author akkkka
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableDubbo
public class FunCampusGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunCampusGatewayApplication.class, args);
    }

}
