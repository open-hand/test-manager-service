package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.JsonImportService;
import io.choerodon.test.manager.domain.service.IExcelImportService;
import io.choerodon.test.manager.domain.service.IJsonImportService;
import io.choerodon.test.manager.domain.service.ITestAppInstanceService;
import io.choerodon.test.manager.domain.service.ITestAutomationResultService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;

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

    private void createStepsAndBackfillStepIds(List<TestCaseStepE> caseSteps) {
        List<TestCaseStepE> createdSteps = TestCaseStepE.createSteps(caseSteps);
        for (int i = 0; i < caseSteps.size(); i++) {
            caseSteps.get(i).setStepId(createdSteps.get(i).getStepId());
        }
    }

    private void createCycleCasesAndBackfillExecuteIds(List<TestCycleCaseE> testCycleCases) {
        List<TestCycleCaseE> createdTestCycleCases = TestCycleCaseE.createCycleCases(testCycleCases);
        for (int i = 0; i < testCycleCases.size(); i++) {
            testCycleCases.get(i).setExecuteId(createdTestCycleCases.get(i).getExecuteId());
        }
    }

    @Override
    @Transactional
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

        // 异步查询组织Id, appName和appVersionName
        CompletableFuture<String> getAppNameTask = CompletableFuture.supplyAsync(() ->
                iJsonImportService.getAppName(projectId, releaseNameFragments.get("appId")));
        CompletableFuture<String> getAppVersionNameTask = CompletableFuture.supplyAsync(() ->
                iJsonImportService.getAppVersionName(projectId, releaseNameFragments.get("appVersionId")));
        Long organizationId = iJsonImportService.getOrganizationId(projectId);
        String folderName = getAppNameTask.join() + "-" + getAppVersionNameTask.join();

        // 异步保存完整json到数据库
        CompletableFuture<Long> saveAutomationResultTask = CompletableFuture.supplyAsync(() -> {
            TestAutomationResultE testAutomationResultE = SpringUtil.getApplicationContext().getBean(TestAutomationResultE.class);
            testAutomationResultE.setResult(json);
            testAutomationResultE.setCreatedBy(createdBy);
            testAutomationResultE.setLastUpdatedBy(lastUpdatedBy);
            return iTestAutomationResultService.add(testAutomationResultE).getId();
        });

        // 创建文件夹、循环和阶段
        TestIssueFolderE folderE = iExcelImportService.getFolder(projectId, versionId, folderName);
        TestCycleE testCycleE = iJsonImportService.getCycle(versionId, "自动化测试");
        TestCycleE testStage = iJsonImportService.getStage(
                versionId, folderName, testCycleE.getCycleId(), folderE.getFolderId());

        // 找到要解析的片段，准备数据容器
        JSONArray issues = JSON.parseObject(json).getJSONObject("suites").getJSONArray("suites");
        List<TestCycleCaseE> allTestCycleCases = new ArrayList<>();
        List<TestCaseStepE> allSteps = new ArrayList<>();

        // 开始并发解析
        issues.parallelStream().forEach(element -> {
            if (element instanceof JSONObject) {
                TestCycleCaseE testCycleCaseE = iJsonImportService.processIssueJson(
                        organizationId, projectId, versionId, folderE.getFolderId(), testStage.getCycleId(), (JSONObject) element);
                synchronized (allTestCycleCases) {
                    allTestCycleCases.add(testCycleCaseE);
                }
            }
        });

        // 将数据容器中的数据保存到数据库

        for (TestCycleCaseE testCycleCaseE : allTestCycleCases) {
            testCycleCaseE.setCreatedBy(createdBy);
            testCycleCaseE.setLastUpdatedBy(lastUpdatedBy);
            for (TestCaseStepE testCaseStepE : testCycleCaseE.getTestCaseSteps()) {
                testCaseStepE.setCreatedBy(createdBy);
                testCaseStepE.setLastUpdatedBy(lastUpdatedBy);
                allSteps.add(testCaseStepE);
            }
        }
        CompletableFuture<Void> createCycleCasesTask = CompletableFuture.runAsync(() ->
                createCycleCasesAndBackfillExecuteIds(allTestCycleCases));
        createStepsAndBackfillStepIds(allSteps);

        createCycleCasesTask.join();

        List<TestCycleCaseStepE> testCycleCaseSteps = new ArrayList<>();
        List<TestCycleCaseStepE> currentTestCycleCaseSteps;
        TestCycleCaseStepE currentCycleCaseStepE;
        int i;
        for (TestCycleCaseE testCycleCaseE : allTestCycleCases) {
            currentTestCycleCaseSteps = testCycleCaseE.getCycleCaseStep();
            for (i = 0; i < currentTestCycleCaseSteps.size(); i++) {
                currentCycleCaseStepE = currentTestCycleCaseSteps.get(i);
                currentCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
                currentCycleCaseStepE.setStepId(testCycleCaseE.getTestCaseSteps().get(i).getStepId());
                currentCycleCaseStepE.setCreatedBy(createdBy);
                currentCycleCaseStepE.setLastUpdatedBy(lastUpdatedBy);
                testCycleCaseSteps.add(currentCycleCaseStepE);
            }
        }

        TestCycleCaseStepE.createCycleCaseSteps(testCycleCaseSteps);

        return saveAutomationResultTask.join();
    }
}
