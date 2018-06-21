package com.test.devops.domain.factory;

import com.test.devops.domain.entity.TestCycleCaseE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCycleCaseEFactory {
	public static TestCycleCaseE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCycleCaseE.class);
	}
}
