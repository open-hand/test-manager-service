package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dataobject.TestAutomationResultDO;

public interface TestAutomationResultMapper extends Mapper<TestAutomationResultDO> {

    int insertOneResult(TestAutomationResultDO testAutomationResultDO);
}
