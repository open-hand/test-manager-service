package io.choerodon.test.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.agile.IssueCreateDTO;
import org.springframework.data.domain.Pageable;
import io.choerodon.test.manager.api.vo.IssueComponentDetailFolderRelVO;
import io.choerodon.test.manager.api.vo.IssueInfosVO;
import io.choerodon.test.manager.api.vo.TestFolderRelQueryVO;
import io.choerodon.test.manager.api.vo.TestIssueFolderRelVO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderRelDTO;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelService {

    TestIssueFolderRelDTO baseInsert(TestIssueFolderRelDTO insert);

    PageInfo<IssueComponentDetailFolderRelVO> query(Long projectId, Long folderId, TestFolderRelQueryVO testFolderRelQueryVO, Pageable pageable, Long organizationId);

    PageInfo<IssueComponentDetailFolderRelVO> queryIssuesById(Long projectId, Long versionId, Long folderId, Long[] issueIds, Long organizationId);

    TestIssueFolderRelVO insertTestAndRelationship(IssueCreateDTO issueCreateDTO, Long projectId, Long folderId, Long versionId, String applyType);

    List<TestIssueFolderRelVO> insertBatchRelationship(Long projectId, List<TestIssueFolderRelVO> testIssueFolderRelVOS);

    void delete(Long projectId, List<Long> issuesId);

    void moveFolderIssue(Long projectId, Long versionId, Long folderId, List<IssueInfosVO> issueInfosVOS);

    void copyIssue(Long projectId, Long versionId, Long folderId, List<IssueInfosVO> issueInfosVOS);

    void updateVersionByFolderWithoutLockAndChangeIssueVersion(TestIssueFolderRelVO testIssueFolderRelVO, List<Long> issues);

    List<TestIssueFolderRelVO> queryByFolder(TestIssueFolderRelVO testIssueFolderRelVO);

    TestIssueFolderRelVO cloneOneIssue(Long projectId, Long issueId);
}
