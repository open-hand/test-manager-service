package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.service.IDevOpsService;
import io.choerodon.test.manager.infra.common.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class IDevOpsServiceImpl implements IDevOpsService {

    @Autowired
    RedissonClient redissonClient;

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
            lock.unlock();
            LogUtils.debugLog(log,Thread.currentThread().getName()+" release redis lock."+res);
        }
    }

}
