package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface ITestCycleCaseService {
//	TestCycleCaseE insert(TestCycleCaseE testCycleCaseE);

	/**
	 * 创建一个测试例
	 *
	 * @param testCycleCaseE
	 * @return
	 */
	TestCycleCaseE runTestCycleCase(TestCycleCaseE testCycleCaseE, Long projectId);

	void delete(TestCycleCaseE testCycleCaseE);

//	void deleteByCycleId(Long cycleId);

//	List<TestCycleCaseE> update(List<TestCycleCaseE> testCycleCaseE);

	Page<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, PageRequest pageRequest);

//	List<TestCycleCaseE> querySubCase(TestCycleCaseE testCycleCaseE);

	List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE);

	TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE);

//	List<TestCycleCaseE> changeCycleCase(List<TestCycleCaseE> testCycleCaseES);

	TestCycleCaseE changeStep(TestCycleCaseE currentStepE, Long projectId);
}
