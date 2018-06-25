package io.choerodon.test.manager.domain.factory;

import io.choerodon.test.manager.domain.entity.TestCycleCaseHistoryE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCycleCaseHistoryEFactory {
	public static TestCycleCaseHistoryE create() {
		return ApplicationContextHelper.getSpringFactory().getBean(TestCycleCaseHistoryE.class);
	}
}
