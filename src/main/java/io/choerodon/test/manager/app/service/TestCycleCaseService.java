package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;

import io.choerodon.agile.api.vo.SearchDTO;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import org.springframework.data.domain.Pageable;

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


    /**
     * 查看执行状态总览
     * @param projectId
     * @param planId
     * @param folderId
     * @return
     */
    ExecutionStatusVO queryExecuteStatus(Long projectId,Long planId,Long folderId);

    TestCycleCaseUpdateVO update(TestCycleCaseUpdateVO testCycleCaseUpdateVO);

    PageInfo<TestFolderCycleCaseVO> listAllCaseByFolderId(Long projectId, Long planId, Long folderId, Pageable pageable, SearchDTO searchDTO);


    List<TestCycleCaseDTO> listByCycleIds(List<Long> cycleId);

    void baseUpdate(TestCycleCaseDTO testCycleCaseDTO);

    /**
     * 批量指派执行用例
     * @param projectId
     * @param userId
     * @param cycleCaseId
     */
    void batchAssignCycleCase(Long projectId, Long userId,  List<Long> cycleCaseId);
}
