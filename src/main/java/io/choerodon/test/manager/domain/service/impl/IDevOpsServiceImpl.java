package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.service.IDevOpsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IDevOpsServiceImpl implements IDevOpsService {

//    @Autowired
//    RedissonClient redissonClient;

    private final static String code="test_manager_redis_lock";


//    @Scheduled(cron = "0/20 * * * * ? ")
    public void getPodStatus(){
//        RLock lock=redissonClient.getLock(code);
//        boolean res = false;
//        try {
//            res = lock.tryLock(1,20, TimeUnit.SECONDS);
//            System.out.println("get Lock,,"+res);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//        if(res){
//            lock.unlock();
//            System.out.println("release Lock");
//        }
    }

}
