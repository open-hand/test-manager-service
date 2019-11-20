package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 10:51
 * @description:
 */
public interface TestCaseLabelService {
    /**
     * label数据迁移
     */
    void labelFix();

    /**
     * 批量插入
     * @param testCaseLabelDTOList
     */
    void batchInsert(List<TestCaseLabelDTO> testCaseLabelDTOList);
}
