package io.choerodon.test.manager.app.eventhandler;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.test.manager.api.vo.event.ProjectEvent;
import io.choerodon.test.manager.app.service.TestProjectInfoService;
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

    private static final String REGISTER_TEST_INIT_PROJECT = "register-test-init-project";
    private static final String REGISTER_ORG = "register-org";

    @Autowired
    private TestProjectInfoService testProjectInfoService;

    @Autowired
    private DemoService demoService;


    private ObjectMapper objectMapper = new ObjectMapper();


    @SagaTask(code = REGISTER_TEST_INIT_PROJECT,
            description = "demo消费创建项目事件初始化项目数据",
            sagaCode = REGISTER_ORG,
            seq = 105)
    public DemoPayload demoInitProject(String message) {
        DemoPayload organizationRegisterPayload = JSONObject.parseObject(message, DemoPayload.class);
        //初始化项目层数据
        ProjectEvent projectEvent = new ProjectEvent();
        projectEvent.setProjectId(organizationRegisterPayload.getProject().getId());
        projectEvent.setProjectCode(organizationRegisterPayload.getProject().getCode());
        //初始化项目层数据
        testProjectInfoService.initializationProjectInfo(projectEvent);
        return organizationRegisterPayload;
    }

    @SagaTask(code = "register-test-init-demo-data",
            description = "创建test的demo数据",
            sagaCode = "register-org",
            seq = 180)
    public OrganizationRegisterEventPayload initTestDemoData(String message) throws IOException {
        DemoPayload demoPayload = objectMapper.readValue(message, DemoPayload.class);
        return demoService.demoInit(demoPayload);
    }
}
