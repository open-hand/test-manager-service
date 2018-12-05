package io.choerodon.test.manager.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dataobject.TestAutomationResultDO;

public interface TestAutomationResultMapper extends BaseMapper<TestAutomationResultDO> {

    int insertOneResult(TestAutomationResultDO testAutomationResultDO);
}
