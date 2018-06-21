package com.test.devops.domain.factory;

import com.test.devops.domain.entity.TestCycleE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCycleEFactory {
	public static TestCycleE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCycleE.class);
	}
}
