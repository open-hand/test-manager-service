package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelService {
    List<IssueInfosDTO> query(Long projectId, Long folderId, Long versionId);

    TestIssueFolderRelDTO insertTestAndRelationship(IssueCreateDTO issueCreateDTO,Long projectId,Long folderId,Long versionId);

    List<TestIssueFolderRelDTO> insertRelationship(Long projectId, List<TestIssueFolderRelDTO> testIssueFolderRelDTOS);

    void delete(TestIssueFolderRelDTO testIssueFolderRelDTO);

    TestIssueFolderRelDTO update(TestIssueFolderRelDTO testIssueFolderRelDTO);
}
