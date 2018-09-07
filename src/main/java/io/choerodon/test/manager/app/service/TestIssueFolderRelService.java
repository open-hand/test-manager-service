package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.agile.api.dto.IssueComponentDetailDTO;
import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.IssueComponentDetailFolderRelDTO;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelService {
    Page<IssueComponentDetailFolderRelDTO> query(Long projectId, Long folderId, Long versionId, SearchDTO searchDTO, PageRequest pageRequest);

    Page<IssueComponentDetailFolderRelDTO> queryIssuesById(Long projectId, Long versionId, Long folderId, Long[] issueIds);

    TestIssueFolderRelDTO insertTestAndRelationship(IssueCreateDTO issueCreateDTO, Long projectId, Long folderId, Long versionId);

    List<TestIssueFolderRelDTO> insertRelationship(Long projectId, List<TestIssueFolderRelDTO> testIssueFolderRelDTOS);

    void delete(TestIssueFolderRelDTO testIssueFolderRelDTO);

    TestIssueFolderRelDTO update(TestIssueFolderRelDTO testIssueFolderRelDTO);

    TestIssueFolderRelDTO updateFolderByIssue(TestIssueFolderRelDTO testIssueFolderRelDTO);

    void moveIssue(Long projectId, Long versionId, Long folderId,List<IssueInfosDTO> issueInfosDTOS);

    void copyIssue(Long projectId, Long versionId, Long folderId,List<IssueInfosDTO> issueInfosDTOS);

    TestIssueFolderRelDTO updateVersionByFolderWithNoLock(TestIssueFolderRelDTO testIssueFolderRelDTO);

    List<TestIssueFolderRelDTO> queryByFolder(TestIssueFolderRelDTO testIssueFolderRelDTO);
}
