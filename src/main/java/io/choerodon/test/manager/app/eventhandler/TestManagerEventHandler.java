package io.choerodon.test.manager.app.eventhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.api.vo.TestCaseStepVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.api.vo.TestIssueFolderRelVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.domain.test.manager.event.InstancePayload;
import io.choerodon.test.manager.domain.test.manager.event.IssuePayload;
import io.choerodon.test.manager.domain.test.manager.event.VersionEvent;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.infra.enums.TestIssueFolderType;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper;

/**
 * Created by WangZhe@choerodon.io on 2018/6/25.
 * Email: ettwz@hotmail.com
 */
@Component
public class TestManagerEventHandler {

    @Autowired
    private TestIssueFolderRelService testIssueFolderRelService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCaseStepService testCaseStepService;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private TestAppInstanceService testAppInstanceService;

    @Autowired
    private TestAutomationHistoryService testAutomationHistoryService;

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

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
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
        testIssueFolderVO.setType(TestIssueFolderType.TYPE_TEMP);
        testIssueFolderVO.setProjectId(versionEvent.getProjectId());
        testIssueFolderVO.setVersionId(versionEvent.getVersionId());
        testIssueFolderVO.setName("临时");
        testIssueFolderService.insert(testIssueFolderVO);
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
        List<TestIssueFolderVO> testIssueFolderVOS = testIssueFolderService.queryByParameter(versionEvent.getProjectId(), versionEvent.getVersionId());
        testIssueFolderVOS.forEach(v -> testIssueFolderService.delete(versionEvent.getProjectId(), v.getFolderId()));
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
        TestCycleCaseDefectRelDTO defectRelE = new TestCycleCaseDefectRelDTO();
        defectRelE.setIssueId(issuePayload.getIssueId());
        testCycleCaseDefectRelMapper.delete(defectRelE);
        TestCycleCaseVO testCycleCaseVO = new TestCycleCaseVO();
        testCycleCaseVO.setIssueId(issuePayload.getIssueId());
        testCycleCaseService.batchDelete(testCycleCaseVO, issuePayload.getProjectId());

        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();
        testIssueFolderRelVO.setIssueId(issuePayload.getIssueId());

        List<Long> issuesId = Lists.newArrayList(issuePayload.getIssueId());
        testIssueFolderRelService.delete(issuePayload.getProjectId(), issuesId);

        TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
        testCaseStepVO.setIssueId(issuePayload.getIssueId());
        testCaseStepService.removeStep(testCaseStepVO);
        return issuePayload;
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
}