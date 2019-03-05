package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.api.dto.testng.TestNgResult;
import io.choerodon.test.manager.api.dto.testng.TestNgSuite;
import io.choerodon.test.manager.api.dto.testng.TestNgTest;
import io.choerodon.test.manager.app.service.JsonImportService;
import io.choerodon.test.manager.domain.service.*;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import io.choerodon.test.manager.infra.common.utils.TestNgUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JsonImportServiceImpl implements JsonImportService {

    private static final Logger logger = LoggerFactory.getLogger(JsonImportServiceImpl.class);
    public static final String APP_INSTANCE_NOT_EXIST = "app instance 不存在";
    public static final String INSTANCE_ID = "instanceId";
    @Autowired
    private IJsonImportService iJsonImportService;
    @Autowired
    private IExcelImportService iExcelImportService;
    @Autowired
    private ITestAutomationResultService iTestAutomationResultService;
    @Autowired
    private ITestAppInstanceService iTestAppInstanceService;
    @Autowired
    ITestAutomationHistoryService historyService;

    private void createStepsAndBackfillStepIds(List<TestCycleCaseE> cycleCases, Long createdBy, Long lastUpdatedBy) {
        List<TestCaseStepE> allSteps = new ArrayList<>();
        for (TestCycleCaseE cycleCase : cycleCases) {
            for (TestCaseStepE testCaseStepE : cycleCase.getTestCaseSteps()) {
                testCaseStepE.setCreatedBy(createdBy);
                testCaseStepE.setLastUpdatedBy(lastUpdatedBy);
                allSteps.add(testCaseStepE);
            }
        }

        if (!allSteps.isEmpty()) {
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
    }

    private void createCycleCasesAndBackfillExecuteIds(List<TestCycleCaseE> testCycleCases, Long projectId) {
        List<TestCycleCaseE> createdTestCycleCases = TestCycleCaseE.createCycleCases(testCycleCases, projectId);
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
        testAppInstanceE.setId(releaseNameFragments.get(INSTANCE_ID));
        TestAppInstanceE instance = iTestAppInstanceService.queryOne(testAppInstanceE);
        if (instance == null) {
            logger.error(APP_INSTANCE_NOT_EXIST);
            throw new CommonException(APP_INSTANCE_NOT_EXIST);
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
        automationHistoryE.setInstanceId(releaseNameFragments.get(INSTANCE_ID));
        automationHistoryE.setTestStatus(TestAutomationHistoryE.Status.COMPLETE);
        automationHistoryE.setLastUpdatedBy(lastUpdatedBy);
        automationHistoryE.setCycleIds(String.valueOf(testStage.getCycleId()));

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

        if (!targetFolderE.getNewFolder() && !allTestCycleCases.isEmpty()) {
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
        if (!allTestCycleCases.isEmpty()) {
            createCycleCasesAndBackfillExecuteIds(allTestCycleCases, projectId);
        }
        if (targetFolderE.getNewFolder() && !allTestCycleCases.isEmpty()) {
            createStepsAndBackfillStepIds(allTestCycleCases, createdBy, lastUpdatedBy);
        }

        if (!allTestCycleCases.isEmpty()) {
            backfillAndCreateCycleCaseStep(allTestCycleCases, automationHistoryE, createdBy, lastUpdatedBy);
        }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long importTestNgReport(String releaseName, String xml) {
        //xml转json，并设置缩进
        org.json.JSONObject xmlJSONObj = XML.toJSONObject(xml);
        String json = xmlJSONObj.toString(4);
        //xml转document
        Document document;
        try {
            document = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            throw new CommonException(e.getMessage());
        }
        if (document == null) {
            throw new CommonException("error.importTestNgReport.document.nutNull");
        }
        TestNgResult result = TestNgUtil.parseXmlToObject(document);
        logger.info("解析结果xml成功");
        // 查询versionId和projectId
        Map<String, Long> releaseNameFragments = iJsonImportService.parseReleaseName(releaseName);
        Long instanceId = releaseNameFragments.get(INSTANCE_ID);
        TestAppInstanceE testAppInstanceE = new TestAppInstanceE();
        testAppInstanceE.setId(instanceId);
        TestAppInstanceE instance = iTestAppInstanceService.queryOne(testAppInstanceE);
        if (instance == null) {
            logger.error(APP_INSTANCE_NOT_EXIST);
            throw new CommonException(APP_INSTANCE_NOT_EXIST);
        }
        Long versionId = instance.getProjectVersionId();
        Long projectId = instance.getProjectId();
        Long createdBy = instance.getCreatedBy();
        Long lastUpdatedBy = instance.getLastUpdatedBy();

        // 查询组织Id, appName和appVersionName
        String appName = iJsonImportService.getAppName(projectId, releaseNameFragments.get("appId"));
        String appVersionName = iJsonImportService.getAppVersionName(projectId, releaseNameFragments.get("appVersionId"));
        Long organizationId = iJsonImportService.getOrganizationId(projectId);
        String folderBaseName = appName + "-" + appVersionName;

        // 保存完整json到数据库
        TestAutomationResultE testAutomationResultE = SpringUtil.getApplicationContext().getBean(TestAutomationResultE.class);
        testAutomationResultE.setResult(json);
        testAutomationResultE.setCreatedBy(createdBy);
        testAutomationResultE.setLastUpdatedBy(lastUpdatedBy);
        Long resultId = iTestAutomationResultService.add(testAutomationResultE).getId();

        TestAutomationHistoryE automationHistoryE = new TestAutomationHistoryE();
        automationHistoryE.setInstanceId(instanceId);
        automationHistoryE.setTestStatus(TestAutomationHistoryE.Status.COMPLETE);
        automationHistoryE.setLastUpdatedBy(lastUpdatedBy);
        List<Long> cycleIds = new ArrayList<>(result.getSuites().size());

        // 创建测试循环
        TestCycleE testCycleE = iJsonImportService.getCycle(versionId, "自动化测试");
        //遍历suite
        for (TestNgSuite suite : result.getSuites()) {
            String folderName = folderBaseName + "-" + suite.getName();
            // 创建文件夹（应用名+镜像名+suite名，同个镜像的suite共用一个文件夹）
            TestIssueFolderE targetFolderE = iJsonImportService.getFolder(projectId, versionId, folderName);
            // 创建阶段（应用名+镜像名+suite名+第几次）
            TestCycleDTO testStage = iJsonImportService.getStage(
                    versionId, folderName, testCycleE.getCycleId(), targetFolderE.getFolderId(), createdBy, lastUpdatedBy);
            cycleIds.add(testStage.getCycleId());
            //处理Case
            handleTestNgCase(organizationId, instance, suite, targetFolderE, testStage, automationHistoryE);

        }
        // 若有多个suite，拼接成listStr
        automationHistoryE.setCycleIds(cycleIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        // 若存在失败的用例，则更新状态为部分成功
        if (!result.getFailed().equals(0L)) {
            automationHistoryE.setTestStatus(TestAutomationHistoryE.Status.PARTIALEXECUTION);
        }

        automationHistoryE.setResultId(resultId);
        iJsonImportService.updateAutomationHistoryStatus(automationHistoryE);
        logger.info("更新TestAutomationHistory状态成功");

        return resultId;
    }

    private void handleTestNgCase(Long organizationId, TestAppInstanceE instance, TestNgSuite suite, TestIssueFolderE targetFolderE, TestCycleDTO testStage, TestAutomationHistoryE automationHistoryE) {
        Long versionId = instance.getProjectVersionId();
        Long projectId = instance.getProjectId();
        Long createdBy = instance.getCreatedBy();
        Long lastUpdatedBy = instance.getLastUpdatedBy();

        List<TestCycleCaseE> allTestCycleCases = new ArrayList<>();
        // 创建测试用例
        List<TestNgTest> tests = suite.getTests();
        for (TestNgTest test : tests) {
            TestCycleCaseE testCycleCaseE = iJsonImportService.handleIssueByTestNg(organizationId, projectId, versionId, targetFolderE.getFolderId(), testStage.getCycleId(), createdBy,
                    test, targetFolderE.getNewFolder());
            allTestCycleCases.add(testCycleCaseE);
        }

        //关联cycleCase到issue
        if (!targetFolderE.getNewFolder()) {
            relatedToExistIssues(allTestCycleCases, targetFolderE);
        }
        //更新case基本信息
        for (TestCycleCaseE testCycleCaseE : allTestCycleCases) {
            testCycleCaseE.setCreatedBy(createdBy);
            testCycleCaseE.setLastUpdatedBy(lastUpdatedBy);
            testCycleCaseE.setAssignedTo(createdBy);
        }
        //创建case，并回填executeId
        if (!allTestCycleCases.isEmpty()) {
            createCycleCasesAndBackfillExecuteIds(allTestCycleCases, projectId);
            logger.info("创建TestCase和TestCycleCase成功");
        }
        //若是第一次创建文件夹，则要创建caseStep
        if (targetFolderE.getNewFolder() && !allTestCycleCases.isEmpty()) {
            createStepsAndBackfillStepIds(allTestCycleCases, createdBy, lastUpdatedBy);
            logger.info("创建TestCaseSteps成功");
        }
        //创建cycleCaseStep
        if (!allTestCycleCases.isEmpty()) {
            backfillAndCreateCycleCaseStep(allTestCycleCases, automationHistoryE, createdBy, lastUpdatedBy);
            logger.info("创建TestCycleCaseSteps成功");
        }
    }
}
