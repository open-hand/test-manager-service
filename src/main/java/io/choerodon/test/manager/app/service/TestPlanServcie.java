package io.choerodon.test.manager.app.service;

import java.util.List;
import io.choerodon.test.manager.api.vo.TestPlanTreeVO;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;

/**
 * @author: 25499
 * @date: 2019/11/26 14:16
 * @description:
 */
public interface TestPlanServcie {

    /**
     * 创建计划
     * @param projectId
     * @param testPlanVO
     * @return
     */
    TestPlanDTO create(Long projectId,TestPlanVO testPlanVO);

    void batchInsert(List<TestPlanDTO> testPlanDTOS);

    TestPlanVO update(Long projectId, TestPlanVO testPlanVO);

    /**
     * 查询测试计划信息和文件夹目录
     * @return
     */
    TestTreeIssueFolderVO ListPlanAndFolderTree(Long projectId, String statusCode);

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
    public void sagaCreatePlan(TestPlanVO testPlanVO);
}
