package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.core.domain.Page;

import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseService {

    void delete(Long cycleCaseId, Long projectId);

    List<TestCycleCaseVO> queryInIssues(Long[] issueIds, Long projectId, Long organizationId);

    List<TestCycleCaseVO> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds, Long projectId, Long organizationId);

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

    void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO);

    List<TestCycleCaseDTO> queryWithAttachAndDefect(TestCycleCaseDTO convert, PageRequest pageRequest);

    /**
     * 创建计划引入测试用例相关信息
     * @param testCycleMap
     * @param caseId
     * @param projectId
     */
    void batchInsertByTestCase(Map<Long, TestCycleDTO> testCycleMap, List<Long> caseId,Long projectId,Long planId);

    /**
     * 查看测试执行的详情
     * @param projectId
     * @param
     * @return
     */
    TestCycleCaseInfoVO queryCycleCaseInfo(Long executeId,Long projectId, Long planId, Long folderId, CaseSearchVO caseSearchVO);


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

    Page<TestFolderCycleCaseVO> listAllCaseByCycleId(Long projectId, Long planId, Long folderId, PageRequest pageRequest, CaseSearchVO caseSearchVO);


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

    void batchUpdateCycleCasePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds);

    Page<TestFolderCycleCaseVO> pagedQueryMyExecutionalCase(Long organizationId, Long projectId, PageRequest pageRequest, CaseSearchVO caseSearchVO);

    /**
     * 批量指派文件夹中的用例
     * @param projectId 项目id
     * @param assignUserId 指派人
     * @param cycleId 循环id
     * @param planId 计划id
     */
    void assignCaseByCycle(Long projectId, Long assignUserId, Long cycleId, Long planId);
}
