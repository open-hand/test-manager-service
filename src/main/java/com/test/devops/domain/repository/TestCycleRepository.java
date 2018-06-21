package com.test.devops.domain.repository;

import com.test.devops.domain.entity.TestCaseStepE;
import com.test.devops.domain.entity.TestCycleE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleRepository {
	TestCycleE insert(TestCycleE testCycleE);

	void delete(TestCycleE testCycleE);

	TestCycleE update(TestCycleE testCycleE);

	Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest);

	List<TestCycleE> query(TestCycleE testCycleE);
}
