package io.choerodon.test.manager.infra.mapper;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestPlanReporterIssueVO;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
public interface TestCaseLinkMapper extends BaseMapper<TestCaseLinkDTO> {
    void batchInsert(@Param("testCaseLinkDTOList") List<TestCaseLinkDTO> testCaseLinkDTOList);

    List<Long> selectIssueIdByPlanId(@Param("planId") Long planId,
                                     @Param("query") TestPlanReporterIssueVO query);

    List<TestPlanReporterIssueVO> selectWithCaseByIssueIds(@Param("issueIds") List<Long> existedIssueIds,
                                                           @Param("planId") Long planId,
                                                           @Param("query") TestPlanReporterIssueVO query);

    /**
     * 查询问题关联的用例
     *
     * @param issueIds 问题id
     * @return 问题关联的用例
     */
    List<TestCaseDTO> listByIssueIds(@Param("issueIds") List<Long> issueIds);
}
