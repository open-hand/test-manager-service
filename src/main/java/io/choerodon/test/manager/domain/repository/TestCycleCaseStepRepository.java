package io.choerodon.test.manager.domain.repository;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseStepRepository {
    TestCycleCaseStepE insert(TestCycleCaseStepE testCycleCaseStepE);

    void delete(TestCycleCaseStepE testCycleCaseStepE);

    TestCycleCaseStepE update(TestCycleCaseStepE testCycleCaseStepE);

    PageInfo<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE, PageRequest pageRequest);

	List<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE);
    List<TestCycleCaseStepE> queryCycleCaseForReporter(Long[] ids);

    List<TestCycleCaseStepE> batchInsert(List<TestCycleCaseStepE> testCycleCaseSteps);
}
