package io.choerodon.test.manager.domain.service.impl;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.api.dto.VersionIssueRelDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.devops.api.dto.ApplicationRepDTO;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.test.manager.domain.service.IExcelImportService;
import io.choerodon.test.manager.domain.service.IJsonImportService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import io.choerodon.test.manager.infra.exception.IssueCreateException;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import io.choerodon.test.manager.infra.feign.ProjectFeignClient;

@Service
public class IJsonImportServiceImpl implements IJsonImportService {

    private static final Logger logger = LoggerFactory.getLogger(IJsonImportServiceImpl.class);

    private static final Pattern DATA_PATTERN = Pattern.compile("(?:@data\\s+)(.*?)(?:\\s*\\n)");

    private static final Pattern EXPECT_PATTERN = Pattern.compile("(?:@expect\\s+)(.*?)(?:\\s*\\n)");

    private static final String ERROR_GET_APP_NAME = "error.get.app.name";

    private static final String ERROR_GET_APP_VERSION_NAME = "error.get.app.version.name";

    private static final String ERROR_GET_ORGANIZATION_ID = "error.get.organization.id";

    private IExcelImportService iExcelImportService;

    @Autowired
    public void setiExcelImportService(IExcelImportService iExcelImportService) {
        this.iExcelImportService = iExcelImportService;
    }

    private ProjectFeignClient projectFeignClient;

    @Autowired
    public void setProjectFeignClient(ProjectFeignClient projectFeignClient) {
        this.projectFeignClient = projectFeignClient;
    }

    private ApplicationFeignClient applicationFeignClient;

    @Autowired
    public void setApplicationFeignClient(ApplicationFeignClient applicationFeignClient) {
        this.applicationFeignClient = applicationFeignClient;
    }

    private IssueDTO createIssue(Long organizationId, Long projectId, Long versionId, Long folderId, String summary) {
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO();

        CompletableFuture<Void> getPriorityIdTask = CompletableFuture
                .supplyAsync(() -> iExcelImportService.getPriorityId(organizationId, projectId))
                .thenAccept(priorityId -> {
                    issueCreateDTO.setPriorityCode("priority-" + priorityId);
                    issueCreateDTO.setPriorityId(priorityId);
                });
        CompletableFuture<Void> getIssueTypeIdTask = CompletableFuture
                .supplyAsync(() -> iExcelImportService.getIssueTypeId(organizationId, projectId, "test"))
                .thenAccept(issueCreateDTO::setIssueTypeId);

        issueCreateDTO.setProjectId(projectId);
        issueCreateDTO.setSummary(summary);
        issueCreateDTO.setTypeCode("issue_test");

        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        versionIssueRelDTO.setVersionId(versionId);
        versionIssueRelDTO.setRelationType("fix");
        issueCreateDTO.setVersionIssueRelDTOList(Lists.newArrayList(versionIssueRelDTO));

        CompletableFuture.allOf(getPriorityIdTask, getIssueTypeIdTask).join();
        IssueDTO issueDTO = iExcelImportService.createIssue(projectId, issueCreateDTO);
        if (issueDTO != null) {
            TestIssueFolderRelE issueFolderRelE = SpringUtil.getApplicationContext().getBean(TestIssueFolderRelE.class);
            issueFolderRelE.setProjectId(projectId);
            issueFolderRelE.setVersionId(versionId);
            issueFolderRelE.setFolderId(folderId);
            issueFolderRelE.setIssueId(issueDTO.getIssueId());
            issueFolderRelE.addSelf();
        }

        return issueDTO;
    }

    private TestStatusE getExecutionStatus(String[] failures, String[] passes, String[] pending, String[] skipped) {
        TestStatusE testStatusE = SpringUtil.getApplicationContext().getBean(TestStatusE.class);
        testStatusE.setProjectId(0L);
        testStatusE.setStatusType(TestStatusE.STATUS_TYPE_CASE);

        if (failures.length > 0 || pending.length > 0 || skipped.length > 0) {
            testStatusE.setStatusName("失败");
        } else if (passes.length > 0) {
            testStatusE.setStatusName("通过");
        }

        return testStatusE.queryOneSelective();
    }

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

