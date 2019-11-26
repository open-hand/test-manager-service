package io.choerodon.test.manager.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;

/**
 * @author: 25499
 * @date: 2019/11/26 14:14
 * @description:
 */
public interface TestPlanMapper extends Mapper<TestPlanDTO> {
    void batchInsert(@Param("testPlanDTOList") List<TestPlanDTO> testPlanDTOList);
}
