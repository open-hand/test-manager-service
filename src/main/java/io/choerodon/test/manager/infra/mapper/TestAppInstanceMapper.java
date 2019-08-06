package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO;

public interface TestAppInstanceMapper extends Mapper<TestAppInstanceDTO> {
    String queryValueByEnvIdAndAppId(@Param("envId") Long envId, @Param("appId") Long appId);

    List<TestAppInstanceDTO> queryDelayInstance(@Param("delayTiming") Date time);

    @Update({"update test_app_instance set pod_name=#{podName},container_name=#{containerName},last_update_date=#{lastUpdateDate} where id=#{id}"})
    int updateInstanceWithoutStatus(TestAppInstanceDTO testAppInstanceDTO);

    @Update({"update test_app_instance set pod_status=#{podStatus},last_update_date=#{lastUpdateDate} where id=#{id} and pod_status < #{podStatus}"})
    int updateStatus(TestAppInstanceDTO testAppInstanceDTO);

    @Update({"update test_app_instance set log_id=#{logId},last_update_date=#{lastUpdateDate} where id=#{id}"})
    int closeInstance(TestAppInstanceDTO testAppInstanceDTO);

}
