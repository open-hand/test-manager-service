package io.choerodon.test.manager.app.service;

import java.util.List;
import io.choerodon.agile.api.vo.IssueInfoDTO;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderRelDTO;

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
}
