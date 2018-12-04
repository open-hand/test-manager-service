package io.choerodon.test.manager.domain.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;

public interface IJsonImportService {
    TestCycleCaseE processIssueJson(Long organizationId, Long projectId, Long versionId, Long folderId, Long cycleId, JSONObject issue);

    TestCycleE getCycle(Long versionId, String folderName);

    TestCycleE getStage(Long versionId, String stageName, Long parentCycleId, Long folderId);

    Long getOrganizationId(Long projectId);

    Map<String, Long> parseReleaseName(String releaseName);

    String getAppName(Long projectId, Long appId);

    String getAppVersionName(Long projectId, Long appVersionId);
}
