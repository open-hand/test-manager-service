package io.choerodon.test.manager.app.service;

import java.util.List;
import io.choerodon.test.manager.infra.dto.TestCaseLabelRelDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 13:49
 * @description:
 */
public interface TestLabelRelService {
    void batchInsert(Long executeId, List<TestCaseLabelRelDTO> testCaseLabelRelDTOS);
}
