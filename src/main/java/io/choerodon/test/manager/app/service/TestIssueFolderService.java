package io.choerodon.test.manager.app.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderService {
    List<TestIssueFolderDTO> query(TestIssueFolderDTO testIssueFolderDTO);

    TestIssueFolderDTO insert(TestIssueFolderDTO testIssueFolderDTO);

    void delete(TestIssueFolderDTO testIssueFolderDTO);

    TestIssueFolderDTO update(TestIssueFolderDTO testIssueFolderDTO);

    JSONObject getTestIssueFolder(Long projectId);
}
