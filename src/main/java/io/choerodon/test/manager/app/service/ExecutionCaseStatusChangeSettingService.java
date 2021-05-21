package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.vo.ExecutionCaseStatusChangeSettingVO;

import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-05-10 16:52
 */
public interface ExecutionCaseStatusChangeSettingService {

    void save(Long projectId, Long organizationId, ExecutionCaseStatusChangeSettingVO executionCaseStatusChangeSettingVO);

    List<ExecutionCaseStatusChangeSettingVO> listByIssueStatusIds(Long projectId, Long organizationId, Long issueTypeId, List<Long> statusIds);

    void updateExecutionStatus(Long projectId, Long cycleCaseId, Long testStatusId);

    ExecutionCaseStatusChangeSettingVO queryByOption(Long projectId, Long organizationId, Long issueTypeId, Long statusId);
}
