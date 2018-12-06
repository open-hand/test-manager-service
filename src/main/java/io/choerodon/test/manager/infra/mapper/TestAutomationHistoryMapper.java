package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TestAutomationHistoryMapper extends BaseMapper<TestAutomationHistoryE> {

    List<TestAutomationHistoryE> queryWithInstance(@Param("params")Map map);
}
