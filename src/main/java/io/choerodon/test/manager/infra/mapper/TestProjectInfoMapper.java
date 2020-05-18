package io.choerodon.test.manager.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestProjectInfoDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
public interface TestProjectInfoMapper extends BaseMapper<TestProjectInfoDTO> {

    void batchInsert(@Param("testProjectInfoDTOList") List<TestProjectInfoDTO> testProjectInfoDTOList);

    int updateProjectCode(@Param("projectId") Long projectId, @Param("projectCode") String projectCode);
}
