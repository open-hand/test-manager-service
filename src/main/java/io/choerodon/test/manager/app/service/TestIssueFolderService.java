package io.choerodon.test.manager.app.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderService {
    List<TestIssueFolderDTO> query(TestIssueFolderDTO testIssueFolderDTO);

    TestIssueFolderDTO insert(TestIssueFolderDTO testIssueFolderDTO);

    void delete(Long folderId);

    TestIssueFolderDTO update(TestIssueFolderDTO testIssueFolderDTO);

    JSONObject getTestIssueFolder(Long projectId);

    Long getDefaultFolderId(Long projectId, Long versionId);

    TestIssueFolderDTO copyFolder(Long projectId, Long folderId, Long versionId);

    TestIssueFolderDTO moveFolder(Long projectId, TestIssueFolderDTO testIssueFolderDTO);
}
