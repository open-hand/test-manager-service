package com.test.devops.domain.factory;

import com.test.devops.domain.entity.TestCycleCaseHistoryE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCycleCaseHistoryEFactory {
	public static TestCycleCaseHistoryE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCycleCaseHistoryE.class);
	}
}
