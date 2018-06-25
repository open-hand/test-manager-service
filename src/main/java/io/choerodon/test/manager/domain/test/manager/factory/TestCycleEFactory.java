package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCycleEFactory {
	public static TestCycleE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCycleE.class);
	}
}
