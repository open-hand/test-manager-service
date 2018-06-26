package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseMapper extends BaseMapper<TestCycleCaseDO> {
    List<TestCycleCaseDO> query(TestCycleCaseDO testCycleCaseDO);
}
