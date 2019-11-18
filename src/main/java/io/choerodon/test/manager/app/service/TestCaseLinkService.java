package io.choerodon.test.manager.app.service;

import java.util.List;
import io.choerodon.agile.api.vo.IssueInfoDTO;
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
    void create(Long project,TestCaseLinkDTO testCaseLinkDTO);

    List<IssueInfoDTO> listIssueInfo(Long projectId,Long caseId);
}