    @Override
    public TestCycleCaseE processIssueJson(Long organizationId, Long projectId, Long versionId, Long folderId, Long cycleId, JSONObject issue) {
        String summary = issue.getString("title");
        if (StringUtils.isBlank(summary)) {
            logger.error("用例 title 不能为空");
            summary = "null";
        }

        IssueDTO issueDTO = createIssue(organizationId, projectId, versionId, folderId, summary);
        if (issueDTO == null) {
            logger.error("issue 创建失败");
            throw new IssueCreateException();
        }

        TestCycleCaseE testCycleCaseE = SpringUtil.getApplicationContext().getBean(TestCycleCaseE.class);
        testCycleCaseE.setIssueId(issueDTO.getIssueId());
        testCycleCaseE.setCycleId(cycleId);
        testCycleCaseE.setVersionId(versionId);
        String[] failures = getExecutionInfo(issue, "failures");
        String[] passes = getExecutionInfo(issue, "passes");
        String[] pending = getExecutionInfo(issue, "pending");
        String[] skipped = getExecutionInfo(issue, "skipped");
        TestStatusE statusE = getExecutionStatus(failures, passes, pending, skipped);
        testCycleCaseE.setExecutionStatus(statusE.getStatusId());
        testCycleCaseE.setExecutionStatusName(statusE.getStatusName());

        JSONArray testCaseStepsArray = issue.getJSONArray("tests");
        List<TestCaseStepE> testCaseSteps = new ArrayList<>();
        List<TestCycleCaseStepE> testCycleCaseSteps = new ArrayList<>();
        TestCaseStepE testCaseStepE;
        for (Object element : testCaseStepsArray) {
            if (element instanceof JSONObject) {
                testCaseStepE = parseTestCaseStepJson(issueDTO.getIssueId(), (JSONObject) element);
                if (testCaseStepE != null) {
                    testCaseSteps.add(testCaseStepE);
                    testCycleCaseSteps.add(parseTestCycleCaseStepJson(testCaseStepE, (JSONObject) element));
                }
            }
        }

        testCycleCaseE.setTestCaseSteps(testCaseSteps);
        testCycleCaseE.setCycleCaseStepEs(testCycleCaseSteps);
        return testCycleCaseE;
    }

    private TestCycleCaseStepE parseTestCycleCaseStepJson(TestCaseStepE testCaseStepE, JSONObject element) {
        TestCycleCaseStepE testCycleCaseStepE = SpringUtil.getApplicationContext().getBean(TestCycleCaseStepE.class);
        testCycleCaseStepE.setTestStep(testCaseStepE.getTestStep());
        testCycleCaseStepE.setTestData(testCaseStepE.getTestData());
        testCycleCaseStepE.setExpectedResult(testCaseStepE.getExpectedResult());
        testCycleCaseStepE.setIssueId(testCaseStepE.getIssueId());

        TestStatusE statusE = SpringUtil.getApplicationContext().getBean(TestStatusE.class);
        statusE.setProjectId(0L);
        statusE.setStatusType(TestStatusE.STATUS_TYPE_CASE_STEP);
        if (element.getBooleanValue("fail") || element.getBooleanValue("pending")) {
            statusE.setStatusName("失败");
        } else if (element.getBooleanValue("skipped")) {
            statusE.setStatusName("未执行");
        } else {
            statusE.setStatusName("通过");
        }

        TestStatusE targetStatusE = statusE.queryOneSelective();
        testCycleCaseStepE.setStepStatus(targetStatusE.getStatusId());
        testCycleCaseStepE.setStatusName(targetStatusE.getStatusName());

        if (!"通过".equals(targetStatusE.getStatusName())) {
            JSONObject err = element.getJSONObject("err");
            if (err != null) {
                testCycleCaseStepE.setComment(err.getString("message"));
            }
        }

        return testCycleCaseStepE;
    }

