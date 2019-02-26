package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.IssueComponentDetailFolderRelDTO;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestFolderRelQueryDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelService {
    Page<IssueComponentDetailFolderRelDTO> query(Long projectId, Long folderId, TestFolderRelQueryDTO testFolderRelQueryDTO, PageRequest pageRequest,Long organizationId);

    Page<IssueComponentDetailFolderRelDTO> queryIssuesById(Long projectId, Long versionId, Long folderId, Long[] issueIds,Long organizationId);

    TestIssueFolderRelDTO insertTestAndRelationship(IssueCreateDTO issueCreateDTO, Long projectId, Long folderId, Long versionId,String applyType);

    List<TestIssueFolderRelDTO> insertBatchRelationship(Long projectId, List<TestIssueFolderRelDTO> testIssueFolderRelDTOS);

    void delete(Long projectId,List<Long> issuesId);

    void moveFolderIssue(Long projectId, Long versionId, Long folderId,List<IssueInfosDTO> issueInfosDTOS);

    void copyIssue(Long projectId, Long versionId, Long folderId,List<IssueInfosDTO> issueInfosDTOS);

    TestIssueFolderRelDTO updateVersionByFolderWithoutLockAndChangeIssueVersion(TestIssueFolderRelDTO testIssueFolderRelDTO,List<Long> issues);

    List<TestIssueFolderRelDTO> queryByFolder(TestIssueFolderRelDTO testIssueFolderRelDTO);

    TestIssueFolderRelDTO cloneOneIssue(Long projectId, Long issueId);
}
