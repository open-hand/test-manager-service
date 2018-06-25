package io.choerodon.test.manager.domain.repository;

import io.choerodon.test.manager.domain.entity.TestCycleCaseE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseRepository {
	TestCycleCaseE insert(TestCycleCaseE testCycleCaseE);

	void delete(TestCycleCaseE testCycleCaseE);

	TestCycleCaseE update(TestCycleCaseE testCycleCaseE);

	Page<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, PageRequest pageRequest);

	List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE);

	TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE);
}
