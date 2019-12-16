package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import feign.FeignException;
import io.choerodon.test.manager.infra.util.RankUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.devops.ApplicationRepDTO;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.testng.TestNgResult;
import io.choerodon.test.manager.app.service.JsonImportService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestAutomationHistoryEnums;
import io.choerodon.test.manager.infra.enums.TestStatusType;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import io.choerodon.test.manager.infra.util.TestNgUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.json.XML;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class JsonImportServiceImpl implements JsonImportService {

    private static final Logger logger = LoggerFactory.getLogger(JsonImportServiceImpl.class);
    private static final String APP_INSTANCE_NOT_EXIST = "app instance 不存在";
    private static final String INSTANCE_ID = "instanceId";
    private static final Pattern DATA_PATTERN = Pattern.compile("(?:@data\\s+)(.*?)(?:\\s*\\n)");
    private static final Pattern EXPECT_PATTERN = Pattern.compile("(?:@expect\\s+)(.*?)(?:\\s*\\n)");
    private static final Pattern AUTO_TEST_STAGE_SUFFIX_PATTERN = Pattern.compile("第(\\d+)次测试");
    private static final String ERROR_GET_APP_NAME = "error.get.app.name";
    private static final String ERROR_GET_APP_VERSION_NAME = "error.get.app.version.name";
    private static final String ERROR_GET_ORGANIZATION_ID = "error.get.organization.id";
    private static final String ERROR_STEP_ID_NOT_NULL = "error.case.step.insert.stepId.should.be.null";

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private TestCycleMapper cycleMapper;

    @Autowired
    private TestAppInstanceMapper testAppInstanceMapper;

    @Autowired
    private TestAutomationHistoryMapper automationHistoryMapper;

    @Autowired
    private TestIssueFolderRelMapper issueFolderRelMapper;

    @Autowired
    private TestCaseStepMapper caseStepMapper;

    @Autowired
    private TestAutomationResultMapper testAutomationResultMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Autowired
    private ApplicationFeignClient applicationFeignClient;

    @Autowired
    private BaseFeignClient baseFeignClient;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private ModelMapper modelMapper;

//    private void createStepsAndBackfillStepIds(List<TestCycleCaseProDTO> cycleCases, Long createdBy, Long lastUpdatedBy) {
//        List<TestCaseStepDTO> allSteps = new ArrayList<>();
//        for (TestCycleCaseProDTO cycleCase : cycleCases) {
//            for (TestCaseStepDTO testCaseStepE : cycleCase.getTestCaseSteps()) {
//                testCaseStepE.setCreatedBy(createdBy);
//                testCaseStepE.setLastUpdatedBy(lastUpdatedBy);
//                allSteps.add(testCaseStepE);
//            }
//        }
//
//        if (!allSteps.isEmpty()) {
//            List<TestCaseStepDTO> createdSteps = createSteps(allSteps);
//            for (int i = 0; i < allSteps.size(); i++) {
//                allSteps.get(i).setStepId(createdSteps.get(i).getStepId());
//            }
//
//            for (TestCycleCaseProDTO cycleCase : cycleCases) {
//                for (int i = 0; i < cycleCase.getCycleCaseStep().size(); i++) {
//                    cycleCase.getCycleCaseStep().get(i).setStepId(cycleCase.getTestCaseSteps().get(i).getStepId());
//                }
//            }
//        }
//    }

//    List<TestCaseStepDTO> createSteps(List<TestCaseStepDTO> testCaseSteps) {
//        TestCaseStepDTO currentStep = testCaseSteps.get(0);
//        currentStep.setRank(RankUtil.Operation.INSERT.getRank(testCaseStepMapper.getLastedRank(currentStep.getIssueId()), null));
//        TestCaseStepDTO prevStep = currentStep;
//
//        for (int i = 1; i < testCaseSteps.size(); i++) {
//            currentStep = testCaseSteps.get(i);
//            currentStep.setRank(RankUtil.Operation.INSERT.getRank(prevStep.getRank(), null));
//            prevStep = currentStep;
//        }
//
//        if (testCaseSteps == null || testCaseSteps.isEmpty()) {
//            throw new CommonException("error.case.step.list.empty");
//        }
//
//        Date now = new Date();
//        for (TestCaseStepDTO testCaseStep : testCaseSteps) {
//            if (testCaseStep == null || testCaseStep.getStepId() != null) {
//                throw new CommonException(ERROR_STEP_ID_NOT_NULL);
//            }
//            testCaseStep.setLastUpdateDate(now);
//            testCaseStep.setCreationDate(now);
//        }
//        testCaseStepMapper.batchInsertTestCaseSteps(testCaseSteps);
//        return testCaseSteps;
//    }

//    private void createCycleCasesAndBackfillExecuteIds(List<TestCycleCaseProDTO> testCycleCases, Long projectId) {
//        List<TestCycleCaseProDTO> createdTestCycleCases = this.createCycleCases(testCycleCases, projectId);
//        for (int i = 0; i < testCycleCases.size(); i++) {
//            testCycleCases.get(i).setExecuteId(createdTestCycleCases.get(i).getExecuteId());
//        }
//    }

    private List<TestCycleCaseProDTO> createCycleCases(List<TestCycleCaseProDTO> testCycleCases, Long projectId) {
        TestCycleCaseProDTO currentCycleCase = testCycleCases.get(0);
        currentCycleCase.setRank(RankUtil.Operation.INSERT.getRank(testCycleCaseMapper.getLastedRank(currentCycleCase.getCycleId()), null));
        TestCycleCaseProDTO prevCycleCase = currentCycleCase;

        for (int i = 1; i < testCycleCases.size(); i++) {
            currentCycleCase = testCycleCases.get(i);
            currentCycleCase.setRank(RankUtil.Operation.INSERT.getRank(prevCycleCase.getRank(), null));
            prevCycleCase = currentCycleCase;
        }
        return modelMapper.map(testCycleCaseService.batchCreateForAutoTest(modelMapper.map(testCycleCases, new TypeToken<List<TestCycleCaseVO>>() {
        }.getType()), projectId), new TypeToken<List<TestCycleCaseProDTO>>() {
        }.getType());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long importMochaReport(String releaseName, String json) {
        Assert.hasText(json, "error.issue.import.json.blank");
        logger.info("==================\n" + json + "\n===================");

        // 查询versionId和projectId
        Map<String, Long> releaseNameFragments = parseReleaseName(releaseName);
        TestAppInstanceDTO testAppInstanceDTO = new TestAppInstanceDTO();
        testAppInstanceDTO.setId(releaseNameFragments.get(INSTANCE_ID));
        TestAppInstanceDTO instance = testAppInstanceMapper.selectOne(testAppInstanceDTO);
        if (instance == null) {
            logger.error(APP_INSTANCE_NOT_EXIST);
            throw new CommonException(APP_INSTANCE_NOT_EXIST);
        }
//        Long versionId = instance.getProjectVersionId();
//        Long projectId = instance.getProjectId();
        Long createdBy = instance.getCreatedBy();
        Long lastUpdatedBy = instance.getLastUpdatedBy();

//        // 查询组织Id, appName和appVersionName
//        String appName = getAppName(projectId, releaseNameFragments.get("appId"));
//        String appVersionName = getAppVersionName(projectId, releaseNameFragments.get("appVersionId"));
//        Long organizationId = getOrganizationId(projectId);
//        String folderName = appName + "-" + appVersionName;

        // 保存完整json到数据库
        TestAutomationResultDTO testAutomationResultDTO = new TestAutomationResultDTO();
        testAutomationResultDTO.setResult(json);
        testAutomationResultDTO.setCreatedBy(createdBy);
        testAutomationResultDTO.setLastUpdatedBy(lastUpdatedBy);
        Date now = new Date();
        testAutomationResultDTO.setCreationDate(now);
        testAutomationResultDTO.setLastUpdateDate(now);
        if (testAutomationResultMapper.insertOneResult(testAutomationResultDTO) != 1) {
            throw new CommonException("error.TestAutomationResult.insert");
        }
        Long resultId = Long.valueOf(testAutomationResultDTO.getId());

//        // 创建文件夹
//        TestIssueFolderProDTO targetFolderE = getFolder(projectId, versionId, folderName);

//        // 创建循环
//        TestCycleDTO testCycleE = getCycle(projectId, versionId, "自动化测试");
//
//        // 创建阶段
//        TestCycleVO testStage = getStage(projectId,
//                versionId, folderName, testCycleE.getCycleId(), targetFolderE.getFolderId(), createdBy, lastUpdatedBy);

        // 找到要解析的片段，准备数据容器
        JSONArray issues = JSON.parseObject(json).getJSONObject("suites").getJSONArray("suites");
        TestAutomationHistoryDTO automationHistoryDTO = new TestAutomationHistoryDTO();
        automationHistoryDTO.setInstanceId(releaseNameFragments.get(INSTANCE_ID));
        automationHistoryDTO.setTestStatus(TestAutomationHistoryEnums.Status.COMPLETE);
        automationHistoryDTO.setLastUpdatedBy(lastUpdatedBy);
//        automationHistoryDTO.setCycleIds(String.valueOf(testStage.getCycleId()));

        // 如果测试用例数量为 0
        if (issues.isEmpty()) {
            automationHistoryDTO.setResultId(resultId);
            updateAutomationHistoryStatus(automationHistoryDTO);
            return resultId;
        }
        List<TestCycleCaseProDTO> allTestCycleCases = new ArrayList<>();
        // 开始解析
        for (Object element : issues) {
            if (element instanceof JSONObject) {
                TestCycleCaseProDTO testCycleCaseE = processIssueJson((JSONObject) element);
                allTestCycleCases.add(testCycleCaseE);
            }
        }

//        if (!targetFolderE.getNewFolder() && !allTestCycleCases.isEmpty()) {
//            relatedToExistIssues(allTestCycleCases, targetFolderE);
//        }

        // 将数据容器中的数据保存到数据库，并更新automation history状态
        for (TestCycleCaseProDTO testCycleCaseDTO : allTestCycleCases) {
//            testCycleCaseE.setCreatedBy(createdBy);
//            testCycleCaseE.setLastUpdatedBy(lastUpdatedBy);
//            testCycleCaseE.setAssignedTo(createdBy);
            if (!testCycleCaseDTO.isPassed()) {
                automationHistoryDTO.setTestStatus(TestAutomationHistoryEnums.Status.PARTIALEXECUTION);
            }
        }
//        if (!allTestCycleCases.isEmpty()) {
//            createCycleCasesAndBackfillExecuteIds(allTestCycleCases, projectId);
//        }
//        if (targetFolderE.getNewFolder() && !allTestCycleCases.isEmpty()) {
//            createStepsAndBackfillStepIds(allTestCycleCases, createdBy, lastUpdatedBy);
//        }

//        if (!allTestCycleCases.isEmpty()) {
//            backfillAndCreateCycleCaseStep(allTestCycleCases, automationHistoryDTO, createdBy, lastUpdatedBy);
//        }

        automationHistoryDTO.setResultId(resultId);
        updateAutomationHistoryStatus(automationHistoryDTO);

        return resultId;
    }

//    private void backfillAndCreateCycleCaseStep(List<TestCycleCaseProDTO> allTestCycleCases, TestAutomationHistoryDTO automationHistoryE,
//                                                Long createdBy, Long lastUpdatedBy) {
//        List<TestCycleCaseStepDTO> testCycleCaseSteps = new ArrayList<>();
//        List<TestCycleCaseStepDTO> currentTestCycleCaseSteps;
//        TestCycleCaseStepDTO currentCycleCaseStepE;
//        int i;
//        for (TestCycleCaseProDTO testCycleCaseE : allTestCycleCases) {
//            currentTestCycleCaseSteps = testCycleCaseE.getCycleCaseStep();
//            for (i = 0; i < currentTestCycleCaseSteps.size(); i++) {
//                currentCycleCaseStepE = currentTestCycleCaseSteps.get(i);
//                if (!currentCycleCaseStepE.isPassed()) {
//                    automationHistoryE.setTestStatus(TestAutomationHistoryEnums.Status.PARTIALEXECUTION);
//                }
//                currentCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
//                currentCycleCaseStepE.setCreatedBy(createdBy);
//                currentCycleCaseStepE.setLastUpdatedBy(lastUpdatedBy);
//                testCycleCaseSteps.add(currentCycleCaseStepE);
//            }
//        }
//
//        if (testCycleCaseSteps.isEmpty()) {
//            throw new CommonException("error.cycle.case.step.list.empty");
//        }
//        Date now = new Date();
//        for (TestCycleCaseStepDTO testCycleCaseStep : testCycleCaseSteps) {
//            if (testCycleCaseStep == null || testCycleCaseStep.getExecuteStepId() != null) {
//                throw new CommonException("error.cycle.case.step.insert.executeStepId.should.be.null");
//            }
//            testCycleCaseStep.setCreationDate(now);
//            testCycleCaseStep.setLastUpdateDate(now);
//        }
//
//        List<TestCycleCaseStepDTO> testCycleCaseStepDTOS = modelMapper.map(testCycleCaseSteps, new TypeToken<List<TestCycleCaseStepDTO>>() {
//        }.getType());
//        testCycleCaseStepMapper.batchInsertTestCycleCaseSteps(testCycleCaseStepDTOS);
//    }

//    private void relatedToExistIssues(List<TestCycleCaseProDTO> allTestCycleCases, TestIssueFolderProDTO targetFolderE) {
//        List<TestIssueFolderRelDTO> issueFolderRels = queryAllUnderFolder(targetFolderE);
//        issueFolderRels.sort((o1, o2) -> (int) (o1.getId() - o2.getId()));
//        for (int i = 0; i < allTestCycleCases.size(); i++) {
//            Long issueId = issueFolderRels.get(i).getIssueId();
//            allTestCycleCases.get(i).setIssueId(issueId);
//            allTestCycleCases.get(i).getCycleCaseStep().forEach(cycleCaseStepE -> cycleCaseStepE.setCaseId(issueId));
//            List<TestCaseStepDTO> testCaseStepEs = queryAllStepsUnderIssue(issueId);
//            if (allTestCycleCases.get(i).getCycleCaseStep().size() != testCaseStepEs.size()) {
//                logger.error("报告内容和 {} 只读文件夹中的内容不一致", targetFolderE.getName());
//                throw new CommonException("报告内容和 " + targetFolderE.getName() + " 只读文件夹中的内容不一致");
//            }
//            for (int j = 0; j < testCaseStepEs.size(); j++) {
//                allTestCycleCases.get(i).getCycleCaseStep().get(j).setStepId(testCaseStepEs.get(j).getStepId());
//            }
//        }
//    }

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
        Map<String, Long> releaseNameFragments = parseReleaseName(releaseName);
        Long instanceId = releaseNameFragments.get(INSTANCE_ID);
        TestAppInstanceDTO testAppInstanceDTO = new TestAppInstanceDTO();
        testAppInstanceDTO.setId(instanceId);
        TestAppInstanceDTO instance = testAppInstanceMapper.selectOne(testAppInstanceDTO);
        if (instance == null) {
            logger.error(APP_INSTANCE_NOT_EXIST);
            throw new CommonException(APP_INSTANCE_NOT_EXIST);
        }
//        Long versionId = instance.getProjectVersionId();
//        Long projectId = instance.getProjectId();
        Long createdBy = instance.getCreatedBy();
        Long lastUpdatedBy = instance.getLastUpdatedBy();

//        // 查询组织Id, appName和appVersionName
//        String appName = getAppName(projectId, releaseNameFragments.get("appId"));
//        String appVersionName = getAppVersionName(projectId, releaseNameFragments.get("appVersionId"));
//        Long organizationId = getOrganizationId(projectId);
//        String folderBaseName = appName + "-" + appVersionName;

        // 保存完整json到数据库
        TestAutomationResultDTO testAutomationResultDTO = new TestAutomationResultDTO();
        testAutomationResultDTO.setResult(json);
        testAutomationResultDTO.setCreatedBy(createdBy);
        testAutomationResultDTO.setLastUpdatedBy(lastUpdatedBy);
        Date now = new Date();
        testAutomationResultDTO.setCreationDate(now);
        testAutomationResultDTO.setLastUpdateDate(now);
        DBValidateUtil.executeAndvalidateUpdateNum(
                testAutomationResultMapper::insertOneResult, testAutomationResultDTO, 1, "error.testAutomationResult.insert");
        Long resultId = testAutomationResultDTO.getId();

        TestAutomationHistoryDTO automationHistoryDTO = new TestAutomationHistoryDTO();
        automationHistoryDTO.setInstanceId(instanceId);
        automationHistoryDTO.setTestStatus(TestAutomationHistoryEnums.Status.COMPLETE);
        automationHistoryDTO.setLastUpdatedBy(lastUpdatedBy);
//        List<Long> cycleIds = new ArrayList<>(result.getSuites().size());

//        // 创建测试循环
//        TestCycleDTO testCycleE = getCycle(projectId, versionId, "自动化测试");
//        //遍历suite
//        for (TestNgSuite suite : result.getSuites()) {
//            String folderName = folderBaseName + "-" + suite.getName();
//            // 创建文件夹（应用名+镜像名+suite名，同个镜像的suite共用一个文件夹）
//            TestIssueFolderProDTO targetFolderE = getFolder(projectId, versionId, folderName);
//            // 创建阶段（应用名+镜像名+suite名+第几次）
//            TestCycleVO testStage = getStage(projectId,
//                    versionId, folderName, testCycleE.getCycleId(), targetFolderE.getFolderId(), createdBy, lastUpdatedBy);
//            cycleIds.add(testStage.getCycleId());
//            //处理Case
//            handleTestNgCase(organizationId, instance, suite, targetFolderE, testStage, automationHistoryDTO);
//
//        }
//        // 若有多个suite，拼接成listStr
//        automationHistoryDTO.setCycleIds(cycleIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        // 若存在失败的用例，则更新状态为部分成功
        if (!result.getFailed().equals(0L)) {
            automationHistoryDTO.setTestStatus(TestAutomationHistoryEnums.Status.PARTIALEXECUTION);
        }

        automationHistoryDTO.setResultId(resultId);
        updateAutomationHistoryStatus(automationHistoryDTO);
        logger.info("更新TestAutomationHistory状态成功");

        return resultId;
    }

//    private void handleTestNgCase(Long organizationId, TestAppInstanceDTO instance, TestNgSuite suite, TestIssueFolderProDTO targetFolderE, TestCycleVO testStage, TestAutomationHistoryDTO automationHistoryE) {
//        Long versionId = instance.getProjectVersionId();
//        Long projectId = instance.getProjectId();
//        Long createdBy = instance.getCreatedBy();
//        Long lastUpdatedBy = instance.getLastUpdatedBy();
//
//        List<TestCycleCaseProDTO> allTestCycleCases = new ArrayList<>();
//        // 创建测试用例
//        List<TestNgTest> tests = suite.getTests();
//        for (TestNgTest test : tests) {
//            TestCycleCaseProDTO testCycleCaseE = handleIssueByTestNg(organizationId, projectId, versionId, targetFolderE.getFolderId(), testStage.getCycleId(), createdBy,
//                    test, targetFolderE.getNewFolder());
//            allTestCycleCases.add(testCycleCaseE);
//        }
//
//        //关联cycleCase到issue
//        if (!targetFolderE.getNewFolder()) {
//            relatedToExistIssues(allTestCycleCases, targetFolderE);
//        }
//        //更新case基本信息
//        for (TestCycleCaseProDTO testCycleCaseE : allTestCycleCases) {
//            testCycleCaseE.setCreatedBy(createdBy);
//            testCycleCaseE.setLastUpdatedBy(lastUpdatedBy);
//            testCycleCaseE.setAssignedTo(createdBy);
//        }
//        //创建case，并回填executeId
//        if (!allTestCycleCases.isEmpty()) {
//            createCycleCasesAndBackfillExecuteIds(allTestCycleCases, projectId);
//            logger.info("创建TestCase和TestCycleCase成功");
//        }
//        //若是第一次创建文件夹，则要创建caseStep
//        if (targetFolderE.getNewFolder() && !allTestCycleCases.isEmpty()) {
//            createStepsAndBackfillStepIds(allTestCycleCases, createdBy, lastUpdatedBy);
//            logger.info("创建TestCaseSteps成功");
//        }
//        //创建cycleCaseStep
//        if (!allTestCycleCases.isEmpty()) {
//            backfillAndCreateCycleCaseStep(allTestCycleCases, automationHistoryE, createdBy, lastUpdatedBy);
//            logger.info("创建TestCycleCaseSteps成功");
//        }
//    }

    private Map<String, Long> parseReleaseName(String releaseName) {
        if (!releaseName.startsWith("att-")) {
            throw new CommonException("releaseName前缀错误");
        }
        String[] strings = releaseName.split("-");
        Map<String, Long> fragments = new HashMap<>();
        fragments.put("appId", Long.parseLong(strings[1]));
        fragments.put("appVersionId", Long.parseLong(strings[2]));
        fragments.put("instanceId", Long.parseLong(strings[3]));

        return fragments;
    }

    private String getAppName(Long projectId, Long appId) {
        try {
            ResponseEntity<ApplicationRepDTO> response = applicationFeignClient.queryByAppId(projectId, appId);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("get app name {} by app id {} project id {}", response.getBody().getName(), appId, projectId);
                return response.getBody().getName();
            } else {
                throw new CommonException(ERROR_GET_APP_NAME);
            }
        } catch (FeignException e) {
            throw new CommonException(ERROR_GET_APP_NAME, e);
        }
    }

