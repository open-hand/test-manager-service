package io.choerodon.test.manager.domain.repository;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseRepository {
    TestCycleCaseE insert(TestCycleCaseE testCycleCaseE);

    void delete(TestCycleCaseE testCycleCaseE);

    TestCycleCaseE update(TestCycleCaseE testCycleCaseE);

    PageInfo<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, PageRequest pageRequest);

    PageInfo<TestCycleCaseE> queryByFatherCycle(List<TestCycleCaseE> testCycleCaseES, PageRequest pageRequest);

    List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE);

    TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE);

    List<TestCycleCaseE> filter(Map map);

    List<TestCycleCaseE> queryByIssue(Long issueId);

    List<TestCycleCaseE> queryInIssue(Long[] issueId);

    List<TestCycleCaseE> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds);

    List<TestCycleCaseE> queryCycleCaseForReporter(Long[] issueIds);

    Long countCaseNotRun(Long[] cycleIds);

    Long countCaseNotPlain(Long[] cycleIds);

    Long countCaseSum(Long[] cycleIds);

    void validateCycleCaseInCycle(TestCycleCaseDO testCycleCase);

    String getLastedRank(Long cycleId);

    List<TestCycleCaseE> batchInsert(Long projectId, List<TestCycleCaseE> testCycleCases);
}
