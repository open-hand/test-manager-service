package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseStepMapper extends Mapper<TestCycleCaseStepDO> {

    List<TestCycleCaseStepDO> queryWithTestCaseStep(@Param("dto") TestCycleCaseStepDO testCycleCaseStepDO, @Param("page") Integer page, @Param("pageSize") Integer pageSize);

    List<TestCycleCaseStepDO> queryWithTestCaseStep_oracle(@Param("dto") TestCycleCaseStepDO testCycleCaseStepDO, @Param("page") int page, @Param("pageSize") int pageSize);

    Long queryWithTestCaseStep_count(@Param("executeId") Long executeId);

    List<TestCycleCaseStepDO> queryCycleCaseForReporter(@Param("ids") Long[] ids);

    int batchInsertTestCycleCaseSteps(List<TestCycleCaseStepDO> testCycleCaseStepDOs);

    void updateAuditFields(@Param("executeIds") Long[] executeId, @Param("userId") Long userId, @Param("date") Date date);
}
