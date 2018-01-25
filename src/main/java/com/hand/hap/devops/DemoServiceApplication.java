package com.hand.hap.devops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableTransactionManagement
//@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class DemoServiceApplication {

    public static void main(String[] args){
        SpringApplication.run(DemoServiceApplication.class, args);
    }

}
