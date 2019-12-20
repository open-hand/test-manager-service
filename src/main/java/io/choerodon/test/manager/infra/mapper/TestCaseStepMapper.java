package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseStepMapper extends Mapper<TestCaseStepDTO> {

    List<TestCaseStepDTO> query(TestCaseStepDTO testCaseStepDTO);

    String getLastedRank(@Param("issueId") Long issueId);

    String getLastedRank_oracle(@Param("issueId") Long issueId);

    int batchInsertTestCaseSteps(List<TestCaseStepDTO> testCaseStepDTOS);

    void updateAuditFields(@Param("issueIds") Long[] issueId, @Param("userId") Long userId, @Param("date") Date date);

    List<TestCaseStepDTO> listByCaseIds(@Param("caseIds") List<Long> caseIds);

    void deleteByCaseId(@Param("caseId") Long caseId);

    int countByProjectIdAndCaseIds(@Param("caseIds") List<Long> caseIds);
}
