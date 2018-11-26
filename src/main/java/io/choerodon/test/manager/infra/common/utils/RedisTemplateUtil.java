package io.choerodon.test.manager.infra.common.utils;

import io.choerodon.core.exception.CommonException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;

public class RedisTemplateUtil {

   public RedisAtomicLong getRedisAtomicLong(String key, RedisTemplate redisTemplate){
       return new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
   }

//   public static boolean lock(RedisConnection redisConnection,String lockKey, String requestId, int expireTime){
//       if( redisConnection.getNativeConnection() instanceof JedisCluster){
//
//       }else if (redisConnection.getNativeConnection() instanceof Jedis){
//
//       }else throw new CommonException("UnSupport redis model");
//
//       String result = redisConnection.setset(lockKey, requestId, "NX", "PX", expireTime);
//       if ("OK".equals(result)) {
//           return true;
//       }
//       return false;
//   }
//
//   public static void release(Jedis RedisConnection,String lockKey, String requestId){
//       String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//       jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
//   }
//
//   public static RedisConnection getJedisConnection(RedisTemplate redisTemplate){
//       return redisTemplate.getConnectionFactory().getConnection()
//   }
}
