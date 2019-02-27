package io.choerodon.test.manager.api.eventhandler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.api.dto.DemoPayload;
import io.choerodon.test.manager.api.dto.OrganizationRegisterEventPayload;
import io.choerodon.test.manager.app.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by WangZhe@choerodon.io on 2019-02-14.
 * Email: ettwz@hotmail.com
 */
@Component
public class DemoDataEventHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    DemoService demoService;

    @SagaTask(code = "register-test-init-demo-data",
            description = "创建test的demo数据",
            sagaCode = "register-org",
            seq = 180)
    public String initTestDemoData(String message) throws IOException {
        DemoPayload demoPayloadE = objectMapper.readValue(message, DemoPayload.class);
        OrganizationRegisterEventPayload result = demoService.demoInit(demoPayloadE);

        return JSON.toJSONString(result);
    }
}
