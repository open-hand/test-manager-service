package io.choerodon.test.manager.infra.common.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

public class RedisTemplateUtil {

   public RedisAtomicLong getRedisAtomicLong(String key, RedisTemplate redisTemplate){
       return new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
   }
}
