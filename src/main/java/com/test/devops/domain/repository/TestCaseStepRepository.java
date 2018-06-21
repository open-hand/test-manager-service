package com.test.devops.domain.repository;

import com.test.devops.domain.entity.TestCaseStepE;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCaseStepRepository {

	TestCaseStepE insert(TestCaseStepE testCaseStepE);

	void delete(TestCaseStepE testCaseStepE);

	TestCaseStepE update(TestCaseStepE testCaseStepE);

	List<TestCaseStepE> query(TestCaseStepE testCaseStepE);

//	public TestCaseStepE queryOne(TestCaseStepE testCaseStepE);
}
