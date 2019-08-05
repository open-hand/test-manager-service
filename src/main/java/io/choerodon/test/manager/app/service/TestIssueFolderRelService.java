package io.choerodon.test.manager.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import io.choerodon.agile.api.vo.IssueCreateDTO;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.vo.IssueComponentDetailFolderRelVO;
import io.choerodon.test.manager.api.vo.IssueInfosVO;
import io.choerodon.test.manager.api.vo.TestFolderRelQueryVO;
import io.choerodon.test.manager.api.vo.TestIssueFolderRelVO;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelService {
    PageInfo<IssueComponentDetailFolderRelVO> query(Long projectId, Long folderId, TestFolderRelQueryVO testFolderRelQueryVO, PageRequest pageRequest, Long organizationId);

    PageInfo<IssueComponentDetailFolderRelVO> queryIssuesById(Long projectId, Long versionId, Long folderId, Long[] issueIds, Long organizationId);

    TestIssueFolderRelVO insertTestAndRelationship(IssueCreateDTO issueCreateDTO, Long projectId, Long folderId, Long versionId, String applyType);

    List<TestIssueFolderRelVO> insertBatchRelationship(Long projectId, List<TestIssueFolderRelVO> testIssueFolderRelVOS);

    void delete(Long projectId, List<Long> issuesId);

    void moveFolderIssue(Long projectId, Long versionId, Long folderId, List<IssueInfosVO> issueInfosVOS);

    void copyIssue(Long projectId, Long versionId, Long folderId, List<IssueInfosVO> issueInfosVOS);

    TestIssueFolderRelVO updateVersionByFolderWithoutLockAndChangeIssueVersion(TestIssueFolderRelVO testIssueFolderRelVO, List<Long> issues);

    List<TestIssueFolderRelVO> queryByFolder(TestIssueFolderRelVO testIssueFolderRelVO);

    TestIssueFolderRelVO cloneOneIssue(Long projectId, Long issueId);
}
