package io.choerodon.test.manager.infra.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestAutomationHistoryDTO;

public interface TestAutomationHistoryMapper extends Mapper<TestAutomationHistoryDTO> {

    int updateTestStatusByInstanceId(TestAutomationHistoryDTO automationHistoryDTO);

    Long queryObjectVersionNumberByInstanceId(TestAutomationHistoryDTO automationHistoryDTO);

    List<TestAutomationHistoryDTO> queryWithInstance(@Param("params") Map map);

    @Update({"update test_automation_history set test_status=#{testStatus},last_update_date=#{lastUpdateDate} where instance_id=#{instanceId}"})
    int shutdownInstance(TestAutomationHistoryDTO automationHistoryDTO);
}
