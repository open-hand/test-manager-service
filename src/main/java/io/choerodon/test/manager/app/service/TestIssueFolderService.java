package io.choerodon.test.manager.app.service;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderService {

    TestIssueFolderDTO insert(TestIssueFolderDTO testIssueFolderDTO);

    void delete(Long projectId,Long folderId);

    TestIssueFolderDTO update(TestIssueFolderDTO testIssueFolderDTO);

    JSONObject getTestIssueFolder(Long projectId);

    Long getDefaultFolderId(Long projectId, Long versionId);

    void copyFolder(Long projectId, Long versionId,Long[] folderIds);

    void moveFolder(Long projectId, List<TestIssueFolderDTO> testIssueFolderDTOS);

    List<TestIssueFolderDTO> queryByParameter(Long projectId,Long versionId);
}
