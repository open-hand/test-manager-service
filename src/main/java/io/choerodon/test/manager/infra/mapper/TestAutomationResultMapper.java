package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestAutomationResultDTO;

public interface TestAutomationResultMapper extends BaseMapper<TestAutomationResultDTO> {

    int insertOneResult(TestAutomationResultDTO testAutomationResultDTO);
}
