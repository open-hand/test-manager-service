package com.test.devops.domain.service;

import com.test.devops.domain.entity.TestCycleCaseE;
import com.test.devops.domain.entity.TestCycleCaseStepE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface ITestCycleCaseStepService {
//	TestCycleCaseStepE insert(TestCycleCaseStepE testCycleCaseStepE);


	List<TestCycleCaseStepE> update(List<TestCycleCaseStepE> testCycleCaseStepE);

//	Page<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE, PageRequest pageRequest);

	/**
	 * 查询循环测试下所有步骤
	 *
	 * @param testCycleCaseE
	 * @return
	 */
	List<TestCycleCaseStepE> querySubStep(TestCycleCaseE testCycleCaseE);

	/**
	 * 启动循环测试下所有步骤
	 *
	 * @param testCycleCaseE
	 */
	void createTestCycleCaseStep(TestCycleCaseE testCycleCaseE);

	/**
	 * 删除CycleCase下所有Step
	 *
	 * @param testCycleCaseE
	 */
	void deleteByTestCycleCase(TestCycleCaseE testCycleCaseE);

	//	/**删除一个Step
//	 * @param testCycleCaseStepE
//	 */
	void deleteStep(TestCycleCaseStepE testCycleCaseStepE);

}
