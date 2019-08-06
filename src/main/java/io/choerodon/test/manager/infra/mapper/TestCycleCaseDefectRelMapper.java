package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelMapper extends Mapper<TestCycleCaseDefectRelDTO> {
    List<TestCycleCaseDefectRelDTO> queryInIssues(@Param("issues") Long[] issues, @Param("projectId") Long projectId);

    List<Long> queryAllIssueIds();

    int updateProjectIdByIssueId(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO);

    List<Long> queryIssueIdAndDefectId(Long projectId);

    void updateAuditFields(@Param("defectId") Long defectId, @Param("userId") Long userId, @Param("date") Date date);
}
