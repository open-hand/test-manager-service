//package io.choerodon.test.manager.domain.service.impl;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.Lists;
//import feign.FeignException;
//import io.choerodon.agile.api.vo.IssueCreateDTO;
//import io.choerodon.agile.api.vo.IssueDTO;
//import io.choerodon.agile.api.vo.ProjectDTO;
//import io.choerodon.agile.api.vo.VersionIssueRelVO;
//import io.choerodon.agile.infra.common.enums.IssueTypeCode;
//import io.choerodon.agile.infra.common.utils.AgileUtil;
//import io.choerodon.agile.infra.common.utils.RankUtil;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.devops.api.vo.ApplicationRepDTO;
//import io.choerodon.devops.api.vo.ApplicationVersionRepDTO;
//import io.choerodon.test.manager.api.vo.TestCycleVO;
//import io.choerodon.test.manager.api.vo.testng.TestNgCase;
//import io.choerodon.test.manager.api.vo.testng.TestNgTest;
//import io.choerodon.test.manager.app.service.TestCycleService;
//import io.choerodon.test.manager.app.service.impl.TestCaseServiceImpl;
//import io.choerodon.test.manager.domain.repository.TestCycleRepository;
//import io.choerodon.test.manager.domain.service.IJsonImportService;
//import io.choerodon.test.manager.domain.test.manager.entity.*;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
//import io.choerodon.test.manager.infra.util.DBValidateUtil;
//import io.choerodon.test.manager.infra.util.SpringUtil;
//import io.choerodon.test.manager.infra.util.TestNgUtil;
//import io.choerodon.test.manager.infra.vo.TestCaseStepDTO;
//import io.choerodon.test.manager.infra.vo.TestIssueFolderRelDTO;
//import io.choerodon.test.manager.infra.exception.IssueCreateException;
//import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
//import io.choerodon.test.manager.infra.feign.IssueFeignClient;
//import io.choerodon.test.manager.infra.feign.ProjectFeignClient;
//import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper;
//import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper;
//import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Service
//public class IJsonImportServiceImpl implements IJsonImportService {
//
//    private static final Logger logger = LoggerFactory.getLogger(IJsonImportServiceImpl.class);
//
//    private static final Pattern DATA_PATTERN = Pattern.compile("(?:@data\\s+)(.*?)(?:\\s*\\n)");
//
//    private static final Pattern EXPECT_PATTERN = Pattern.compile("(?:@expect\\s+)(.*?)(?:\\s*\\n)");
//
//    private static final Pattern AUTO_TEST_STAGE_SUFFIX_PATTERN = Pattern.compile("第(\\d+)次测试");
//
//    private static final String ERROR_GET_APP_NAME = "error.get.app.name";
//
//    private static final String ERROR_GET_APP_VERSION_NAME = "error.get.app.version.name";
//
//    private static final String ERROR_GET_ORGANIZATION_ID = "error.get.organization.id";
//
//    @Autowired
//    private TestCaseStepMapper caseStepMapper;
//
//    @Autowired
//    private TestAutomationHistoryMapper automationHistoryMapper;
//
//    @Autowired
//    private TestCycleService testCycleService;
//
//    @Autowired
//    private TestCaseServiceImpl testCaseService;
//
//    @Autowired
//    private IssueFeignClient issueFeignClient;
//
//    @Autowired
//    private TestCycleRepository testCycleRepository;
//
//    public void setIssueFeignClient(IssueFeignClient issueFeignClient) {
//        this.issueFeignClient = issueFeignClient;
//    }
//
//    private ProjectFeignClient projectFeignClient;
//
//    @Autowired
//    public void setProjectFeignClient(ProjectFeignClient projectFeignClient) {
//        this.projectFeignClient = projectFeignClient;
//    }
//
//    private ApplicationFeignClient applicationFeignClient;
//
//    @Autowired
//    public void setApplicationFeignClient(ApplicationFeignClient applicationFeignClient) {
//        this.applicationFeignClient = applicationFeignClient;
//    }
//
//    @Autowired
//    private TestIssueFolderRelMapper issueFolderRelMapper;
//
//    @Override
//    public List<TestIssueFolderRelE> queryAllUnderFolder(TestIssueFolderE issueFolderE) {
//        TestIssueFolderRelE issueFolderRelE = new TestIssueFolderRelE();
//        issueFolderRelE.setFolderId(issueFolderE.getFolderId());
//
//        TestIssueFolderRelDTO issueFolderRelDO = modeMapper.map(issueFolderRelE, TestIssueFolderRelDTO.class);
//        List<TestIssueFolderRelDTO> issueFolderRelDOs = issueFolderRelMapper.select(issueFolderRelDO);
//        return ConvertHelper.convertList(issueFolderRelDOs, TestIssueFolderRelE.class);
//    }
//
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
//        VersionIssueRelVO versionIssueRelDTO = new VersionIssueRelVO();
//        versionIssueRelDTO.setVersionId(versionId);
//        versionIssueRelDTO.setRelationType("fix");
//        issueCreateDTO.setVersionIssueRelVOList(Lists.newArrayList(versionIssueRelDTO));
//
//        IssueDTO issueDTO = testCaseService.createTest(issueCreateDTO, projectId, "test");
//        if (issueDTO != null) {
//            TestIssueFolderRelE issueFolderRelE = SpringUtil.getApplicationContext().getBean(TestIssueFolderRelE.class);
//            issueFolderRelE.setProjectId(projectId);
//            issueFolderRelE.setVersionId(versionId);
//            issueFolderRelE.setFolderId(folderId);
//            issueFolderRelE.setIssueId(issueDTO.getIssueId());
//            issueFolderRelE.addSelf();
//        }
//
//        return issueDTO;
//    }
//
//    private TestStatusE getExecutionStatus(String[] failures, String[] passes, String[] pending, String[] skipped) {
//        TestStatusE testStatusE = SpringUtil.getApplicationContext().getBean(TestStatusE.class);
//        testStatusE.setProjectId(0L);
//        testStatusE.setStatusType(TestStatusType.STATUS_TYPE_CASE);
//
//        if (failures.length > 0 || pending.length > 0 || skipped.length > 0) {
//            testStatusE.setStatusName("失败");
//        } else if (passes.length > 0) {
//            testStatusE.setStatusName("通过");
//        }
//
//        return testStatusE.queryOneSelective();
//    }
//
//    private String[] getExecutionInfo(JSONObject issue, String key) {
//        JSONArray jsonArray = issue.getJSONArray(key);
//        String[] info = new String[jsonArray.size()];
//        for (int i = 0; i < jsonArray.size(); i++) {
//            if (jsonArray.get(i) instanceof String) {
//                info[i] = (String) jsonArray.get(i);
//            }
//        }
//
//        return info;
//    }
//
//    @Override
//    public TestCycleCaseE processIssueJson(Long organizationId, Long projectId, Long versionId, Long folderId, Long cycleId, Long createdBy, JSONObject issue, boolean newFolder) {
//        String summary = issue.getString("title");
//        if (StringUtils.isBlank(summary)) {
//            logger.error("用例 title 不能为空");
//            summary = "null";
//        }
//
//        TestCycleCaseE testCycleCaseE = SpringUtil.getApplicationContext().getBean(TestCycleCaseE.class);
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
//        String[] failures = getExecutionInfo(issue, "failures");
//        String[] passes = getExecutionInfo(issue, "passes");
//        String[] pending = getExecutionInfo(issue, "pending");
//        String[] skipped = getExecutionInfo(issue, "skipped");
//        TestStatusE statusE = getExecutionStatus(failures, passes, pending, skipped);
//        testCycleCaseE.setExecutionStatus(statusE.getStatusId());
//        testCycleCaseE.setExecutionStatusName(statusE.getStatusName());
//
//        JSONArray testCaseStepsArray = issue.getJSONArray("tests");
//        List<TestCaseStepE> testCaseSteps = new ArrayList<>();
//        List<TestCycleCaseStepE> testCycleCaseSteps = new ArrayList<>();
//        TestCaseStepE testCaseStepE;
//        TestCycleCaseStepE cycleCaseStepE;
//        for (Object element : testCaseStepsArray) {
//            if (element instanceof JSONObject) {
//                testCaseStepE = parseTestCaseStepJson((JSONObject) element);
//                if (testCaseStepE != null) {
//                    testCaseSteps.add(testCaseStepE);
//                    cycleCaseStepE = parseTestCycleCaseStepJson(testCaseStepE, (JSONObject) element);
//                    testCycleCaseSteps.add(cycleCaseStepE);
//                    if (issueDTO != null) {
//                        testCaseStepE.setIssueId(issueDTO.getIssueId());
//                        cycleCaseStepE.setIssueId(issueDTO.getIssueId());
//                    }
//                }
//            }
//        }
//
//        testCycleCaseE.setTestCaseSteps(testCaseSteps);
//        testCycleCaseE.setCycleCaseStepEs(testCycleCaseSteps);
//        return testCycleCaseE;
//    }
//
//    private TestCycleCaseStepE parseTestCycleCaseStepJson(TestCaseStepE testCaseStepE, JSONObject element) {
//        TestCycleCaseStepE testCycleCaseStepE = SpringUtil.getApplicationContext().getBean(TestCycleCaseStepE.class);
//        testCycleCaseStepE.setTestStep(testCaseStepE.getTestStep());
//        testCycleCaseStepE.setTestData(testCaseStepE.getTestData());
//        testCycleCaseStepE.setExpectedResult(testCaseStepE.getExpectedResult());
//
//        TestStatusE statusE = SpringUtil.getApplicationContext().getBean(TestStatusE.class);
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
//        TestStatusE targetStatusE = statusE.queryOneSelective();
//        testCycleCaseStepE.setStepStatus(targetStatusE.getStatusId());
//        testCycleCaseStepE.setStatusName(targetStatusE.getStatusName());
//
//        if (!"通过".equals(targetStatusE.getStatusName())) {
//            JSONObject err = element.getJSONObject("err");
//            if (err != null) {
//                testCycleCaseStepE.setComment(err.getString("message"));
//            }
//        }
//
//        return testCycleCaseStepE;
//    }
//
//    @Override
//    @Transactional
//    public TestCycleE getCycle(Long projectId, Long versionId, String folderName) {
//        TestCycleE testCycleE = SpringUtil.getApplicationContext().getBean(TestCycleE.class);
//        testCycleE.setVersionId(versionId);
//        testCycleE.setCycleName(folderName);
//        testCycleE.setType(TestCycleE.CYCLE);
//        testCycleE.setProjectId(projectId);
//        TestCycleE targetCycle = testCycleE.queryOne();
//        if (targetCycle == null) {
//            logger.info("{} 循环不存在，创建", folderName);
//            testCycleE.setType(TestCycleE.CYCLE);
//            testCycleE.setFromDate(new Date());
//            testCycleE.setToDate(testCycleE.getFromDate());
//            testCycleE.checkRank();
//            testCycleE.setRank(RankUtil.Operation.INSERT.getRank(testCycleRepository.getLastedRank(testCycleE), null));
//            return testCycleE.addSelf();
//        }
//
//        logger.info("{} 循环已存在", folderName);
//        return targetCycle;
//    }
//
//    @Override
//    @Transactional
//    public TestCycleVO getStage(Long projectId, Long versionId, String stageName, Long parentCycleId, Long folderId, Long createdBy, Long lastUpdatedBy) {
//        TestCycleE testCycleE = SpringUtil.getApplicationContext().getBean(TestCycleE.class);
//        testCycleE.setVersionId(versionId);
//        testCycleE.setFolderId(folderId);
//        testCycleE.setParentCycleId(parentCycleId);
//        testCycleE.setType(TestCycleE.FOLDER);
//        testCycleE.setProjectId(projectId);
//
//        int lastTestStageNumber = 0;
//        List<TestCycleE> childCycleEs = testCycleE.querySelf();
//        for (TestCycleE cycleE : childCycleEs) {
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
//        testCycleE.setType(TestCycleE.FOLDER);
//        testCycleE.setFromDate(new Date());
//        testCycleE.setToDate(testCycleE.getFromDate());
//        testCycleE.setCreatedBy(createdBy);
//        testCycleE.setLastUpdatedBy(lastUpdatedBy);
//        return testCycleService.insertWithoutSyncFolder(projectId, modeMapper.map(testCycleE, TestCycleVO.class));
//    }
//
//    @Override
//    public Long getOrganizationId(Long projectId) {
//        try {
//            ResponseEntity<ProjectDTO> response = projectFeignClient.query(projectId);
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
//
//    @Override
//    public Map<String, Long> parseReleaseName(String releaseName) {
//        if (!releaseName.startsWith("att-")) {
//            throw new CommonException("releaseName前缀错误");
//        }
//        String[] strings = releaseName.split("-");
//        Map<String, Long> fragments = new HashMap<>();
//        fragments.put("appId", Long.parseLong(strings[1]));
//        fragments.put("appVersionId", Long.parseLong(strings[2]));
//        fragments.put("instanceId", Long.parseLong(strings[3]));
//
//        return fragments;
//    }
//
//    @Override
//    public String getAppName(Long projectId, Long appId) {
//        try {
//            ResponseEntity<ApplicationRepDTO> response = applicationFeignClient.queryByAppId(projectId, appId);
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                logger.info("get app name {} by app id {} project id {}", response.getBody().getName(), appId, projectId);
//                return response.getBody().getName();
//            } else {
//                throw new CommonException(ERROR_GET_APP_NAME);
//            }
//        } catch (FeignException e) {
//            throw new CommonException(ERROR_GET_APP_NAME, e);
//        }
//    }
//
//    @Override
//    public String getAppVersionName(Long projectId, Long appVersionId) {
//        try {
//            ResponseEntity<List<ApplicationVersionRepDTO>> responses = applicationFeignClient.getAppversion(projectId, Lists.newArrayList(appVersionId));
//            ApplicationVersionRepDTO response = responses.getBody().get(0);
//            if (!responses.getStatusCode().is2xxSuccessful() || response.getVersion() == null) {
//                throw new CommonException(ERROR_GET_APP_VERSION_NAME);
//            }
//            logger.info("get app version name {} by app version id {} project id {}", response.getVersion(), appVersionId, projectId);
//            return response.getVersion();
//        } catch (FeignException e) {
//            throw new CommonException(ERROR_GET_APP_VERSION_NAME, e);
//        }
//    }
//
//    @Override
//    @Transactional
//    public void updateAutomationHistoryStatus(TestAutomationHistoryDTO automationHistoryE) {
//        Long objectVersionNumber = automationHistoryMapper.queryObjectVersionNumberByInstanceId(automationHistoryE);
//        automationHistoryE.setObjectVersionNumber(objectVersionNumber);
//        automationHistoryE.setLastUpdateDate(new Date());
//        DBValidateUtil.executeAndvalidateUpdateNum(automationHistoryMapper::updateTestStatusByInstanceId,
//                automationHistoryE, 1, "error.update.testStatus.by.instanceId");
//    }
//
//    @Override
//    public List<TestCaseStepE> queryAllStepsUnderIssue(Long issueId) {
//        TestCaseStepE caseStepE = TestCaseStepEFactory.create();
//        caseStepE.setIssueId(issueId);
//        TestCaseStepDTO caseStepDO = modeMapper.map(caseStepE, TestCaseStepDTO.class);
//        List<TestCaseStepDTO> caseStepDOs = caseStepMapper.query(caseStepDO);
//        return ConvertHelper.convertList(caseStepDOs, TestCaseStepE.class);
//    }
//
//    @Override
//    public TestIssueFolderE getFolder(Long projectId, Long versionId, String folderName) {
//        TestIssueFolderE targetFolderE;
//        TestIssueFolderE folderE = SpringUtil.getApplicationContext().getBean(TestIssueFolderE.class);
//        folderE.setProjectId(projectId);
//        folderE.setVersionId(versionId);
//        folderE.setName(folderName);
//        targetFolderE = folderE.queryOne(folderE);
//        if (targetFolderE == null) {
//            folderE.setType(TestIssueFolderE.TYPE_CYCLE);
//            logger.info("{} 文件夹不存在，创建", folderName);
//            targetFolderE = folderE.addSelf();
//            targetFolderE.setNewFolder(true);
//        } else {
//            targetFolderE.setNewFolder(false);
//            logger.info("{} 文件夹已存在", folderName);
//        }
//
//        return targetFolderE;
//    }
//
//    private TestCaseStepE parseTestCaseStepJson(JSONObject testCaseStep) {
//        String testStep = testCaseStep.getString("title");
//        String code = testCaseStep.getString("code");
//        String testData = getTestData(code);
//        String expectedResult = getExpectedResult(code);
//        if (StringUtils.isBlank(testStep) && StringUtils.isBlank(testData) && StringUtils.isBlank(expectedResult)) {
//            return null;
//        }
//
//        TestCaseStepE caseStepE = SpringUtil.getApplicationContext().getBean(TestCaseStepE.class);
//        caseStepE.setTestStep(testStep);
//        caseStepE.setTestData(testData);
//        caseStepE.setExpectedResult(expectedResult);
//
//        return caseStepE;
//    }
//
//    private String getExpectedResult(String code) {
//        Matcher matcher = EXPECT_PATTERN.matcher(code);
//        return matcher.find() ? matcher.group(1) : null;
//    }
//
//    private String getTestData(String code) {
//        Matcher matcher = DATA_PATTERN.matcher(code);
//        return matcher.find() ? matcher.group(1) : null;
//    }
//
//    @Override
//    public TestCycleCaseE handleIssueByTestNg(Long organizationId, Long projectId, Long versionId, Long folderId, Long cycleId, Long createdBy, TestNgTest test, boolean newFolder) {
//        String summary = test.getName();
//        if (StringUtils.isBlank(summary)) {
//            logger.error("用例 title 不能为空");
//            summary = "null";
//        }
//
//        TestCycleCaseE testCycleCaseE = SpringUtil.getApplicationContext().getBean(TestCycleCaseE.class);
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
//        TestStatusE caseStatusE = SpringUtil.getApplicationContext().getBean(TestStatusE.class);
//        caseStatusE.setProjectId(0L);
//        caseStatusE.setStatusType(TestStatusType.STATUS_TYPE_CASE);
//        caseStatusE.setStatusName(test.getStatus().equals(TestNgUtil.TEST_PASSED) ? "通过" : "失败");
//        TestStatusE caseStatus = caseStatusE.queryOneSelective();
//        testCycleCaseE.setExecutionStatus(caseStatus.getStatusId());
//        testCycleCaseE.setExecutionStatusName(caseStatus.getStatusName());
//
//        List<TestNgCase> cases = test.getCases();
//        List<TestCaseStepE> testCaseSteps = new ArrayList<>();
//        List<TestCycleCaseStepE> testCycleCaseSteps = new ArrayList<>();
//        TestCaseStepE testCaseStepE;
//        TestCycleCaseStepE testCycleCaseStepE;
//        for (TestNgCase testNgCase : cases) {
//            //获取TestCaseStep
//            testCaseStepE = SpringUtil.getApplicationContext().getBean(TestCaseStepE.class);
//            testCaseStepE.setTestStep(testNgCase.getDescription() != null ? testNgCase.getDescription() : testNgCase.getName());
//            testCaseStepE.setTestData(testNgCase.getInputData());
//            testCaseStepE.setExpectedResult(testNgCase.getExpectData());
//            //获取TestCycleCaseStep
//            testCycleCaseStepE = SpringUtil.getApplicationContext().getBean(TestCycleCaseStepE.class);
//            testCycleCaseStepE.setTestStep(testCaseStepE.getTestStep());
//            testCycleCaseStepE.setTestData(testCaseStepE.getTestData());
//            testCycleCaseStepE.setExpectedResult(testCaseStepE.getExpectedResult());
//            testCycleCaseStepE.setComment(testNgCase.getExceptionMessage());
//            //查询状态
//            TestStatusE stepStatusE = SpringUtil.getApplicationContext().getBean(TestStatusE.class);
//            stepStatusE.setProjectId(0L);
//            stepStatusE.setStatusType(TestStatusType.STATUS_TYPE_CASE_STEP);
//            stepStatusE.setStatusName(testNgCase.getStatus().equals(TestNgUtil.TEST_PASSED) ? "通过" : "失败");
//            TestStatusE stepStatus = stepStatusE.queryOneSelective();
//            testCycleCaseStepE.setStepStatus(stepStatus.getStatusId());
//            testCycleCaseStepE.setStatusName(stepStatus.getStatusName());
//            if (issueDTO != null) {
//                testCaseStepE.setIssueId(issueDTO.getIssueId());
//                testCycleCaseStepE.setIssueId(issueDTO.getIssueId());
//            }
//            testCaseSteps.add(testCaseStepE);
//            testCycleCaseSteps.add(testCycleCaseStepE);
//        }
//
//        testCycleCaseE.setTestCaseSteps(testCaseSteps);
//        testCycleCaseE.setCycleCaseStepEs(testCycleCaseSteps);
//        return testCycleCaseE;
//    }
//}
