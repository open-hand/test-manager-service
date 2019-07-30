package io.choerodon.test.manager.infra.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

@Component
public class RedisTemplateUtil {

    public RedisAtomicLong getRedisAtomicLong(String key, RedisTemplate redisTemplate) {
        return new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
    }
}
