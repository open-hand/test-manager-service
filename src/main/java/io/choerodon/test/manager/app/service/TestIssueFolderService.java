package io.choerodon.test.manager.app.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.api.vo.TestIssueFolderWithVersionNameVO;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderService {

    TestIssueFolderVO insert(TestIssueFolderVO testIssueFolderVO);

    void delete(Long projectId, Long folderId);

    TestIssueFolderVO update(TestIssueFolderVO testIssueFolderVO);

    JSONObject getTestIssueFolder(Long projectId);

    Long getDefaultFolderId(Long projectId, Long versionId);

    void copyFolder(Long projectId, Long versionId, Long[] folderIds);

    void moveFolder(Long projectId, List<TestIssueFolderVO> testIssueFolderVOS);

    List<TestIssueFolderVO> queryByParameter(Long projectId, Long versionId);

    List<TestIssueFolderWithVersionNameVO> queryByParameterWithVersionName(Long projectId, Long versionId);
}
