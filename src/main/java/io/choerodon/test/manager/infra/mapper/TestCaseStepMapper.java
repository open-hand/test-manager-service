package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCaseStepDO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCaseStepMapper extends BaseMapper<TestCaseStepDO> {

    List<TestCaseStepDO> query(TestCaseStepDO testCaseStepDO);
}
