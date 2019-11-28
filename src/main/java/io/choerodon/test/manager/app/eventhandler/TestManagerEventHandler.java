package io.choerodon.test.manager.app.eventhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.api.vo.CaseSelectVO;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.api.vo.event.ProjectEvent;
import io.choerodon.test.manager.infra.constant.SagaTaskCodeConstants;
import io.choerodon.test.manager.infra.constant.SagaTopicCodeConstants;
import io.choerodon.test.manager.infra.dto.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.api.vo.event.InstancePayload;
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

//    @Autowired
//    private TestIssueFolderRelService testIssueFolderRelService;
//
//    @Autowired
//    private TestCycleCaseService testCycleCaseService;
//
//    @Autowired
//    private TestCaseStepService testCaseStepService;
//
//    @Autowired
//    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private TestAppInstanceService testAppInstanceService;

    @Autowired
    private TestAutomationHistoryService testAutomationHistoryService;

    @Autowired
    private TestProjectInfoService testProjectInfoService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestPlanServcie testPlanServcie;

//    @Autowired
//    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(TestManagerEventHandler.class);

//    private void loggerInfo(Object o) {
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.info("data: {}", o);
//        }
//    }

//    /**
//     * 创建临时循环事件
//     *
//     * @param message
//     */
//    @SagaTask(code = "test-create-version",
//            description = "创建临时循环事件",
//            sagaCode = "agile-create-version",
//            //enabledDbRecord = true,
//            seq = 1)
//    public VersionEvent handleProjectVersionCreateEvent(String message) throws IOException {
//        VersionEvent versionEvent = objectMapper.readValue(message, VersionEvent.class);
//        loggerInfo(versionEvent);
//        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
//        testIssueFolderVO.setType(TestIssueFolderType.TYPE_TEMP);
//        testIssueFolderVO.setProjectId(versionEvent.getProjectId());
//        testIssueFolderVO.setVersionId(versionEvent.getVersionId());
//        testIssueFolderVO.setName("临时");
//        testIssueFolderService.create(versionEvent.getProjectId(),testIssueFolderVO);
//        return versionEvent;
//    }
//
//    /**
//     * 版本删除事件
//     *
//     * @param message
//     */
//    @SagaTask(code = "test-delete-version",
//            description = "删除version事件，删除相关测试数据",
//            sagaCode = "agile-delete-version",
//            //enabledDbRecord = true,
//            seq = 1)
//    public VersionEvent handleProjectVersionDeleteEvent(String message) throws IOException {
//        VersionEvent versionEvent = objectMapper.readValue(message, VersionEvent.class);
//        loggerInfo(versionEvent);
//        List<TestIssueFolderVO> testIssueFolderVOS = testIssueFolderService.queryByParameter(versionEvent.getProjectId(), versionEvent.getVersionId());
//        testIssueFolderVOS.forEach(v -> testIssueFolderService.delete(versionEvent.getProjectId(), v.getFolderId()));
//        return versionEvent;
//    }
//
//    /**
//     * 问题删除事件
//     *
//     * @param message
//     */
//    @SagaTask(code = "test-delete-issue",
//            description = "删除issue事件，删除相关测试数据",
//            sagaCode = "agile-delete-issue",
//            // enabledDbRecord = true,
//            seq = 1)
//    public IssuePayload handleProjectIssueDeleteEvent(String message) throws IOException {
//        IssuePayload issuePayload = objectMapper.readValue(message, IssuePayload.class);
//        TestCycleCaseDefectRelDTO defectRelE = new TestCycleCaseDefectRelDTO();
//        defectRelE.setIssueId(issuePayload.getIssueId());
//        testCycleCaseDefectRelMapper.delete(defectRelE);
//        TestCycleCaseVO testCycleCaseVO = new TestCycleCaseVO();
//        testCycleCaseVO.setIssueId(issuePayload.getIssueId());
//        testCycleCaseService.batchDelete(testCycleCaseVO, issuePayload.getProjectId());
//
//        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();
//        testIssueFolderRelVO.setIssueId(issuePayload.getIssueId());
//
//        List<Long> issuesId = Lists.newArrayList(issuePayload.getIssueId());
//        testIssueFolderRelService.delete(issuePayload.getProjectId(), issuesId);
//
//        TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
//        testCaseStepVO.setIssueId(issuePayload.getIssueId());
//        testCaseStepService.removeStep(testCaseStepVO);
//        return issuePayload;
//    }


    @SagaTask(code = TASK_PROJECT_CREATE,
            description = "test消费创建项目事件初始化项目数据",
            sagaCode = PROJECT_CREATE,
            seq = 2)
    public String handleProjectInitByConsumeSagaTask(String message) {
        ProjectEvent projectEvent = JSONObject.parseObject(message, ProjectEvent.class);
        LOGGER.info("接受创建项目消息{}", message);
        testProjectInfoService.initializationProjectInfo(projectEvent);
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
        LOGGER.info(message);
        TestPlanVO testPlanVO = objectMapper.readValue(message, TestPlanVO.class);
        // 获取用例和文件夹信息
        List<TestIssueFolderDTO> testIssueFolderDTOS = new ArrayList<>();
        List<TestCaseDTO> testCaseDTOS = new ArrayList<>();
        List<TestCaseDTO> allTestCase = testCaseService.listCaseByProjectId(testPlanVO.getProjectId());

        // 是否自选
        if (!testPlanVO.getCustom()) {
            testCaseDTOS.addAll(allTestCase);
            List<Long> folderIds = testCaseDTOS.stream().map(TestCaseDTO::getFolderId).collect(Collectors.toList());
            testIssueFolderDTOS = testIssueFolderService.listFolderByFolderIds(folderIds);
        } else {
            Map<Long, CaseSelectVO> maps = testPlanVO.getCaseSelected();
            List<Long> folderIds = maps.keySet().stream().collect(Collectors.toList());
            testIssueFolderDTOS = testIssueFolderService.listFolderByFolderIds(folderIds);
            Map<Long, List<TestCaseDTO>> caseMap = allTestCase.stream().collect(Collectors.groupingBy(TestCaseDTO::getFolderId));
            List<Long> caseIds = new ArrayList<>();
            for (Long key : maps.keySet()) {
                CaseSelectVO caseSelectVO = maps.get(key);
                // 判断是否是自选
                if (!caseSelectVO.getCustom()) {
                    testCaseDTOS.addAll(caseMap.get(key));
                } else {
                    // 判断是反选还是正向选择
                    if (ObjectUtils.isEmpty(caseSelectVO.getSelected())) {
                        // 反选就
                        List<Long> unSelected = caseSelectVO.getUnSelected();
                        // 获取文件夹所有的测试用例
                        List<Long> allList = caseMap.get(key).stream().filter(v -> ObjectUtils.isEmpty(v)).map(TestCaseDTO::getCaseId).collect(Collectors.toList());
                        allList.removeAll(unSelected);
                        caseIds.addAll(allList);
                    } else {
                        caseIds.addAll(caseSelectVO.getSelected());
                    }
                }
            }

            if (!CollectionUtils.isEmpty(caseIds)) {
                testCaseDTOS.addAll(testCaseService.listByCaseIds(testPlanVO.getProjectId(), caseIds));
            }

        }
        TestPlanDTO testPlanDTO = modelMapper.map(testPlanVO, TestPlanDTO.class);
        // 创建测试循环
        List<TestCycleDTO> testCycleDTOS = testCycleService.batchInsertByFoldersAndPlan(testPlanDTO, testIssueFolderDTOS);
        // 创建测试循环用例
        Map<Long, TestCycleDTO> testCycleMap = testCycleDTOS.stream().collect(Collectors.toMap(TestCycleDTO::getFolderId, Function.identity()));
        testCycleCaseService.batchInsertByTestCase(testCycleMap, testCaseDTOS);
        TestPlanDTO testPlan = new TestPlanDTO();
        testPlan.setPlanId(testPlanDTO.getPlanId());
        testPlan.setInitStatus("done");
        testPlan.setObjectVersionNumber(testPlanDTO.getObjectVersionNumber());
        testPlanServcie.baseUpdate(testPlan);

    }
}