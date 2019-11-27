package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestPlanVO;
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
}
