package io.choerodon.test.manager.api.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.api.dto.DemoPayload;
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

    @SagaTask(code = "test-demo-data", description = "初始化测试管理demo数据", sagaCode = "agile-demo-for-test", seq = 1)
    public void initTestDemoData(String message) throws IOException {
        DemoPayload demoPayloadE = objectMapper.readValue(message, DemoPayload.class);
        demoService.demoInit(demoPayloadE);
    }
}
