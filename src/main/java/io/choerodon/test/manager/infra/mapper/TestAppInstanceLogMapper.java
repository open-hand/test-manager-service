package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestAppInstanceLogDTO;
import org.apache.ibatis.annotations.Param;

public interface TestAppInstanceLogMapper extends BaseMapper<TestAppInstanceLogDTO> {
    int insertTestAppInstanceLog(@Param("logE") TestAppInstanceLogDTO logE);
}
