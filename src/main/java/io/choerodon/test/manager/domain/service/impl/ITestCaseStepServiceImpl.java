package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import io.choerodon.agile.infra.common.utils.RankUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@Service
public class ITestCaseStepServiceImpl implements ITestCaseStepService {

    @Autowired
    ITestCycleCaseStepService testCycleCaseStepService;


    @Override
    public List<TestCaseStepE> query(TestCaseStepE testCaseStepE) {
        return testCaseStepE.querySelf();
    }


    @Override
    public void removeStep(TestCaseStepE testCaseStepE) {
        testCaseStepE.querySelf().forEach(v -> deleteCycleCaseStep(v));
        testCaseStepE.deleteSelf();
    }

    private void deleteCycleCaseStep(TestCaseStepE testCaseStepE) {
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        testCycleCaseStepE.setStepId(testCaseStepE.getStepId());
        testCycleCaseStepService.deleteStep(testCycleCaseStepE);
    }


}
