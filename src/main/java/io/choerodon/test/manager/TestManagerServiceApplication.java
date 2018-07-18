package io.choerodon.test.manager;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

//@EnableTransactionManagement
//@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@EnableChoerodonResourceServer
public class TestManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestManagerServiceApplication.class, args);
    }

}