//    private String getAppVersionName(Long projectId, Long appVersionId) {
//        try {
//            Long[] appVersionIds = new Long[1];
//            appVersionIds[0] = appVersionId;
//            ResponseEntity<List<AppServiceVersionRespVO>> responses = applicationFeignClient.getAppversion(projectId, appVersionIds);
//            AppServiceVersionRespVO response = responses.getBody().get(0);
//            if (!responses.getStatusCode().is2xxSuccessful() || response.getVersion() == null) {
//                throw new CommonException(ERROR_GET_APP_VERSION_NAME);
//            }
//            logger.info("get app version name {} by app version id {} project id {}", response.getVersion(), appVersionId, projectId);
//            return response.getVersion();
//        } catch (FeignException e) {
//            throw new CommonException(ERROR_GET_APP_VERSION_NAME, e);
//        }
//    }

//    private Long getOrganizationId(Long projectId) {
//        try {
//            ResponseEntity<ProjectDTO> response = baseFeignClient.queryProject(projectId);
//            if (response.getStatusCode().is2xxSuccessful()) {
//                logger.info("get organization id {} by project id {}", response.getBody().getOrganizationId(), projectId);
//                return response.getBody().getOrganizationId();
//            } else {
//                throw new CommonException(ERROR_GET_ORGANIZATION_ID);
//            }
//        } catch (FeignException e) {
//            throw new CommonException(ERROR_GET_ORGANIZATION_ID, e);
//        }
//    }

