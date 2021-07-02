package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.choerodon.test.manager.api.vo.*;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.dto.TestStatusDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseMapper extends BaseMapper<TestCycleCaseDTO> {
    List<TestCycleCaseDTO> queryWithAttachAndDefect(@Param("dto") TestCycleCaseDTO testCycleCaseDTO, @Param("page") int page, @Param("pageSize") int pageSize);

    /**
     * 查询为父cycle的所有子阶段的信息
     *
     * @param testCycleCaseDTOS
     * @param page
     * @param pageSize
     * @return
     */
    List<TestCycleCaseDTO> queryByFatherCycleWithAttachAndDefect(@Param("dtos") List<TestCycleCaseDTO> testCycleCaseDTOS, @Param("page") int page, @Param("pageSize") int pageSize, @Param("sort") String sort);

    Long queryWithAttachAndDefectCount(@Param("dto") TestCycleCaseDTO testCycleCaseDTO);

    List<TestCycleCaseDTO> filter(Map map);

    /**
     * 查询issues下的cycleCase
     *
     * @param issueId
     * @return
     */
    List<TestCycleCaseDTO> queryInIssues(@Param("ids") Long[] issueId);

    List<TestCycleCaseDTO> queryCaseAllInfoInCyclesOrVersions(@Param("cycleIds") Long[] cycleIds, @Param("versionIds") Long[] versionIds);

    List<TestCycleCaseDTO> queryCycleCaseForReporter(@Param("ids") Long[] issueIds);

    /**
     * 获取cycle（除了temp类型）下是否存的同issue的测试用例
     *
     * @param testCycleCase
     * @return
     */
    Long validateCycleCaseInCycle(TestCycleCaseDTO testCycleCase);

    /**
     * 获取plan下最后一个case的rank
     *
     * @param planId
     * @return
     */
    String getLastedRank(@Param("planId") Long planId);

    /**
     * 获取plan下第一个case的rank
     *
     * @param planId
     * @return
     */
    String getFirstRank(@Param("planId") Long planId);

    int batchInsertTestCycleCases(List<TestCycleCaseDTO> testCycleCaseDTOS);

    void updateAuditFields(@Param("executeIds") Long[] executeId, @Param("userId") Long userId, @Param("date") Date date);

    /**
     * 查询执行详情
     *
     * @param executeId
     * @return
     */
    TestCycleCaseDTO queryByCaseId(@Param("executeId") Long executeId);

    /**
     * 查询状态总览
     */
    List<TestStatusDTO> queryExecutionStatus(@Param("planId") Long planId, @Param("cycleIds") Set<Long> cycleIds);

    /**
     * 查询文件下的执行
     */

    List<TestCycleCaseDTO> queryFolderCycleCase(@Param("planId") Long planId, @Param("cycleIds") Set<Long> cycleIds, @Param("searchDTO") CaseSearchVO caseSearchVO);

    void fixCycleCase();

    void fixSource();

    void fixRank(@Param("testCycleCaseDTOS") List<TestCycleCaseDTO> testCycleCaseDTOS);

    List<TestCycleCaseDTO> listByCycleIds(@Param("cycleIds") List<Long> cycleId);

    void batchInsert(@Param("list") List<TestCycleCaseDTO> testCycleCaseDTOS);

    /**
     * 批量指派执行用例
     */
    void batchAssign(@Param("assignUserId") Long assignUserId, @Param("cycleCaseIds") List<Long> cycleCaseIds);

    void batchDeleteByExecutIds(@Param("list") List<Long> executeIds);

    @MapKey("caseId")
    Map<Long, CaseCompareVO> queryTestCaseMap(@Param("list") List<Long> caseIds);

    List<Long> listByPlanId(@Param("planId") Long planId);

    List<TestCycleCaseDTO> selectByPlanId();

    TestCycleCaseDTO selectByExecuteId(@Param("executeId") Long executeId);

    TestCycleCaseDTO selectCycleCaseAndStep(@Param("executeId") Long executeId);

    List<TestCycleCaseDTO> listAsyncCycleCase(@Param("projectId") Long projectId, @Param("caseId") Long caseId);

    List<FormStatusVO> selectPlanStatus(@Param("planId") Long planId);

    Integer countByCycleIds(@Param("list") List<Long> cycIds);

    int updateExecuteStatus(@Param("executeId") Long executeId);

    void batchUpdateCycleCasePriority(@Param("priorityId") Long priorityId, @Param("changePriorityId") Long changePriorityId, @Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);

    long checkPriorityDelete(@Param("priorityId") Long priorityId, @Param("projectIds") List<Long> projectIds);

    List<Long> selectALLProjectId();

    int updatePriorityByProject(@Param("projectIds") List<Long> projectIds, @Param("priorityId") Long priorityId);

    Integer selectCaseCount(@Param("planId") Long planId);

    List<TestFolderCycleCaseVO> pagedQueryMyExecutionalCase(@Param("userId") Long userId,
                                                            @Param("projectIds") List<Long> projectIds,
                                                            @Param("organizationId") Long organizationId,
                                                            @Param("searchDTO") CaseSearchVO caseSearchVO);

    /**
     * 查询测试用例关联的指定冲刺下的执行
     *
     * @param caseIdList 用例id
     * @param sprintId 冲刺id
     * @return 测试用例关联的指定冲刺下的执行
     */
    List<TestCycleCaseLinkVO> selectTestCycleByCaseAndSprint(@Param("caseIdList") List<Long> caseIdList, @Param("sprintId") Long sprintId);

    List<Long> listStatusBySprintIdAndIssueId(@Param("projectId") Long projectId, @Param("issueId")  Long issueId, @Param("sprintId")  Long sprintId);

    /**
     * 通过循环id批量更新指派人
     * @param assignUserId 指派人
     * @param cycleIds 循环id
     */
    void batchAssignByCycle(@Param("assignUserId") Long assignUserId, @Param("cycleIds") Set<Long> cycleIds);
}
