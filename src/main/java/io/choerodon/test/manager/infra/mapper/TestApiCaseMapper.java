package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestApiCaseDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestApiCaseMapper extends BaseMapper<TestApiCaseDTO> {

    List<TestApiCaseDTO> queryByTaskId(@Param("taskId") Long taskId);
}
