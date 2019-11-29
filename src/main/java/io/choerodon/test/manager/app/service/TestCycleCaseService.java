package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;

import io.choerodon.test.manager.api.vo.ExecutionStatusVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseInfoVO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import org.springframework.data.domain.Pageable;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseService {

    void delete(Long cycleCaseId, Long projectId);

    PageInfo<TestCycleCaseVO> queryByCycle(TestCycleCaseVO dto, Pageable pageable, Long projectId, Long organizationId);

    PageInfo<TestCycleCaseVO> queryByCycleWithFilterArgs(Long cycleId, Pageable pageable, Long projectId, TestCycleCaseVO searchDTO);

    TestCycleCaseVO queryOne(Long cycleCaseId, Long projectId, Long cycleId, Long organizationId);

    List<TestCycleCaseVO> queryByIssuse(Long issuseId, Long projectId, Long organizationId);

    List<TestCycleCaseVO> queryInIssues(Long[] issueIds, Long projectId, Long organizationId);

    List<TestCycleCaseVO> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds, Long projectId, Long organizationId);

    void batchDelete(TestCycleCaseVO testCycleCaseVO, Long projectId);

    /**
     * 启动一个测试例
     *
     * @param testCycleCaseVO
     * @return
     */
    TestCycleCaseVO create(TestCycleCaseVO testCycleCaseVO, Long projectId);

    List<TestCycleCaseVO> batchCreateForAutoTest(List<TestCycleCaseVO> list, Long projectId);

    List<Long> getActiveCase(Long range, Long projectId, String day);

    /**
     * 修改一个case
     *
     * @param testCycleCaseVO
     */
    TestCycleCaseVO changeOneCase(TestCycleCaseVO testCycleCaseVO, Long projectId);

    /**
     * 修改一堆case
     *
     * @param cycleCaseDTOS
     */
    void batchChangeCase(Long projectId, List<TestCycleCaseVO> cycleCaseDTOS);

    Long countCaseNotRun(Long projectId);

    Long countCaseNotPlain(Long projectId);

    Long countCaseSum(Long projectId);

    void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO);

    List<TestCycleCaseDTO> queryWithAttachAndDefect(TestCycleCaseDTO convert, Pageable pageable);

    /**
     * 创建计划引入测试用例相关信息
     * @param testCycleMap
     * @param testCaseDTOS
     */
    void batchInsertByTestCase(Map<Long, TestCycleDTO> testCycleMap, List<TestCaseDTO> testCaseDTOS);

    TestCycleCaseInfoVO queryCycleCaseInfo(Long projectId, Long executeId);


    List<ExecutionStatusVO> queryStepStatus(Long planId);

    List<TestCycleCaseDTO> listByCycleIds(List<Long> cycleId);
}
