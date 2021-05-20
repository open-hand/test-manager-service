package io.choerodon.test.manager.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author superlee
 * @since 2020-06-23
 */
@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {

    @Override
    public Executor getAsyncExecutor() {
        return new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(15));
    }

    @Bean(name = "excelTaskExecutor")
    public Executor excelPoolTaskExecutor() {
        //定义线程池
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(3);
        //最大线程数
        taskExecutor.setMaxPoolSize(3);
        //线程名称前缀
        taskExecutor.setThreadNamePrefix("Excel-ThreadPool-");
        //线程池中线程最大空闲时间,单位：秒
        taskExecutor.setKeepAliveSeconds(0);
        taskExecutor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(taskExecutor);
    }
}
