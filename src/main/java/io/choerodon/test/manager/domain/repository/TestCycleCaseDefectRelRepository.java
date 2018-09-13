package io.choerodon.test.manager.domain.repository;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelRepository {
    TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    TestCycleCaseDefectRelE update(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    Page<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE, PageRequest pageRequest);

    List<TestCycleCaseDefectRelE> queryInIssues(Long[] issues,Long projectId);

    List<Long> queryAllIssueIds();

    Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    List<Long> queryIssueIdAndDefectId(Long projectId);
}
