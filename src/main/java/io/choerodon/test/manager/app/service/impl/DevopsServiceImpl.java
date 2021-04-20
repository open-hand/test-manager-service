package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.devops.AppServiceVersionRespVO;
import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper;
import io.choerodon.test.manager.infra.util.TypeUtil;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DevopsServiceImpl implements DevopsService {

    private static final Logger logger = LoggerFactory.getLogger(DevopsServiceImpl.class);

    @Value("${autotesting.lock.leaseTimeSeconds:10}")
    int leaseTime;

    @Value("${autotesting.lock.delayTimeMinutes:15}")
    int delayTime;

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Autowired
    private ApplicationFeignClient applicationFeignClient;

    @Autowired
    private TestAppInstanceMapper testAppInstanceMapper;
    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public void getTestStatus(Map<Long, List<String>> releaseName) {
        applicationFeignClient.getTestStatus(releaseName);
    }

    @Override
    public List<Long> getAppVersionId(String appName, Long projectId, Long appId) {

        ResponseEntity<Page<AppServiceVersionRespVO>> list = applicationFeignClient.pageByOptions(projectId, 0, 9999999, true, "id", appId, appName);
        return list.getBody().getContent().stream().map(AppServiceVersionRespVO::getId).collect(Collectors.toList());
    }

    @Override
    public Map<Long, AppServiceVersionRespVO> getAppversion(Long projectId, List<Long> appVersionIds) {
        if (!appVersionIds.isEmpty()) {
            return applicationFeignClient.getAppversion(projectId, TypeUtil.longsToArray(appVersionIds)).getBody().stream()
                    .collect(Collectors.toMap(AppServiceVersionRespVO::getId, Function.identity()));
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void getPodStatus() {
        List<String> services = discoveryClient.getServices();
        if (!services.contains("devops-service")) {
            return;
        }
        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        Lock lock = redisLockRegistry.obtain("task_lock");
        boolean res = false;
        try {
            res = lock.tryLock(leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("try lock error: {}", e);
            Thread.currentThread().interrupt();
        }
        if (res) {
            if (Thread.currentThread().isInterrupted()) {
                lock.unlock();
                return;
            }
            try {
                List<TestAppInstanceDTO> list = queryDelayInstance(delayTime);
                if (!ObjectUtils.isEmpty(list)) {
                    Map releaseList = list.stream().collect(Collectors.groupingBy(TestAppInstanceDTO::getEnvId,
                            Collectors.mapping(v -> "att-" + Long.toHexString(v.getAppId()) + "-" + Long.toHexString(v.getAppVersionId()) + "-" + Long.toHexString(v.getId()), Collectors.toList())));
                    getTestStatus(releaseList);
                    logger.debug("send to devops server for get instance status: {}", JSONObject.toJSONString(releaseList));
                }
            } catch (Exception e) {
                logger.error("get instance status error: {}", e);
            }
            lock.unlock();
        }
    }

    private List<TestAppInstanceDTO> queryDelayInstance(int delayTime) {
        Date delayTiming = DateUtils.addMinutes(new Date(), -delayTime);
        return testAppInstanceMapper.queryDelayInstance(delayTiming);
    }
}
