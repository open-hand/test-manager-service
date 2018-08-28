package io.choerodon.test.manager.domain.repository;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDefectRelDO;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelRepository {
    TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    TestCycleCaseDefectRelE update(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    Page<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE, PageRequest pageRequest);

    List<TestCycleCaseDefectRelE> queryInIssues(Long[] issues);

    List<Long> queryAllIssueIds();

    Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    Map<Long,List<Long>> queryIssueIdAndDefectId(Long projectId);
}
