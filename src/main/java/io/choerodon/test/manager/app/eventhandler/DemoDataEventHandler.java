package io.choerodon.test.manager.app.eventhandler;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.api.vo.DemoPayload;
import io.choerodon.test.manager.api.vo.OrganizationRegisterEventPayload;
import io.choerodon.test.manager.app.service.DemoService;

/**
 * Created by WangZhe@choerodon.io on 2019-02-14.
 * Email: ettwz@hotmail.com
 */
@Component
public class DemoDataEventHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DemoService demoService;

    @SagaTask(code = "register-test-init-demo-data",
            description = "创建test的demo数据",
            sagaCode = "register-org",
            seq = 180)
    public OrganizationRegisterEventPayload initTestDemoData(String message) throws IOException {
        DemoPayload demoPayload = objectMapper.readValue(message, DemoPayload.class);
        return demoService.demoInit(demoPayload);
    }
}
