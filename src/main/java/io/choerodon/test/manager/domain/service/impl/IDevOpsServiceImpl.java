package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.app.service.TestAppInstanceService;
import io.choerodon.test.manager.domain.service.IDevOpsService;
import io.choerodon.test.manager.domain.service.ITestAppInstanceService;
import io.choerodon.test.manager.domain.service.ITestAutomationHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;
import io.choerodon.test.manager.infra.common.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class IDevOpsServiceImpl implements IDevOpsService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    DevopsService devopsService;

    @Autowired
    ITestAppInstanceService testAppInstanceService;


    @Value("${autotesting.lock.leaseTimeSeconds:10}")
    int leaseTime;

    Log log= LogFactory.getLog(this.getClass());

    private static final String CODE="test_manager_task_lock";


    public void getPodStatus(){
        if(Thread.currentThread().isInterrupted())
            return;

        RLock lock=redissonClient.getLock(CODE);
        boolean res = false;
        try {
            res = lock.tryLock(0,leaseTime, TimeUnit.SECONDS);
            LogUtils.debugLog(log,Thread.currentThread().getName()+" get redis lock."+res);

        } catch (InterruptedException e) {
            LogUtils.errorLog(log,e);
            Thread.currentThread().interrupt();
        }
        if(res){
            if(Thread.currentThread().isInterrupted()){
                lock.unlock();
                return;
            }
            TestAppInstanceE instanceE=new TestAppInstanceE();
            instanceE.setPodStatus(0L);
            try {
                List<TestAppInstanceE> list=testAppInstanceService.query(instanceE);
                if(!ObjectUtils.isEmpty(list)){
                    Map releaseList=list.stream().collect(Collectors.groupingBy(TestAppInstanceE::getEnvId,
                            Collectors.mapping((v)->"att-"+v.getAppId()+"-"+v.getAppVersionId()+"-"+v.getId(),Collectors.toList())));
                    devopsService.getTestStatus(releaseList);
                }
            }catch (Exception e){
                log.warn(e);
            }
            lock.unlock();
            LogUtils.debugLog(log,Thread.currentThread().getName()+" release redis lock."+res);
        }
    }

}