    @Override
    @Transactional
    public TestCycleE getCycle(Long versionId, String folderName) {
        TestCycleE testCycleE = SpringUtil.getApplicationContext().getBean(TestCycleE.class);
        testCycleE.setVersionId(versionId);
        testCycleE.setCycleName(folderName);
        TestCycleE targetCycle = testCycleE.queryOne();
        if (targetCycle == null) {
            logger.info("{} 循环不存在，创建", folderName);
            testCycleE.setType(TestCycleE.CYCLE);
            testCycleE.setFromDate(new Date());
            testCycleE.setToDate(testCycleE.getFromDate());
            return testCycleE.addSelf();
        }

        logger.info("{} 循环已存在", folderName);
        return targetCycle;
    }

    @Override
    @Transactional
    public TestCycleE getStage(Long versionId, String stageName, Long parentCycleId, Long folderId) {
        TestCycleE testCycleE = SpringUtil.getApplicationContext().getBean(TestCycleE.class);
        testCycleE.setVersionId(versionId);
        testCycleE.setCycleName(stageName);
        TestCycleE targetStage = testCycleE.queryOne();
        if (targetStage == null) {
            logger.info("{} 阶段不存在，创建", stageName);
            testCycleE.setParentCycleId(parentCycleId);
            testCycleE.setFolderId(folderId);
            testCycleE.setType(TestCycleE.FOLDER);
            testCycleE.setFromDate(new Date());
            testCycleE.setToDate(testCycleE.getFromDate());
            return testCycleE.addSelf();
        }

        logger.info("{} 阶段已存在", stageName);
        return targetStage;
    }

    @Override
    public Long getOrganizationId(Long projectId) {
        try {
            ResponseEntity<ProjectDTO> response = projectFeignClient.query(projectId);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("get organization id {} by project id {}", response.getBody().getOrganizationId(), projectId);
                return response.getBody().getOrganizationId();
            } else {
                throw new CommonException(ERROR_GET_ORGANIZATION_ID);
            }
        } catch (FeignException e) {
            throw new CommonException(ERROR_GET_ORGANIZATION_ID, e);
        }
    }

    @Override
    public Map<String, Long> parseReleaseName(String releaseName) {
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

    @Override
    public String getAppName(Long projectId, Long appId) {
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

    @Override
    public String getAppVersionName(Long projectId, Long appVersionId) {
        try {
            ResponseEntity<List<ApplicationVersionRepDTO>> responses = applicationFeignClient.getAppversion(projectId, Lists.newArrayList(appVersionId));
            ApplicationVersionRepDTO response = responses.getBody().get(0);
            if (!responses.getStatusCode().is2xxSuccessful() || response.getVersion() == null) {
                throw new CommonException(ERROR_GET_APP_VERSION_NAME);
            }
            logger.info("get app version name {} by app version id {} project id {}", response.getVersion(), appVersionId, projectId);
            return response.getVersion();
        } catch (FeignException e) {
            throw new CommonException(ERROR_GET_APP_VERSION_NAME, e);
        }
    }

    private TestCaseStepE parseTestCaseStepJson(Long issueId, JSONObject testCaseStep) {
        String testStep = testCaseStep.getString("title");
        String code = testCaseStep.getString("code");
        String testData = getTestData(code);
        String expectedResult = getExpectedResult(code);
        if (StringUtils.isBlank(testStep) && StringUtils.isBlank(testData) && StringUtils.isBlank(expectedResult)) {
            return null;
        }

        TestCaseStepE caseStepE = SpringUtil.getApplicationContext().getBean(TestCaseStepE.class);
        caseStepE.setIssueId(issueId);
        caseStepE.setTestStep(testStep);
        caseStepE.setTestData(testData);
        caseStepE.setExpectedResult(expectedResult);

        return caseStepE;
    }

    private String getExpectedResult(String code) {
        Matcher matcher = EXPECT_PATTERN.matcher(code);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String getTestData(String code) {
        Matcher matcher = DATA_PATTERN.matcher(code);
        return matcher.find() ? matcher.group(1) : null;
    }
}
