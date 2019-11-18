package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.infra.dto.TestDataLogDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
public interface TestDataLogService {
    /**
     * 创建日志
     * @param testDataLogDTO
     */
    void create(TestDataLogDTO testDataLogDTO);

    /**
     * 删除日志
     * @param dataLogDTO
     */
    void delete(TestDataLogDTO dataLogDTO);
}
