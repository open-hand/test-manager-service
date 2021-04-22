package io.choerodon.test.manager.infra.util;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.test.manager.infra.mapper.TestProjectInfoMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * @author zhaotianxin
 * @date 2021-04-21 16:28
 */
public class CaseNumUtil {
    public static final String REDIS_CASE_NUM_FLAG = "caseNum:";

    public static Long getNewCaseNum(Long projectId) {
        RedisTemplate<String, Object> redisTemplate = SpringBeanUtil.getBean(RedisTemplate.class);
        RedisAtomicLong atomicLong = new RedisAtomicLong(REDIS_CASE_NUM_FLAG + projectId, redisTemplate.getConnectionFactory());
        if (atomicLong.get() == 0) {
            synchronized (CaseNumUtil.class) {
                atomicLong = new RedisAtomicLong(REDIS_CASE_NUM_FLAG + projectId, redisTemplate.getConnectionFactory());
                if (atomicLong.get() == 0) {
                    TestProjectInfoMapper projectInfoMapper = ApplicationContextHelper.getSpringFactory().getBean(TestProjectInfoMapper.class);
                    Long caseNum = projectInfoMapper.queryByProjectId(projectId).getCaseMaxNum();
                    atomicLong = new RedisAtomicLong(REDIS_CASE_NUM_FLAG + projectId, redisTemplate.getConnectionFactory(), caseNum);
                }
            }
        }
        return atomicLong.incrementAndGet();
    }
}
