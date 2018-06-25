package io.choerodon.test.manager.domain.factory;

import io.choerodon.test.manager.domain.entity.TestCaseStepE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCaseStepEFactory {
	public static TestCaseStepE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCaseStepE.class);
	}
}
