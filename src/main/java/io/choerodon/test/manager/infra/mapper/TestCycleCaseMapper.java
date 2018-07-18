package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseMapper extends BaseMapper<TestCycleCaseDO> {
    List<TestCycleCaseDO> query(TestCycleCaseDO testCycleCaseDO);

	List<TestCycleCaseDO> filter(Map map);

	List<TestCycleCaseDO> queryByIssue(@Param("issueId") Long issueId);

	List<TestCycleCaseDO> queryCycleCaseForReporter(@Param("ids") Long[] issueIds);

	Long countCaseNotRun(@Param("ids") Long[] cycleIds);

	Long countCaseNotPlain(@Param("ids") Long[] cycleIds);

	Long countCaseSum(@Param("ids") Long[] cycleIds);

	List<TestCycleCaseDO> validateCycleCaseInCycle(TestCycleCaseDO testCycleCase);

	String getLastedRank(@Param("cycleId") Long cycleId);
}
