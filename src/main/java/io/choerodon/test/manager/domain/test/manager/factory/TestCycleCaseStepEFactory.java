package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCycleCaseStepEFactory {
	public static TestCycleCaseStepE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCycleCaseStepE.class);
	}
}
