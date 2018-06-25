package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseStepService {

	/**
	 * 更新循环步骤
	 *
	 * @param testCycleCaseStepDTO
	 * @return
	 */
	List<TestCycleCaseStepDTO> update(List<TestCycleCaseStepDTO> testCycleCaseStepDTO);


	/**
	 * 查询循环测试步骤
	 *
	 * @param testCycleCaseDTO
	 * @return
	 */
	List<TestCycleCaseStepDTO> querySubStep(Long CycleCaseId);

	/**
	 * 启动循环测试下所有步骤
	 *
	 * @param testCycleCaseDTO
	 */
	void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO);

	/**
	 * 删除CycleCase下所有Step
	 *
	 * @param testCycleCaseDTO
	 */
	void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO);
}