//    private TestIssueFolderProDTO getFolder(Long projectId, Long versionId, String folderName) {
//        TestIssueFolderProDTO targetFolderE;
//        TestIssueFolderDTO folderE = new TestIssueFolderDTO();
//        folderE.setProjectId(projectId);
//        folderE.setName(folderName);
//        TestIssueFolderDTO select = testIssueFolderMapper.selectOne(folderE);
//        if (select == null) {
//            folderE.setType(TestIssueFolderType.TYPE_CYCLE);
//            logger.info("{} 文件夹不存在，创建", folderName);
//            testIssueFolderMapper.insert(folderE);
//            targetFolderE = modelMapper.map(folderE, TestIssueFolderProDTO.class);
//            targetFolderE.setNewFolder(true);
//        } else {
//            targetFolderE = modelMapper.map(select, TestIssueFolderProDTO.class);
//            targetFolderE.setNewFolder(false);
//            logger.info("{} 文件夹已存在", folderName);
//        }
//        return targetFolderE;
//    }

//    @Transactional
//    public TestCycleVO getStage(Long projectId, Long versionId, String stageName, Long parentCycleId, Long folderId, Long createdBy, Long lastUpdatedBy) {
//        TestCycleDTO testCycleE = new TestCycleDTO();
//        testCycleE.setVersionId(versionId);
//        testCycleE.setFolderId(folderId);
//        testCycleE.setParentCycleId(parentCycleId);
//        testCycleE.setType(TestCycleType.FOLDER);
//        testCycleE.setProjectId(projectId);
//
//        int lastTestStageNumber = 0;
//        List<TestCycleDTO> childCycleEs = cycleMapper.select(testCycleE);
//        for (TestCycleDTO cycleE : childCycleEs) {
//            String suffix = cycleE.getCycleName().substring(cycleE.getCycleName().lastIndexOf('-') + 1);
//            String prefix = cycleE.getCycleName().substring(0, cycleE.getCycleName().lastIndexOf('-'));
//            Matcher matcher = AUTO_TEST_STAGE_SUFFIX_PATTERN.matcher(suffix);
//            if (Objects.equals(stageName, prefix) && matcher.matches()) {
//                int stageNumber = Integer.parseInt(matcher.group(1));
//                if (stageNumber > lastTestStageNumber) {
//                    lastTestStageNumber = stageNumber;
//                }
//            }
//        }
//
//        testCycleE.setCycleName(stageName + "-第" + ++lastTestStageNumber + "次测试");
//        logger.info("创建阶段 {}", testCycleE.getCycleName());
//        testCycleE.setType(TestCycleType.FOLDER);
//        testCycleE.setFromDate(new Date());
//        testCycleE.setToDate(testCycleE.getFromDate());
//        testCycleE.setCreatedBy(createdBy);
//        testCycleE.setLastUpdatedBy(lastUpdatedBy);
//        return testCycleService.insertWithoutSyncFolder(projectId, modelMapper.map(testCycleE, TestCycleVO.class));
//    }

