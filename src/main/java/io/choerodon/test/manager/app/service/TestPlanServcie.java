package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.infra.dto.TestPlanDTO;

/**
 * @author: 25499
 * @date: 2019/11/26 14:16
 * @description:
 */
public interface TestPlanServcie {
    void batchInsert(List<TestPlanDTO> testPlanDTOList);
}
