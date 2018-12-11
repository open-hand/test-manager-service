package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TestAppInstanceMapper extends BaseMapper<TestAppInstanceE> {
    String queryValueByEnvIdAndAppId(@Param("envId") Long envId, @Param("appId") Long appId);

    List<TestAppInstanceE> queryDelayInstance(@Param("delayTiming") Date time);
}
