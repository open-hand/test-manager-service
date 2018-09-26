package io.choerodon.test.manager.domain.service;

import java.util.List;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface ITestCycleCaseDefectRelService {
    TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelE testCycleCaseDefectRelE);
}
