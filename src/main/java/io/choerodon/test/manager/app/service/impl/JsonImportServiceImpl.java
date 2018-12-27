package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.JsonImportService;
import io.choerodon.test.manager.domain.service.IExcelImportService;
import io.choerodon.test.manager.domain.service.IJsonImportService;
import io.choerodon.test.manager.domain.service.ITestAppInstanceService;
import io.choerodon.test.manager.domain.service.ITestAutomationResultService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JsonImportServiceImpl implements JsonImportService {

    private static final Logger logger = LoggerFactory.getLogger(JsonImportServiceImpl.class);

    @Autowired
    private IJsonImportService iJsonImportService;

    @Autowired
    private IExcelImportService iExcelImportService;

    @Autowired
    private ITestAutomationResultService iTestAutomationResultService;

    @Autowired
    private ITestAppInstanceService iTestAppInstanceService;

    private void createStepsAndBackfillStepIds(List<TestCycleCaseE> cycleCases, Long createdBy, Long lastUpdatedBy) {
        List<TestCaseStepE> allSteps = new ArrayList<>();
        for (TestCycleCaseE cycleCase : cycleCases) {
            for (TestCaseStepE testCaseStepE : cycleCase.getTestCaseSteps()) {
                testCaseStepE.setCreatedBy(createdBy);
                testCaseStepE.setLastUpdatedBy(lastUpdatedBy);
                allSteps.add(testCaseStepE);
            }
        }

        List<TestCaseStepE> createdSteps = TestCaseStepE.createSteps(allSteps);
        for (int i = 0; i < allSteps.size(); i++) {
            allSteps.get(i).setStepId(createdSteps.get(i).getStepId());
        }

        for (TestCycleCaseE cycleCase : cycleCases) {
            for (int i = 0; i < cycleCase.getCycleCaseStep().size(); i++) {
                cycleCase.getCycleCaseStep().get(i).setStepId(cycleCase.getTestCaseSteps().get(i).getStepId());
            }
        }
    }

    private void createCycleCasesAndBackfillExecuteIds(List<TestCycleCaseE> testCycleCases,Long projectId) {
        List<TestCycleCaseE> createdTestCycleCases = TestCycleCaseE.createCycleCases(testCycleCases,projectId);
        for (int i = 0; i < testCycleCases.size(); i++) {
            testCycleCases.get(i).setExecuteId(createdTestCycleCases.get(i).getExecuteId());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long importMochaReport(String releaseName, String json) {
        Assert.hasText(json, "error.issue.import.json.blank");

        // 查询versionId和projectId
        Map<String, Long> releaseNameFragments = iJsonImportService.parseReleaseName(releaseName);
        TestAppInstanceE testAppInstanceE = new TestAppInstanceE();
        testAppInstanceE.setId(releaseNameFragments.get("instanceId"));
        TestAppInstanceE instance = iTestAppInstanceService.queryOne(testAppInstanceE);
        if (instance == null) {
            logger.error("app instance 不存在");
            throw new CommonException("app instance 不存在");
        }
        Long versionId = instance.getProjectVersionId();
        Long projectId = instance.getProjectId();
        Long createdBy = instance.getCreatedBy();
        Long lastUpdatedBy = instance.getLastUpdatedBy();

        // 查询组织Id, appName和appVersionName
        String appName = iJsonImportService.getAppName(projectId, releaseNameFragments.get("appId"));
        String appVersionName = iJsonImportService.getAppVersionName(projectId, releaseNameFragments.get("appVersionId"));
        Long organizationId = iJsonImportService.getOrganizationId(projectId);
        String folderName = appName + "-" + appVersionName;

        // 保存完整json到数据库
        TestAutomationResultE testAutomationResultE = SpringUtil.getApplicationContext().getBean(TestAutomationResultE.class);
        testAutomationResultE.setResult(json);
        testAutomationResultE.setCreatedBy(createdBy);
        testAutomationResultE.setLastUpdatedBy(lastUpdatedBy);
        Long resultId = iTestAutomationResultService.add(testAutomationResultE).getId();

        // 创建文件夹
        TestIssueFolderE targetFolderE = iJsonImportService.getFolder(projectId, versionId, folderName);

        // 创建循环
        TestCycleE testCycleE = iJsonImportService.getCycle(versionId, "自动化测试");

        // 创建阶段
        TestCycleDTO testStage = iJsonImportService.getStage(
                versionId, folderName, testCycleE.getCycleId(), targetFolderE.getFolderId(), createdBy, lastUpdatedBy);

        // 找到要解析的片段，准备数据容器
        JSONArray issues = JSON.parseObject(json).getJSONObject("suites").getJSONArray("suites");
        TestAutomationHistoryE automationHistoryE = new TestAutomationHistoryE();
        automationHistoryE.setInstanceId(releaseNameFragments.get("instanceId"));
        automationHistoryE.setTestStatus(TestAutomationHistoryE.Status.COMPLETE);
        automationHistoryE.setLastUpdatedBy(lastUpdatedBy);
        automationHistoryE.setCycleId(testStage.getCycleId());

        // 如果测试用例数量为 0
        if (issues.isEmpty()) {
            automationHistoryE.setResultId(resultId);
            iJsonImportService.updateAutomationHistoryStatus(automationHistoryE);
            return resultId;
        }

        List<TestCycleCaseE> allTestCycleCases = new ArrayList<>();

        // 开始解析
        for (Object element : issues) {
            if (element instanceof JSONObject) {
                TestCycleCaseE testCycleCaseE = iJsonImportService.processIssueJson(
                        organizationId, projectId, versionId, targetFolderE.getFolderId(), testStage.getCycleId(), createdBy,
                        (JSONObject) element, targetFolderE.getNewFolder());
                allTestCycleCases.add(testCycleCaseE);
            }
        }

        if (!targetFolderE.getNewFolder()) {
            relatedToExistIssues(allTestCycleCases, targetFolderE);
        }

        // 将数据容器中的数据保存到数据库，并更新automation history状态
        for (TestCycleCaseE testCycleCaseE : allTestCycleCases) {
            testCycleCaseE.setCreatedBy(createdBy);
            testCycleCaseE.setLastUpdatedBy(lastUpdatedBy);
            testCycleCaseE.setAssignedTo(createdBy);
            if (!testCycleCaseE.isPassed()) {
                automationHistoryE.setTestStatus(TestAutomationHistoryE.Status.PARTIALEXECUTION);
            }
        }
        createCycleCasesAndBackfillExecuteIds(allTestCycleCases,projectId);
        if (targetFolderE.getNewFolder()) {
            createStepsAndBackfillStepIds(allTestCycleCases, createdBy, lastUpdatedBy);
        }

        backfillAndCreateCycleCaseStep(allTestCycleCases, automationHistoryE, createdBy, lastUpdatedBy);

        automationHistoryE.setResultId(resultId);
        iJsonImportService.updateAutomationHistoryStatus(automationHistoryE);

        return resultId;
    }

    private void backfillAndCreateCycleCaseStep(List<TestCycleCaseE> allTestCycleCases, TestAutomationHistoryE automationHistoryE,
                                                Long createdBy, Long lastUpdatedBy) {
        List<TestCycleCaseStepE> testCycleCaseSteps = new ArrayList<>();
        List<TestCycleCaseStepE> currentTestCycleCaseSteps;
        TestCycleCaseStepE currentCycleCaseStepE;
        int i;
        for (TestCycleCaseE testCycleCaseE : allTestCycleCases) {
            currentTestCycleCaseSteps = testCycleCaseE.getCycleCaseStep();
            for (i = 0; i < currentTestCycleCaseSteps.size(); i++) {
                currentCycleCaseStepE = currentTestCycleCaseSteps.get(i);
                if (!currentCycleCaseStepE.isPassed()) {
                    automationHistoryE.setTestStatus(TestAutomationHistoryE.Status.PARTIALEXECUTION);
                }
                currentCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
                currentCycleCaseStepE.setCreatedBy(createdBy);
                currentCycleCaseStepE.setLastUpdatedBy(lastUpdatedBy);
                testCycleCaseSteps.add(currentCycleCaseStepE);
            }
        }

        TestCycleCaseStepE.createCycleCaseSteps(testCycleCaseSteps);
    }

    private void relatedToExistIssues(List<TestCycleCaseE> allTestCycleCases, TestIssueFolderE targetFolderE) {
        List<TestIssueFolderRelE> issueFolderRels = iJsonImportService.queryAllUnderFolder(targetFolderE);
        issueFolderRels.sort((o1, o2) -> (int) (o1.getId() - o2.getId()));
        for (int i = 0; i < allTestCycleCases.size(); i++) {
            Long issueId = issueFolderRels.get(i).getIssueId();
            allTestCycleCases.get(i).setIssueId(issueId);
            allTestCycleCases.get(i).getCycleCaseStep().forEach(cycleCaseStepE -> cycleCaseStepE.setIssueId(issueId));
            List<TestCaseStepE> testCaseStepEs = iJsonImportService.queryAllStepsUnderIssue(issueId);
            if (allTestCycleCases.get(i).getCycleCaseStep().size() != testCaseStepEs.size()) {
                logger.error("报告内容和 {} 只读文件夹中的内容不一致", targetFolderE.getName());
                throw new CommonException("报告内容和 " + targetFolderE.getName() + " 只读文件夹中的内容不一致");
            }
            for (int j = 0; j < testCaseStepEs.size(); j++) {
                allTestCycleCases.get(i).getCycleCaseStep().get(j).setStepId(testCaseStepEs.get(j).getStepId());
            }
        }
    }
}
