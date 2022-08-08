package io.choerodon.test.manager.infra.config;

import org.hzero.core.message.MessageAccessor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author superlee
 * @since 2022-08-08
 */
@Configuration
public class TestMessagesLoaderConfig {

    @Bean
    public SmartInitializingSingleton testMessagesLoader() {
        return () -> {
            MessageAccessor.addBasenames(new String[]{"classpath:messages/messages_test"});
        };
    }

}
