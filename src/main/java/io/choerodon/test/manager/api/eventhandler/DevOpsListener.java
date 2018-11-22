package io.choerodon.test.manager.api.eventhandler;

import io.choerodon.test.manager.domain.service.IDevOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class DevOpsListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    IDevOpsService iDevOpsService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent()==null){
            iDevOpsService.getPodStatus();
        }
    }
}
