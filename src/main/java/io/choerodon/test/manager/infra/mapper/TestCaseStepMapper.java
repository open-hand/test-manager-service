package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dataobject.TestCaseStepDO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseStepMapper extends Mapper<TestCaseStepDO> {

    List<TestCaseStepDO> query(TestCaseStepDO testCaseStepDO);

    String getLastedRank(@Param("issueId") Long issueId);

    String getLastedRank_oracle(@Param("issueId") Long issueId);

    int batchInsertTestCaseSteps(List<TestCaseStepDO> testCaseStepDOs);

    void updateAuditFields(@Param("issueIds") Long[] issueId, @Param("userId") Long userId, @Param("date") Date date);
}
