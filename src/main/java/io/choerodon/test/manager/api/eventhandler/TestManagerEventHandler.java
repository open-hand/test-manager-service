package io.choerodon.test.manager.api.eventhandler;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.service.ITestAutomationHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.event.InstancePayload;
import io.choerodon.test.manager.domain.test.manager.event.IssuePayload;
import io.choerodon.test.manager.domain.test.manager.event.VersionEvent;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangZhe@choerodon.io on 2018/6/25.
 * Email: ettwz@hotmail.com
 */
@Component
public class TestManagerEventHandler {

    @Autowired
    TestIssueFolderRelService testIssueFolderRelService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCaseStepService testCaseStepService;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private FixDataService fixDataService;

    @Autowired
    private TestAppInstanceService testAppInstanceService;

    @Autowired
    private ITestAutomationHistoryService iTestAutomationHistoryService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(TestManagerEventHandler.class);

    private void loggerInfo(Object o) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("data: {}", o);
        }
    }

    /**
     * 创建临时循环事件
     *
     * @param message
     */
    @SagaTask(code = "test-create-version",
            description = "创建临时循环事件",
            sagaCode = "agile-create-version",
            //enabledDbRecord = true,
            seq = 1)
    public VersionEvent handleProjectVersionCreateEvent(String message) throws IOException {
        VersionEvent versionEvent = objectMapper.readValue(message, VersionEvent.class);
        loggerInfo(versionEvent);
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setType(TestIssueFolderE.TYPE_TEMP);
        testIssueFolderDTO.setProjectId(versionEvent.getProjectId());
        testIssueFolderDTO.setVersionId(versionEvent.getVersionId());
        testIssueFolderDTO.setName("临时");
        testIssueFolderService.insert(testIssueFolderDTO);
        return versionEvent;
    }

    /**
     * 版本删除事件
     *
     * @param message
     */
    @SagaTask(code = "test-delete-version",
            description = "删除version事件，删除相关测试数据",
            sagaCode = "agile-delete-version",
            //enabledDbRecord = true,
            seq = 1)
    public VersionEvent handleProjectVersionDeleteEvent(String message) throws IOException {
        VersionEvent versionEvent = objectMapper.readValue(message, VersionEvent.class);
        loggerInfo(versionEvent);
        List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderService.queryByParameter(versionEvent.getProjectId(), versionEvent.getVersionId());
        testIssueFolderDTOS.forEach(v -> testIssueFolderService.delete(versionEvent.getProjectId(), v.getFolderId()));
        return versionEvent;
    }

    /**
     * 问题删除事件
     *
     * @param message
     */
    @SagaTask(code = "test-delete-issue",
            description = "删除issue事件，删除相关测试数据",
            sagaCode = "agile-delete-issue",
            // enabledDbRecord = true,
            seq = 1)
    public IssuePayload handleProjectIssueDeleteEvent(String message) throws IOException {
        IssuePayload issuePayload = objectMapper.readValue(message, IssuePayload.class);
        TestCycleCaseDefectRelE defectRelE = TestCycleCaseDefectRelEFactory.create();
        defectRelE.setIssueId(issuePayload.getIssueId());
        defectRelE.deleteSelf();
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setIssueId(issuePayload.getIssueId());
        testCycleCaseService.batchDelete(testCycleCaseDTO, issuePayload.getProjectId());

        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setIssueId(issuePayload.getIssueId());

        List<Long> issuesId = Lists.newArrayList(issuePayload.getIssueId());
        testIssueFolderRelService.delete(issuePayload.getProjectId(), issuesId);

        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
        testCaseStepDTO.setIssueId(issuePayload.getIssueId());
        testCaseStepService.removeStep(testCaseStepDTO);
        return issuePayload;
    }

    /**
     * 问题删除事件
     *
     * @param message
     */
    @SagaTask(code = "test-fix-data",
            description = "修复数据",
            sagaCode = "test-fix-cycle-data",
            seq = 1)
    public void fixData(String message) throws IOException {
        TestIssueFolderDTO testIssueFolderDTO = objectMapper.readValue(message, TestIssueFolderDTO.class);
        fixDataService.fixCycleData(testIssueFolderDTO.getProjectId());
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
        Long instanceId = Long.valueOf(TestAppInstanceE.getInstanceIDFromReleaseName(instanceE.getReleaseNames()));
        if (instanceE.getStatus() == 1L) {
            testAppInstanceService.updateStatus(instanceId, 2L);
        } else if (instanceE.getStatus() == -1L) {
            testAppInstanceService.updateStatus(instanceId,3L);
            iTestAutomationHistoryService.shutdownInstance(instanceId, instanceE.getStatus());
        }
    }


    @SagaTask(code = "test-update-instance-history", description = "更新AppinstanceHistory状态", sagaCode = "test-status-saga", seq = 1)
    public void updateInstanceHistory(String message) throws IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, InstancePayload.class);
        List<InstancePayload> list = objectMapper.readValue(message, javaType);
        list.stream().filter(v -> v.getStatus().equals(0L)).distinct().forEach(u -> {
            Long instanceId = Long.valueOf(TestAppInstanceE.getInstanceIDFromReleaseName(u.getReleaseNames()));
            testAppInstanceService.updateStatus(instanceId,3L);
            iTestAutomationHistoryService.shutdownInstance(instanceId, u.getStatus());
        });
    }
}