//    @Transactional
//    public TestCycleDTO getCycle(Long projectId, Long versionId, String folderName) {
//        TestCycleDTO testCycleE = new TestCycleDTO();
//        testCycleE.setVersionId(versionId);
//        testCycleE.setCycleName(folderName);
//        testCycleE.setType(TestCycleType.CYCLE);
//        testCycleE.setProjectId(projectId);
//        TestCycleDTO targetCycle = cycleMapper.selectOne(testCycleE);
//        if (targetCycle == null) {
//            logger.info("{} 循环不存在，创建", folderName);
//            testCycleE.setType(TestCycleType.CYCLE);
//            testCycleE.setFromDate(new Date());
//            testCycleE.setToDate(testCycleE.getFromDate());
//            testCycleService.checkRank(modelMapper.map(testCycleE, TestCycleVO.class));
//            String rank;
//            if (testCycleE.getType().equals(TestCycleType.CYCLE)) {
//                rank = cycleMapper.getCycleLastedRank(testCycleE.getVersionId());
//            } else {
//                rank = cycleMapper.getPlanLastedRank(testCycleE.getParentCycleId());
//            }
//            testCycleE.setRank(RankUtil.Operation.INSERT.getRank(rank, null));
//            cycleMapper.insert(testCycleE);
//            return testCycleE;
//        }
//
//        logger.info("{} 循环已存在", folderName);
//        return targetCycle;
//    }

    @Transactional
    public void updateAutomationHistoryStatus(TestAutomationHistoryDTO automationHistoryE) {
        Long objectVersionNumber = automationHistoryMapper.queryObjectVersionNumberByInstanceId(automationHistoryE);
        automationHistoryE.setObjectVersionNumber(objectVersionNumber);
        automationHistoryE.setLastUpdateDate(new Date());
        DBValidateUtil.executeAndvalidateUpdateNum(automationHistoryMapper::updateTestStatusByInstanceId,
                automationHistoryE, 1, "error.update.testStatus.by.instanceId");
    }

    private TestCycleCaseProDTO processIssueJson(JSONObject issue) {
//        String summary = issue.getString("title");
//        if (StringUtils.isBlank(summary)) {
//            logger.error("用例 title 不能为空");
//            summary = "null";
//        }

        TestCycleCaseProDTO testCycleCaseDTO = new TestCycleCaseProDTO();

//        IssueDTO issueDTO = null;
//        if (newFolder) {
//            issueDTO = createIssue(organizationId, projectId, versionId, folderId, createdBy, summary);
//            if (issueDTO == null) {
//                logger.error("issue 创建失败");
//                throw new IssueCreateException();
//            }
//            testCycleCaseE.setIssueId(issueDTO.getIssueId());
//        }

//        testCycleCaseE.setCycleId(cycleId);
//        testCycleCaseE.setVersionId(versionId);
        String[] failures = getExecutionInfo(issue, "failures");
        String[] passes = getExecutionInfo(issue, "passes");
        String[] pending = getExecutionInfo(issue, "pending");
        String[] skipped = getExecutionInfo(issue, "skipped");
        TestStatusDTO statusDTO = getExecutionStatus(failures, passes, pending, skipped);
        testCycleCaseDTO.setExecutionStatus(statusDTO.getStatusId());
        testCycleCaseDTO.setExecutionStatusName(statusDTO.getStatusName());

//        JSONArray testCaseStepsArray = issue.getJSONArray("tests");
//        List<TestCaseStepDTO> testCaseSteps = new ArrayList<>();
//        List<TestCycleCaseStepDTO> testCycleCaseSteps = new ArrayList<>();
//        TestCaseStepDTO testCaseStepE;
//        TestCycleCaseStepDTO cycleCaseStepE;
//        for (Object element : testCaseStepsArray) {
//            if (element instanceof JSONObject) {
//                testCaseStepE = parseTestCaseStepJson((JSONObject) element);
//                if (testCaseStepE != null) {
//                    testCaseSteps.add(testCaseStepE);
//                    cycleCaseStepE = parseTestCycleCaseStepJson(testCaseStepE, (JSONObject) element);
//                    testCycleCaseSteps.add(cycleCaseStepE);
//                    if (issueDTO != null) {
//                        testCaseStepE.setIssueId(issueDTO.getIssueId());
//                        cycleCaseStepE.setCaseId(issueDTO.getIssueId());
//                    }
//                }
//            }
//        }

//        testCycleCaseE.setTestCaseSteps(testCaseSteps);
//        testCycleCaseE.setCycleCaseStep(testCycleCaseSteps);
        return testCycleCaseDTO;
    }

