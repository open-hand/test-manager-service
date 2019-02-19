package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseStepMapper extends BaseMapper<TestCycleCaseStepDO> {

    List<TestCycleCaseStepDO> queryWithTestCaseStep(@Param("dto") TestCycleCaseStepDO testCycleCaseStepDO, @Param("page") Integer page, @Param("pageSize") Integer pageSize);
    List<TestCycleCaseStepDO> queryWithTestCaseStep_oracle(@Param("dto") TestCycleCaseStepDO testCycleCaseStepDO, @Param("page") int page, @Param("pageSize") int pageSize);

    Long queryWithTestCaseStep_count(@Param("executeId") Long executeId);

    List<TestCycleCaseStepDO> queryCycleCaseForReporter(@Param("ids") Long[] ids);

    int batchInsertTestCycleCaseSteps(List<TestCycleCaseStepDO> testCycleCaseStepDOs);

    void updateAuditFields(@Param("executeIds") Long[] executeId, @Param("userId") Long userId, @Param("date") Date date);
}
