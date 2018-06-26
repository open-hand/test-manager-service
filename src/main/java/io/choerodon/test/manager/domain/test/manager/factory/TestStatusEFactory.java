package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
public class TestStatusEFactory {
	public static TestStatusE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestStatusE.class);
	}

}
