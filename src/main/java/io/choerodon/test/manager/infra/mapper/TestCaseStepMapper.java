package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dataobject.TestCaseStepDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseStepMapper extends BaseMapper<TestCaseStepDO> {

    List<TestCaseStepDO> query(TestCaseStepDO testCaseStepDO);

    String getLastedRank(@Param("issueId") Long issueId);
    String getLastedRank_oracle(@Param("issueId") Long issueId);

    int batchInsertTestCaseSteps(List<TestCaseStepDO> testCaseStepDOs);

    void updateAuditFields(@Param("issueIds") Long[] issueId, @Param("userId") Long userId, @Param("date") Date date);
}
