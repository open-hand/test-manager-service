//package io.choerodon.test.manager.domain.service;
//
//import com.alibaba.fastjson.JSONObject;
//import io.choerodon.test.manager.api.vo.TestCycleVO;
//import io.choerodon.test.manager.api.vo.testng.TestNgTest;
//import io.choerodon.test.manager.domain.test.manager.entity.*;
//
//import java.util.List;
//import java.util.Map;
//
//public interface IJsonImportService {
//
//    List<TestIssueFolderRelE> queryAllUnderFolder(TestIssueFolderE issueFolderE);
//
//    TestCycleCaseE processIssueJson(Long organizationId, Long projectId, Long versionId, Long folderId, Long cycleId, Long createdBy, JSONObject issue, boolean newFolder);
//
//    TestCycleE getCycle(Long projectId, Long versionId, String folderName);
//
//    TestCycleVO getStage(Long projectId, Long versionId, String stageName, Long parentCycleId, Long folderId, Long createdBy, Long lastUpdatedBy);
//
//    Long getOrganizationId(Long projectId);
//
//    Map<String, Long> parseReleaseName(String releaseName);
//
//    String getAppName(Long projectId, Long appId);
//
//    String getAppVersionName(Long projectId, Long appVersionId);
//
//    void updateAutomationHistoryStatus(TestAutomationHistoryDTO automationHistoryE);
//
//    List<TestCaseStepE> queryAllStepsUnderIssue(Long issueId);
//
//    TestIssueFolderE getFolder(Long projectId, Long versionId, String folderName);
//
//    TestCycleCaseE handleIssueByTestNg(Long organizationId, Long projectId, Long versionId, Long folderId, Long cycleId, Long createdBy, TestNgTest test, boolean newFolder);
//}
