package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface TestAppInstanceMapper extends BaseMapper<TestAppInstanceE> {
    String queryValueByEnvIdAndAppId(@Param("envId") Long envId, @Param("appId") Long appId);

    List<TestAppInstanceE> queryDelayInstance(@Param("delayTiming") Date time);

    @Update({"update test_app_instance set pod_name=#{podName},container_name=#{containerName} where id=#{id}"})
    int updateInstanceWithoutStatus(TestAppInstanceE testAppInstanceE);

    @Update({"update test_app_instance set pod_status=#{podStatus} where id=#{id} and pod_status < #{podStatus}"})
    int updateStatus(TestAppInstanceE testAppInstanceE);

    @Update({"update test_app_instance set log_id=#{logId} where id=#{id}"})
    int closeInstance(TestAppInstanceE testAppInstanceE);

}
