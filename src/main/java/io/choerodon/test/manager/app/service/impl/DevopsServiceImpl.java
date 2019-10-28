package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.choerodon.devops.api.vo.AppServiceVersionRespVO;
import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper;
import io.choerodon.test.manager.infra.util.LogUtils;
import io.choerodon.test.manager.infra.util.TypeUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DevopsServiceImpl implements DevopsService {

    private static final String CODE = "test_manager_task_lock";
    private Log log = LogFactory.getLog(this.getClass());

    @Value("${autotesting.lock.leaseTimeSeconds:10}")
    int leaseTime;

    @Value("${autotesting.lock.delayTimeMinutes:15}")
    int delayTime;

    @Autowired
    private RedissonClient redissonClient;

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

        ResponseEntity<PageInfo<AppServiceVersionRespVO>> list = applicationFeignClient.pageByOptions(projectId, 0, 9999999, true, "id", appId, appName);
        return list.getBody().getList().stream().map(AppServiceVersionRespVO::getId).collect(Collectors.toList());
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
        if (Thread.currentThread().isInterrupted())
            return;

        RLock lock = redissonClient.getLock(CODE);
        boolean res = false;
        try {
            res = lock.tryLock(0, leaseTime, TimeUnit.SECONDS);
            LogUtils.debugLog(log, Thread.currentThread().getName() + " get redis lock." + res);

        } catch (InterruptedException e) {
            LogUtils.errorLog(log, e);
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
                            Collectors.mapping(v -> "att-" + v.getAppId() + "-" + v.getAppVersionId() + "-" + v.getId(), Collectors.toList())));
                    getTestStatus(releaseList);
                    LogUtils.debugLog(log, " send to devops server for get instance status:" + JSONObject.toJSONString(releaseList));
                }
            } catch (Exception e) {
                LogUtils.warnLog(log, "Thread.currentThread().getName() warn :", e);
            }
            lock.unlock();
            LogUtils.debugLog(log, Thread.currentThread().getName() + " release redis lock." + res);
        }
    }

    private List<TestAppInstanceDTO> queryDelayInstance(int delayTime) {
        Date delayTiming = DateUtils.addMinutes(new Date(), -delayTime);
        return testAppInstanceMapper.queryDelayInstance(delayTiming);
    }
}
