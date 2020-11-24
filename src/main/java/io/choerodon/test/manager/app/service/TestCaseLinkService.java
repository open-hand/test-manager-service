package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestCaseLinkVO;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.api.vo.agile.IssueInfoDTO;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
public interface TestCaseLinkService {
    /**
     * 删除问题链接
     * @param project
     * @param linkId
     */
    void delete(Long project,Long linkId);

    /**
     * 创建问题链接
     * @param project
     * @param testCaseLinkDTO
     */
    TestCaseLinkDTO create(Long project,TestCaseLinkDTO testCaseLinkDTO);

    /**
     * 查询case关联的issue信息
     * @param projectId
     * @param caseId
     * @return
     */
    List<IssueInfoDTO> listIssueInfo(Long projectId,Long caseId);

    void copyByCaseId(Long projectId, Long caseId, Long oldCaseId);

    List<IssueLinkVO> queryLinkIssues(Long projectId, Long caseId);

    void batchInsert(List<TestCaseLinkDTO> testCaseLinkDTOList);

    List<TestCaseLinkDTO> create(Long projectId, Long caseId, List<Long> issueIds);

    List<TestCaseLinkDTO> createAndLink(Long projectId, Long issueId, TestCaseVO testCaseVO);

    void createByIssue(Long projectId, Long issueId, List<Long> caseIds);

    List<TestCaseLinkVO> queryLinkCases(Long projectId, Long issueId);
}
