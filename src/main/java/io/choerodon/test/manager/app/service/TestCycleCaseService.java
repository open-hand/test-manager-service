package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;

import io.choerodon.test.manager.api.vo.agile.SearchDTO;
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

    void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO);

    List<TestCycleCaseDTO> queryWithAttachAndDefect(TestCycleCaseDTO convert, Pageable pageable);

    /**
     * 创建计划引入测试用例相关信息
     * @param testCycleMap
     * @param caseId
     * @param projectId
     */
    void batchInsertByTestCase(Map<Long, TestCycleDTO> testCycleMap, List<Long> caseId,Long projectId);

    /**
     * 查看测试执行的详情
     * @param projectId
     * @param
     * @return
     */
    TestCycleCaseInfoVO queryCycleCaseInfo(Long executeId,Long projectId, Long planId, Long folderId, SearchDTO searchDTO);


    /**
     * 查看执行状态总览
     * @param projectId
     * @param planId
     * @param folderId
     * @return
     */
    ExecutionStatusVO queryExecuteStatus(Long projectId,Long planId,Long folderId);

    void updateCaseAndStep(Long projectId,TestCycleCaseUpdateVO testCycleCaseUpdateVO,Boolean isAsync);

    void update(TestCycleCaseVO testCycleCaseVO);

    PageInfo<TestFolderCycleCaseVO> listAllCaseByCycleId(Long projectId, Long planId, Long folderId, Pageable pageable, SearchDTO searchDTO);


    List<TestCycleCaseDTO> listByCycleIds(List<Long> cycleId);

    void baseUpdate(TestCycleCaseDTO testCycleCaseDTO);

    /**
     * 批量指派执行用例
     * @param projectId
     * @param userId
     * @param cycleCaseId
     */
    void batchAssignCycleCase(Long projectId, Long userId,  List<Long> cycleCaseId);

    /**
     * 查询执行和步骤信息
     * @param executeId
     * @return
     */
    TestCycleCaseUpdateVO queryCaseAndStep(Long executeId);

    void batchDeleteByExecuteIds(List<Long> deleteCycleCaseIds);

    /**
     * 用例更新对比查询
     * @param projectId
     * @param executeId
     */
    CaseChangeVO selectUpdateCompare(Long projectId, Long executeId);

    /**
     * 更新用例
     * @param projectId
     * @param caseCompareRepVO
     * @return
     */
    void updateCompare(Long projectId, CaseCompareRepVO caseCompareRepVO);


    /**
     * 忽略更新
     * @param projectId
     * @param executedId
     */
    void ignoreUpdate(Long projectId, Long executedId);

    /**
     * 导入用例
     * @param projectId
     * @param cycleId
     * @param map
     */
    void importCase(Long projectId, Long cycleId, Map<Long,CaseSelectVO> map,Long planId);

    /**
     * 复制测试执行
     * @param cycleMapping
     * @param cycIds
     */
    void cloneCycleCase(Map<Long, Long> cycleMapping, List<Long> cycIds);


    TestCycleCaseDTO baseInsert(TestCycleCaseDTO testCycleCaseDTO);
}