//    private IssueDTO createIssue(Long organizationId, Long projectId, Long versionId, Long folderId, Long createdBy, String summary) {
//        IssueCreateDTO issueCreateDTO = new IssueCreateDTO();
//        issueCreateDTO.setTypeCode(IssueTypeCode.ISSUE_AUTO_TEST);
//        issueCreateDTO.setProjectId(projectId);
//        issueCreateDTO.setSummary(summary);
//        issueCreateDTO.setAssigneeId(createdBy);
//        issueCreateDTO.setReporterId(createdBy);
//
//        issueCreateDTO.setIssueTypeId(AgileUtil.queryIssueTypeId(projectId, organizationId, IssueTypeCode.ISSUE_AUTO_TEST, issueFeignClient));
//        Long priorityId = AgileUtil.queryDefaultPriorityId(projectId, organizationId, issueFeignClient);
//        issueCreateDTO.setPriorityCode("priority-" + priorityId);
//        issueCreateDTO.setPriorityId(priorityId);
//
//        VersionIssueRelVO versionIssueRelVO = new VersionIssueRelVO();
//        versionIssueRelVO.setVersionId(versionId);
//        versionIssueRelVO.setRelationType("fix");
//        issueCreateDTO.setVersionIssueRelVOList(Lists.newArrayList(versionIssueRelVO));
//
//        IssueDTO issueDTO = testCaseService.createTest(issueCreateDTO, projectId, "test");
//        if (issueDTO != null) {
//            TestIssueFolderRelDTO issueFolderRelE = new TestIssueFolderRelDTO();
//            issueFolderRelE.setProjectId(projectId);
//            issueFolderRelE.setVersionId(versionId);
//            issueFolderRelE.setFolderId(folderId);
//            issueFolderRelE.setIssueId(issueDTO.getIssueId());
//            issueFolderRelMapper.insert(issueFolderRelE);
//        }
//
//        return issueDTO;
//    }

    private String[] getExecutionInfo(JSONObject issue, String key) {
        JSONArray jsonArray = issue.getJSONArray(key);
        String[] info = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.get(i) instanceof String) {
                info[i] = (String) jsonArray.get(i);
            }
        }

        return info;
    }

    private TestStatusDTO getExecutionStatus(String[] failures, String[] passes, String[] pending, String[] skipped) {
        TestStatusDTO testStatusE = new TestStatusDTO();
        testStatusE.setProjectId(0L);
        testStatusE.setStatusType(TestStatusType.STATUS_TYPE_CASE);

        if (failures.length > 0 || pending.length > 0 || skipped.length > 0) {
            testStatusE.setStatusName("失败");
        } else if (passes.length > 0) {
            testStatusE.setStatusName("通过");
        }

        return testStatusMapper.selectOne(testStatusE);
    }

