package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
public class TestCycleCaseDefectRelEFactory {
    public static TestCycleCaseDefectRelE create() {
        return ApplicationContextHelper.getSpringFactory().getBean(TestCycleCaseDefectRelE.class);
    }

    private TestCycleCaseDefectRelEFactory() {
    }
}
