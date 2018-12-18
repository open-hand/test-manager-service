package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface TestAutomationHistoryMapper extends BaseMapper<TestAutomationHistoryE> {

    int updateTestStatusByInstanceId(TestAutomationHistoryE automationHistoryE);

    Long queryObjectVersionNumberByInstanceId(TestAutomationHistoryE automationHistoryE);

    List<TestAutomationHistoryE> queryWithInstance(@Param("params")Map map);

    @Update({"update test_automation_history set test_status=#{testStatus},last_update_date=#{lastUpdateDate} where instance_id=#{instanceId}"})
    int shutdownInstance(TestAutomationHistoryE automationHistoryE);
}
