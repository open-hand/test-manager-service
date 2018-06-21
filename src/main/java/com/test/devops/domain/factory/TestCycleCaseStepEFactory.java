package com.test.devops.domain.factory;

import com.test.devops.domain.entity.TestCycleCaseStepE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCycleCaseStepEFactory {
	public static TestCycleCaseStepE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCycleCaseStepE.class);
	}
}
