package io.choerodon.test.manager.api.eventhandler;

import io.choerodon.test.manager.domain.service.IDevOpsService;
import io.choerodon.test.manager.infra.common.utils.LogUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Component
public class DevOpsListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    IDevOpsService iDevOpsService;

    ScheduledExecutorService taskService= Executors.newSingleThreadScheduledExecutor(r -> new Thread(r,"AutoTesting ScheduleTask(0)"));


    @Value("${autotesting.task.scheduleTimeSeconds:20}")
    int period;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ContextRefreshedEvent && ((ContextRefreshedEvent)event).getApplicationContext().getParent().getParent()==null){
            taskService.scheduleAtFixedRate(()->iDevOpsService.getPodStatus(),0,period, TimeUnit.SECONDS);
            LogUtils.infoLog(LogFactory.getLog(this.getClass()),"start a autoTesting ScheduleTask");
        }
        if(event instanceof ContextClosedEvent){
            taskService.shutdown();
            LogUtils.infoLog(LogFactory.getLog(this.getClass()),"closing autoTesting ScheduleTask");
        }
    }
}
