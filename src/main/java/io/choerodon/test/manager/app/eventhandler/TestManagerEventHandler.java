package io.choerodon.test.manager.app.eventhandler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.api.vo.event.*;
import io.choerodon.test.manager.infra.constant.SagaTaskCodeConstants;
import io.choerodon.test.manager.infra.constant.SagaTopicCodeConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestPlanInitStatus;
import io.choerodon.test.manager.infra.mapper.TestPlanMapper;
import io.choerodon.test.manager.infra.mapper.TestProjectInfoMapper;
import org.hzero.core.base.BaseConstants;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.app.service.*;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * Created by WangZhe@choerodon.io on 2018/6/25.
 * Email: ettwz@hotmail.com
 */
@Component
public class TestManagerEventHandler {

    public static final String TASK_PROJECT_CREATE = "test-create-project";

    public static final String PROJECT_CREATE = "iam-create-project";

    /**
     * 测试管理模块
     */
    private static final String MODULE_TEST = "N_TEST";

    @Autowired
    private TestAppInstanceService testAppInstanceService;

    @Autowired
    private TestAutomationHistoryService testAutomationHistoryService;

    @Autowired
    private TestProjectInfoService testProjectInfoService;

    @Autowired
    private TestPlanService testPlanService;

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private TestPriorityService testPriorityService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(TestManagerEventHandler.class);

    @SagaTask(code = TASK_PROJECT_CREATE,
            description = "test消费创建项目事件初始化项目数据",
            sagaCode = PROJECT_CREATE,
            seq = 3)
    public String handleProjectInitByConsumeSagaTask(String message) {
        ProjectEvent projectEvent = JSONObject.parseObject(message, ProjectEvent.class);
        LOGGER.info("接受创建项目消息{}", message);
        List<ProjectEventCategory> projectEventCategories = projectEvent.getProjectCategoryVOS();
        if (!ObjectUtils.isEmpty(projectEventCategories)) {
            Set<String> codes =
                    projectEventCategories
                            .stream()
                            .map(ProjectEventCategory::getCode)
                            .collect(Collectors.toSet());
            if (codes.contains(MODULE_TEST)) {
                testProjectInfoService.initializationProjectInfo(projectEvent);
                testIssueFolderService.initializationFolderInfo(projectEvent);
            }
        }
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

    @SagaTask(code = SagaTaskCodeConstants.TEST_MANAGER_CREATE_PLAN_TASK, description = "创建计划", sagaCode = SagaTopicCodeConstants.TEST_MANAGER_CREATE_PLAN, seq = 1)
    public void createPlan(String message) {
        TestPlanVO testPlanVO = JSONObject.parseObject(message, TestPlanVO.class);
        try {
            testPlanService.sagaCreatePlan(testPlanVO);
        } catch (Exception e) {
            testPlanService.setPlanInitStatusFail(modelMapper.map(testPlanMapper.selectByPrimaryKey(testPlanVO.getPlanId()),TestPlanVO.class));
            throw e;
        }

    }

    @SagaTask(code = SagaTaskCodeConstants.TEST_MANAGER_CLONE_PLAN_TASK, description = "复制计划", sagaCode = SagaTopicCodeConstants.TEST_MANAGER_CLONE_PLAN, seq = 1)
    public void clonePlan(String message) {
        Map<String, Long> map = null ;
        try {
            map = JSONObject.parseObject(message,Map.class);
            testPlanService.sagaClonePlan(map);

        } catch (Exception e) {
            if (map != null) {
                testPlanService.setPlanInitStatusFail(modelMapper.map(testPlanMapper.selectByPrimaryKey(map.get("new")), TestPlanVO.class));
            }
            throw e;
        }
    }

    @SagaTask(code = SagaTaskCodeConstants.TEST_MANAGER_PLAN_FAIL_TASK, description = "改变计划为失败状态", sagaCode = SagaTopicCodeConstants.TEST_MANAGER_PLAN_FAIL, seq = 1)
    public void changeStatusFail(String message) {
        TestPlanVO testPlanVO = JSONObject.parseObject(message, TestPlanVO.class);
        testPlanVO.setInitStatus(TestPlanInitStatus.FAIL);
        testPlanService.update(testPlanVO.getProjectId(),testPlanVO);
    }

    @SagaTask(code = SagaTaskCodeConstants.ORG_CREATE,
            description = "创建组织事件",
            sagaCode = SagaTaskCodeConstants.ORG_CREATE,
            seq = 1)
    public String handleOrgaizationCreateByConsumeSagaTask(String data) {
        LOGGER.info("消费创建组织消息{}", data);
        OrganizationCreateEventPayload organizationEventPayload = JSON.parseObject(data, OrganizationCreateEventPayload.class);
        Assert.notNull(organizationEventPayload, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        testPriorityService.createDefaultPriority(organizationEventPayload.getId());
        return data;
    }

    /**
     * 更新项目事件
     *
     * @param message message
     */
    @SagaTask(code = SagaTaskCodeConstants.TASK_PROJECT_UPDATE, sagaCode = SagaTaskCodeConstants.PROJECT_UPDATE,seq = 2,
            description = "test-manager消费更新项目事件初始化项目数据")
    public String handleProjectUpdateByConsumeSagaTask(String message) {
        ProjectEvent projectEvent = JSON.parseObject(message, ProjectEvent.class);
        LOGGER.info("接受更新项目消息{}", message);
        Long projectId = projectEvent.getProjectId();
        TestProjectInfoDTO dto = new TestProjectInfoDTO();
        dto.setProjectId(projectId);
        if (testProjectInfoMapper.select(dto).isEmpty() && !CollectionUtils.isEmpty(projectEvent.getProjectCategoryVOS())) {
            Set<String> codes =
                    projectEvent.getProjectCategoryVOS()
                            .stream()
                            .map(ProjectEventCategory::getCode)
                            .collect(Collectors.toSet());
            if (codes.contains(MODULE_TEST)) {
                testProjectInfoService.initializationProjectInfo(projectEvent);
                testIssueFolderService.initializationFolderInfo(projectEvent);
            }
        } else {
            LOGGER.info("项目{}已初始化，跳过项目初始化", projectEvent.getProjectCode());
        }
        return message;
    }

}