package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestApiAssertionDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestApiAssertionMapper extends BaseMapper<TestApiAssertionDTO> {
    List<TestApiAssertionDTO> listByCaseIds(@Param("caseIds") List<Long> caseIds);
}