//    private TestCaseStepDTO parseTestCaseStepJson(JSONObject testCaseStep) {
//        String testStep = testCaseStep.getString("title");
//        String code = testCaseStep.getString("code");
//        String testData = getTestData(code);
//        String expectedResult = getExpectedResult(code);
//        if (StringUtils.isBlank(testStep) && StringUtils.isBlank(testData) && StringUtils.isBlank(expectedResult)) {
//            return null;
//        }
//
//        TestCaseStepDTO caseStepE = new TestCaseStepDTO();
//        caseStepE.setTestStep(testStep);
//        caseStepE.setTestData(testData);
//        caseStepE.setExpectedResult(expectedResult);
//
//        return caseStepE;
//    }

//    private String getTestData(String code) {
//        Matcher matcher = DATA_PATTERN.matcher(code);
//        return matcher.find() ? matcher.group(1) : null;
//    }

//    private String getExpectedResult(String code) {
//        Matcher matcher = EXPECT_PATTERN.matcher(code);
//        return matcher.find() ? matcher.group(1) : null;
//    }

//    private TestCycleCaseStepDTO parseTestCycleCaseStepJson(TestCaseStepDTO testCaseStepE, JSONObject element) {
//        TestCycleCaseStepDTO testCycleCaseStepE = new TestCycleCaseStepDTO();
//        testCycleCaseStepE.setTestStep(testCaseStepE.getTestStep());
//        testCycleCaseStepE.setTestData(testCaseStepE.getTestData());
//        testCycleCaseStepE.setExpectedResult(testCaseStepE.getExpectedResult());
//
//        TestStatusDTO statusE = new TestStatusDTO();
//        statusE.setProjectId(0L);
//        statusE.setStatusType(TestStatusType.STATUS_TYPE_CASE_STEP);
//        if (element.getBooleanValue("fail") || element.getBooleanValue("pending")) {
//            statusE.setStatusName("失败");
//        } else if (element.getBooleanValue("skipped")) {
//            statusE.setStatusName("未执行");
//        } else {
//            statusE.setStatusName("通过");
//        }
//
//        TestStatusDTO targetStatusE = testStatusMapper.selectOne(statusE);
//        testCycleCaseStepE.setStepStatus(targetStatusE.getStatusId());
//        testCycleCaseStepE.setStatusName(targetStatusE.getStatusName());
//
//        if (!"通过".equals(targetStatusE.getStatusName())) {
//            JSONObject err = element.getJSONObject("err");
//            if (err != null) {
//                testCycleCaseStepE.setDescription(err.getString("message"));
//            }
//        }
//
//        return testCycleCaseStepE;
//    }

