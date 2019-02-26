package io.choerodon.test.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseService {

    void delete(Long cycleCaseId, Long projectId);

    Page<TestCycleCaseDTO> queryByCycle(TestCycleCaseDTO dto, PageRequest pageRequest, Long projectId, Long organizationId);

    Page<TestCycleCaseDTO> queryByCycleWithFilterArgs(Long cycleId, PageRequest pageRequest, Long projectId, TestCycleCaseDTO searchDTO);

    TestCycleCaseDTO queryOne(Long cycleCaseId, Long projectId, Long cycleId, Long organizationId);

    List<TestCycleCaseDTO> queryByIssuse(Long issuseId, Long projectId, Long organizationId);

    List<TestCycleCaseDTO> queryInIssues(Long[] issueIds, Long projectId, Long organizationId);

    List<TestCycleCaseDTO> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds, Long projectId, Long organizationId);

    void batchDelete(TestCycleCaseDTO testCycleCaseDTO, Long projectId);

    /**
     * 启动一个测试例
     *
     * @param testCycleCaseDTO
     * @return
     */
    TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId);

    List<TestCycleCaseE> batchCreateForAutoTest(List<TestCycleCaseDTO> list, Long projectId);

    List<Long> getActiveCase(Long range, Long projectId, String day);

    /**
     * 修改一个case
     *
     * @param testCycleCaseDTO
     */
    TestCycleCaseDTO changeOneCase(TestCycleCaseDTO testCycleCaseDTO, Long projectId);

    /**
     * 修改一堆case
     *
     * @param cycleCaseDTOS
     */
    void batchChangeCase(List<TestCycleCaseDTO> cycleCaseDTOS);


    Long countCaseNotRun(Long projectId);

    Long countCaseNotPlain(Long projectId);

    Long countCaseSum(Long projectId);
}
