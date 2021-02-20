package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;

/**
 * @author: 25499
 * @date: 2019/11/26 14:16
 * @description:
 */
public interface TestPlanService {

    /**
     * 创建计划
     * @param projectId
     * @param testPlanVO
     * @return
     */
    TestPlanDTO create(Long projectId,TestPlanVO testPlanVO);

    TestPlanVO update(Long projectId, TestPlanVO testPlanVO);

    void baseUpdate(TestPlanDTO testPlanDTO);

    void delete(Long projectId, Long planId);

    /**
     * 查询计划的信息
     * @param projectId
     * @param planId
     * @return
     */
    TestPlanVO queryPlanInfo(Long projectId, Long planId);

    /**
     * saga调用创建计划
     * @param testPlanVO
     */
     void sagaCreatePlan(TestPlanVO testPlanVO);

    TestPlanVO queryPlan(Long projectId, Long planId);

    void updateStatusCode(Long projectId, TestPlanDTO testPlanDTO);

    /**
     * 复制计划
     * @param projectId
     * @param planId
     * @param name
     * @return
     */
    TestPlanVO clone(Long projectId, Long planId, String name);

    /**
     * saga复制计划
     * @param map
     */
    void sagaClonePlan(Map<String,Long> map);

    /**
     * 改变计划的init状态为fail
     * @param testPlanVO
     */
    void setPlanInitStatusFail(TestPlanVO testPlanVO);

    List<TestPlanVO> projectPlan(Long projectId);

    List<FormStatusVO> planStatus(Long projectId,Long planId);

    TestPlanDTO baseCreate(TestPlanDTO testPlanDTO);

    /**
     * 构建计划树
     * @param projectId
     * @param statusCode
     * @return
     */
    TestTreeIssueFolderVO buildPlanTree(Long projectId, String statusCode);

    /**
     * 改变计划日历时间
     * @param projectId
     * @param testCycleVO
     * @param isCycle
     */
    void operatePlanCalendar(Long projectId, TestCycleVO testCycleVO, Boolean isCycle);

    /**
     * 根据起始日期排序
     * @param projectId 项目id
     * @param planId 计划id
     */
    void orderByFromDate(Long projectId, Long planId);

    /**
     * 查询测试计划报表info数据
     *
     * @param projectId
     * @param planId
     * @return
     */
    TestPlanReporterInfoVO reporterInfo(Long projectId, Long planId);

    Page<TestPlanReporterIssueVO> pagedQueryIssues(Long projectId,
                                                   Long planId,
                                                   PageRequest pageRequest,
                                                   TestPlanReporterIssueVO query);

    Page<TestPlanReporterIssueVO> pagedQueryBugs(Long projectId,
                                                 Long planId,
                                                 PageRequest pageRequest,
                                                 TestPlanReporterIssueVO query);

    /**
     * 项目下计划重名校验
     *
     * @param projectId
     * @param name
     * @return
     */
    Boolean checkName(Long projectId, String name);
}
