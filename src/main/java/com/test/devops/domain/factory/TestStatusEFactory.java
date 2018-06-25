package com.test.devops.domain.factory;

import com.test.devops.domain.entity.TestStatusE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
public class TestStatusEFactory {
	public static TestStatusE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestStatusE.class);
	}

}
