package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
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

    List<TestCycleCaseDTO> queryByBug(@Param("projectId") Long projectId, @Param("bugId") Long bugId);

    void batchDeleteByExecutIds(@Param("list") List<Long> executeIds);

    List<TestCycleCaseDefectRelDTO> listByExecuteIds(@Param("list") List<Long> olderExecuteId);

    void batchInsert(@Param("list") List<TestCycleCaseDefectRelDTO> list);

    void batchDeleteByLinkIdsAndType(@Param("list") List<Long> needDeleteExecutedStepIds, @Param("type")String attachmentCaseStep);
}
