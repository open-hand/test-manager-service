package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
public class TestCaseStepEFactory {
    public static TestCaseStepE create() {
        return ApplicationContextHelper.getSpringFactory().getBean(TestCaseStepE.class);
    }

    private TestCaseStepEFactory() {
    }
}
