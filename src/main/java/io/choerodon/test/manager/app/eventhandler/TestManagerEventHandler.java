package io.choerodon.test.manager.app.eventhandler;

import java.io.IOException;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.api.vo.event.ProjectEvent;
import io.choerodon.test.manager.infra.constant.SagaTaskCodeConstants;
import io.choerodon.test.manager.infra.constant.SagaTopicCodeConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestPlanInitStatus;
import io.choerodon.test.manager.infra.mapper.TestPlanMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.api.vo.event.InstancePayload;

/**
 * Created by WangZhe@choerodon.io on 2018/6/25.
 * Email: ettwz@hotmail.com
 */
@Component
public class TestManagerEventHandler {

    public static final String TASK_PROJECT_CREATE = "test-create-project";

    public static final String PROJECT_CREATE = "iam-create-project";

    @Autowired
    private TestAppInstanceService testAppInstanceService;

    @Autowired
    private TestAutomationHistoryService testAutomationHistoryService;

    @Autowired
    private TestProjectInfoService testProjectInfoService;

    @Autowired
    private TestPlanServcie testPlanServcie;

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private ModelMapper modelMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(TestManagerEventHandler.class);

    @SagaTask(code = TASK_PROJECT_CREATE,
            description = "test消费创建项目事件初始化项目数据",
            sagaCode = PROJECT_CREATE,
            seq = 3)
    public String handleProjectInitByConsumeSagaTask(String message) {
        ProjectEvent projectEvent = JSONObject.parseObject(message, ProjectEvent.class);
        LOGGER.info("接受创建项目消息{}", message);
        testProjectInfoService.initializationProjectInfo(projectEvent);
        testIssueFolderService.initializationFolderInfo(projectEvent);
        return message;
    }

    @SagaTask(code = "test-start-instance", description = "更新Appinstance状态", sagaCode = "test-pod-update-saga", seq = 1)
    public void updateInstance(String message) throws IOException {
        InstancePayload instanceE = objectMapper.readValue(message, InstancePayload.class);
        testAppInstanceService.updateInstance(instanceE.getReleaseNames(), instanceE.getPodName(), instanceE.getConName());
    }

    @SagaTask(code = "test-close-instance", description = "更新Appinstance状态", sagaCode = "test-job-log-saga", seq = 1)
    public void closeInstance(String message) throws IOException {
        InstancePayload instanceE = objectMapper.readValue(message, InstancePayload.class);
        testAppInstanceService.updateLog(instanceE.getReleaseNames(), instanceE.getLogFile());
        Long instanceId = Long.valueOf(TestAppInstanceDTO.getInstanceIDFromReleaseName(instanceE.getReleaseNames()));
        if (instanceE.getStatus() == 1L) {
            testAppInstanceService.updateStatus(instanceId, 2L);
        } else if (instanceE.getStatus() == -1L) {
            testAppInstanceService.updateStatus(instanceId, 3L);
            testAutomationHistoryService.shutdownInstance(instanceId, instanceE.getStatus());
        }
    }


    @SagaTask(code = "test-update-instance-history", description = "更新AppinstanceHistory状态", sagaCode = "test-status-saga", seq = 1)
    public void updateInstanceHistory(String message) throws IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, InstancePayload.class);
        List<InstancePayload> list = objectMapper.readValue(message, javaType);
        list.stream().filter(v -> v.getStatus().equals(0L)).distinct().forEach(u -> {
            Long instanceId = Long.valueOf(TestAppInstanceDTO.getInstanceIDFromReleaseName(u.getReleaseNames()));
            testAppInstanceService.updateStatus(instanceId, 3L);
            testAutomationHistoryService.shutdownInstance(instanceId, u.getStatus());
        });
    }

    @SagaTask(code = SagaTaskCodeConstants.TEST_MANAGER_CREATE_PLAN, description = "创建计划", sagaCode = SagaTopicCodeConstants.TEST_MANAGER_CREATE_PLAN, seq = 1)
    public void createPlan(String message) throws IOException {
        TestPlanVO testPlanVO = objectMapper.readValue(message, TestPlanVO.class);
        try {
            testPlanServcie.sagaCreatePlan(testPlanVO);
        } catch (Exception e) {
            testPlanServcie.SetPlanInitStatusFail(modelMapper.map(testPlanMapper.selectByPrimaryKey(testPlanVO.getPlanId()),TestPlanVO.class));
            throw e;
        }

    }

    @SagaTask(code = SagaTaskCodeConstants.TEST_MANAGER_CLONE_PLAN, description = "创建计划", sagaCode = SagaTopicCodeConstants.TEST_MANAGER_CLONE_PLAN, seq = 1)
    public void clonePlan(String message) throws IOException {
        Map<String, Integer> map = null ;
        try {
            map = (Map<String, Integer>) objectMapper.readValue(message, Map.class);
            testPlanServcie.sagaClonePlan(map);
        } catch (Exception e) {
            testPlanServcie.SetPlanInitStatusFail(modelMapper.map(testPlanMapper.selectByPrimaryKey(map.get("new").longValue()),TestPlanVO.class));
            throw e;
        }
    }

    @SagaTask(code = SagaTaskCodeConstants.TEST_MANAGER_PLAN_FAIL, description = "创建计划", sagaCode = SagaTopicCodeConstants.TEST_MANAGER_PLAN_FAIL, seq = 1)
    public void changeStatusFail(String message) throws IOException {
        TestPlanVO testPlanVO = objectMapper.readValue(message, TestPlanVO.class);
        testPlanVO.setInitStatus(TestPlanInitStatus.FAIL);
        testPlanServcie.update(testPlanVO.getProjectId(),testPlanVO);
    }

}