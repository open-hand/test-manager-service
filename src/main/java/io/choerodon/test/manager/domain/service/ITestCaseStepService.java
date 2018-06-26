package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface ITestCaseStepService {

    List<TestCaseStepE> query(TestCaseStepE testCaseStepE);

    void removeStep(TestCaseStepE testCaseStepE);

    List<TestCaseStepE> batchInsertStep(List<TestCaseStepE> testCaseStepES);

    TestCaseStepE changeStep(TestCaseStepE testCaseStepE);

}
