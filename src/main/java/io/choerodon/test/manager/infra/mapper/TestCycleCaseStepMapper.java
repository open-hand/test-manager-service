package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseStepMapper extends BaseMapper<TestCycleCaseStepDO> {

	List<TestCycleCaseStepDO> queryWithTestCaseStep(TestCycleCaseStepDO testCycleCaseStepDO);
}
