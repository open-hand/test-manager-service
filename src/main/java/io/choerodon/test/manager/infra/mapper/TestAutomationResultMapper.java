package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestAutomationResultDTO;

public interface TestAutomationResultMapper extends Mapper<TestAutomationResultDTO> {

    int insertOneResult(TestAutomationResultDTO testAutomationResultDTO);
}
