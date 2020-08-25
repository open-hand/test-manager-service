package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.infra.dto.TestPriorityDTO;

/**
 * 应用服务
 *
 * @author jiaxu.cui@hand-china.com 2020-08-19 17:25:29
 */
public interface TestPriorityService {

    List<TestPriorityDTO> list(Long organizationId, TestPriorityDTO testPriorityDTO);

    TestPriorityDTO create(Long organizationId, TestPriorityDTO testPriorityDTO);

    TestPriorityDTO update(Long organizationId, TestPriorityDTO testPriorityDTO);

    void delete(Long organizationId, TestPriorityDTO testPriorityDTO);

    void batchChangeIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds);

    Long checkPriorityDelete(Long organizationId, Long priorityId, List<Long> projectIds);

    void changePriorityEnabled(Long organizationId, Long id, boolean enableFlag);

    List<TestPriorityDTO> updateByList(List<TestPriorityDTO> list, Long organizationId);

    Boolean checkName(Long organizationId, String name);

    Long checkDelete(Long organizationId, Long id);
}
