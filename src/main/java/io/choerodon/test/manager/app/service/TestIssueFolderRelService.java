package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelService {
    List<TestIssueFolderRelDTO> query(TestIssueFolderRelDTO testIssueFolderRelDTO);

    TestIssueFolderRelDTO insertTestAndRelationship(IssueCreateDTO issueCreateDTO,Long projectId,Long folderId,Long versionId);

    TestIssueFolderRelDTO insertRelationship(Long projectId,Long folderId,Long versionId,Long issueId);

    void delete(TestIssueFolderRelDTO testIssueFolderRelDTO);

    TestIssueFolderRelDTO update(TestIssueFolderRelDTO testIssueFolderRelDTO);
}
