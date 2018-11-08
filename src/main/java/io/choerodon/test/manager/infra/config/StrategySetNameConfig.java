package io.choerodon.test.manager.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by zongw.lee@gmail.com on 07/11/2018
 */
@Configuration
public class StrategySetNameConfig {
    public StrategySetNameConfig() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}
