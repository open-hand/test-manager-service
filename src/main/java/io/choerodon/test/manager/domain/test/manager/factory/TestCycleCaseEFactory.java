package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
public class TestCycleCaseEFactory {
	public static TestCycleCaseE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCycleCaseE.class);
	}
}
