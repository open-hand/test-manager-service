package io.choerodon.test.manager.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
public interface TestCaseLinkMapper extends BaseMapper<TestCaseLinkDTO> {
    void batchInsert(@Param("testCaseLinkDTOList") List<TestCaseLinkDTO> testCaseLinkDTOList);

    Set<Long> selectIssueIdByPlanId(@Param("planId") Long planId);
}