//    private List<TestIssueFolderRelDTO> queryAllUnderFolder(TestIssueFolderProDTO issueFolderE) {
//        TestIssueFolderProDTO issueFolderRelE = new TestIssueFolderProDTO();
//        issueFolderRelE.setFolderId(issueFolderE.getFolderId());
//
//        TestIssueFolderRelDTO issueFolderRelDO = modelMapper.map(issueFolderRelE, TestIssueFolderRelDTO.class);
//        return issueFolderRelMapper.select(issueFolderRelDO);
//    }

//    private List<TestCaseStepDTO> queryAllStepsUnderIssue(Long issueId) {
//        TestCaseStepDTO caseStepE = new TestCaseStepDTO();
//        caseStepE.setIssueId(issueId);
//        return caseStepMapper.query(caseStepE);
//    }

//    private TestCycleCaseProDTO handleIssueByTestNg(Long organizationId, Long projectId, Long versionId, Long folderId, Long cycleId, Long createdBy, TestNgTest test, boolean newFolder) {
//        String summary = test.getName();
//        if (StringUtils.isBlank(summary)) {
//            logger.error("用例 title 不能为空");
//            summary = "null";
//        }
//
//        TestCycleCaseProDTO testCycleCaseE = new TestCycleCaseProDTO();
//
//        IssueDTO issueDTO = null;
//        if (newFolder) {
//            issueDTO = createIssue(organizationId, projectId, versionId, folderId, createdBy, summary);
//            if (issueDTO == null) {
//                logger.error("issue 创建失败");
//                throw new IssueCreateException();
//            }
//            testCycleCaseE.setIssueId(issueDTO.getIssueId());
//        }
//
//        testCycleCaseE.setCycleId(cycleId);
//        testCycleCaseE.setVersionId(versionId);
//        //查询状态
//        TestStatusDTO caseStatusE = new TestStatusDTO();
//        caseStatusE.setProjectId(0L);
//        caseStatusE.setStatusType(TestStatusType.STATUS_TYPE_CASE);
//        caseStatusE.setStatusName(test.getStatus().equals(TestNgUtil.TEST_PASSED) ? "通过" : "失败");
//        TestStatusDTO caseStatus = testStatusMapper.selectOne(caseStatusE);
//        testCycleCaseE.setExecutionStatus(caseStatus.getStatusId());
//        testCycleCaseE.setExecutionStatusName(caseStatus.getStatusName());
//
//        List<TestNgCase> cases = test.getCases();
//        List<TestCaseStepDTO> testCaseSteps = new ArrayList<>();
//        List<TestCycleCaseStepDTO> testCycleCaseSteps = new ArrayList<>();
//        TestCaseStepDTO testCaseStepE;
//        TestCycleCaseStepDTO testCycleCaseStepE;
//        for (TestNgCase testNgCase : cases) {
//            //获取TestCaseStep
//            testCaseStepE = new TestCaseStepDTO();
//            testCaseStepE.setTestStep(testNgCase.getDescription() != null ? testNgCase.getDescription() : testNgCase.getName());
//            testCaseStepE.setTestData(testNgCase.getInputData());
//            testCaseStepE.setExpectedResult(testNgCase.getExpectData());
//            //获取TestCycleCaseStep
//            testCycleCaseStepE = new TestCycleCaseStepDTO();
//            testCycleCaseStepE.setTestStep(testCaseStepE.getTestStep());
//            testCycleCaseStepE.setTestData(testCaseStepE.getTestData());
//            testCycleCaseStepE.setExpectedResult(testCaseStepE.getExpectedResult());
//            testCycleCaseStepE.setDescription(testNgCase.getExceptionMessage());
//            //查询状态
//            TestStatusDTO stepStatusE = new TestStatusDTO();
//            stepStatusE.setProjectId(0L);
//            stepStatusE.setStatusType(TestStatusType.STATUS_TYPE_CASE_STEP);
//            stepStatusE.setStatusName(testNgCase.getStatus().equals(TestNgUtil.TEST_PASSED) ? "通过" : "失败");
//            TestStatusDTO stepStatus = testStatusMapper.selectOne(stepStatusE);
//            testCycleCaseStepE.setStepStatus(stepStatus.getStatusId());
//            testCycleCaseStepE.setStatusName(stepStatus.getStatusName());
//            if (issueDTO != null) {
//                testCaseStepE.setIssueId(issueDTO.getIssueId());
//                testCycleCaseStepE.setCaseId(issueDTO.getIssueId());
//            }
//            testCaseSteps.add(testCaseStepE);
//            testCycleCaseSteps.add(testCycleCaseStepE);
//        }
//
//        testCycleCaseE.setTestCaseSteps(testCaseSteps);
//        testCycleCaseE.setCycleCaseStep(testCycleCaseSteps);
//        return testCycleCaseE;
//    }
}
