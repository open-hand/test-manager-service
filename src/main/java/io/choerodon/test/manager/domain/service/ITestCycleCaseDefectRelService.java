package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface ITestCycleCaseDefectRelService {
    TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

//    List<TestCycleCaseDefectRelE> update(List<TestCycleCaseDefectRelE> testCycleCaseDefectRelE);

    List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

    List<TestCycleCaseDefectRelE> query(Long linkId, String defectType, Long projectId);

    void populateDefectInfo(List<TestCycleCaseDefectRelE> lists, Long projectId);
}
