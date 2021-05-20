package io.choerodon.test.manager.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

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
}
