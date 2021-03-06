package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import io.choerodon.test.manager.api.vo.TestPlanReporterIssueVO;
import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelMapper extends BaseMapper<TestCycleCaseDefectRelDTO> {
    List<TestCycleCaseDefectRelDTO> queryInIssues(@Param("issues") Long[] issues, @Param("projectId") Long projectId);

    List<Long> queryAllIssueIds();

    int updateProjectIdByIssueId(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO);

    List<Long> queryIssueIdAndDefectId(Long projectId);

    void updateAuditFields(@Param("defectId") Long defectId, @Param("userId") Long userId, @Param("date") Date date);

//    List<TestCycleCaseDTO> queryByBug(@Param("projectId") Long projectId, @Param("bugId") Long bugId);

    void batchDeleteByExecutIds(@Param("list") List<Long> executeIds,@Param("type") String type);

    List<TestCycleCaseDefectRelDTO> listByExecuteIds(@Param("list") List<Long> olderExecuteId,@Param("type") String type);

    void batchInsert(@Param("list") List<TestCycleCaseDefectRelDTO> list);

    void batchDeleteByLinkIdsAndType(@Param("list") List<Long> needDeleteExecutedStepIds, @Param("type")String attachmentCaseStep);

    Set<Long> selectIssueIdByPlanId(@Param("planId") Long planId,
                                    @Param("query") TestPlanReporterIssueVO query);

    List<TestPlanReporterIssueVO> selectWithCaseByIssueIds(@Param("issueIds") List<Long> issueIds,
                                                           @Param("planId") Long planId,
                                                           @Param("query") TestPlanReporterIssueVO query);
}
